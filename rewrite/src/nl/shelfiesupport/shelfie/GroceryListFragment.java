package nl.shelfiesupport.shelfie;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.List;


public class GroceryListFragment extends Fragment {
    private GroceryListAdapter groceryListAdapter;
    private ListView groceryListLayout;
    private FragmentActivity activityContext;
    private GroceryList groceryList;
    private Shelf shelf;


    private class GroceryListAdapter extends ArrayAdapter<ShelfItem> {
        private final Context context;
        private final List<ShelfItem> objects;

        private class ClickListener implements View.OnClickListener, View.OnLongClickListener {
            private final GroceryListAdapter adapter;
            private final ShelfItem item;
            private final int position;

            public ClickListener(ShelfItem item, int position, GroceryListAdapter adapter) {
                this.item = item;
                this.position = position;
                this.adapter = adapter;
            }

            @Override
            public void onClick(View v) {
                groceryList.setSelectedItem(item);
                adapter.notifyDataSetChanged();
            }

            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle(item.getName())
                        .setMessage(context.getString(R.string.delete_grocery_confirm))
                        .setCancelable(false)
                        .setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                groceryList.removeGrocery(position);
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(context.getString(R.string.no), null)
                        .show();
                return false;
            }
        }

        public GroceryListAdapter(Context context, List<ShelfItem> objects) {
            super(context, R.layout.shelf_row, objects);
            this.context = context;
            this.objects = objects;
        }


        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout layout;

            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                layout = (LinearLayout) inflater.inflate(R.layout.grocery, parent,false);
            } else {
                layout = (LinearLayout) convertView;
            }

            TextView nameView = (TextView) layout.findViewById(R.id.groceryName);
            TextView desiredAmount = (TextView) layout.findViewById(R.id.groceryAmount);
            String nameText = objects.get(position).getName();
            if(!objects.get(position).getStore().equals(Store.getDefault())) {
                nameText += " (" + objects.get(position).getStore() + ")";
            }
            nameView.setText(nameText);
            desiredAmount.setText("" + objects.get(position).getDesiredAmount());

            ClickListener clickListener = new ClickListener(objects.get(position), position, this);
            layout.setOnLongClickListener(clickListener);
            layout.setOnClickListener(clickListener);

            if(objects.get(position).isSelected()) {
                layout.setBackgroundColor(context.getResources().getColor(R.color.shelfie_dark_blue));
            } else {
                layout.setBackgroundColor(context.getResources().getColor(android.R.color.background_dark));
            }

            return layout;
        }
    }


    private class GroceryClickListener implements View.OnClickListener {

        private final ClickOperation operation;
        private final TextView amount;
        private final TextView itemName ;
        private final TextView storeName;

        public GroceryClickListener(ClickOperation operation, TextView amount, TextView itemName, TextView storeName) {
            this.operation = operation;
            this.amount = amount;
            this.itemName = itemName;
            this.storeName = storeName;
        }

        @Override
        public void onClick(View v) {
            switch (operation) {
                case PREV:
                    shelf.prevItem();
                    itemName.setText(shelf.getCurrentItem().getName());
                    if(!shelf.getCurrentItem().getStore().equals(Store.getDefault())) {
                        storeName.setText(shelf.getCurrentItem().getStore().getName());
                    } else {
                        storeName.setText("");
                    }
                    amount.setText("" + shelf.getCurrentItem().getDesiredAmount());
                    break;

                case INCREASE_AMOUNT:
                    int newAmount = Integer.parseInt(amount.getText().toString()) + 1;
                    amount.setText("" + newAmount);
                    break;

                case DECREASE_AMOUNT:
                    int newAmount1 = Integer.parseInt(amount.getText().toString()) - 1;
                    if(newAmount1 < 0) { newAmount1 = 0; }
                    amount.setText("" + newAmount1);
                    break;

                case ADD:
                    groceryList.addGrocery(shelf.getCurrentItem(), Integer.parseInt(amount.getText().toString()));
                    groceryListAdapter.notifyDataSetChanged();
                    groceryListLayout.setSelection(groceryList.getSelectedItemPosition());

                case NEXT:
                    shelf.nextItem();
                    itemName.setText(shelf.getCurrentItem().getName());
                    if(!shelf.getCurrentItem().getStore().equals(Store.getDefault())) {
                        storeName.setText(shelf.getCurrentItem().getStore().getName());
                    } else {
                        storeName.setText("");
                    }

                    amount.setText("" + shelf.getCurrentItem().getDesiredAmount());
                default:
            }
        }

    }

    public void reinit() {
        if(activityContext == null) { return; }
        groceryListLayout = (ListView) activityContext.findViewById(R.id.grocery_list);
        if(groceryListLayout == null) { return; }

        groceryList = GroceryList.getInstance(activityContext);
        shelf = Shelf.getInstance(activityContext);
        groceryListAdapter = new GroceryListAdapter(activityContext, groceryList.getGroceries());
        groceryListLayout.setAdapter(groceryListAdapter);
        if(groceryListAdapter != null) {
            groceryListAdapter.notifyDataSetChanged();
        }
        if(shelf.getItems().size() == 0) {
            Toast.makeText(activityContext, R.string.shelf_is_empty, Toast.LENGTH_LONG).show();
            disableShelfButtons();
        } else {
            enableShelfButtons();
            init();
        }
    }

    private void init() {
        final ShelfItem currentItem = shelf.getCurrentItem();
        final TextView itemName = (TextView) activityContext.findViewById(R.id.itemName);
        final TextView storeName = (TextView) activityContext.findViewById(R.id.storeName);
        final TextView amount = (TextView) activityContext.findViewById(R.id.amount);
        Button yesButton = (Button) activityContext.findViewById(R.id.yesButt);
        Button noButton = (Button) activityContext.findViewById(R.id.noButt);
        ImageButton plusButton = (ImageButton) activityContext.findViewById(R.id.plusButton);
        ImageButton minusButton = (ImageButton) activityContext.findViewById(R.id.minusButton);
        ImageButton nextButton = (ImageButton) activityContext.findViewById(R.id.nextButt);
        ImageButton prevButton = (ImageButton) activityContext.findViewById(R.id.prevButt);

        itemName.setText(currentItem.getName());
        if(!currentItem.getStore().equals(Store.getDefault())) { storeName.setText(currentItem.getStore().getName()); }
        else { storeName.setText(""); }
        amount.setText("" + currentItem.getDesiredAmount());

        yesButton.setOnClickListener(new GroceryClickListener(ClickOperation.ADD, amount, itemName, storeName));
        noButton.setOnClickListener(new GroceryClickListener(ClickOperation.NEXT, amount, itemName, storeName));
        nextButton.setOnClickListener(new GroceryClickListener(ClickOperation.NEXT, amount, itemName, storeName));
        prevButton.setOnClickListener(new GroceryClickListener(ClickOperation.PREV, amount, itemName, storeName));
        plusButton.setOnClickListener(new GroceryClickListener(ClickOperation.INCREASE_AMOUNT, amount, itemName, storeName));
        minusButton.setOnClickListener(new GroceryClickListener(ClickOperation.DECREASE_AMOUNT, amount, itemName, storeName));

    }
    @Override
    public void onResume () {
        super.onResume();
        activityContext = getActivity();

        reinit();
    }

    private void enableShelfButtons() {
        Button yesButton = (Button) activityContext.findViewById(R.id.yesButt);
        Button noButton = (Button) activityContext.findViewById(R.id.noButt);
        ImageButton[] imgButtons = {
                (ImageButton) activityContext.findViewById(R.id.plusButton),
                (ImageButton) activityContext.findViewById(R.id.minusButton),
                (ImageButton) activityContext.findViewById(R.id.nextButt),
                (ImageButton) activityContext.findViewById(R.id.prevButt)
        };

        yesButton.setEnabled(true);
        noButton.setEnabled(true);
        for(ImageButton button : imgButtons) {
            button.setEnabled(true);
            button.setAlpha(1.0f);
        }

    }

    private void disableShelfButtons() {
        TextView itemName = (TextView) activityContext.findViewById(R.id.itemName);
        TextView storeName = (TextView) activityContext.findViewById(R.id.storeName);
        TextView amount = (TextView) activityContext.findViewById(R.id.amount);
        Button yesButton = (Button) activityContext.findViewById(R.id.yesButt);
        Button noButton = (Button) activityContext.findViewById(R.id.noButt);
        ImageButton[] imgButtons = {
                (ImageButton) activityContext.findViewById(R.id.plusButton),
                (ImageButton) activityContext.findViewById(R.id.minusButton),
                (ImageButton) activityContext.findViewById(R.id.nextButt),
                (ImageButton) activityContext.findViewById(R.id.prevButt)
        };

        yesButton.setEnabled(false);
        noButton.setEnabled(false);
        for(ImageButton button : imgButtons) {
            button.setEnabled(false);
            button.setAlpha(0.7f);
        }
        itemName.setText("");
        storeName.setText("");
        amount.setText("");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.grocery_list, container, false);
        return rootView;
    }


}
