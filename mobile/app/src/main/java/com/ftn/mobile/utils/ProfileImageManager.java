package com.ftn.mobile.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ftn.mobile.BuildConfig;
import com.ftn.mobile.R;
import com.ftn.mobile.data.remote.ApiProvider;
import com.ftn.mobile.data.remote.Utils;
import com.google.android.material.imageview.ShapeableImageView;

import okhttp3.MultipartBody;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileImageManager {
    public static final int PICK_IMAGE_REQUEST = 101;
    public static final int REQUEST_STORAGE_PERMISSION = 102;

    private final Activity activity;
    private final ShapeableImageView imgUser;
    private final Runnable onUploadSuccess;

    public ProfileImageManager(Activity activity,
                               ShapeableImageView imgUser,
                               Runnable onUploadSuccess) {
        this.activity = activity;
        this.imgUser = imgUser;
        this.onUploadSuccess = onUploadSuccess;
    }

    // ---------- LOAD IMAGE ----------
    public void loadImage(String imageUrl) {
        String fullUrl;
        if (imageUrl == null || imageUrl.equals("null") || imageUrl.isEmpty()) {
            fullUrl = BuildConfig.BASE_URL + "api/v1/users/image/default-profile-image.jpg";
        } else {
            String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            fullUrl = BuildConfig.BASE_URL + "api/v1/users/image/" + filename;
        }

        Glide.with(activity)
                .load(fullUrl)
                .error(R.mipmap.page_mock)
                .into(imgUser);
    }

    // ---------- PICK ----------
    public void checkPermissionAndPick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (activity.checkSelfPermission(android.Manifest.permission.READ_MEDIA_IMAGES)
                    == PackageManager.PERMISSION_GRANTED) {
                openPicker();
            } else {
                activity.requestPermissions(
                        new String[]{android.Manifest.permission.READ_MEDIA_IMAGES},
                        REQUEST_STORAGE_PERMISSION);
            }
        } else {
            if (activity.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                openPicker();
            } else {
                activity.requestPermissions(
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_STORAGE_PERMISSION);
            }
        }
    }

    private void openPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        activity.startActivityForResult(
                Intent.createChooser(intent, "Select Picture"),
                PICK_IMAGE_REQUEST
        );
    }

    // ---------- HANDLE RESULT ----------
    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null) {
            upload(data.getData());
        }
    }

    // ---------- UPLOAD ----------
    private void upload(Uri uri) {
        try {
            MultipartBody.Part part =
                    Utils.uriToPart(activity, uri, "image");

            ApiProvider.user().uploadImage(part).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(activity, "Image updated!", Toast.LENGTH_SHORT).show();
                        onUploadSuccess.run();
                    } else {
                        Toast.makeText(activity, "Upload failed: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(activity, "Upload failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(activity, "Failed to read image", Toast.LENGTH_SHORT).show();
        }
    }
}
