package nl.shelfiesupport.shelfie;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ImportActivity extends Activity implements Responder {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.do_import);
        Uri uri = getIntent().getData();

        List<String> params = uri.getPathSegments();

        if(params.size() == 0) {
            Toast.makeText(this, getString(R.string.import_failed), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
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
                Shelf shelf = Shelf.getInstance(this);
                shelf.importFromJSON(obj);
                shelf.setChanged(true);
                shelf.save(this);
                Toast.makeText(this, getString(R.string.import_ok) , Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                Toast.makeText(this, getString(R.string.import_failed), Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(this, getString(R.string.import_failed), Toast.LENGTH_LONG).show();
        }
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }
}