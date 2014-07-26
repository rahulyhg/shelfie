package nl.rene.shelfie;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

public class ImportActivity extends Activity implements Responder {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Uri uri = getIntent().getData();

        List<String> params = uri.getPathSegments();
        Toast.makeText(this, getString(R.string.importing), Toast.LENGTH_LONG).show();


        new ImportTask(this).execute(params.get(0));


    }

    @Override
    public void respondWith(String response) {
        if(response != null) {
            Toast.makeText(this, getString(R.string.import_ok) , Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, getString(R.string.import_failed), Toast.LENGTH_LONG).show();
        }
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);


    }
}