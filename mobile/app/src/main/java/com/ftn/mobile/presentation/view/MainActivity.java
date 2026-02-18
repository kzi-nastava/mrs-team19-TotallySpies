package com.ftn.mobile.presentation.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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
import com.ftn.mobile.data.remote.ApiProvider;
import com.ftn.mobile.data.remote.dto.RideTrackingDTO;
import com.ftn.mobile.presentation.fragments.DriverHistoryFragment;
import com.ftn.mobile.presentation.fragments.DriverScheduledRidesFragment;
import com.ftn.mobile.presentation.fragments.HomeFragment;
import com.ftn.mobile.presentation.fragments.ReportFragment;
import com.ftn.mobile.presentation.fragments.RideFormUnregisteredFragment;
import com.ftn.mobile.presentation.fragments.RideOrderingFragment;
import com.ftn.mobile.presentation.fragments.DriverRegistrationFragment;
import com.ftn.mobile.presentation.fragments.PricingFragment;
import com.ftn.mobile.presentation.fragments.RideTrackingFragment;
import com.ftn.mobile.presentation.fragments.profile.ProfileFragment;
import com.google.android.material.navigation.NavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

            MenuItem loginItem = menu.findItem(R.id.nav_login);
            if (loginItem != null) loginItem.setVisible(!isLoggedIn);

            MenuItem registerItem = menu.findItem(R.id.nav_register);
            if (registerItem != null) registerItem.setVisible(!isLoggedIn);

            MenuItem logoutItem = menu.findItem(R.id.nav_logout);
            if (logoutItem != null) logoutItem.setVisible(isLoggedIn);

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

            MenuItem trackingItem = menu.findItem(R.id.nav_tracking);
            if (trackingItem != null) {
                boolean isPassenger = "ROLE_PASSENGER".equals(role);
                trackingItem.setVisible(isPassenger);
            }

            MenuItem reportItem = menu.findItem(R.id.nav_report);
            if (reportItem != null){
                reportItem.setVisible(isLoggedIn);
            }

            if(!isLoggedIn){
                showRideFormUnregistered();
            }
            else{
                hideRideFormUnregistered();
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

            if (id == R.id.nav_home) {
                openFragment(new HomeFragment(), "SmartRide");
            } else if (id == R.id.nav_history) {
                openFragment(new DriverHistoryFragment(), "Ride History");
            } else if (id == R.id.nav_profile){
                openFragment(new ProfileFragment(), "Profile");
            } else if (id == R.id.nav_register) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
            else if (id == R.id.nav_login) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
            else if(id == R.id.nav_logout){
                TokenStorage.clear(MainActivity.this);
                UserRoleManger.updateRole(null);
                getSupportFragmentManager().popBackStack(
                        null,
                        androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
                );
                showRideFormUnregistered();
                Toast.makeText(MainActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
            }
            else if (id == R.id.nav_tracking) {
                ApiProvider.ride().getActiveRide().enqueue(new Callback<RideTrackingDTO>() {
                    @Override
                    public void onResponse(Call<RideTrackingDTO> call, Response<RideTrackingDTO> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Long activeRideId = response.body().getRideId();
                            openTrackingFragment(activeRideId);
                        } else {
                            Toast.makeText(MainActivity.this, "no active ride.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<RideTrackingDTO> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "server error.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else if (id == R.id.nav_ride_ordering) {
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
        if (savedInstanceState == null) {
            openFragment(new HomeFragment(), "SmartRide");
        }
    }
    private void  showRideFormUnregistered() {
        Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (current instanceof RideFormUnregisteredFragment) return;

        getSupportFragmentManager().popBackStack(null,
                androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new RideFormUnregisteredFragment())
                .commit();

        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Welcome to SmartRide!");
    }

    private void hideRideFormUnregistered() {
        Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (current instanceof RideFormUnregisteredFragment) {
            getSupportFragmentManager().beginTransaction()
                    .remove(current)
                    .commit();
            if (getSupportActionBar() != null) getSupportActionBar().setTitle("SmartRide");
        }
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

    private void openTrackingFragment(Long id) {
        RideTrackingFragment fragment = new RideTrackingFragment();
        Bundle args = new Bundle();
        args.putLong("rideId", id);
        fragment.setArguments(args);
        openFragment(fragment, "Ride Tracker");
    }
}