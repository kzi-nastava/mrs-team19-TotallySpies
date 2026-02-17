package com.ftn.mobile.presentation.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.ftn.mobile.R;
import com.ftn.mobile.data.local.TokenStorage;
import com.ftn.mobile.data.local.UserRoleManger;
import com.ftn.mobile.presentation.fragments.DriverHistoryFragment;
import com.ftn.mobile.presentation.fragments.DriverScheduledRidesFragment;
import com.ftn.mobile.presentation.fragments.ReportFragment;
import com.ftn.mobile.presentation.fragments.RideOrderingFragment;
import com.ftn.mobile.presentation.fragments.DriverRegistrationFragment;
import com.ftn.mobile.presentation.fragments.PricingFragment;
import com.ftn.mobile.presentation.fragments.profile.ProfileFragment;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout), (v, insets) -> {
            v.setPadding(insets.getInsets(WindowInsetsCompat.Type.systemBars()).left,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).right,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom);
            return insets;
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.nav_profile).setVisible(false);

        UserRoleManger.getRoleLiveData().observe(this, role -> {
            boolean isLoggedIn = (role != null);

            MenuItem profileItem = menu.findItem(R.id.nav_profile);
            if (profileItem != null) {
                profileItem.setVisible(isLoggedIn);
            }

            MenuItem orderRideItem = menu.findItem(R.id.nav_ride_ordering);
            if (orderRideItem != null) {
                boolean isPassenger = "ROLE_PASSENGER".equals(role);
                orderRideItem.setVisible(isPassenger);
            }

            MenuItem historyItem = menu.findItem(R.id.nav_history);
            if (historyItem != null) {
                boolean isDriver = "ROLE_DRIVER".equals(role);
                historyItem.setVisible(isDriver);

            }
            MenuItem scheduledItem = menu.findItem(R.id.nav_scheduled);
            if (scheduledItem != null) {
                boolean isDriver = "ROLE_DRIVER".equals(role);
                scheduledItem.setVisible(isDriver);
            }

            MenuItem registerDriverItem = menu.findItem(R.id.nav_register_driver);
            if(profileItem != null){
                boolean isAdmin = "ROLE_ADMIN".equals(role);
                registerDriverItem.setVisible(isAdmin);
            }

            MenuItem pricingItem = menu.findItem(R.id.nav_pricing);
            if (pricingItem != null) {
                boolean isAdmin = "ROLE_ADMIN".equals(role);
                pricingItem.setVisible(isAdmin);
            }

            MenuItem reportItem = menu.findItem(R.id.nav_report);
            if (reportItem != null){
                reportItem.setVisible(isLoggedIn);
            }
        });

        String token = TokenStorage.get(this);
        if (token != null) {
            if (!UserRoleManger.isTokenExpired(token)) {
                UserRoleManger.updateRole(token);
            } else {
                TokenStorage.clear(this);
                UserRoleManger.updateRole(null);
            }
        } else{
            UserRoleManger.updateRole(null);
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_history) {
                openFragment(new DriverHistoryFragment(), "Ride History");
            } else if (id == R.id.nav_home) {
                removeCurrentFragment();
                getSupportActionBar().setTitle("SmartRide");
            } else if (id == R.id.nav_profile){
                openFragment(new ProfileFragment(), "Profile");
            } else if (id == R.id.nav_register) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
            else if (id == R.id.nav_login) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_ride_ordering) {
                openFragment(new RideOrderingFragment(), "Ride ordering");
            } else if (id == R.id.nav_register_driver){
                openFragment(new DriverRegistrationFragment(), "Driver registration");
            }
            else if (id == R.id.nav_pricing) {
                openFragment(new PricingFragment(), "Pricing");
            }
            else if (id == R.id.nav_scheduled) {
                openFragment(new DriverScheduledRidesFragment(), "My Rides");
            }
            else if (id == R.id.nav_report) {
                openFragment(new ReportFragment(), "Reports");
            }
            drawerLayout.closeDrawers();
            return true;
        });
    }
    private void openFragment(Fragment fragment, String title) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    private void removeCurrentFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            getSupportFragmentManager().popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }
}