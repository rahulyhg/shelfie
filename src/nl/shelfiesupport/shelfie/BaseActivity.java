package nl.shelfiesupport.shelfie;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;


public class BaseActivity extends Activity implements Responder  {
    protected Shelf shelf;
    protected GroceryList groceryList;

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
                intent = new Intent(this, AddShelfActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
            Log.d("SHELFIE", id);
            Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.share_subject) + " " +
                    getString(R.string.via) + ": http://getshelfie.herokuapp.com/" + id);
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_title)));
        } catch (JSONException e) {
            Log.w("SHELFIE", "failed to parse response: " + jsonStr);
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
}