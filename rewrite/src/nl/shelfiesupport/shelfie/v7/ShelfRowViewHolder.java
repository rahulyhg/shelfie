package nl.shelfiesupport.shelfie.v7;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import nl.shelfiesupport.shelfie.*;

import java.util.List;

public class ShelfRowViewHolder extends RecyclerView.ViewHolder {

    private final List<ShelfItem> shelfItems;
    private StoreSpinner storePicker;
    private final ImageButton decrementButton;
    private final ImageButton incrementButton;
    private final ImageButton upArrow;
    private final ImageButton downArrow;
    private final TextView nameView;
    private final TextView desiredAmountView;
    private StoreSpinnerAdapter storePickerAdapter;
    private final Context context;
    private final LinearLayout rowLayout;
    private final EditShelfFragment editShelfFragment;
    private final ShelfRecyclerViewAdapter adapter;

    public TextView getDesiredAmountView() {
        return desiredAmountView;
    }
    public ImageButton getDecrementButton() { return decrementButton; }
    public ImageButton getIncrementButton() {
        return incrementButton;
    }
    public ImageButton getUpArrow() {
        return upArrow;
    }
    public ImageButton getDownArrow() {
        return downArrow;
    }


    public ShelfRowViewHolder(LinearLayout rowLayout,
                              final Context context, final EditShelfFragment editShelfFragment,
                              final ShelfRecyclerViewAdapter shelfRecyclerViewAdapter,
                              final List<ShelfItem> shelfItems) {
        super(rowLayout);
        this.shelfItems = shelfItems;
        this.context = context;
        this.rowLayout = rowLayout;
        this.editShelfFragment = editShelfFragment;
        this.adapter =  shelfRecyclerViewAdapter;
        nameView = (TextView) rowLayout.findViewById(R.id.itemNameInShelfRow);
        desiredAmountView = (TextView) rowLayout.findViewById(R.id.itemDesiredAmt);
        decrementButton = (ImageButton) rowLayout.findViewById(R.id.minAmt);
        incrementButton = (ImageButton) rowLayout.findViewById(R.id.plusAmt);
        upArrow = (ImageButton) rowLayout.findViewById(R.id.upButton);
        downArrow = (ImageButton) rowLayout.findViewById(R.id.downButton);

        storePicker = (StoreSpinner) rowLayout.findViewById(R.id.store_picker);
        storePicker.setEditShelfFragment(editShelfFragment);
        storePicker.setOnItemSelectedListener(storePicker);
        storePicker.setShelfRecyclerViewAdapter(adapter);
        storePickerAdapter = new StoreSpinnerAdapter(context,
                Inventory.getStores(context), editShelfFragment);
        storePicker.setAdapter(storePickerAdapter);
        storePickerAdapter.notifyDataSetChanged();
    }

    public void setShelfItem(ShelfItem shelfItem, int position) {
        setName(shelfItem.getName());
        setStore(shelfItem.getStore());
        setDesiredAmount("" + shelfItem.getDesiredAmount());

        getDecrementButton().setOnClickListener(new ClickListener(shelfItem, desiredAmountView, position, this.adapter, ClickOperation.DECREASE_AMOUNT));
        getIncrementButton().setOnClickListener(new ClickListener(shelfItem, desiredAmountView, position, this.adapter, ClickOperation.INCREASE_AMOUNT));
        getUpArrow().setOnClickListener(new ClickListener(shelfItem, desiredAmountView, position, this.adapter, ClickOperation.SWAP_UP));
        getDownArrow().setOnClickListener(new ClickListener(shelfItem, desiredAmountView, position, this.adapter, ClickOperation.SWAP_DOWN));
        rowLayout.setOnLongClickListener(new ClickListener(shelfItem, desiredAmountView, position, this.adapter, ClickOperation.DELETE));
        rowLayout.setOnClickListener(new SelectClickListener(position));

        if(shelfItem.isSelected()) {
            rowLayout.setBackgroundColor(context.getResources().getColor(R.color.shelfie_dark_blue));
        } else {
            rowLayout.setBackgroundColor(context.getResources().getColor(android.R.color.background_dark));

        }
    }

    private void setName(String name) {
        this.nameView.setText(name);
    }

    private void setStore(Store store) {
        storePicker.setSelection(Inventory.getStores(context).indexOf(store));
        storePickerAdapter.notifyDataSetChanged();
    }

    private void setDesiredAmount(String desiredAmount) {
        this.desiredAmountView.setText(desiredAmount);
    }

    private class SelectClickListener implements View.OnClickListener {
        private final int position;

        public SelectClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            editShelfFragment.shelf.setSelectedItem(shelfItems.get(position));
            RecyclerView container = (RecyclerView)((FragmentActivity) context).findViewById(R.id.edit_shelf_list);
            int darkColor = context.getResources().getColor(android.R.color.background_dark);
            for(int i = 0; i < container.getChildCount(); i++) {
                container.getChildAt(i).setBackgroundColor(darkColor);
            }
            rowLayout.setBackgroundColor(context.getResources().getColor(R.color.shelfie_dark_blue));
        }
    }

    private class ClickListener implements View.OnClickListener, View.OnLongClickListener {
        private final ShelfRecyclerViewAdapter adapter;
        private final ClickOperation clickOperation;
        private final ShelfItem item;
        private final int position;
        private final TextView textView;

        public ClickListener(ShelfItem item, TextView view, int position, ShelfRecyclerViewAdapter adapter, ClickOperation operation) {
            this.item = item;
            this.position = position;
            this.adapter = adapter;
            this.clickOperation = operation;
            this.textView = view;
        }

        public void onClick(View v) {
            switch (clickOperation) {
                case INCREASE_AMOUNT:
                    editShelfFragment.shelf.adjustDesiredAmount(item, 1);
                    textView.setText("" + item.getDesiredAmount());
                    break;
                case DECREASE_AMOUNT:
                    editShelfFragment.shelf.adjustDesiredAmount(item, -1);
                    textView.setText("" + item.getDesiredAmount());
                    break;
                case SWAP_UP:
                    editShelfFragment.shelf.swapItems(position, position - 1);
                    break;
                case SWAP_DOWN:
                    editShelfFragment.shelf.swapItems(position, position + 1);
                    break;
                default: break;
            }
            ((ShelfieFragmentListener) context).reinitGroceryListFragment();
            adapter.notifyDataSetChanged();
        }

        @Override
        public boolean onLongClick(View v) {
            new AlertDialog.Builder(context)
                    .setTitle(item.getName())
                    .setMessage(context.getString(R.string.delete_item_confirm))
                    .setCancelable(false)
                    .setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            editShelfFragment.shelf.removeItem(position);
                            ((ShelfieFragmentListener) context).reinitGroceryListFragment();
                            adapter.notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton(context.getString(R.string.no), null)
                    .show();
            return false;
        }
    }
}
