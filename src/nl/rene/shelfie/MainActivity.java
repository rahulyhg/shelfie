package nl.rene.shelfie;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends BaseActivity  {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setTitle(getString(R.string.app_name));

        setContentView(R.layout.main);
/*         try {
            new ImportTask(this).execute(new URL(getString(R.string.service)));
        } catch (MalformedURLException e) {
            Log.w("SHELFIE", "Malformed url");
        }  */
    }


}
