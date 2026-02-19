package com.ftn.mobile.data.local;

import android.util.Base64;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.json.JSONObject;

public class UserRoleManger {
    private static final MutableLiveData<String> roleLiveData = new MutableLiveData<>();

    public static void updateRole(String jwtToken) {
        String role = parseRoleFromToken(jwtToken);
        roleLiveData.setValue(role);
        Log.d("USER_ROLE", "Trenutna rola korisnika: " + role);
    }

    /** LiveData za pretplate (UI) */
    public static LiveData<String> getRoleLiveData() {
        return roleLiveData;
    }

    /** Direktno dobijanje trenutne rolu (nije LiveData) */
    public static String getCurrentRole() {
        return roleLiveData.getValue();
    }

    /** Parsira JWT token i vraća rolu */
    private static String parseRoleFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return null;

            String payload = parts[1];
            byte[] decodedBytes = Base64.decode(payload, Base64.URL_SAFE);
            String payloadJson = new String(decodedBytes);

            JSONObject json = new JSONObject(payloadJson);
            return json.getString("role"); // ROLE_DRIVER / ROLE_ADMIN / ROLE_PASSENGER
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isTokenExpired(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return true;

            String payload = parts[1];
            byte[] decodedBytes = Base64.decode(payload, Base64.URL_SAFE);
            String payloadJson = new String(decodedBytes);

            JSONObject json = new JSONObject(payloadJson);
            long exp = json.getLong("exp"); // JWT 'exp' je u sekundama
            long currentTime = System.currentTimeMillis() / 1000;

            return exp < currentTime;
        } catch (Exception e) {
            return true; // Ako ne može da parsira, smatramo da je istekao
        }
    }
    public static Long getUserIdFromToken(String token) {
        try {
            String[] chunks = token.split("\\.");
            if (chunks.length >= 2) {
                String payload = new String(android.util.Base64.decode(chunks[1], android.util.Base64.URL_SAFE));
                JSONObject json = new JSONObject(payload);
                return json.getLong("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
