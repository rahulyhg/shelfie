package nl.shelfiesupport.shelfie;

import android.app.ActionBar;
import android.app.AlertDialog;
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
import java.util.Locale;

public class MainActivity extends BaseActivity implements VoteResponder {


    @Override
    public void onResume() {
        super.onResume();
        findViewById(R.id.edit_shelf).setBackgroundColor(getResources().getColor(R.color.shelfie_blue));
        findViewById(R.id.make_list).setBackgroundColor(getResources().getColor(R.color.shelfie_blue));
        initShelfPicker();

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

        if(Inventory.isInfoSuppressed()) {
            hideInfo();
        } else {
            ViewFlipper infoFlipper = (ViewFlipper) findViewById(R.id.info_flipper);
            infoFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
            infoFlipper.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        }


        if(Inventory.mayFetchNext() || Inventory.getVoteFetchData() == null) {
            final VoteResponder self = this;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    new FetchVotesTask(self).execute();
                }
            }, 400);
        } else {
            renderVotes();
        }

    }

    private void hideInfo() {

        findViewById(R.id.votesTitle).setVisibility(View.VISIBLE);
        findViewById(R.id.votesList).setVisibility(View.VISIBLE);
        findViewById(R.id.info_wrapper).setVisibility(View.GONE);
    }


    public void toggleInfoViews(@SuppressWarnings("UnusedParameters") View view) {
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

    public void removeInfo(@SuppressWarnings("UnusedParameters") View view) {
        Inventory.setInfoSuppressed(true);
        hideInfo();
    }

    @Override
    public void respondToVotesFetchWith(String response) {
        Inventory.setNextFetch(System.currentTimeMillis() + 3600000);
        if(response != null) {
            try {
                Inventory.setVoteFetchData(new JSONObject("{votes: " + response + "}"));
                Inventory.getInstance(this).save(this);
                renderVotes();
            } catch (JSONException e) {
                Log.w(Tag.SHELFIE, "Failed to parse json in VOTE fetch");
            }
        } else {
            Log.d(Tag.SHELFIE, "NO VOTES LANDED");
        }
    }

    private class VotesAdapter extends ArrayAdapter<JSONObject> {
        private final Context context;
        private final List<JSONObject> objects;
        private final String currentVote;

        public VotesAdapter(Context context, List<JSONObject> objects, String currentVote) {
            super(context, R.layout.vote_row, objects);
            this.context = context;
            this.objects = objects;
            this.currentVote = currentVote;
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
                String lang = "en";
                if (Locale.getDefault().getLanguage().equalsIgnoreCase("nl")) {
                    lang = "nl";
                }
                final String voteId = objects.get(position).getString("_id");
                JSONObject localized = objects.get(position).getJSONObject(lang);
                TextView shortDesc = (TextView) layout.findViewById(R.id.short_desc);
                TextView score = (TextView) layout.findViewById(R.id.score);
                shortDesc.setText(localized.getString("short"));
                score.setText("" + objects.get(position).getInt("score"));

                if(currentVote == null) {
                    View upVoteButton = layout.findViewById(R.id.upVote);
                    layout.findViewById(R.id.noVote).setVisibility(View.GONE);

                    upVoteButton.setVisibility(View.VISIBLE);
                    upVoteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Inventory.addVote(context, voteId);
                            Inventory.setNextFetch(-1);
                            new VoteTask(voteId, 1).execute();
                            recreate();
                        }
                    });
                } else if(currentVote.equalsIgnoreCase(voteId)) {
                    View downVoteButton = layout.findViewById(R.id.downVote);
                    layout.findViewById(R.id.noVote).setVisibility(View.GONE);
                    downVoteButton.setVisibility(View.VISIBLE);
                    downVoteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Inventory.retractVote(context, voteId);
                            Inventory.setNextFetch(-1);
                            new VoteTask(voteId, -1).execute();
                            recreate();
                        }
                    });
                }

                final String longDesc = localized.getString("long");
                layout.findViewById(R.id.voteTextWrapper).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(context)
                                .setMessage(longDesc)
                                .setCancelable(false)
                                .setPositiveButton(getString(R.string.ok), null)
                                .show();
                    }
                });
            } catch(JSONException e) {
                Log.w(Tag.SHELFIE, e.getMessage());
            }

            return layout;
        }
    }

    private void renderVotes() {
        JSONObject voteFetchData = Inventory.getVoteFetchData();
        if(voteFetchData == null) {

            return;
        }
        try {
            final ListView voteListView = (ListView) findViewById(R.id.votesList);
            final List<JSONObject> votesList = new ArrayList<JSONObject>();
            final JSONArray jsonVotes = voteFetchData.getJSONArray("votes");
            String currentVote = null;
            for(int i = 0; i < jsonVotes.length(); i++) {
                if(Inventory.votedFor(this, jsonVotes.getJSONObject(i).getString("_id"))) {
                    currentVote = jsonVotes.getJSONObject(i).getString("_id");
                }
                votesList.add(jsonVotes.getJSONObject(i));
            }

            final VotesAdapter votesAdapter = new VotesAdapter(this, votesList, currentVote);
            voteListView.setAdapter(votesAdapter);
            votesAdapter.notifyDataSetChanged();

        } catch (JSONException ignored) {
        }

    }
}
