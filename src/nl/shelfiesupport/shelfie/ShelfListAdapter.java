package nl.shelfiesupport.shelfie;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

class ShelfListAdapter extends ArrayAdapter<ShelfItem> {
    private final Context context;
    private final List<ShelfItem> objects;
    private final EditShelfActivity editShelfActivity;

    private class SelectClickListener implements View.OnClickListener {
        private ShelfListAdapter adapter;
        private ShelfItem item;

        public SelectClickListener(ShelfItem item, ShelfListAdapter adapter) {
            this.adapter = adapter;
            this.item = item;
        }

        @Override
        public void onClick(View v) {
            editShelfActivity.shelf.setSelectedItem(item);
            adapter.notifyDataSetChanged();
        }
    }

    private class ClickListener implements View.OnClickListener, View.OnLongClickListener {
        private ShelfListAdapter adapter;
        private ClickOperation clickOperation;
        private ShelfItem item;
        private int position;
        private TextView textView;

        public ClickListener(ShelfItem item, TextView view, int position, ShelfListAdapter adapter, ClickOperation operation) {
            this.item = item;
            this.position = position;
            this.adapter = adapter;
            this.clickOperation = operation;
            this.textView = view;
        }

        public void onClick(View v) {
            switch (clickOperation) {
                case INCREASE_AMOUNT:
                    editShelfActivity.shelf.adjustDesiredAmount(item, 1);
                    textView.setText("" + item.getDesiredAmount());
                    break;
                case DECREASE_AMOUNT:
                    editShelfActivity.shelf.adjustDesiredAmount(item, -1);
                    textView.setText("" + item.getDesiredAmount());
                    break;
                case SWAP_UP:
                    editShelfActivity.shelf.swapItems(position, position - 1);
                    break;
                case SWAP_DOWN:
                    editShelfActivity.shelf.swapItems(position, position + 1);
                    break;
                default: break;
            }
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
                            editShelfActivity.shelf.removeItem(position);
                            adapter.notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton(context.getString(R.string.no), null)
                    .show();
            return false;
        }
    }

    public ShelfListAdapter(Context context, List<ShelfItem> objects, EditShelfActivity editShelfActivity) {
        super(context, R.layout.shelf_row, objects);
        this.editShelfActivity = editShelfActivity;
        this.context = context;
        this.objects = objects;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ShelfItemRowLayout shelfItemRowLayout;
        if(convertView == null) {
            shelfItemRowLayout = new ShelfItemRowLayout(context, editShelfActivity);
        } else {
            shelfItemRowLayout = (ShelfItemRowLayout) convertView;
        }

        shelfItemRowLayout.setName(objects.get(position).getName());
        shelfItemRowLayout.setDesiredAmount("" + objects.get(position).getDesiredAmount());
        shelfItemRowLayout.setStore(objects.get(position).getStore());
        TextView desiredAmountView = shelfItemRowLayout.getDesiredAmountView();
        shelfItemRowLayout.getDecrementButton().setOnClickListener(new ClickListener(objects.get(position), desiredAmountView, position, this, ClickOperation.DECREASE_AMOUNT));
        shelfItemRowLayout.getIncrementButton().setOnClickListener(new ClickListener(objects.get(position), desiredAmountView, position, this, ClickOperation.INCREASE_AMOUNT));
        shelfItemRowLayout.getUpArrow().setOnClickListener(new ClickListener(objects.get(position), desiredAmountView, position, this, ClickOperation.SWAP_UP));
        shelfItemRowLayout.getDownArrow().setOnClickListener(new ClickListener(objects.get(position), desiredAmountView, position, this, ClickOperation.SWAP_DOWN));
        shelfItemRowLayout.setOnLongClickListener(new ClickListener(objects.get(position), desiredAmountView, position, this, ClickOperation.DELETE));
        shelfItemRowLayout.setOnClickListener(new SelectClickListener(objects.get(position), this));
        if(objects.get(position).isSelected()) {
            shelfItemRowLayout.setBackgroundColor(context.getResources().getColor(R.color.shelfie_dark_blue));
        } else {
            shelfItemRowLayout.setBackgroundColor(context.getResources().getColor(android.R.color.background_dark));
        }
        return shelfItemRowLayout;
    }
}
