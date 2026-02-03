package com.ftn.mobile.data.remote;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class Utils {
    //to convert RequestBody to string in multipart requests like registration
    public static RequestBody textPart(String value) {
        return RequestBody.create(
                value,
                MediaType.parse("text/plain")
        );
    }
    //to convert uri to MultipartBody.Part
    public static MultipartBody.Part uriToPart(Context context, Uri uri, String partName) throws Exception {
        ContentResolver resolver = context.getContentResolver();

        // filename
        String fileName = "profile.jpg";
        Cursor cursor = resolver.query(uri, null, null, null, null);
        if (cursor != null) {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            if (cursor.moveToFirst() && nameIndex >= 0) {
                fileName = cursor.getString(nameIndex);
            }
            cursor.close();
        }

        // copy content into cache file
        File file = new File(context.getCacheDir(), fileName);
        try (InputStream in = resolver.openInputStream(uri);
             FileOutputStream out = new FileOutputStream(file)) {
            byte[] buf = new byte[4096];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        }

        RequestBody fileBody = RequestBody.create(file, MediaType.parse("image/*"));
        return MultipartBody.Part.createFormData(partName, file.getName(), fileBody);
    }

    //to extract error message from http response
    public static String extractErrorMessage(Response<?> response) {
        try {
            ResponseBody errorBody = response.errorBody();
            if (errorBody == null) return "Unknown error";

            String raw = errorBody.string();
            if (raw == null || raw.isEmpty()) return "Unknown error";

            try {
                JSONObject obj = new JSONObject(raw);
                if (obj.has("message")) {
                    String msg = obj.getString("message");
                    if (msg != null && !msg.isEmpty()) return msg;
                }
                if (obj.has("error")) {
                    return obj.getString("error");
                }
            } catch (Exception ignoreJson) {
            }

            return raw;

        } catch (Exception e) {
            return "Error reading server message";
        }
    }

}
