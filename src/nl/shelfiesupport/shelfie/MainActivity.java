package nl.shelfiesupport.shelfie;

import android.app.ActionBar;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends BaseActivity implements VoteResponder {


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

        initShelfPicker();


        if(Inventory.isInfoSuppressed()) {
            hideInfo();
        } else {
            ViewFlipper infoFlipper = (ViewFlipper) findViewById(R.id.info_flipper);
            infoFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
            infoFlipper.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        }

        initAds(R.id.adView);
        if(Inventory.mayFetchNext()) {
            final VoteResponder self = this;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    new FetchVotesTask(self).execute();
                }
            }, 1000);

        }

    }

    private void hideInfo() {
        findViewById(R.id.info_layout).setVisibility(View.INVISIBLE);
    }


    public void toggleInfoViews(View view) {
        final View[] infos = {
                findViewById(R.id.info_flipper),
                findViewById(R.id.info_frame)
        };
        for(View infoView : infos) {
            if (infoView.getVisibility() == View.GONE) {
                infoView.setVisibility(View.VISIBLE);
            } else {
                infoView.setVisibility(View.GONE);
            }
        }

    }

    public void removeInfo(View view) {
        Inventory.setInfoSuppressed(true);
        hideInfo();
    }

    @Override
    public void respondToVotesFetchWith(String response) {
        Inventory.setNextFetch(System.currentTimeMillis() + 3600000);
        if(response != null) {
            try {
                Inventory.setVoteFetchData(new JSONObject("{votes: " + response + "}"));
                renderVotes();
            } catch (JSONException e) {
                Log.w("SHELFIE", "Failed to parse json in VOTE fetch");
            }
        } else {
            Log.d("SHELFIE", "NO VOTES LANDED");
        }
    }

    private void renderVotes() {
        JSONObject voteFetchData = Inventory.getVoteFetchData();
        if(voteFetchData == null) { return; }
        try {
            JSONArray jsonVotes = voteFetchData.getJSONArray("votes");
            Log.d("SHELFIE", "vote data: " + jsonVotes);
        } catch (JSONException ignored) {
        }

    }
}
