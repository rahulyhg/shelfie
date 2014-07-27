package nl.rene.shelfie;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ImportActivity extends BaseActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Uri uri = getIntent().getData();

        List<String> params = uri.getPathSegments();
        Toast.makeText(this, getString(R.string.importing), Toast.LENGTH_LONG).show();

        if(params.size() == 0) {
            Toast.makeText(this, getString(R.string.import_failed), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            new ImportTask(this).execute(params.get(0));
        }


    }

    @Override
    public void respondWith(String response) {
        if(response != null) {
            try {
                JSONObject obj = new JSONObject(response);
                if(obj.has("not") && obj.getString("not").equals("found")) { throw new JSONException("object not found"); }
                shelf = Shelf.fromJSON(obj);
                shelf.save(this);
                Toast.makeText(this, getString(R.string.import_ok) , Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                Toast.makeText(this, getString(R.string.import_failed), Toast.LENGTH_LONG).show();
                shelf = Shelf.getInstance(this);
            }

        } else {
            Toast.makeText(this, getString(R.string.import_failed), Toast.LENGTH_LONG).show();
        }
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}