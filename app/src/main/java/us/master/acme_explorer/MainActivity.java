package us.master.acme_explorer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import us.master.acme_explorer.common.Constants;
import us.master.acme_explorer.common.PermissionsService;

import static us.master.acme_explorer.common.Util.checkInstance;
import static us.master.acme_explorer.common.Util.currentUser;
import static us.master.acme_explorer.common.Util.googleSignInOptions;
import static us.master.acme_explorer.common.Util.locationEnabled;
import static us.master.acme_explorer.common.Util.mAuth;
import static us.master.acme_explorer.ui.trips.TripsFragment.GALLERY_PERMISSION_REQUEST;
import static us.master.acme_explorer.ui.trips.TripsFragment.PICK_PHOTO_CODE;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PERMISSION_REQUEST_CODE_LOCATION = 0x134;
    private AppBarConfiguration mAppBarConfiguration;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration =
                new AppBarConfiguration.Builder(
                        R.id.nav_main_menu,
                        R.id.nav_available_trips,
                        R.id.nav_selected_trips
                ).setOpenableLayout(drawer)
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
//            Log.e(TAG, "updateUI: " + currentUser.getEmail());
            checkInstance();
            assert currentUser != null;
            if (mAuth == null) mAuth = FirebaseAuth.getInstance();
            if (currentUser == null) {
                currentUser = mAuth.getCurrentUser();
            }
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
        requestAllPermissions();
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

    public void findMe(MenuItem item) {
        if (locationEnabled) navigate();
        else requestLocation();
    }

    private void locationEnabled() {
        locationEnabled = true;
        navigate();
    }

    private void navigate() {
        Bundle data = new Bundle();
        data.putString(Constants.maps, "");
        Navigation.findNavController(this, R.id.nav_host_fragment)
                .navigate(R.id.nav_maps, data);
    }

    public void close(MenuItem item) {
        checkInstance();
        // Google sign out
        GoogleSignInClient gsc = GoogleSignIn.getClient(this, googleSignInOptions);
        gsc.signOut().addOnCompleteListener(this, task -> {
            mAuth.signOut();
            startActivity(new Intent(this, UserMainActivity.class));
            finish();
        });
    }

    private void requestAllPermissions() {
        PermissionsService permissionsService = new PermissionsService(
                this,
                getApplicationContext(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                new int[]{0x12, 0x20},
                null, null);

        permissionsService.checkPermissions(toolbar, null);
    }

    public void requestLocation() {
        PermissionsService permissionsService = new PermissionsService(
                this,
                this.getApplicationContext(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION},
                new int[]{PERMISSION_REQUEST_CODE_LOCATION, PERMISSION_REQUEST_CODE_LOCATION},
                new int[]{R.string.location_rationale_1,
                        R.string.location_rationale_1},
                null);
        permissionsService.checkPermissions(toolbar, this::locationEnabled);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE_LOCATION: {
                for (int i = 0; i < permissions.length; i++)
                    if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION))
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) locationEnabled();
                        else {
                            Snackbar.make(toolbar,
                                    R.string.location_permission_no_granted_1,
                                    Snackbar.LENGTH_LONG).show();
                            locationEnabled = false;
                        }
            }
            break;
            case GALLERY_PERMISSION_REQUEST: {
                for (int i = 0; i < permissions.length; i++)
                    if (permissions[i].equals(Manifest.permission.READ_EXTERNAL_STORAGE))
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto, PICK_PHOTO_CODE);
                        } else Snackbar.make(toolbar,
                                R.string.gallery_permission_no_granted,
                                Snackbar.LENGTH_LONG).show();
            }
            break;
        }
    }
}
