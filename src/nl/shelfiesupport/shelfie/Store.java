package nl.shelfiesupport.shelfie;


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
}
