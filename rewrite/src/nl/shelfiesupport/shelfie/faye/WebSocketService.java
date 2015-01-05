package nl.shelfiesupport.shelfie.faye;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import com.saulpower.fayeclient.FayeClient;
import nl.shelfiesupport.shelfie.Remoting;
import nl.shelfiesupport.shelfie.Tag;
import org.json.JSONObject;
import java.net.URI;

public class WebSocketService extends IntentService implements FayeClient.FayeListener {

    public final String TAG = Tag.SHELFIE_NET;
    public static boolean isUp = false;
    private String mChannel = null;
    FayeClient mClient;

    private static final class WebSocketHandler extends Handler {

    }

    public WebSocketService() {
        super("WebSocketService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(isUp) {
            Log.i(TAG, "service is already up");
            return;
        }
        isUp = true;

        mChannel = intent.getStringExtra("channel");
        if(mChannel == null) { return; }
        Log.i(TAG, "Starting Web Socket for channel: " + mChannel);

        String baseUrl = Remoting.SERVICE_URL + "/chans";

        URI uri = URI.create(baseUrl);

        mClient = new FayeClient(new WebSocketHandler(), uri, mChannel);
        mClient.setFayeListener(this);
        mClient.connectToServer(new JSONObject());

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void connectedToServer() {
        Log.i(TAG, "Connected to Server");
    }

    @Override
    public void disconnectedFromServer() {
        Log.i(TAG, "Disonnected to Server");
        isUp = false;
    }

    @Override
    public void subscribedToChannel(String subscription) {
        Log.i(TAG, String.format("Subscribed to channel %s on Faye", subscription));
    }

    @Override
    public void subscriptionFailedWithError(String error) {
        Log.i(TAG, String.format("Subscription failed with error: %s", error));
        isUp = false;
    }

    @Override
    public void messageReceived(JSONObject json) {
        Log.i(TAG, String.format("Received message %s", json.toString()));
    }
}