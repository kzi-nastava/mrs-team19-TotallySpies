package com.ftn.mobile.presentation.view;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import androidx.appcompat.app.AppCompatActivity;

import com.ftn.mobile.R;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (findViewById(R.id.btnManageUsers) != null) {
            findViewById(R.id.btnManageUsers).setOnClickListener(v -> {
                Intent intent = new Intent(this, AdminListActivity.class);
                intent.putExtra("TYPE", "USERS");
                startActivity(intent);
            });
        }

        if (findViewById(R.id.btnManageDrivers) != null) {
            findViewById(R.id.btnManageDrivers).setOnClickListener(v -> {
                Intent intent = new Intent(this, AdminListActivity.class);
                intent.putExtra("TYPE", "DRIVERS");
                startActivity(intent);
            });
        }

        if (findViewById(R.id.btnCarInfo) != null) {
            findViewById(R.id.btnCarInfo).setOnClickListener(v -> {
                showCarInfoDialog();
            });
        }

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void showCarInfoDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_car_info);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        dialog.findViewById(R.id.btnCloseDialog).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}