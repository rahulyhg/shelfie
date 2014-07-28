package nl.rene.shelfie;

import android.os.AsyncTask;
import android.util.Log;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImportTask extends AsyncTask<String, Integer, String> {
    Responder responder;

    public ImportTask(Responder responder) {
        super();
        this.responder = responder;
    }

    @Override
    protected String doInBackground(String... id) {
        if(id.length == 0) { return null; }
        String response = null;
        HttpURLConnection connection = null;
        InputStream is = null;
        BufferedReader reader = null;


        try {
            URL url = new URL(Remoting.SERVICE_URL);
            String sendData = "{\"action\": \"fetch\", \"id\": \"" + id[0] + "\" }";
            Log.d("SHELFIE", sendData);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
            dos.writeBytes(sendData);
            dos.flush();
            dos.close();
            is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String ln;
            while((ln = reader.readLine()) != null) { sb.append(ln); }
            response = sb.toString();
        } catch (IOException e) {
            Log.w("SHELFIE", "Failed to open service");
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
