package nl.rene.shelfie;

import android.os.Bundle;

public class MainActivity extends BaseActivity  {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(false);
        getActionBar().setHomeButtonEnabled(false);

        getActionBar().setTitle(getString(R.string.app_name));

        setContentView(R.layout.main);
    }


}
