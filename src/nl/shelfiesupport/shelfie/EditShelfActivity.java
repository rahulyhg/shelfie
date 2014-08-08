package nl.shelfiesupport.shelfie;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;


public class EditShelfActivity extends BaseActivity {

    private ShelfListAdapter adapter;


    private class ShelfListAdapter extends ArrayAdapter<ShelfItem> {
        private final Context context;
        private final List<ShelfItem> objects;

        private class SelectClickListener implements View.OnClickListener {
            private ShelfListAdapter adapter;
            private ShelfItem item;

            public SelectClickListener(ShelfItem item, ShelfListAdapter adapter) {
                this.adapter = adapter;
                this.item = item;
            }

            @Override
            public void onClick(View v) {
                shelf.setSelectedItem(item);
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
                        shelf.adjustDesiredAmount(item, 1);
                        textView.setText("" + item.getDesiredAmount());
                        break;
                    case DECREASE_AMOUNT:
                        shelf.adjustDesiredAmount(item, -1);
                        textView.setText("" + item.getDesiredAmount());
                        break;
                    case SWAP_UP:
                        shelf.swapItems(position, position - 1);
                        break;
                    case SWAP_DOWN:
                        shelf.swapItems(position, position + 1);
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
                                shelf.removeItem(position);
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(context.getString(R.string.no), null)
                        .show();
                return false;
            }
        }

        public ShelfListAdapter(Context context, List<ShelfItem> objects) {
            super(context, R.layout.shelf_row, objects);
            this.context = context;
            this.objects = objects;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ShelfItemRowLayout shelfItemRowLayout;
            if(convertView == null) {
                shelfItemRowLayout = new ShelfItemRowLayout(context);
            } else {
                shelfItemRowLayout = (ShelfItemRowLayout) convertView;
            }

            shelfItemRowLayout.setName(objects.get(position).getName());
            shelfItemRowLayout.setDesiredAmount("" + objects.get(position).getDesiredAmount());
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

    @Override
    public void onResume() {
        super.onResume();
        findViewById(R.id.make_list).setBackgroundColor(getResources().getColor(R.color.shelfie_blue));
        findViewById(R.id.edit_shelf).setBackgroundColor(getResources().getColor(R.color.shelfie_darker_blue));
        final ListView shelfLayout = (ListView) findViewById(R.id.edit_shelf_list);
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


        final ListView shelfLayout = (ListView) findViewById(R.id.edit_shelf_list);
        adapter = new ShelfListAdapter(this, shelf.getItems());
        shelfLayout.setAdapter(adapter);

        initShelfPicker();

        findViewById(R.id.edit_shelf).setBackgroundColor(getResources().getColor(R.color.shelfie_darker_blue));
        findViewById(R.id.make_list).setBackgroundColor(getResources().getColor(R.color.shelfie_blue));

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        if(size.y > 640) {
            initAds(R.id.adView2);
        } else {
            View ad = findViewById(R.id.adView2);
            ((LinearLayout)ad.getParent()).removeView(ad);
        }

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
    }
}