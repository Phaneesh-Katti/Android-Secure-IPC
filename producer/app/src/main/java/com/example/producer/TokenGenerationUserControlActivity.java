package com.example.producer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
//import androidx.appcompat.app.AppCompatActivity;

public class TokenGenerationUserControlActivity extends Activity {
    public static final String ACTION_NEW_TOKEN = "com.example.producer.NEW_TOKEN";

    private Button btnStartTokenGeneration;
    private Button btnStopTokenGeneration;
    private TextView tvTokens;
    private TokenReceiver tokenReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token_generation_user_control);

        btnStartTokenGeneration = findViewById(R.id.btnStartTokenGeneration);
        btnStopTokenGeneration = findViewById(R.id.btnStopTokenGeneration);
        tvTokens = findViewById(R.id.tvTokens);

        btnStartTokenGeneration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTokenGenerationService();
            }
        });

        btnStopTokenGeneration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTokenGenerationService();
            }
        });

        // Initialize the token receiver
        tokenReceiver = new TokenReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register the receiver with the NOT_EXPORTED flag since this is for internal use only
        IntentFilter filter = new IntentFilter(ACTION_NEW_TOKEN);
        registerReceiver(tokenReceiver, filter, RECEIVER_NOT_EXPORTED);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister receiver
        unregisterReceiver(tokenReceiver);
    }

    private void startTokenGenerationService() {
        Intent serviceIntent = new Intent(this, GenerateTokensService.class);
        startService(serviceIntent);
        btnStartTokenGeneration.setEnabled(false);
        btnStopTokenGeneration.setEnabled(true);
    }

    private void stopTokenGenerationService() {
        Intent serviceIntent = new Intent(this, GenerateTokensService.class);
        stopService(serviceIntent);
        btnStartTokenGeneration.setEnabled(true);
        btnStopTokenGeneration.setEnabled(false);
    }

    // BroadcastReceiver to display tokens in the UI
    private class TokenReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_NEW_TOKEN.equals(intent.getAction())) {
                String token = intent.getStringExtra("token");
                if (token != null) {
                    // Prepend the new token to the TextView
                    tvTokens.setText(token + "\n\n" + tvTokens.getText());
                }
            }
        }
    }
}