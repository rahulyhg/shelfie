package nl.shelfiesupport.shelfie;

import org.json.JSONException;
import org.json.JSONObject;

public class ShelfItem {
    private String name;
    private int desiredAmount;
    private boolean selected = false;

    public ShelfItem(String name, int desiredNumber) {
        this.name = name;
        this.desiredAmount = desiredNumber;
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
        return me;
    }


    @Override
    public String toString() {
        return "ShelfItem{" +
                "name='" + name + '\'' +
                ", desiredAmount=" + desiredAmount +
                '}';
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }
}
