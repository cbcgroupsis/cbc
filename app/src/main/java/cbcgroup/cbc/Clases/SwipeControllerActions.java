package cbcgroup.cbc.Clases;

import android.util.Log;

public abstract class SwipeControllerActions {

    public void onLeftClicked(int position)
    {

        Log.w("DEBUGSWIPE","left->"+position);
    }

    public void onRightClicked(int position)
    {
        Log.w("DEBUGSWIPE","right->"+position);
    }

}