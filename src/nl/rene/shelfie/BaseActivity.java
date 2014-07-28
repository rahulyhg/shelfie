package nl.rene.shelfie;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

public class BaseActivity extends Activity implements Responder {
    protected Shelf shelf;
    private View exportButton = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        shelf = Shelf.getInstance(this);
    }

    public void onPause() {
        super.onPause();
        Shelf shelf = Shelf.getInstance(this);
        if(shelf != null) { shelf.save(this); }
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
        return true;
    }

    @SuppressWarnings("unused")
    public void startGroceryListActivity(View view) {
        Intent intent = new Intent(this, GroceryListActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @SuppressWarnings("unused")
    public void startEditShelfActivity(View view) {
        Intent intent = new Intent(this, EditShelfActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @SuppressWarnings("unused")
    public void startShelfExport(View view) {
        if(shelf == null) { shelf = Shelf.getInstance(this); }
        Toast.makeText(this, getString(R.string.exporting), Toast.LENGTH_LONG).show();
        exportButton = view;
        exportButton.setEnabled(false);

        new ExportTask(this, shelf).execute("");
    }

    private void parseAndShareExport(String jsonStr) {
        try {
            String id = ((JSONObject) new JSONObject(jsonStr).getJSONArray("added").get(0)).getString("_id");
            Log.d("SHELFIE", id);
            Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "http://getshelfie.herokuapp.com/" + id);
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_title)));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } catch (JSONException e) {
            Log.w("SHELFIE", "failed to parse response: " + jsonStr);
            Toast.makeText(this, getString(R.string.export_failed), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void respondWith(String response) {
        if(exportButton != null) { exportButton.setEnabled(true); }
        if(response != null) {
            parseAndShareExport(response);
        } else {
            Toast.makeText(this, getString(R.string.export_failed), Toast.LENGTH_LONG).show();
        }
    }
}