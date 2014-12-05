package nl.shelfiesupport.shelfie;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
        baseView.findViewById(R.id.removeItem).setVisibility(View.GONE);
        return baseView;
    }

    @Override
    public View getDropDownView(final int position, View convertView, final ViewGroup parent) {
        final View view = getBaseView(position, convertView, parent);
        view.findViewById(R.id.removeItem).setVisibility(View.VISIBLE);
        if(objects.get(position).equals(Store.getDefault())) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editShelfActivity.showNewStorePrompt();
                }
            });
            view.findViewById(R.id.removeItem).setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.removeItem).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new AlertDialog.Builder(context)
                            .setTitle(Inventory.getStores(context).get(position).getName())
                            .setMessage(context.getString(R.string.delete_store_confirm))
                            .setCancelable(false)
                            .setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Inventory.removeStore(context, position);
                                    editShelfActivity.refresh();
                                }
                            })
                            .setNegativeButton(context.getString(R.string.no), null)
                            .show();
                }
            });
        }

        return view;
    }
}
