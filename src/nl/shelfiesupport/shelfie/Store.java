package nl.shelfiesupport.shelfie;


import android.util.Log;

public class Store {
    private static final Store defaultInstance = new Store();
    private String name = "_default_store_";

    private Store() {

    }

    public Store(String name) {
        this.name = name;
    }

    public static Store getDefault() {
        return defaultInstance;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        Log.i(Tag.SHELFIE, "to string called");
        return getName();
    }
}
