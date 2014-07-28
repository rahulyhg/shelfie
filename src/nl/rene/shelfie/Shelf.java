package nl.rene.shelfie;

import android.content.Context;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Shelf {
    private static final String FILENAME = "theshelf.json";
    private List<ShelfItem> items;

    private boolean changed = false;
    private static Shelf instance = null;
    private int currentItem = 0;
    private String exportId = null;

    private Shelf(JSONObject me) {
        this.items = new ArrayList<ShelfItem>();
        try {
            JSONArray jsonItems = me.getJSONArray("items");
            for (int i = 0; i < jsonItems.length(); i++) {
                JSONObject jsonItem = jsonItems.getJSONObject(i);
                ShelfItem item = new ShelfItem(jsonItem.getString("name"), jsonItem.getInt("desiredAmount"));
                items.add(item);
            }
            if(me.has("_id")) { exportId = me.getString("_id"); }
        } catch (JSONException ignored) {

        }
    }

    private Shelf(String filename, Context context) {
        FileInputStream is;
        this.items = new ArrayList<ShelfItem>();
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
            JSONArray jsonItems = me.getJSONArray("items");
            for(int i = 0; i < jsonItems.length(); i++) {
                JSONObject jsonItem = jsonItems.getJSONObject(i);
                ShelfItem item = new ShelfItem(jsonItem.getString("name"), jsonItem.getInt("desiredAmount"));
                items.add(item);
            }
            if(me.has("_id")) { exportId = me.getString("_id"); }
            is.close();
        } catch(IOException ignored) {
        } catch(JSONException ignored) {
        }
    }

    public static Shelf getInstance(Context context) {
        if(instance == null) { instance = new Shelf(FILENAME, context); }
        return instance;
    }

    public static Shelf fromJSON(JSONObject json) {
        instance = new Shelf(json);
        return instance;
    }

    public List<ShelfItem> getItems() {
        return this.items;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject me = new JSONObject();
        JSONArray jsonItems = new JSONArray();
        me.put("name", "standard_shelf");
        for(ShelfItem item : items) {
            jsonItems.put(item.toJSON());
        }
        me.put("items", jsonItems);
        if(exportId != null) { me.put("_id", exportId); }
        return me;
    }

    @Override
    public String toString() {
        return "Shelf{items=" + items + "}";
    }

    public void save(Context context) {
        if(!changed) { return; }
        FileOutputStream os;
        try {
            os = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            Log.d("SHELFIE", "Saving Shelf: " + toJSON().toString());
            os.write(toJSON().toString().getBytes());
            changed = false;
            os.close();
        } catch(IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addItem(ShelfItem item) {
        items.add(item);
        setChanged(true);
    }

    public void removeItem(int position) {
        items.remove(position);
        setChanged(true);
    }

    public void setChanged(boolean changed) {
        if(changed) { Log.d("SHELFIE", "Change in Shelf registered"); }
        this.changed = changed;
    }

    public void adjustDesiredAmount(ShelfItem item, int relAmt) {
        item.adjustDesiredAmount(relAmt);
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
}
