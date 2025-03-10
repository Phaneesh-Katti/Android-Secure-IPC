package com.example.consumer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
//import androidx.appcompat.app.AppCompatActivity;

public class DisplayTokensActivity extends Activity {
    private static final String PROVIDER_AUTHORITY = "com.example.producer.tokenprovider";
    private static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_AUTHORITY + "/tokens");

    private Button btnRefreshTokens;
    private TextView tvStatusMessage;
    private TextView tvTokenList;
    private TokensReadyReceiver tokensReadyReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_tokens);

        btnRefreshTokens = findViewById(R.id.btnRefreshTokens);
        tvStatusMessage = findViewById(R.id.tvStatusMessage);
        tvTokenList = findViewById(R.id.tvTokenList);

        btnRefreshTokens.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchTokens();
            }
        });

        // Initialize the tokens ready receiver
        tokensReadyReceiver = new TokensReadyReceiver();

        // Initial fetch
        fetchTokens();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register the receiver
        IntentFilter filter = new IntentFilter(TokensReadyBroadcastReceiver.ACTION_TOKENS_READY);
        registerReceiver(tokensReadyReceiver, filter, RECEIVER_NOT_EXPORTED);
        Log.d("DisplayTokensActivity", "Registered local broadcast receiver");
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister receiver
        unregisterReceiver(tokensReadyReceiver);
        Log.d("DisplayTokensActivity", "Unregistered local broadcast receiver");
    }

    private void fetchTokens() {
        try {
            // Query the content provider
            Cursor cursor = getContentResolver().query(CONTENT_URI, null, null, null, null);

            if (cursor != null) {
                StringBuilder tokensBuilder = new StringBuilder();
                int count = 0;

                // Get column indices
                int timestampIndex = cursor.getColumnIndex("timestamp");
                int latitudeIndex = cursor.getColumnIndex("latitude");
                int longitudeIndex = cursor.getColumnIndex("longitude");

                // Process the tokens
                while (cursor.moveToNext()) {
                    String timestamp = cursor.getString(timestampIndex);
                    double latitude = cursor.getDouble(latitudeIndex);
                    double longitude = cursor.getDouble(longitudeIndex);

                    tokensBuilder.append("[")
                            .append(timestamp).append(", ")
                            .append(latitude).append(", ")
                            .append(longitude).append("]\n\n");
                    count++;
                }

                cursor.close();

                if (count > 0) {
                    tvTokenList.setText(tokensBuilder.toString());
                    tvStatusMessage.setText("Displaying " + count + " tokens");
                } else {
                    tvTokenList.setText("No tokens available");
                    tvStatusMessage.setText("No tokens found in the producer app");
                }
            } else {
                tvTokenList.setText("Error: Unable to query tokens");
                tvStatusMessage.setText("Failed to connect to producer app");
            }
        } catch (Exception e) {
            tvTokenList.setText("Error: " + e.getMessage());
            tvStatusMessage.setText("Failed to retrieve tokens");
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // BroadcastReceiver to handle notifications from the TokensReadyBroadcastReceiver
    private class TokensReadyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            Log.d("DisplayTokensActivity", "Received local broadcast: " + intent.getAction());
            Log.d("DisplayTokensActivity", "Message: " + (message != null ? message : "null"));
            tvStatusMessage.setText(message != null ? message : "New tokens available");

            // Automatically refresh tokens
            Log.d("DisplayTokensActivity", "Fetching tokens after broadcast...");
            fetchTokens();
        }
    }
}