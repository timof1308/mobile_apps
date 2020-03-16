package de.vms.vmsapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final String TOOLBAR_TITLE_KEY = "toolbarTitleKey";
    private DrawerLayout drawer;
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //source: https://www.youtube.com/watch?v=bjYstsO1PgI
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Change email in navHeader

        SharedPreferences sp1 = this.getSharedPreferences("Login", MODE_PRIVATE);

        String email = sp1.getString("email", null);
        //String token = sp1.getString("token", null);

        TextView navEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.NavMailId);
        navEmail.setText(email);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close); //connect the drawer and the toolbar
        drawer.addDrawerListener(toggle);
        toggle.syncState(); //for hamburger menu to rotate

        if (savedInstanceState == null) {
            //Intent homeFragment = new Intent(MainActivity.this, HomeFragment.class); //if HomeFragment is AppCompat
            //startActivity(homeFragment);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit(); //Activity starts with this fragment
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mToolbar.setTitle(savedInstanceState.getString(TOOLBAR_TITLE_KEY));
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                mToolbar.setTitle(R.string.app_name);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                //Intent homeFragment = new Intent(MainActivity.this, HomeFragment.class); //if HomeFragment is AppCompat
                //startActivity(homeFragment);
                break;
            case R.id.nav_meetings:
                mToolbar.setTitle(R.string.meetings);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MeetingsFragment()).commit();
                break;
            case R.id.nav_dashboard:
                mToolbar.setTitle(R.string.dashboard);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DashboardFragment()).commit();
                break;
            case R.id.nav_profile:
                mToolbar.setTitle(R.string.profile);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                break;
            case R.id.nav_rooms:
                mToolbar.setTitle(R.string.rooms);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RoomsFragment()).commit();
                break;
            case R.id.nav_companies:
                mToolbar.setTitle(R.string.companies);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CompaniesFragment()).commit();
                break;
            case R.id.nav_logout:
                mToolbar.setTitle(R.string.logout);
                Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);

                // clear login token
                SharedPreferences shared_pref = getSharedPreferences("app", MODE_PRIVATE);
                SharedPreferences.Editor shared_pref_edit = shared_pref.edit();
                shared_pref_edit.putString("token", null);
                shared_pref_edit.apply();

                startActivity(intent);
                break;
//            case R.id.nav_share:
//                Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.nav_send:
//                Toast.makeText(this, "Send", Toast.LENGTH_SHORT).show();
//                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    //use this method to close the drawer
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(TOOLBAR_TITLE_KEY, (String) mToolbar.getTitle());
        super.onSaveInstanceState(outState);
    }
}
