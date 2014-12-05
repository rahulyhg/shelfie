package nl.shelfiesupport.shelfie;

import android.content.Context;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class Shelf {
    private static Shelf instance = null;
    private List<ShelfItem> items;

    private boolean changed = false;
    private int currentItem = 0;

    public void setExportId(String exportId) {
        this.exportId = exportId;
        this.setChanged(true);
    }

    private String exportId = null;
    private String name = "standard_shelf";

    public Shelf() {

        this.items = new ArrayList<ShelfItem>();
    }

    public Shelf(String name) {
        this();
        this.name = name;
    }

    public Shelf(JSONObject me) {
        fromJSON(me);
    }

    public Shelf(JSONObject me, boolean isImported) {
        fromJSON(me, isImported);
    }

    private void fromJSON(JSONObject me) {
        fromJSON(me, false);
    }

    private void fromJSON(JSONObject me, boolean isImported) {
        this.items = new ArrayList<ShelfItem>();
        Log.d(Tag.SHELFIE, "loading shelf from inventory: " + me.toString());
        try {
            JSONArray jsonItems = me.getJSONArray("items");
            for (int i = 0; i < jsonItems.length(); i++) {
                JSONObject jsonItem = jsonItems.getJSONObject(i);
                ShelfItem item = new ShelfItem(jsonItem.getString("name"), jsonItem.getInt("desiredAmount"));
                if(jsonItem.has("store")) {
                    item.setStore(new Store(jsonItem.getJSONObject("store")));
                }
                items.add(item);
            }
            if(me.has("_id") && !isImported) {
                Log.d(Tag.SHELFIE, "found export id " + me.getString("_id"));
                exportId = me.getString("_id");
            }
            if(me.has("name")) { name = me.getString("name"); }
        } catch (JSONException e) {
            Log.e(Tag.SHELFIE, "Failed to load JSON");
        }
    }

    public static Shelf getInstance(Context context) {
        if(instance == null) { instance = Inventory.getShelf(context); }
        return instance;
    }

    public List<ShelfItem> getItems() {
        return this.items;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject me = new JSONObject();
        JSONArray jsonItems = new JSONArray();
        me.put("name", name);
        for(ShelfItem item : items) {
            jsonItems.put(item.toJSON());
        }
        me.put("items", jsonItems);
        if(exportId != null) {
            me.put("_id", exportId);
        }
        return me;
    }

    @Override
    public String toString() {
        return "Shelf{items=" + items + "}";
    }

    public void save(Context context) {
        if(!changed) { return; }
        if(Inventory.getInstance(context).save(context)) {
            changed = false;
        }
    }

    public void addItem(ShelfItem item) {
        ShelfItem selectedItem = getSelectedItem();
        if(selectedItem != null) {
            items.add(items.indexOf(selectedItem) + 1, item);
        } else {
            items.add(item);
        }
        setSelectedItem(item);
        setChanged(true);
    }

    public void removeItem(int position) {
        items.remove(position);
        setChanged(true);
    }

    public void setChanged(boolean changed) {
        if(changed) { Log.d(Tag.SHELFIE, "Change in Shelf registered"); }
        this.changed = changed;
    }

    public void adjustDesiredAmount(ShelfItem item, int relAmt) {
        item.adjustDesiredAmount(relAmt);
        setChanged(true);
    }

    public void setStore(ShelfItem item, Store store) {
        Log.d(Tag.SHELFIE, "Setting store to " + store.getName() + " for shelfitem " + item.getName());
        item.setStore(store);
        setChanged(true);
    }

    public void swapItems(int position, int pos2) {
        if(items.isEmpty() || pos2 < 0 || items.size() <= pos2) { return; }

        ShelfItem a = items.get(position);
        ShelfItem b = items.get(pos2);
        if(a != null && b != null) {
            items.set(pos2, a);
            items.set(position, b);
            setChanged(true);
        }
    }

    public ShelfItem getCurrentItem() {
        return items.get(currentItem);
    }


    public void nextItem() {
        if(currentItem < items.size() - 1) {
            currentItem++;
        }
    }

    public void prevItem() {
        if(currentItem > 0) { currentItem--; }
    }

    public String getExportId() {
        return exportId;
    }

    public int getSelectedItemPosition() {
        ShelfItem item = getSelectedItem();
        if(item != null) {
            return items.indexOf(item);
        }
        return items.size() - 1;
    }

    public ShelfItem getSelectedItem() {
        for(ShelfItem shelfItem : items) {
            if(shelfItem.isSelected()) { return shelfItem; }
        }
        return null;
    }

    public void setSelectedItem(ShelfItem item) {
        for(ShelfItem shelfItem : items) {
            if(item.equals(shelfItem)) {
                shelfItem.setSelected(true);
            } else {
                shelfItem.setSelected(false);
            }

        }
    }

    public String getName() {
        return name;
    }

    public static void setInstanceChanged(Context context) {
        instance = null;
        getInstance(context);
    }

    public void overwriteWith(Context context, Shelf importedShelf) {
        items.clear();
        for(ShelfItem item : importedShelf.getItems()) {
            items.add(item);
        }
        setChanged(true);
        save(context);
    }

    public void setName(String name) {
        this.name = name;
    }
}
