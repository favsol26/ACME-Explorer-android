package us.master.acme_explorer.common;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

import us.master.acme_explorer.R;

import static androidx.core.app.ActivityCompat.requestPermissions;
import static androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale;

public class PermissionsService {
    private Context context;
    private String[] permissions;
    private Activity activity;
    private int[] requestCode;
    private int[] explanation;

    public PermissionsService(Activity activity,
                              String[] permissions, int[] requestCode, int[] explanation) {
        this.context = activity.getApplicationContext();
        this.activity = activity;
        this.permissions = permissions;
        this.requestCode = requestCode;
        this.explanation = explanation;
    }

    public void checkPermissions(View view, functionInterface callback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (int i = 0; i < permissions.length; i++)
                if (ContextCompat.checkSelfPermission(context, permissions[i]) !=
                        PackageManager.PERMISSION_GRANTED) {
                    int fi = i;
                    if (shouldShowRequestPermissionRationale(activity, permissions[i])) {
                        View.OnClickListener action =
                                v -> requestPermissions(activity, permissions, requestCode[fi]);
                        Snackbar.make(view, explanation[i], Snackbar.LENGTH_LONG)
                                .setAction(R.string.allow_permission, action)
                                .show();
                    } else requestPermissions(activity, permissions, requestCode[fi]);
                } else {
                    callback.function();
                }
        }
    }

    public interface functionInterface {
        void function();
    }
}
