package us.master.acme_explorer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import static us.master.acme_explorer.common.Util.checkInstance;
import static us.master.acme_explorer.common.Util.currentUser;
import static us.master.acme_explorer.common.Util.googleSignInOptions;
import static us.master.acme_explorer.common.Util.mAuth;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration =
                new AppBarConfiguration.Builder(
                        R.id.nav_main_menu,
                        R.id.nav_available_trips,
                        R.id.nav_selected_trips,
                        R.id.nav_profile
                ).setDrawerLayout(drawer)
                        .build();

        NavController navController = Navigation.findNavController(
                this,
                R.id.nav_host_fragment
        );
        NavigationUI.setupActionBarWithNavController(
                this,
                navController,
                mAppBarConfiguration
        );
        NavigationUI.setupWithNavController(navigationView, navController);

        updateUI(navigationView.getHeaderView(0));
    }

    private void updateUI(View navView) {
        ImageView mImViewProf = navView.findViewById(R.id.image_view_profile);
        TextView uName = navView.findViewById(R.id.txv_name_profile),
                uMail = navView.findViewById(R.id.txv_mail_profile);
        try {
            Log.e(TAG, "updateUI: " + currentUser.getEmail());
            checkInstance();
            if (mAuth == null) mAuth = FirebaseAuth.getInstance();
            if (currentUser == null) {
                currentUser = mAuth.getCurrentUser();
                assert currentUser != null;
                showUserData(mImViewProf, uName, uMail);
            } else
                showUserData(mImViewProf, uName, uMail);
        } catch (Exception e) {
            Log.e(TAG, "updateUI: " + e.getMessage());
            startActivity(new Intent(this, SplashActivity.class));
            finish();
        }

    }

    private void showUserData(ImageView mImViewProf, TextView uName, TextView uMail) {
        uMail.setText(currentUser.getEmail());
        uName.setText(currentUser.getDisplayName());
        Picasso.get()
                .load(currentUser.getPhotoUrl())
                .fit()
                .error(R.mipmap.ic_launcher_acme_explorer_round)
                .placeholder(android.R.drawable.ic_menu_myplaces)
                .into(mImViewProf);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(
                this,
                R.id.nav_host_fragment
        );
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void close(MenuItem item) {
        checkInstance();
        mAuth.signOut();
        // Google sign out
        GoogleSignInClient gsc = GoogleSignIn.getClient(this, googleSignInOptions);
        gsc.signOut().addOnCompleteListener(this, task -> logOut());
    }

    private void logOut() {
        startActivity(new Intent(this, UserMainActivity.class));
        finish();
    }
}
