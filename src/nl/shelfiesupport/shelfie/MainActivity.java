package nl.shelfiesupport.shelfie;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
        } else {
            renderVotes();
        }

    }

    private void hideInfo() {

        findViewById(R.id.info_wrapper).setVisibility(View.GONE);
    }


    public void toggleInfoViews(View view) {
        final View[] toggleViews = {
                findViewById(R.id.info_flipper),
                findViewById(R.id.info_frame),
                findViewById(R.id.votesTitle),
                findViewById(R.id.votesList)
        };

        for(View infoView : toggleViews) {
            if (infoView.getVisibility() == View.GONE) {
                infoView.setVisibility(View.VISIBLE);
            } else {
                infoView.setVisibility(View.GONE);
            }
        }
        final View infoWrapper = findViewById(R.id.info_wrapper);
        ViewGroup.LayoutParams params = infoWrapper.getLayoutParams();
        if(toggleViews[0].getVisibility() == View.GONE) {
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        } else {
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        infoWrapper.setLayoutParams(params);
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

    private class VotesAdapter extends ArrayAdapter<JSONObject> {
        private final Context context;
        private final List<JSONObject> objects;

        public VotesAdapter(Context context, List<JSONObject> objects) {
            super(context, R.layout.vote_row, objects);
            this.context = context;
            this.objects = objects;
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout layout;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                layout = (LinearLayout) inflater.inflate(R.layout.vote_row, parent, false);
            } else {
                layout = (LinearLayout) convertView;
            }

            try {
                JSONObject localized = objects.get(position).getJSONObject("en");
                TextView shortDesc = (TextView) layout.findViewById(R.id.short_desc);
                TextView score = (TextView) layout.findViewById(R.id.score);
                shortDesc.setText(localized.getString("short"));
                score.setText("" + objects.get(position).getInt("score"));
            } catch(JSONException e) {
                Log.w("SHELFIE", e.getMessage());
            }

            return layout;
        }
    }

    private void renderVotes() {
        JSONObject voteFetchData = Inventory.getVoteFetchData();
        if(voteFetchData == null) { return; }
        try {
            final ListView voteListView = (ListView) findViewById(R.id.votesList);
            final List<JSONObject> votesList = new ArrayList<JSONObject>();
            final JSONArray jsonVotes = voteFetchData.getJSONArray("votes");
            final VotesAdapter votesAdapter = new VotesAdapter(this, votesList);

            voteListView.setAdapter(votesAdapter);

            for(int i = 0; i < jsonVotes.length(); i++) {
                votesList.add(jsonVotes.getJSONObject(i));
            }

            votesAdapter.notifyDataSetChanged();
          //  JSONArray jsonVotes = voteFetchData.getJSONArray("votes");
            Log.d("SHELFIE", "vote data: " + jsonVotes);

        } catch (JSONException ignored) {
        }

    }
}
