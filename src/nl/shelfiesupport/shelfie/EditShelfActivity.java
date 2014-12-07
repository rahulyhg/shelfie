package nl.shelfiesupport.shelfie;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


public class EditShelfActivity extends BaseActivity {

    protected ShelfListAdapter adapter;


    public void addItem(@SuppressWarnings("UnusedParameters") View button) {
        final ListView shelfLayout = (ListView) findViewById(R.id.edit_shelf_list);
        final EditText newItem = (EditText) findViewById(R.id.addInput);
        String value = newItem.getText().toString();
        if(value.trim().length() > 1) {
            shelf.addItem(new ShelfItem(value, 1));
            adapter.notifyDataSetChanged();
            shelfLayout.setSelection(shelf.getSelectedItemPosition());
            newItem.setText("");
        }
    }

    public void refresh() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        findViewById(R.id.make_list).setBackgroundColor(getResources().getColor(R.color.shelfie_blue));
        findViewById(R.id.edit_shelf).setBackgroundColor(getResources().getColor(R.color.shelfie_darker_blue));

        initShelfPicker();


        final ListView shelfLayout = (ListView) findViewById(R.id.edit_shelf_list);
        adapter = new ShelfListAdapter(this, shelf.getItems(), this);
        shelfLayout.setAdapter(adapter);
        EditText newItem = (EditText) findViewById(R.id.addInput);

        newItem.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String value = v.getText().toString();
                if((actionId == EditorInfo.IME_ACTION_DONE || actionId == 0) && value.trim().length() > 1) {
                    shelf.addItem(new ShelfItem(value, 1));
                    v.setText("");
                    adapter.notifyDataSetChanged();
                    shelfLayout.setSelection(shelf.getSelectedItemPosition());
                    return true;
                }
                return false;
            }
        });

        shelfLayout.setSelection(shelf.getSelectedItemPosition());
        if(adapter != null) {
            adapter.notifyDataSetChanged();
        }

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_shelf);
        ActionBar actionBar = getActionBar();
        if(actionBar != null) { actionBar.setTitle(getString(R.string.edit_shelf_title)); }

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);

    }

    public void showNewStorePrompt() {
        final Context self = this;
        final EditText input = new EditText(this);
        input.setHint(this.getString(R.string.new_store_hint));
        new AlertDialog.Builder(this)
                .setTitle(this.getString(R.string.add_store_title))
                .setView(input)
                .setPositiveButton(this.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (input.getText().toString().trim().length() == 0) {
                            showNewStorePrompt();
                        } else {
                            Store newStore = new Store(input.getText().toString().trim());
                            Inventory.addStore(self, newStore);
                            Shelf theShelf = Inventory.getShelf(self);
                            ShelfItem selectedShelfItem = theShelf.getSelectedItem();
                            theShelf.setStore(selectedShelfItem, newStore);
                            refresh();
                        }
                    }
                })
                .setNegativeButton(this.getString(R.string.cancel), null)
                .show();
    }

}