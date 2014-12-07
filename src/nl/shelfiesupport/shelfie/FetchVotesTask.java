package nl.shelfiesupport.shelfie;

import android.os.AsyncTask;
import android.util.Log;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchVotesTask extends AsyncTask<String, Integer, String> {
    final VoteResponder responder;

    public FetchVotesTask(VoteResponder responder) {
        this.responder = responder;
    }


    @Override
    protected String doInBackground(String... params) {
        String response = null;
        HttpURLConnection connection = null;
        InputStream is = null;
        BufferedReader reader = null;


        try {
            URL url = new URL(Remoting.SERVICE_URL_FEATURES);
            connection = (HttpURLConnection) url.openConnection();
            Log.d(Tag.SHELFIE_NET, url.toString());
            connection.setRequestProperty("Accept-Charset", "utf-8");
            connection.setRequestProperty(Remoting.ALLOWANCE_HEADER, "expire");

            is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String ln;
            while((ln = reader.readLine()) != null) { sb.append(ln); }
            response = sb.toString();

        } catch (IOException e) {
            Log.w(Tag.SHELFIE, "Failed to open service");
        } finally {
            if(reader != null) { try { reader.close(); } catch (IOException ignored) { /* ignore */ } }
            if(is != null) { try { is.close(); } catch (IOException ignored) { /* ignore */ } }
            if(connection != null) { connection.disconnect(); }
        }
        return response;
    }

    @Override
    protected void onPostExecute(String response) {
        responder.respondToVotesFetchWith(response);
    }
}
