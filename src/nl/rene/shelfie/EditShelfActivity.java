package nl.rene.shelfie;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;


public class EditShelfActivity extends BaseActivity {

    private ShelfListAdapter adapter;

    private class ShelfListAdapter extends ArrayAdapter<ShelfItem> {
        private final Context context;
        private final List<ShelfItem> objects;

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


        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO: implement viewholder pattern
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.shelf_row, parent,false);

            TextView nameInput = (TextView) layout.findViewById(R.id.itemName);

            TextView desiredAmountInput = (TextView) layout.findViewById(R.id.itemDesiredAmt);
            nameInput.setText(objects.get(position).getName());
            desiredAmountInput.setText("" + objects.get(position).getDesiredAmount());

            ImageButton decrementButton = (ImageButton) layout.findViewById(R.id.minAmt);
            ImageButton incrementButton = (ImageButton) layout.findViewById(R.id.plusAmt);
            ImageButton upArrow = (ImageButton) layout.findViewById(R.id.upButton);
            ImageButton downArrow = (ImageButton) layout.findViewById(R.id.downButton);
            decrementButton.setOnClickListener(new ClickListener(objects.get(position), desiredAmountInput, position, this, ClickOperation.DECREASE_AMOUNT));
            incrementButton.setOnClickListener(new ClickListener(objects.get(position), desiredAmountInput, position,  this, ClickOperation.INCREASE_AMOUNT));
            upArrow.setOnClickListener(new ClickListener(objects.get(position), desiredAmountInput, position, this, ClickOperation.SWAP_UP));
            downArrow.setOnClickListener(new ClickListener(objects.get(position), desiredAmountInput, position, this, ClickOperation.SWAP_DOWN));

            layout.setOnLongClickListener(new ClickListener(objects.get(position), desiredAmountInput, position, this, ClickOperation.DELETE));


            return layout;
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_shelf);
        getActionBar().setTitle(getString(R.string.app_name) + " - " + getString(R.string.edit_shelf_title));

        final ListView shelfLayout = (ListView) findViewById(R.id.edit_shelf_list);
        adapter = new ShelfListAdapter(this, shelf.getItems());
        shelfLayout.setAdapter(adapter);
        findViewById(R.id.edit_shelf).setBackgroundColor(getResources().getColor(R.color.shelfie_darker_blue));
        EditText newItem = (EditText) findViewById(R.id.addInput);
        newItem.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String value = v.getText().toString();
                if(actionId == EditorInfo.IME_ACTION_DONE && value.trim().length() > 1) {
                    shelf.addItem(new ShelfItem(value, 1));
                    v.setText("");
                    adapter.notifyDataSetChanged();
                    shelfLayout.setSelection(adapter.getCount() - 1);
                    return true;
                }
                return false;
            }
        });
    }
}