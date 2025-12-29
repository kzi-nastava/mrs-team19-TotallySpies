package com.ftn.mobile.presentation.view;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ftn.mobile.R;
import com.ftn.mobile.data.model.CarInfo;
import com.ftn.mobile.data.model.Driver;
import com.ftn.mobile.data.model.User;
import com.ftn.mobile.presentation.adapter.DriverAdapter;
import com.ftn.mobile.presentation.adapter.UserAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdminListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin_list);

        RecyclerView rv = findViewById(R.id.recyclerViewAdmin);
        rv.setLayoutManager(new LinearLayoutManager(this));

        TextView title = findViewById(R.id.tvMangeTitle);



        String type = getIntent().getStringExtra("TYPE");

        if ("USERS".equals(type)) {
            title.setText("Manage Users");
            ArrayList<User> users = new ArrayList<>();
            users.add(new User("jovana@gmail.com", "Jovana", "Aleksic", "Ul. Valentina Vodnika 10, Novi Sad", "","+381 65 123 1234", "12345678"));
            users.add(new User("marko@gmail.com", "Marko", "Markovic", "Zeleznicka 14, Novi Sad", "","+381 65 123 1234", "12345678"));
            users.add(new User("jelena@gmail.com", "Jelena", "Markovic", "Strazilovska 14, Novi Sad", "","+381 65 123 1234", "12345678"));
            users.add(new User("nikola@gmail.com", "Nikola", "Nikolic", "Bul. Oslobodjenja 14, Novi Sad", "","+381 65 123 1234", "12345678"));
            UserAdapter adapter = new UserAdapter(this, users);
            rv.setAdapter(adapter);
        } else if ("DRIVERS".equals(type)) {
            title.setText("Manage Drivers");
            ArrayList<Driver> drivers = new ArrayList<>();
            drivers.add(new Driver(new User("jovana@gmail.com", "Jovana", "Aleksic", "Ul. Valentina Vodnika 10, Novi Sad", "","+381 65 123 1234", "12345678"), new CarInfo("Model1", "Standard","NS 123 AA", "4", false, false)));
            drivers.add(new Driver(new User("marko@gmail.com", "Marko", "Markovic", "Zeleznicka 14, Novi Sad", "","+381 65 123 1234", "12345678"), new CarInfo("Model3", "Standard","NS 456 AA", "4", false, true)));
            drivers.add(new Driver(new User("jelena@gmail.com", "Jovan", "Jovic", "Ul. Valentina Vodnika 10, Novi Sad", "","+381 65 123 1234", "12345678"), new CarInfo("Model10", "Standard","NS 123 AA", "4", false, false)));
            drivers.add(new Driver(new User("nikola@gmail.com", "Nikola", "Nikolic", "Bul. Oslobodjenja 14, Novi Sad", "","+381 65 123 1234", "12345678"), new CarInfo("Model5", "Standard","NS 789 AA", "4", true, true)));
            DriverAdapter adapter = new DriverAdapter(this, drivers);
            rv.setAdapter(adapter);
        }

        findViewById(R.id.btnBackAdmin).setOnClickListener(v -> finish());
    }
}

