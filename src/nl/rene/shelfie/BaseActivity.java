package nl.rene.shelfie;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class BaseActivity extends Activity {
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
}