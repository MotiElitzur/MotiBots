package moti.bots.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoStart extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction() != null &&
                intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){

            context.startService(new Intent(context, BotsService.class));
        }
    }
}