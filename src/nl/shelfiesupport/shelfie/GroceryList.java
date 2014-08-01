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
    private List<ShelfItem> groceries;
    private static final long DAY_DURATION = 86400000;

    private GroceryList(String filename, Context context) {
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
            long updatedAt = me.getLong("updatedAt");
            if(System.currentTimeMillis() < DAY_DURATION + updatedAt) {
                JSONArray jsonGroceries = me.getJSONArray("groceries");
                for(int i = 0; i < jsonGroceries.length(); i++) {
                    JSONObject jsonGrocery = jsonGroceries.getJSONObject(i);
                    ShelfItem grocery = new ShelfItem(jsonGrocery.getString("name"), jsonGrocery.getInt("desiredAmount"));
                    groceries.add(grocery);
                }
            }
            is.close();
        } catch(IOException ignored) {
        } catch(JSONException ignored) {
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
            Log.d("SHELFIE", "Saving GroceryList: " + toJSON().toString());
            os.write(toJSON().toString().getBytes());
            changed = false;
            os.close();
        } catch(IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setChanged(boolean changed) {
        if(changed) { Log.d("SHELFIE", "Change in GroceryList registered"); }
        this.changed = changed;
    }

    public void removeGrocery(int position) {
        if(groceries.get(position) != null) { groceries.remove(position); setChanged(true);}
    }

    public List<ShelfItem> getGroceries() {
        return groceries;
    }

    public void addGrocery(ShelfItem item, int amount) {
        ShelfItem grocery = new ShelfItem(item.getName(), amount);
        ShelfItem selectedItem = getSelectedItem();
        if(selectedItem != null) {
            groceries.add(groceries.indexOf(selectedItem) + 1, grocery);
        } else {
            groceries.add(grocery);
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
}
