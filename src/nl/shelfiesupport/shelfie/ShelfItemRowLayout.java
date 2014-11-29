package nl.shelfiesupport.shelfie;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;


public class ShelfItemRowLayout extends RelativeLayout implements DialogInterface.OnClickListener {

    private final Spinner storePicker;
    private final ImageButton decrementButton;
    private final ImageButton incrementButton;
    private final ImageButton upArrow;
    private final ImageButton downArrow;
    private final TextView nameView;
    private final TextView desiredAmountView;
    private final ArrayAdapter<String> storePickerAdapter;
    private final Context context;
    private final ShelfItem shelfItem;
    private final EditShelfActivity editShelfActivity;


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

    public ShelfItemRowLayout(final Context context, final ShelfItem shelfItem, EditShelfActivity editShelfActivity) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.shelf_row, this);
        this.context = context;
        this.shelfItem = shelfItem;
        this.editShelfActivity = editShelfActivity;
        nameView = (TextView) findViewById(R.id.itemName);
        desiredAmountView = (TextView) findViewById(R.id.itemDesiredAmt);
        decrementButton = (ImageButton) findViewById(R.id.minAmt);
        incrementButton = (ImageButton) findViewById(R.id.plusAmt);
        upArrow = (ImageButton) findViewById(R.id.upButton);
        downArrow = (ImageButton) findViewById(R.id.downButton);
        storePicker = (Spinner) findViewById(R.id.store_picker);
        storePickerAdapter = new ArrayAdapter<String>(context, R.layout.store_picker_row, Inventory.getStoreNames(context));
        storePicker.setAdapter(storePickerAdapter);

        storePicker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position < Inventory.getStores(context).size()) {
                    shelfItem.setStore(Inventory.getStores(context).get(position));
                    storePicker.setSelection(position);
                    storePickerAdapter.notifyDataSetChanged();
                } else {
                    showNewStorePrompt();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        Store store = shelfItem.getStore();
        storePicker.setSelection(Inventory.getStores(context).indexOf(store));
        storePickerAdapter.notifyDataSetChanged();
    }

    public void showNewStorePrompt() {
        final ShelfItemRowLayout self = this;
        final EditText input = new EditText(context);
        input.setHint(context.getString(R.string.new_store_hint));
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.add_store_title))
                .setView(input)
                .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (input.getText().toString().trim().length() == 0) {
                            showNewStorePrompt();
                        } else {
                            Store newStore = new Store(input.getText().toString().trim());
                            Inventory.addStore(context, newStore);
                            shelfItem.setStore(newStore);
                            Shelf.getInstance(context).setSelectedItem(shelfItem);
                            editShelfActivity.refresh();
                        }
                    }
                })
                .setNegativeButton(context.getString(R.string.cancel), self)
                .show();
    }


    public void setName(String name) {
        this.nameView.setText(name);
    }

    public void setStore(Store store) {
        storePicker.setSelection(Inventory.getStores(context).indexOf(store));

    }

    public void setDesiredAmount(String desiredAmount) {
        this.desiredAmountView.setText(desiredAmount);
    }


}
