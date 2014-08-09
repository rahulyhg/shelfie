package nl.shelfiesupport.shelfie;

import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by RenéenEllen on 9-8-2014.
 */
public class VoteTask extends AsyncTask<String, Integer, String> {

    private final int amount;
    private final String id;

    public VoteTask(String id, int amount) {
        this.id = id;
        this.amount = amount;
    }

    private JSONObject buildQuery() throws JSONException {
        JSONObject query = new JSONObject();
        query.put("action", "vote");
        query.put("id", id);
        query.put("amount", amount);
        return query;
    }

    @Override
    protected String doInBackground(String... params) {
        String response = null;
        HttpURLConnection connection = null;
        InputStream is = null;
        BufferedReader reader = null;


        try {
            URL url = new URL(Remoting.SERVICE_URL);
            String sendData = buildQuery().toString();
            Log.d("SHELFIE", sendData);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept-Charset", "utf-8");
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            connection.setDoOutput(true);
            DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
            dos.write(sendData.getBytes(Charset.forName("UTF-8")));
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
        } catch (JSONException e) {
            Log.w("SHELFIE", "Failed generate JSON");
        } finally {
            if(reader != null) { try { reader.close(); } catch (IOException ignored) { /* ignore */ } }
            if(is != null) { try { is.close(); } catch (IOException ignored) { /* ignore */ } }
            if(connection != null) { connection.disconnect(); }
        }
        Log.d("SHELFIE", response);
        return response;
    }
}
