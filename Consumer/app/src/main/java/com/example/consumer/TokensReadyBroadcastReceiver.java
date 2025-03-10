package com.example.consumer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class TokensReadyBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "TokensReadyReceiver";
    public static final String ACTION_TOKENS_READY = "com.example.consumer.TOKENS_READY";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Received broadcast from producer app: " + intent.getAction());

        // Forward the broadcast to the DisplayTokensActivity
        Intent localIntent = new Intent(ACTION_TOKENS_READY);
        localIntent.putExtra("message", intent.getStringExtra("message"));
        context.sendBroadcast(localIntent);
        Log.d(TAG, "Forwarded broadcast locally");
    }
}