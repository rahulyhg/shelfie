package nl.shelfiesupport.shelfie;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;


@SuppressLint("ViewConstructor")
public class ShelfItemRowLayout extends RelativeLayout {

    private final StoreSpinner storePicker;
    private final ImageButton decrementButton;
    private final ImageButton incrementButton;
    private final ImageButton upArrow;
    private final ImageButton downArrow;
    private final TextView nameView;
    private final TextView desiredAmountView;
    private final StoreSpinnerAdapter storePickerAdapter;
    private final Context context;


    public TextView getDesiredAmountView() {
        return desiredAmountView;
    }

    public ImageButton getDecrementButton() {

        return decrementButton;
    }

    public ImageButton getIncrementButton() {
        return incrementButton;
    }

    public ImageButton getUpArrow() {
        return upArrow;
    }

    public ImageButton getDownArrow() {
        return downArrow;
    }

    public ShelfItemRowLayout(final Context context, final EditShelfFragment editShelfFragment) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.shelf_row, this);
        this.context = context;

        nameView = (TextView) findViewById(R.id.itemNameInShelfRow);
        desiredAmountView = (TextView) findViewById(R.id.itemDesiredAmt);
        decrementButton = (ImageButton) findViewById(R.id.minAmt);
        incrementButton = (ImageButton) findViewById(R.id.plusAmt);
        upArrow = (ImageButton) findViewById(R.id.upButton);
        downArrow = (ImageButton) findViewById(R.id.downButton);

        storePicker = (StoreSpinner) findViewById(R.id.store_picker);
        storePicker.setEditShelfFragment(editShelfFragment);
        storePicker.setOnItemSelectedListener(storePicker);
        storePickerAdapter = new StoreSpinnerAdapter(context,
                Inventory.getStores(context), editShelfFragment);
        storePicker.setAdapter(storePickerAdapter);
    }

    public void setName(String name) {
        this.nameView.setText(name);
    }

    public void setStore(Store store) {
        storePicker.setSelection(Inventory.getStores(context).indexOf(store));
        storePickerAdapter.notifyDataSetChanged();
    }

    public void setDesiredAmount(String desiredAmount) {
        this.desiredAmountView.setText(desiredAmount);
    }

}
