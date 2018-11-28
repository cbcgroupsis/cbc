package cbcgroup.cbc.Clases;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class cbcFirebaseMessaginService extends FirebaseMessagingService
{
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        super.onMessageReceived( remoteMessage );
        Log.w("TAG_NOTIFI","from:"+remoteMessage.getFrom().toString());
    }
}
