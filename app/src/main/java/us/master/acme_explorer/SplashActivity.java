package us.master.acme_explorer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import us.master.acme_explorer.common.Util;

import static us.master.acme_explorer.common.Util.currentUser;
import static us.master.acme_explorer.common.Util.googleSignInOptions;
import static us.master.acme_explorer.common.Util.mAuth;

public class SplashActivity extends AppCompatActivity {
    //    private static final String TAG = SplashActivity.class.getSimpleName();
    final int splashDuration = 3500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Util.checkInstance();
        currentUser = mAuth.getCurrentUser();

        googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        new Handler().postDelayed(() -> {
            startActivity(
                    new Intent(SplashActivity.this,
                            currentUser == null ? UserMainActivity.class : MainActivity.class)
            );
            finish();
        }, splashDuration);
    }
}
