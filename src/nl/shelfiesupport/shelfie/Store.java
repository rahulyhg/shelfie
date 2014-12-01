package nl.shelfiesupport.shelfie;


import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

public class Store {
    private static final Store defaultInstance = new Store();
    private String name = "_default_store_";

    private Store() {
        name = "_default_store_";
    }

    public Store(String name) {
        this.name = name;
    }

    public Store(JSONObject jsonObject) throws JSONException {
        this.name = jsonObject.getString("name");
    }

    public static Store getDefault() {
        return defaultInstance;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Store))
            return false;

        Store other = (Store) o;
        return other.getName().equalsIgnoreCase(name);
    }

    @Override
    public String toString() {
        Log.i(Tag.SHELFIE, "to string called");
        return getName();
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject me = new JSONObject();
        me.put("name", name);
        return me;
    }
}
