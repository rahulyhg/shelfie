package nl.rene.shelfie;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BaseActivity extends Activity implements Responder {
    protected Shelf shelf;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shelf = Shelf.getInstance(this);
    }

    public void onPause() {
        super.onPause();
        Shelf shelf = Shelf.getInstance(this);
        if(shelf != null) { shelf.save(this); }
    }



    @SuppressWarnings("unused")
    public void startGroceryListActivity(View view) {
        Intent intent = new Intent(this, GroceryListActivity.class);
        startActivity(intent);
    }

    @SuppressWarnings("unused")
    public void startEditShelfActivity(View view) {
        Intent intent = new Intent(this, EditShelfActivity.class);
        startActivity(intent);
    }

    @SuppressWarnings("unused")
    public void startShelfExport(View view) {
        if(shelf == null) { shelf = Shelf.getInstance(this); }
        new ExportTask(this, shelf).execute("");
    }

    private void parseAndShareExport(String jsonStr) {
        try {
            String id = ((JSONObject) new JSONObject(jsonStr).getJSONArray("added").get(0)).getString("_id");
            Log.d("SHELFIE", id);
            Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "http://porkfront.herokuapp.com/" + id);
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