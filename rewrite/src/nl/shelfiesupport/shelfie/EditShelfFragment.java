package nl.shelfiesupport.shelfie;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class EditShelfFragment extends Fragment {

    private FragmentActivity activityContext;
    public Shelf shelf;
    private ShelfListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.edit_shelf, container, false);
        return rootView;
    }

    public void reinit() {
        if(activityContext == null) { return; }

        shelf = Shelf.getInstance(activityContext);
        View rootView = getView();
        if(rootView == null) { return; }
        final ListView shelfLayout = (ListView) rootView.findViewById(R.id.edit_shelf_list);


        adapter = new ShelfListAdapter(activityContext, shelf.getItems(), this);
        shelfLayout.setAdapter(adapter);
        getView().findViewById(R.id.addItem).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });
        EditText newItem = (EditText) getView().findViewById(R.id.addInput);

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
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activityContext = getActivity();
        reinit();
    }

    public void addItem() {
        final ListView shelfLayout = (ListView) getView().findViewById(R.id.edit_shelf_list);
        final EditText newItem = (EditText) getView().findViewById(R.id.addInput);
        String value = newItem.getText().toString();
        if(value.trim().length() > 1) {
            shelf.addItem(new ShelfItem(value, 1));
            adapter.notifyDataSetChanged();
            shelfLayout.setSelection(shelf.getSelectedItemPosition());
            newItem.setText("");
        }
        ((ShelfieFragmentListener) activityContext).reinitGroceryListFragment();
    }

    public void showNewStorePrompt() {
        final EditText input = new EditText(activityContext);
        input.setHint(this.getString(R.string.new_store_hint));
        new AlertDialog.Builder(activityContext)
                .setTitle(this.getString(R.string.add_store_title))
                .setView(input)
                .setPositiveButton(this.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (input.getText().toString().trim().length() == 0) {
                            showNewStorePrompt();
                        } else {
                            Store newStore = new Store(input.getText().toString().trim());
                            Inventory.addStore(activityContext, newStore);
                            Shelf theShelf = Inventory.getShelf(activityContext);
                            ShelfItem selectedShelfItem = theShelf.getSelectedItem();
                            theShelf.setStore(selectedShelfItem, newStore);
                            ((ShelfieFragmentListener) activityContext).reinitGroceryListFragment();
                        }
                    }
                })
                .setNegativeButton(this.getString(R.string.cancel), null)
                .show();
    }
}
