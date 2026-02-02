package com.ftn.mobile.data.local;
import android.content.Context;
public class TokenStorage {
    private static final String PREF = "auth";
    private static final String KEY = "jwt";

    public static void save(Context ctx, String token) {
        ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY, token)
                .apply();
    }

    public static String get(Context ctx) {
        return ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)
                .getString(KEY, null);
    }

    public static void clear(Context ctx) {
        ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)
                .edit()
                .remove(KEY)
                .apply();
    }
}
