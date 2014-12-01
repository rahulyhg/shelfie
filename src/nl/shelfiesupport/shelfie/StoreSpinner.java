package nl.shelfiesupport.shelfie;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

public class StoreSpinner extends Spinner implements AdapterView.OnItemSelectedListener {

    private ShelfItem shelfItem;
    private EditShelfActivity editShelfActivity;

    public StoreSpinner(Context context) {
        super(context);
    }
    public StoreSpinner(Context context, int mode) {
        super(context, mode);
    }
    public StoreSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public StoreSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public StoreSpinner(Context context, AttributeSet attrs, int defStyle, int mode) {
        super(context, attrs, defStyle, mode);
    }

    public void showNewStorePrompt() {
        final EditText input = new EditText(getContext());
        input.setHint(getContext().getString(R.string.new_store_hint));
        new AlertDialog.Builder(getContext())
                .setTitle(getContext().getString(R.string.add_store_title))
                .setView(input)
                .setPositiveButton(getContext().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (input.getText().toString().trim().length() == 0) {
                            showNewStorePrompt();
                        } else {
                            Store newStore = new Store(input.getText().toString().trim());
                            Inventory.addStore(getContext(), newStore);
                            shelfItem.setStore(newStore);
                            Shelf.getInstance(getContext()).setSelectedItem(shelfItem);
                            editShelfActivity.refresh();
                        }
                    }
                })
                .setNegativeButton(getContext().getString(R.string.cancel), null)
                .show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(position < Inventory.getStores(getContext()).size()) {
            shelfItem.setStore(Inventory.getStores(getContext()).get(position));
            setSelection(position);
            ((StoreSpinnerAdapter)getAdapter()).notifyDataSetChanged();
        } else {
            showNewStorePrompt();
        }
    }

    @Override
    public boolean performClick() {
        editShelfActivity.shelf.setSelectedItem(shelfItem);
        editShelfActivity.adapter.notifyDataSetChanged();
        return super.performClick();
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void setShelfItem(ShelfItem shelfItem) {
        this.shelfItem = shelfItem;
    }

    public void setEditShelfActivity(EditShelfActivity editShelfActivity) {
        this.editShelfActivity = editShelfActivity;
    }
}
