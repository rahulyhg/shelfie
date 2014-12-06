package nl.shelfiesupport.shelfie;

import android.app.ActionBar;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.List;


public class GroceryListActivity extends BaseActivity {

    private GroceryListAdapter groceryListAdapter;
    private ListView groceryListLayout;


    private class GroceryListAdapter extends ArrayAdapter<ShelfItem> {
        private final Context context;
        private final List<ShelfItem> objects;

        private class ClickListener implements View.OnClickListener, View.OnLongClickListener {
            private GroceryListAdapter adapter;
            private ShelfItem item;
            private int position;

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


    private void init() {
        final ShelfItem currentItem = shelf.getCurrentItem();
        final TextView itemName = (TextView) findViewById(R.id.itemName);
        final TextView storeName = (TextView) findViewById(R.id.storeName);
        final TextView amount = (TextView) findViewById(R.id.amount);
        Button yesButton = (Button) findViewById(R.id.yesButt);
        Button noButton = (Button) findViewById(R.id.noButt);
        ImageButton plusButton = (ImageButton) findViewById(R.id.plusButton);
        ImageButton minusButton = (ImageButton) findViewById(R.id.minusButton);
        ImageButton nextButton = (ImageButton) findViewById(R.id.nextButt);
        ImageButton prevButton = (ImageButton) findViewById(R.id.prevButt);

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
    public void onResume() {
        super.onResume();
        findViewById(R.id.make_list).setBackgroundColor(getResources().getColor(R.color.shelfie_darker_blue));
        findViewById(R.id.edit_shelf).setBackgroundColor(getResources().getColor(R.color.shelfie_blue));
        initShelfPicker();

        groceryListLayout = (ListView) findViewById(R.id.grocery_list);
        groceryListAdapter = new GroceryListAdapter(this, groceryList.getGroceries());
        groceryListLayout.setAdapter(groceryListAdapter);

        if(shelf.getItems().size() == 0) {
            Toast.makeText(this, R.string.shelf_is_empty, Toast.LENGTH_LONG).show();
            disableShelfButtons();
        } else {
            enableShelfButtons();
            init();
        }

        if(groceryListAdapter != null) {
            groceryListAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grocery_list);
        ActionBar actionBar = getActionBar();
        if(actionBar != null) {
            actionBar.setTitle(getString(R.string.make_grocery_list));
        }
    }

    private void enableShelfButtons() {
        Button yesButton = (Button) findViewById(R.id.yesButt);
        Button noButton = (Button) findViewById(R.id.noButt);
        ImageButton[] imgButtons = {
                (ImageButton) findViewById(R.id.plusButton),
                (ImageButton) findViewById(R.id.minusButton),
                (ImageButton) findViewById(R.id.nextButt),
                (ImageButton) findViewById(R.id.prevButt)
        };

        yesButton.setEnabled(true);
        noButton.setEnabled(true);
        for(ImageButton button : imgButtons) {
            button.setEnabled(true);
            button.setAlpha(1.0f);
        }

    }

    private void disableShelfButtons() {
        TextView itemName = (TextView) findViewById(R.id.itemName);
        TextView storeName = (TextView) findViewById(R.id.storeName);
        TextView amount = (TextView) findViewById(R.id.amount);
        Button yesButton = (Button) findViewById(R.id.yesButt);
        Button noButton = (Button) findViewById(R.id.noButt);
        ImageButton[] imgButtons = {
            (ImageButton) findViewById(R.id.plusButton),
            (ImageButton) findViewById(R.id.minusButton),
            (ImageButton) findViewById(R.id.nextButt),
            (ImageButton) findViewById(R.id.prevButt)
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.add_grocery_menu_button:
                showAddGroceryPrompt();
                break;

            case R.id.delete_groceries:
                deleteGroceries();
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAddGroceryPrompt() {
        final EditText input = new EditText(this);
        input.setHint(getString(R.string.grocery_hint));
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.add_grocery))
                .setView(input)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (input.getText().toString().trim().length() == 0) {
                            showAddGroceryPrompt();
                        } else {
                            ShelfItem grocery = new ShelfItem(input.getText().toString(), 1);
                            if (groceryList != null && groceryListAdapter != null) {
                                groceryList.addGrocery(grocery, 1);
                                groceryListAdapter.notifyDataSetChanged();
                            }

                        }
                    }
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }

    private void deleteGroceries() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.delete_groceries))
                .setMessage(getString(R.string.delete_groceries_confirm))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (groceryList != null && groceryListAdapter != null) {
                            groceryList.clear();
                            groceryListAdapter.notifyDataSetChanged();
                        }
                    }
                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }
}