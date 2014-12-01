package nl.shelfiesupport.shelfie;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

public class StoreSpinnerAdapter extends ArrayAdapter<Store> {

    private final Context context;
    private final List<Store> objects;
    private final EditShelfActivity editShelfActivity;
    private final ShelfItem shelfItem;
    private int layoutResource;

    public StoreSpinnerAdapter(Context context, int resource, List<Store> objects,
                               EditShelfActivity editShelfActivity, ShelfItem shelfItem) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;
        this.layoutResource = resource;
        this.editShelfActivity = editShelfActivity;
        this.shelfItem = shelfItem;
    }

    public View getBaseView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        TextView storePickerRow;
        if(convertView == null) {
            storePickerRow = (TextView) inflater.inflate(layoutResource, null);
        } else {
            storePickerRow = (TextView) convertView;
        }
        String name = objects.get(position).getName();
        if(name.equalsIgnoreCase("_default_store_")) {
            name = context.getString(R.string.default_store);
        }
        storePickerRow.setText(name);
        return storePickerRow;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View baseView = getBaseView(position, convertView, parent);
        return baseView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View baseView = getBaseView(position, convertView, parent);
        return baseView;
    }
}
