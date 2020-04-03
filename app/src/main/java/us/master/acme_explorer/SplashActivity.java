package us.master.acme_explorer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class SplashActivity extends AppCompatActivity {
    final String path = "https://media1.tenor.com/images/abf1ea96e2430d1f315a4142ed630193/tenor.gif";
    final int splashDuration = 3500;
    ImageView imageViewSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        imageViewSplash = findViewById(R.id.image_view_splash);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        Toast.makeText(this, width + " " + height, Toast.LENGTH_SHORT).show();

        Glide.with(this)
                .load(path)//R.drawable.tenor2
                .override(width, height)
                .placeholder(android.R.drawable.ic_menu_myplaces)
                .error(android.R.drawable.stat_notify_error)
                .into(imageViewSplash);

        new Handler().postDelayed(() -> startActivity(
                new Intent(SplashActivity.this, MainActivity.class)
        ), splashDuration);
    }
}
