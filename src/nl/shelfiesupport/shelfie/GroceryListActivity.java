package nl.shelfiesupport.shelfie;

import android.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

            TextView nameInput = (TextView) layout.findViewById(R.id.groceryName);
            TextView desiredAmount = (TextView) layout.findViewById(R.id.groceryAmount);

            nameInput.setText(objects.get(position).getName());
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

        public GroceryClickListener(ClickOperation operation, TextView amount, TextView itemName) {
            this.operation = operation;
            this.amount = amount;
            this.itemName = itemName;
        }

        @Override
        public void onClick(View v) {
            switch (operation) {
                case PREV:
                    shelf.prevItem();
                    itemName.setText(shelf.getCurrentItem().getName());
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

                case NEXT:
                    shelf.nextItem();
                    itemName.setText(shelf.getCurrentItem().getName());
                    amount.setText("" + shelf.getCurrentItem().getDesiredAmount());
                default:
            }
        }

    }


    private void init() {
        final ShelfItem currentItem = shelf.getCurrentItem();
        final ListView groceryListLayout = (ListView) findViewById(R.id.grocery_list);
        final TextView itemName = (TextView) findViewById(R.id.itemName);
        final TextView amount = (TextView) findViewById(R.id.amount);
        Button yesButton = (Button) findViewById(R.id.yesButt);
        Button noButton = (Button) findViewById(R.id.noButt);
        ImageButton plusButton = (ImageButton) findViewById(R.id.plusButton);
        ImageButton minusButton = (ImageButton) findViewById(R.id.minusButton);
        ImageButton nextButton = (ImageButton) findViewById(R.id.nextButt);
        ImageButton prevButton = (ImageButton) findViewById(R.id.prevButt);
        groceryListAdapter = new GroceryListAdapter(this, groceryList.getGroceries());
        groceryListLayout.setAdapter(groceryListAdapter);

        itemName.setText(currentItem.getName());
        amount.setText("" + currentItem.getDesiredAmount());

        yesButton.setOnClickListener(new GroceryClickListener(ClickOperation.ADD, amount, itemName));
        noButton.setOnClickListener(new GroceryClickListener(ClickOperation.NEXT, amount, itemName));
        nextButton.setOnClickListener(new GroceryClickListener(ClickOperation.NEXT, amount, itemName));
        prevButton.setOnClickListener(new GroceryClickListener(ClickOperation.PREV, amount, itemName));
        plusButton.setOnClickListener(new GroceryClickListener(ClickOperation.INCREASE_AMOUNT, amount, itemName));
        minusButton.setOnClickListener(new GroceryClickListener(ClickOperation.DECREASE_AMOUNT, amount, itemName));

    }

    @Override
    public void onResume() {
        super.onResume();
        findViewById(R.id.make_list).setBackgroundColor(getResources().getColor(R.color.shelfie_darker_blue));
        findViewById(R.id.edit_shelf).setBackgroundColor(getResources().getColor(R.color.shelfie_blue));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.grocery_list);
        findViewById(R.id.make_list).setBackgroundColor(getResources().getColor(R.color.shelfie_darker_blue));
        findViewById(R.id.edit_shelf).setBackgroundColor(getResources().getColor(R.color.shelfie_blue));

        ActionBar actionBar = getActionBar();
        if(actionBar != null) {
            actionBar.setTitle(getString(R.string.make_grocery_list));
        }

        if(shelf.getItems().size() == 0) {
            Toast.makeText(this, R.string.shelf_is_empty, Toast.LENGTH_LONG).show();
            startEditShelfActivity(findViewById(R.id.edit_shelf));
        } else {
            init();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.grocery_list_options, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.delete_groceries:
                deleteGroceries();
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteGroceries() {
        if(groceryList != null) { groceryList.clear(); }
        if(groceryListAdapter != null) { groceryListAdapter.notifyDataSetChanged(); }
    }
}