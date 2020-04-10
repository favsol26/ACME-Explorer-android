package us.master.acme_explorer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;

import us.master.acme_explorer.common.Constants;
import us.master.acme_explorer.entity.Trip;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = SplashActivity.class.getSimpleName();
    final int splashDuration = 3500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Date date = new Date();
        long n = date.getTime();
        Log.d(TAG, "onCreate: " + n);
        Constants.tripList = Trip.generateTrips(2 + n % 100);
        new Handler().postDelayed(() -> {
            startActivity(
                    new Intent(SplashActivity.this, MainActivity.class)
            );
            finish();
        }, splashDuration);
    }
}
