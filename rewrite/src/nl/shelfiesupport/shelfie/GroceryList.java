package nl.shelfiesupport.shelfie;

import android.content.Context;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GroceryList {
    private static final String FILENAME = "the_groceries.json";
    private static GroceryList instance = null;
    private boolean changed = false;
    private final List<ShelfItem> groceries;

    private GroceryList(@SuppressWarnings("SameParameterValue") String filename, Context context) {
        FileInputStream is;
        this.groceries = new ArrayList<ShelfItem>();
        try {
            is = context.openFileInput(filename);

            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            String line = r.readLine();
            StringBuilder sb = new StringBuilder();
            while(line != null) {
                sb.append(line);
                line = r.readLine();
            }
            JSONObject me = new JSONObject(sb.toString());
            JSONArray jsonGroceries = me.getJSONArray("groceries");
            for(int i = 0; i < jsonGroceries.length(); i++) {
                JSONObject jsonGrocery = jsonGroceries.getJSONObject(i);
                ShelfItem grocery = new ShelfItem(jsonGrocery.getString("name"), jsonGrocery.getInt("desiredAmount"));
                if(jsonGrocery.has("store")) {
                    grocery.setStore(new Store(jsonGrocery.getJSONObject("store")));
                }
                groceries.add(grocery);
            }
            is.close();
        } catch(IOException ignored) {
            Log.e(Tag.SHELFIE, "Failed to load grofrom file");
        } catch(JSONException ignored) {
            Log.e(Tag.SHELFIE, "Failed to parse JSON");
        }
    }

    public static GroceryList getInstance(Context context) {
        if(instance == null) { instance = new GroceryList(FILENAME, context); }
        return instance;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject me = new JSONObject();
        JSONArray jsonGroceries = new JSONArray();

        for(ShelfItem grocery : groceries) {
            jsonGroceries.put(grocery.toJSON());
        }
        me.put("groceries", jsonGroceries);
        me.put("updatedAt", System.currentTimeMillis());
        return me;
    }

    public void save(Context context) {
        if(!changed) { return; }
        FileOutputStream os;
        try {
            os = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            Log.d(Tag.SHELFIE, "Saving GroceryList: " + toJSON().toString());
            os.write(toJSON().toString().getBytes());
            changed = false;
            os.close();
        } catch(IOException e) {
            Log.e(Tag.SHELFIE, "Failed to save to file");
        } catch (JSONException e) {
            Log.e(Tag.SHELFIE, "Failed to parse JSON");
        }
    }

    public void setChanged(@SuppressWarnings("SameParameterValue") boolean changed) {
        if(changed) { Log.d(Tag.SHELFIE, "Change in GroceryList registered"); }
        this.changed = changed;
    }

    public void removeGrocery(int position) {
        if(groceries.get(position) != null) { groceries.remove(position); setChanged(true);}
    }

    public List<ShelfItem> getGroceries() {
        return groceries;
    }

    public void addGrocery(ShelfItem item, int amount) {
        ShelfItem grocery = new ShelfItem(item.getName(), amount, item.getStore());
        ShelfItem selectedItem = getSelectedItem();
        if(selectedItem != null && selectedItem.getStore().equals(item.getStore())) {
            groceries.add(groceries.indexOf(selectedItem) + 1, grocery);
        } else {
            int idx;
            boolean found = false;
            for(idx = 0 ; idx < groceries.size(); idx++) {
                if(groceries.get(idx).getStore().equals(item.getStore())) {
                    found = true;
                } else if(found) {
                    break;
                }
            }
            groceries.add(idx,grocery);
        }
        setSelectedItem(grocery);
        setChanged(true);
    }

    public void clear() {
        groceries.clear();
        setChanged(true);
    }

    public ShelfItem getSelectedItem() {
        for(ShelfItem shelfItem : groceries) {
            if(shelfItem.isSelected()) { return shelfItem; }
        }
        return null;
    }

    public void setSelectedItem(ShelfItem item) {
        for(ShelfItem shelfItem : groceries) {
            if(shelfItem.equals(item)) {
                shelfItem.setSelected(true);
            } else {
                shelfItem.setSelected(false);
            }
        }
    }

    public String asText() {
        StringBuilder sb = new StringBuilder();
        for(ShelfItem shelfItem : groceries) {
            sb.append(shelfItem.getName());
            sb.append(": ");
            sb.append(shelfItem.getDesiredAmount());
            if(!shelfItem.getStore().equals(Store.getDefault())) {
                sb.append(" (" + shelfItem.getStore() + ")");
            }
            sb.append("\r\n");
        }
        return sb.toString();
    }

    public int getSelectedItemPosition() {
        ShelfItem item = getSelectedItem();
        if(item != null) {
            return groceries.indexOf(item);
        }
        return groceries.size() - 1;
    }

}
