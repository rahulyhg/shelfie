package nl.shelfiesupport.shelfie;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

public class ShelfItem {
    private String name;
    private Store store = Store.getDefault();
    private int desiredAmount;
    private boolean selected = false;

    public ShelfItem(String name, int desired) {
        this(name, desired, Store.getDefault());
    }

    public ShelfItem(String name, int desiredNumber, Store store) {
        this.name = name;
        this.desiredAmount = desiredNumber;
        this.store = store;
    }

    public String getName() {
        return name;
    }

    public int getDesiredAmount() {
        return desiredAmount;
    }

    public void adjustDesiredAmount(int by) {
        desiredAmount += by;
        if(desiredAmount < 1) { desiredAmount = 1; }
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject me = new JSONObject();
        me.put("name", name);
        me.put("desiredAmount", desiredAmount);
        me.put("store", store.toJSON());
        return me;
    }

    public Store getStore() {
        return store;
    }

    @Override
    public String toString() {
        return "ShelfItem{" +
                "desiredAmount=" + desiredAmount +
                ", name='" + name + '\'' +
                ", store=" + store +
                '}';
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setStore(Store store) {
        this.store = store;
    }
}
