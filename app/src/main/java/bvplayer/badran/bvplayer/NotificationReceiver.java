package bvplayer.badran.bvplayer;

import static bvplayer.badran.bvplayer.ApplicationNotification.ACTION_NEXT;
import static bvplayer.badran.bvplayer.ApplicationNotification.ACTION_PLAY;
import static bvplayer.badran.bvplayer.ApplicationNotification.ACTION_PREVIOUS;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String actionName = intent.getAction();
        Intent intentService = new Intent(context, VideoService.class);
        if (actionName != null) {
            switch (actionName){
                case ACTION_PREVIOUS:
                intentService.putExtra("ActionName", "previous");
                context.startService(intentService);
                break;

                case ACTION_PLAY:
                    intentService.putExtra("ActionName", "playPause");
                    context.startService(intentService);
                    break;

                case ACTION_NEXT:
                    intentService.putExtra("ActionName", "next");
                    context.startService(intentService);
                    break;
            }
        }
    }
}
