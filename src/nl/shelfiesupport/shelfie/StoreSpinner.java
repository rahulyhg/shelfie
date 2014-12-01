package nl.shelfiesupport.shelfie;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.util.List;

public class StoreSpinner extends Spinner implements AdapterView.OnItemSelectedListener {
    private EditShelfActivity editShelfActivity;
    private boolean isInit = false;

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






    @Override
    public boolean performClick() {
        View currentShelfRow = this;
        while(!(currentShelfRow instanceof ShelfItemRowLayout)) {
            currentShelfRow = (View) currentShelfRow.getParent();
        }
        currentShelfRow.performClick();

        if(Inventory.getStores(getContext()).size() == 1) {
            editShelfActivity.showNewStorePrompt();
            return false;
        }
        return super.performClick();
    }

    public void setEditShelfActivity(EditShelfActivity editShelfActivity) {
        this.editShelfActivity = editShelfActivity;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(isInit) {
            Store selectedStore = Inventory.getStores(getContext()).get(position);
            ShelfItem currentShelfItem = Inventory.getShelf(getContext()).getSelectedItem();
            if (selectedStore != null && currentShelfItem != null) {
                Inventory.getShelf(getContext()).setStore(currentShelfItem, selectedStore);
            }
        } else {
            isInit = true;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
