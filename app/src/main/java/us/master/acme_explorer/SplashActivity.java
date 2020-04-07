package us.master.acme_explorer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import us.master.acme_explorer.common.Constants;
import us.master.acme_explorer.entity.Trip;

public class SplashActivity extends AppCompatActivity {
    final int splashDuration = 3500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Constants.tripList = Trip.generateTrips(8);
        new Handler().postDelayed(() -> {
            startActivity(
                    new Intent(SplashActivity.this, MainActivity.class)
            );
            finish();
        }, splashDuration);
    }
}
