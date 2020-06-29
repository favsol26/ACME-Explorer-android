package us.master.acme_explorer.common;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.single.PermissionListener;

import us.master.acme_explorer.R;

import static androidx.core.app.ActivityCompat.requestPermissions;
import static androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale;
import static androidx.core.content.ContextCompat.startActivity;
import static us.master.acme_explorer.common.Util.locationEnabled;

public class PermissionsService {
    private Activity activity;
    private Context context;
    private String[] permissions;
    private int[] requestCode;
    private int[] explanation;
    private int[] permissionDeniedMsg;

    public PermissionsService(Activity activity, Context context,
                              String[] permissions,
                              @Nullable int[] requestCode,
                              @Nullable int[] explanation,
                              @Nullable int[] permissionDeniedMsg) {
        this.activity = activity;
        this.context = context;
        this.permissions = permissions;
        this.requestCode = requestCode;
        this.explanation = explanation;
        this.permissionDeniedMsg = permissionDeniedMsg;
    }

    public void checkPermissions(View view, @Nullable functionInterface callback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (int i = 0; i < permissions.length; i++)
                if (ActivityCompat.checkSelfPermission(context, permissions[i]) !=
                        PackageManager.PERMISSION_GRANTED) {
                    int fi = i;
                    if (shouldShowRequestPermissionRationale(activity, permissions[i])) {
                        if (explanation != null) {
                            View.OnClickListener action =
                                    v -> requestPermissions(activity, permissions, requestCode[fi]);
                            Snackbar.make(view, explanation[i], Snackbar.LENGTH_LONG)
                                    .setAction(R.string.allow_permission, action)
                                    .show();
                        }
                    } else requestPermissions(activity, permissions, requestCode[fi]);
                } else if (callback != null) callback.function();
        }
    }

    public void checkPermission(View view, functionInterface callback) {
        PermissionRequestErrorListener errorListener =
                error -> {
                    Toast.makeText(activity, R.string.to_back, Toast.LENGTH_SHORT).show();
                    Log.e("Dexter", "There was an error: " + error.toString());
                };

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                callback.function();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                if (!response.isPermanentlyDenied()) {
                    Snackbar.make(view, permissionDeniedMsg[0], Snackbar.LENGTH_LONG).show();
                } else {
                    DialogInterface.OnClickListener negBtn = (dialog, which) -> {
                    }, posBtn = (dialog, which) -> {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.fromParts("package", context.getPackageName(),
                                null));
                        startActivity(context, intent, null);
                    };
                    Util.showDialogMessage(context,
                            R.string.dialog_permission_tittle, permissionDeniedMsg[1],
                            R.string.open_setting, android.R.string.no, posBtn, negBtn);
                }
                locationEnabled = false;
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission,
                                                           PermissionToken token) {
                View.OnClickListener action = v -> token.continuePermissionRequest();
                Snackbar.make(view, explanation[0], Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.allow_permission, action)
                        .show();
            }
        };

        Dexter.withContext(this.context)
                .withPermission(permissions[0])
                .withListener(permissionListener)
                .withErrorListener(errorListener)
                .check();
    }

    public interface functionInterface {
        void function();
    }
}
