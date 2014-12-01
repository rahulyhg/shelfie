package nl.shelfiesupport.shelfie;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.List;

public class StoreSpinnerAdapter extends ArrayAdapter<Store> {

    private final Context context;
    private final List<Store> objects;
    private int layoutResource;
    private final EditShelfActivity editShelfActivity;


    public StoreSpinnerAdapter(Context context, int resource, List<Store> objects, final EditShelfActivity editShelfActivity) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;
        this.layoutResource = resource;
        this.editShelfActivity = editShelfActivity;
    }

    public View getBaseView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        LinearLayout storePickerRow;
        if(convertView == null) {
            storePickerRow = (LinearLayout) inflater.inflate(layoutResource, null);
        } else {
            storePickerRow = (LinearLayout) convertView;
        }
        String name = objects.get(position).getName();
        if(objects.get(position).equals(Store.getDefault())) {
            name = context.getString(R.string.default_store);
        }
        ((TextView)storePickerRow.findViewById(R.id.textView)).setText(name);
        return storePickerRow;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View baseView = getBaseView(position, convertView, parent);
        baseView.findViewById(R.id.removeItem).setVisibility(View.INVISIBLE);
        return baseView;
    }

    @Override
    public View getDropDownView(final int position, View convertView, ViewGroup parent) {
        View baseView = getBaseView(position, convertView, parent);
        baseView.findViewById(R.id.removeItem).setVisibility(View.VISIBLE);
        if(objects.get(position).equals(Store.getDefault())) {
            baseView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editShelfActivity.showNewStorePrompt();
                }
            });
            baseView.findViewById(R.id.removeItem).setVisibility(View.INVISIBLE);
        } else {
            baseView.findViewById(R.id.removeItem).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Inventory.removeStore(context, position);
                    editShelfActivity.refresh();
                }
            });

        }

        return baseView;
    }
}
