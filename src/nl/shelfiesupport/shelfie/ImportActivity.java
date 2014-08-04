package nl.shelfiesupport.shelfie;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
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

    private void startMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    private void showOverwriteDialog(final Shelf localShelf, final Shelf importedShelf) {
        final Context context = this;
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.overwrite_shelf) + ": " + importedShelf.getName())
                .setMessage(getString(R.string.overwrite_shelf_confirm))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        localShelf.overwriteWith(context, importedShelf);
                        startMain();
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showNewNameDialog(importedShelf);
                    }
                })
                .show();
    }

    private void showNewNameDialog(final Shelf importedShelf) {
        final Context context = this;
        final EditText input = new EditText(this);
        input.setText(importedShelf.getName());
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.import_with_new_name))
                .setView(input)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newName = input.getText().toString().trim();
                        if(newName.length() < 1) {
                            showNewNameDialog(importedShelf);
                            return;
                        }
                        importedShelf.setName(newName);
                        Inventory.saveImportedShelf(context, importedShelf);
                        Toast.makeText(context, getString(R.string.import_ok) + ": " +
                                importedShelf.getName(), Toast.LENGTH_LONG).show();
                        startMain();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, getString(R.string.import_canceled) + ": " +
                                importedShelf.getName(), Toast.LENGTH_LONG).show();
                        startMain();
                    }
                })
                .show();


    }

    @Override
    public void respondWith(String response) {
        if(response != null) {
            try {
                JSONObject obj = new JSONObject(response);
                if(obj.has("not") && obj.getString("not").equals("found")) { throw new JSONException("object not found"); }
                Shelf shelf = new Shelf(obj);
                Shelf localShelf = Inventory.findShelfByName(this, shelf.getName());
                if(localShelf != null) {
                    showOverwriteDialog(localShelf, shelf);
                    return;
                } else {
                    Inventory.saveImportedShelf(this, shelf);
                    Toast.makeText(this, getString(R.string.import_ok) + ": " +
                            shelf.getName(), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Toast.makeText(this, getString(R.string.import_failed), Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(this, getString(R.string.import_failed), Toast.LENGTH_LONG).show();
        }
        startMain();
    }

}