package nl.shelfiesupport.shelfie;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

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
        if(currentShelfAdapter != null) { currentShelfSpinner.setOnItemSelectedListener(this); }
    }

    @SuppressWarnings("unused")
    public void startShelfExport(View view) {
        if(shelf == null) { shelf = Shelf.getInstance(this); }
        Toast.makeText(this, getString(R.string.exporting), Toast.LENGTH_LONG).show();
        exportButton = view;
        exportButton.setEnabled(false);
        exportButton.setBackgroundColor(getResources().getColor(R.color.shelfie_darker_blue));

        new ExportTask(this, shelf).execute("");
    }

    public void deleteCurrentShelf(View view) {
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
    public void respondWith(String response) {
        super.respondWith(response);
        if(exportButton != null) {
            exportButton.setEnabled(true);
            exportButton.setBackgroundColor(getResources().getColor(R.color.shelfie_blue));
        }

    }
}
