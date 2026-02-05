package com.ftn.mobile.utils;
import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import com.ftn.mobile.App;

public class DialogBox {
    public static void showDialog(Context context, String title, String message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
}
