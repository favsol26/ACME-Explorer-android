package us.master.acme_explorer.common;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Date;

import us.master.acme_explorer.R;

public class FirebaseStorageService {
    private static final String TAG = FirebaseStorageService.class.getSimpleName();

    private String fileName;
    private StorageReference mStorageRef;

    public FirebaseStorageService(Context context) {
        this.mStorageRef = FirebaseStorage
                .getInstance().getReference(context.getString(R.string.app_name));
    }

    public void getImageUrl(ImageView photoImgVw, String folder, String path) {
        fileName = "IMG_" + new Date().getTime() + path.substring(path.lastIndexOf("."));
        Uri file = Uri.fromFile(new File(path));
        UploadTask uploadTask = this.mStorageRef.child(folder).child("images")
                .child(fileName).putFile(file);

        uploadTask.addOnFailureListener(e -> Log.e(TAG, "getImageUrl: " + e.getMessage()));
        uploadTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.i(TAG, "getImageUrl: image saved");
                this.mStorageRef.child(folder).child("images").child(fileName)
                        .getDownloadUrl().addOnCompleteListener(task1 -> {
                    String url = String.valueOf(task1.getResult());
                    photoImgVw.setContentDescription(url);
                    Glide.with(photoImgVw)
                            .load(url)
                            .placeholder(android.R.drawable.ic_menu_myplaces)
                            .into(photoImgVw);
                    Log.i(TAG, "getImageUrl: image url confirmed ->" + url);
                });
            }
        });
    }

    public void deleteImageUrl(String folder,
                               @Nullable String urlImage) throws IndexOutOfBoundsException {
        this.fileName = urlImage != null
                ? urlImage.substring(urlImage.lastIndexOf("IMG"), urlImage.lastIndexOf("?"))
                : this.fileName;

        Task<Void> task1 = this.mStorageRef
                .child(folder)
                .child("images")
                .child(this.fileName).delete();

        task1.addOnCompleteListener(command -> {
            if (command.isSuccessful()) {
                Log.d(TAG, "deleteImageUrl: image deleted");
            }
        });
        task1.addOnFailureListener(e -> Log.e(TAG, "deleteImageUrl: error " + e.getMessage()));
    }

}
