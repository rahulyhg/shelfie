package nl.rene.shelfie;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImportTask extends AsyncTask<URL, Integer, String> {

    Responder responder;

    public ImportTask(Responder responder) {
        super();
        this.responder = responder;
    }

    @Override
    protected String doInBackground(URL... urls) {
        String response = null;
        HttpURLConnection connection = null;
        InputStream is = null;
        BufferedReader reader = null;
        if(urls.length == 0) { return null; }
        try {
            connection = (HttpURLConnection) urls[0].openConnection();
            is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String ln;
            while((ln = reader.readLine()) != null) { sb.append(ln); }
            response = sb.toString();
        } catch (IOException e) {
            Log.w("SHELFIE", "Failed to open URL: " + urls[0]);
        } finally {
            if(reader != null) { try { reader.close(); } catch (IOException ignored) { /* ignore */ } }
            if(is != null) { try { is.close(); } catch (IOException ignored) { /* ignore */ } }
            if(connection != null) { connection.disconnect(); }
        }

        return response;
    }

    @Override
    protected void onPostExecute(String response) {
        responder.respondWith(response);
    }
}
