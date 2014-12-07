package nl.shelfiesupport.shelfie;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import org.json.JSONException;
import org.json.JSONObject;

public class BaseActivity extends Activity implements Responder, AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {
    protected Shelf shelf;
    protected GroceryList groceryList;
    protected ArrayAdapter<String> currentShelfAdapter;

    protected void initShelfPicker() {
        final View currentShelfSpinner = findViewById(R.id.currentShelfSpinner);
        currentShelfAdapter = new ArrayAdapter<String>(this, R.layout.spinner_row, Inventory.getShelfNames(this));

        if(currentShelfSpinner != null) {

            if(currentShelfSpinner instanceof Spinner) {
                ((Spinner) currentShelfSpinner).setAdapter(currentShelfAdapter);

                ((Spinner) currentShelfSpinner).setSelection(Inventory.getSelectedShelfIndex());
                ((Spinner) currentShelfSpinner).setOnItemSelectedListener(this);
            } else if(currentShelfSpinner instanceof ListView) {
                ((ListView) currentShelfSpinner).setAdapter(currentShelfAdapter);

                ((ListView) currentShelfSpinner).setItemChecked(Inventory.getSelectedShelfIndex(), true);
                ((ListView) currentShelfSpinner).setOnItemClickListener(this);

            }
        }

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
        shelf = Shelf.getInstance(this);
        groceryList = GroceryList.getInstance(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        shelf = Shelf.getInstance(this);
        groceryList = GroceryList.getInstance(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        shelf = Shelf.getInstance(this);
        groceryList = GroceryList.getInstance(this);
        if(shelf != null) { shelf.save(this); }
        if(groceryList != null) { groceryList.save(this); }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if(this instanceof GroceryListActivity) {
            inflater.inflate(R.menu.grocery_top_menu, menu);
        } else {
            inflater.inflate(R.menu.main_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    public void deleteCurrentShelf() {
        final Context context = this;

        if(Inventory.getShelfNames(this).size() < 2) {
            Toast.makeText(context,
                    context.getString(R.string.uneedaleastoneshelf), Toast.LENGTH_LONG).show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.delete_shelf) + ": " + shelf.getName())
                    .setMessage(getString(R.string.delete_shelf_confirm))
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Inventory.deleteCurrentShelf(context);
                            recreate();
                        }
                    })
                    .setNegativeButton(getString(R.string.no), null)
                    .show();
        }
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        Intent intent;
        switch(item.getItemId()) {
            case android.R.id.home:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;

            case R.id.add_shelf:
                showAddShelfPrompt();
                break;

            case R.id.main_menu_delete_shelf:
                deleteCurrentShelf();
                break;

            case R.id.email_menu_button:
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("message/rfc822");
                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.share_groceries_subject));
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, groceryList.asText());
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share_groceries_title)));
                break;

            case R.id.share_menu_button:
                if (shelf == null) {
                    shelf = Shelf.getInstance(this);
                }
                Toast.makeText(this, getString(R.string.exporting), Toast.LENGTH_LONG).show();
                new ExportTask(this, shelf).execute("");
                break;

            default:
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAddShelfPrompt() {
        final EditText input = new EditText(this);
        final Context self = this;
        input.setHint(getString(R.string.shelf_name));
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.add_shelf))
                .setView(input)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (input.getText().toString().trim().length() == 0) {
                            showAddShelfPrompt();
                        } else {
                            Inventory.createNewShelf(self, input.getText().toString().trim());
                            Intent intent = new Intent(self, EditShelfActivity.class);
                            startActivity(intent);
                        }
                    }
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }



    @SuppressWarnings("unused")
    public void startGroceryListActivity(View view) {
        Intent intent = new Intent(this, GroceryListActivity.class);
        view.setBackgroundColor(getResources().getColor(R.color.shelfie_darker_blue));

        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @SuppressWarnings("unused")
    public void startEditShelfActivity(View view) {
        Intent intent = new Intent(this, EditShelfActivity.class);
        view.setBackgroundColor(getResources().getColor(R.color.shelfie_darker_blue));

        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    protected void parseAndShareExport(String jsonStr) {
        try {
            String id = ((JSONObject) new JSONObject(jsonStr).getJSONArray("added").get(0)).getString("_id");
            shelf.setExportId(id);
            shelf.save(this);
            Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.share_subject) + ": " +
                    shelf.getName() + " " +
                    getString(R.string.via) + ": http://getshelfie.herokuapp.com/" + id);
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_title)));
        } catch (JSONException e) {
            Log.w(Tag.SHELFIE, "failed to parse response: " + jsonStr);
            Toast.makeText(this, getString(R.string.export_failed), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void respondWith(String response) {
        if(response != null) {
            parseAndShareExport(response);
        } else {
            Toast.makeText(this, getString(R.string.export_failed), Toast.LENGTH_LONG).show();
        }
    }


    private void selectShelf(int position) {

        if(Inventory.getSelectedShelfIndex() != position) {
            Inventory.setSelectedShelfByIndex(this, position);
            if(this instanceof MainActivity) {
                shelf = Shelf.getInstance(this);
            } else {
                recreate();
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectShelf(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectShelf(position);
     }
}