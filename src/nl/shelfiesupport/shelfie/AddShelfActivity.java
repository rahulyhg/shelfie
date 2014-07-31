package nl.shelfiesupport.shelfie;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddShelfActivity extends BaseActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_shelf);
    }

    public void addShelf(View view) {
        EditText shelfName = (EditText) findViewById(R.id.shelfName);
        view.setBackgroundColor(getResources().getColor(R.color.shelfie_darker_blue));

        if(shelfName.getText().toString().trim().length() > 0) {
            Toast.makeText(this, "TODO: create new shelf named: " + shelfName.getText(), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } else {
            final View viewA = view;
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    viewA.setBackgroundColor(getResources().getColor(R.color.shelfie_blue));
                }
            }, 80);
        }
    }
}
