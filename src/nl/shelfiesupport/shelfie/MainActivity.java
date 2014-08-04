package nl.shelfiesupport.shelfie;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends BaseActivity   {
    private View exportButton = null;

    @Override
    public void onResume() {
        super.onResume();
        findViewById(R.id.export_shelf).setBackgroundColor(getResources().getColor(R.color.shelfie_blue));
        findViewById(R.id.edit_shelf).setBackgroundColor(getResources().getColor(R.color.shelfie_blue));
        findViewById(R.id.make_list).setBackgroundColor(getResources().getColor(R.color.shelfie_blue));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setHomeButtonEnabled(false);
            actionBar.setTitle(getString(R.string.app_name));
        }

        setContentView(R.layout.main);
        findViewById(R.id.export_shelf).setBackgroundColor(getResources().getColor(R.color.shelfie_blue));
        findViewById(R.id.edit_shelf).setBackgroundColor(getResources().getColor(R.color.shelfie_blue));
        findViewById(R.id.make_list).setBackgroundColor(getResources().getColor(R.color.shelfie_blue));

        final Spinner currentShelfSpinner = (Spinner) findViewById(R.id.currentShelfSpinner);
        initSpinner(currentShelfSpinner);
        if(currentShelfAdapter != null) { currentShelfSpinner.setOnItemSelectedListener(this); }    }

    @SuppressWarnings("unused")
    public void startShelfExport(View view) {
        if(shelf == null) { shelf = Shelf.getInstance(this); }
        Toast.makeText(this, getString(R.string.exporting), Toast.LENGTH_LONG).show();
        exportButton = view;
        exportButton.setEnabled(false);
        exportButton.setBackgroundColor(getResources().getColor(R.color.shelfie_darker_blue));

        new ExportTask(this, shelf).execute("");
    }



    @Override
    public void respondWith(String response) {
        super.respondWith(response);
        if(exportButton != null) {
            exportButton.setEnabled(true);
            exportButton.setBackgroundColor(getResources().getColor(R.color.shelfie_blue));
        }

    }
}
