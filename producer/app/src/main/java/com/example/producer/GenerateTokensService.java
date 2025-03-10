package com.example.producer;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class GenerateTokensService extends Service {
    private static final String TAG = "GenerateTokensService";
    private static final long TOKEN_GENERATION_INTERVAL = 15000; // 15 seconds
    private static final String BROADCAST_ACTION = "com.example.producer.NEW_TOKENS";
    private static final int BROADCAST_THRESHOLD = 4;

    private Handler handler;
    private AtomicBoolean isRunning = new AtomicBoolean(false);
    private AtomicInteger tokenCount = new AtomicInteger(0);
    private TokenDatabaseHelper dbHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler(Looper.getMainLooper());
        dbHelper = new TokenDatabaseHelper(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isRunning.compareAndSet(false, true)) {
            handler.post(tokenGenerationRunnable);
            Log.d(TAG, "Token generation service started");
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        isRunning.set(false);
        handler.removeCallbacks(tokenGenerationRunnable);
        Log.d(TAG, "Token generation service stopped");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private final Runnable tokenGenerationRunnable = new Runnable() {
        @Override
        public void run() {
            if (isRunning.get()) {
                generateToken();

                // Schedule next token generation
                handler.postDelayed(this, TOKEN_GENERATION_INTERVAL);
            }
        }
    };

    private void generateToken() {
        // Get current timestamp
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:ss", Locale.getDefault());
        String timestamp = sdf.format(new Date());

        // For the assignment, we'll simulate location data (normally you'd use LocationManager)
        // Using random variations around coordinates in the assignment
        double baseLatitude = 28.543680;
        double baseLongitude = 77.198692;

        // Add small random variation to make it realistic
        Random random = new Random();
        double latitude = baseLatitude + (random.nextDouble() - 0.5) * 0.01;
        double longitude = baseLongitude + (random.nextDouble() - 0.5) * 0.01;

        // Format as [timestamp, latitude, longitude]
        String token = "[" + timestamp + ", " + latitude + ", " + longitude + "]";

        // Save token to database
        dbHelper.insertToken(timestamp, latitude, longitude);

        // Increment counter and check if broadcast should be sent
        int count = tokenCount.incrementAndGet();
        Log.d(TAG, "Generated token: " + token);

        // Send broadcast with new token
        Intent localIntent = new Intent(TokenGenerationUserControlActivity.ACTION_NEW_TOKEN);
        localIntent.putExtra("token", token);
        sendBroadcast(localIntent);

        // Check if we need to send broadcast to consumer app
        if (count % BROADCAST_THRESHOLD == 0) {
            Intent broadcastIntent = new Intent(BROADCAST_ACTION);
            broadcastIntent.setPackage("com.example.consumer");
            broadcastIntent.putExtra("message", "New tokens available");
            sendBroadcast(broadcastIntent);
            Log.d(TAG, "Broadcast sent to consumer app");
        }
    }
}