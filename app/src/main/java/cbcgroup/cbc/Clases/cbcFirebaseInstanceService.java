package cbcgroup.cbc.Clases;


import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class cbcFirebaseInstanceService extends FirebaseInstanceIdService
{
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String token= FirebaseInstanceId.getInstance().getToken();
        Log.w("TAG_TOKEN","token->"+token);
    }
}
