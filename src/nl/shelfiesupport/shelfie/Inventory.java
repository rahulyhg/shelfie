package nl.shelfiesupport.shelfie;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;
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
    private static int currentShelfIndex = 0;
    private static boolean infoSuppressed = false;
    public static boolean expire = false;

    private List<Shelf> shelves;
    private List<String> votes = new ArrayList<String>();


    private static JSONObject voteFetchData = null;
    private static long nextFetch = -1;

    private Inventory(String filename, Context context) {
        FileInputStream is;
        this.shelves = new ArrayList<Shelf>();
        this.votes = new ArrayList<String>();
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

            if(me.has("votes")) {
                JSONArray jsonVotes = me.getJSONArray("votes");
                for(int i = 0; i < jsonVotes.length(); i++) {
                    votes.add(jsonVotes.getString(i));
                }
            }
            if(me.has("vote_fetch_data")) {
                voteFetchData = me.getJSONObject("vote_fetch_data");
            }
            if(me.has("next_fetch")) {
                nextFetch = me.getLong("next_fetch");
            }
            is.close();
        } catch(IOException ignored) {
        } catch(JSONException ignored) {
        }
        if(this.shelves.size() == 0) {
            this.shelves.add(new Shelf(context.getString(R.string.default_shelf)));
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
        JSONArray jsonVotes = new JSONArray();
        for(String vote : votes) {
            jsonVotes.put(vote);
        }
        me.put("votes", jsonVotes);
        me.put("shelves", jsonShelves);
        if(voteFetchData != null) {
            me.put("vote_fetch_data", voteFetchData);
        }
        me.put("next_fetch", nextFetch);
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
            if(os != null) { try { os.close(); } catch (Exception ignored) { } }
        }
        return saved;
    }

    public static Shelf getShelf(Context context) {

        return getInstance(context).shelves.get(currentShelfIndex);
    }

    public static void createNewShelf(Context context, String name) {
        Inventory inventory = getInstance(context);
        Shelf newShelf = new Shelf(name);
        inventory.shelves.add(newShelf);
        inventory.save(context);
        currentShelfIndex = inventory.shelves.indexOf(newShelf);
        Shelf.setInstanceChanged(context);
    }

    public static void saveImportedShelf(Context context, Shelf newShelf) {
        Inventory inventory = getInstance(context);
        inventory.shelves.add(newShelf);
        inventory.save(context);
    }

    public static List<String> getShelfNames(Context context) {
        Inventory inventory = getInstance(context);
        List<String>names = new ArrayList<String>();
        for(Shelf shelf : inventory.shelves) {
            names.add(shelf.getName());
        }
        return names;
    }

    public static void setSelectedShelfByIndex(Context context, int index) {

        if(getShelfNames(context).size() <= index) {
            currentShelfIndex = 0;
        } else {
            currentShelfIndex = index;
        }
        Shelf.setInstanceChanged(context);
        Log.d("SHELFIE", "Selected shelf: " + getInstance(context).shelves.get(currentShelfIndex));
    }

    public static int getSelectedShelfIndex() {
        return currentShelfIndex;
    }

    public static Shelf findShelfByName(Context context, String name) {
        Inventory inventory = getInstance(context);
        for(Shelf shelf : inventory.shelves) {
            if(name.equals(shelf.getName())) { return shelf; }
        }
        return null;
    }

    public static void deleteCurrentShelf(Context context) {
        Inventory inventory = getInstance(context);
        if(inventory.shelves.size() > 1) {
            inventory.shelves.remove(getShelf(context));
            inventory.save(context);
            currentShelfIndex = 0;
            Shelf.setInstanceChanged(context);
        } else {
            Toast.makeText(context,
                    context.getString(R.string.uneedaleastoneshelf), Toast.LENGTH_LONG).show();
        }
    }

    public static void addVote(Context context, String vote) {
        Inventory inventory = getInstance(context);
        inventory.votes.add(vote);
        inventory.save(context);
    }

    public static void retractVote(Context context, String vote) {
        Inventory inventory = getInstance(context);
        inventory.votes.remove(vote);
        inventory.save(context);
    }

    public static boolean votedFor(Context context, String vote) {
        Inventory inventory = getInstance(context);
        return inventory.votes.indexOf(vote) > -1;
    }
    public static boolean isInfoSuppressed() {
        return infoSuppressed;
    }

    public static void setInfoSuppressed(boolean infoSuppressed) {
        Inventory.infoSuppressed = infoSuppressed;
    }

    public static boolean mayFetchNext() {
        return System.currentTimeMillis() > nextFetch;
    }

    public static void setNextFetch(long nextFetch) {
        Inventory.nextFetch = nextFetch;
    }

    public static JSONObject getVoteFetchData() {
        return voteFetchData;
    }

    public static void setVoteFetchData(JSONObject voteFetchData) {
        Inventory.voteFetchData = voteFetchData;
    }


}
