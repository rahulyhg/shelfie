package nl.shelfiesupport.shelfie;


import android.content.Context;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private static final String FILENAME = "theinventory.json";
    private static Inventory instance = null;

    private List<Shelf> shelves;

    private Inventory(String filename, Context context) {
        FileInputStream is;
        this.shelves = new ArrayList<Shelf>();
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

            JSONArray jsonShelves = me.getJSONArray("shelves");
            for(int i = 0; i < jsonShelves.length(); i++) {
                shelves.add(new Shelf((JSONObject) jsonShelves.get(i)));
            }
            is.close();
        } catch(IOException ignored) {
        } catch(JSONException ignored) {
        }
        if(this.shelves.size() == 0) {
            this.shelves.add(new Shelf());
        }
    }

    public static Inventory getInstance(Context context) {
        if(instance == null) { instance = new Inventory(FILENAME, context); }
        return instance;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject me = new JSONObject();
        JSONArray jsonShelves = new JSONArray();
        for(Shelf shelf : shelves) {
            jsonShelves.put(shelf.toJSON());
        }
        me.put("shelves", jsonShelves);
        return me;
    }

    public boolean save(Context context) {
        FileOutputStream os = null;
        boolean saved = false;
        try {
            os = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            Log.d("SHELFIE", "Saving Inventory: " + toJSON().toString());
            os.write(toJSON().toString().getBytes());
            saved = true;
        } catch(IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if(os != null) { try { os.close(); } catch (Exception ignoed) { } }
        }
        return saved;
    }

    public static Shelf getShelf(Context context) {
        return getInstance(context).shelves.get(0);
    }
}
