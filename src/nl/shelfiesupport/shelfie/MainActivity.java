package nl.shelfiesupport.shelfie;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Spinner;
import android.widget.ViewFlipper;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends BaseActivity   {

    @Override
    public void onResume() {
        super.onResume();
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
        findViewById(R.id.edit_shelf).setBackgroundColor(getResources().getColor(R.color.shelfie_blue));
        findViewById(R.id.make_list).setBackgroundColor(getResources().getColor(R.color.shelfie_blue));

        final Spinner currentShelfSpinner = (Spinner) findViewById(R.id.currentShelfSpinner);
        initSpinner(currentShelfSpinner);
        if(currentShelfAdapter != null) { currentShelfSpinner.setOnItemSelectedListener(this); }

        initAds(R.id.adView);

        if(Inventory.isInfoSuppressed()) {
            hideInfo();
        } else {
            ViewFlipper infoFlipper = (ViewFlipper) findViewById(R.id.info_flipper);
            infoFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
            infoFlipper.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        }
    }

    private void hideInfo() {
        findViewById(R.id.info_layout).setVisibility(View.INVISIBLE);
    }

    public void removeInfo(View view) {
        Inventory.setInfoSuppressed(true);
        hideInfo();
    }

}
