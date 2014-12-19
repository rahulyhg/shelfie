package nl.shelfiesupport.shelfie;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

@SuppressWarnings("ALL")
public class StoreSpinner extends Spinner implements AdapterView.OnItemSelectedListener {
    private EditShelfFragment editShelfFragment;
    private boolean readyForSelect = false;

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
            editShelfFragment.showNewStorePrompt();
            return false;
        }
        readyForSelect = true;
        return super.performClick();
    }

    public void setEditShelfFragment(EditShelfFragment editShelfFragment) {
        this.editShelfFragment = editShelfFragment;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(readyForSelect) {
            Store selectedStore = Inventory.getStores(getContext()).get(position);
            ShelfItem currentShelfItem = Inventory.getShelf(getContext()).getSelectedItem();
            if (selectedStore != null && currentShelfItem != null) {
                Inventory.getShelf(getContext()).setStore(currentShelfItem, selectedStore);
            }
            readyForSelect = false;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
