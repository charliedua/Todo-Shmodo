package com.example.todoapp;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MainApp extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private static final String TAG = "MainApp";
    ViewPager ViewPager_main;
    BottomNavigationView bottomNavigationView;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private FirebaseAuth mAuth;
    private TextView tv_nav_header_name;
    private long mBackPressed;
    private Toolbar toolbar;

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null)    // No one is signed in, bugfix
        {
            RedirectToLogin();
        }
    }

    private void RedirectToLogin()
    {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_app);

        tv_nav_header_name = findViewById(R.id.tv_nav_header_name);

        toolbar = findViewById( R.id.tb_main );

        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);

        toggle = new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );

        drawer.addDrawerListener(toggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();
        View hView = navigationView.getHeaderView(0);
        TextView nav_user = hView.findViewById(R.id.tv_nav_header_name);
        nav_user.setText(mAuth.getCurrentUser().getDisplayName());

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomBarNavigationItemSelectedListener());

        ViewPager_main = findViewById(R.id.ViewPager_main);

        CustomViewPagerAdaptor BottomBarAdapter = new CustomViewPagerAdaptor(
                getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        );

        BottomBarAdapter.addFragment(new FilesFragment());
        BottomBarAdapter.addFragment(new MainFragment());
        BottomBarAdapter.addFragment(new SettingsFragment());

        ViewPager_main.setAdapter(BottomBarAdapter);
        ViewPager_main.setCurrentItem(1); // Actual Fragment change
        bottomNavigationView.getMenu().getItem(1).setChecked(true); //Bar change

        ViewPager_main.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {
            }

            public void onPageScrollStateChanged(int state)
            {
            }

            public void onPageSelected(int position)
            {
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
        toolbar.setTitle("Current Lists");
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if ( toggle.onOptionsItemSelected( item ) )
        {
            return true;
        }

        return super.onOptionsItemSelected( item );
    }

    // Handles double back to exit.
    public void onBackPressed()
    {
        if ( mBackPressed + TIME_INTERVAL > System.currentTimeMillis() )
        {
            this.finishAffinity();
            return;
        }
        else
        {
            Toast.makeText(getBaseContext(), "Tap back button in order to exit", Toast.LENGTH_SHORT)
                    .show();
        }

        mBackPressed = System.currentTimeMillis();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.nav_item_add_files:
                ViewPager_main.setCurrentItem(0);
                bottomNavigationView.getMenu().getItem(0).setChecked(true);
                break;
            case R.id.nav_item_settings:
                ViewPager_main.setCurrentItem(1);
                bottomNavigationView.getMenu().getItem(1).setChecked(true);
                break;
            case R.id.nav_item_logout:
                new AlertDialog.Builder(this)
                        .setTitle("Title")
                        .setMessage("Do you really want to Logout?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(
                                android.R.string.yes,
                                new DialogInterface.OnClickListener()
                                {
                                    public void onClick(
                                            DialogInterface dialog,
                                            int whichButton
                                                       )
                                    {
                                        mAuth.signOut();
                                        MainApp.this.finish();
                                    }
                                }
                                          )
                        .setNegativeButton(android.R.string.no, null).show();
                Toast.makeText(this, "Successfully Logged out!", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    private class CustomViewPagerAdaptor extends FragmentPagerAdapter
    {
        List<Fragment> mFragments = new ArrayList<>();

        public CustomViewPagerAdaptor(
                @NonNull FragmentManager fm,
                int behavior)
        {
            super(fm, behavior);
        }

        public boolean addFragment(Fragment fragment)
        {
            if (fragment != null)
            {
                mFragments.add(fragment);
                return true;
            }
            return false;
        }

        @NonNull
        @Override
        public Fragment getItem(int position)
        {
            return mFragments.get(position);
        }

        @Override
        public int getCount()
        {
            return mFragments.size();
        }
    }

    private class BottomBarNavigationItemSelectedListener
            implements BottomNavigationView.OnNavigationItemSelectedListener
    {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
        {
            switch (menuItem.getItemId())
            {
                case R.id.navigation_files:
                    ViewPager_main.setCurrentItem(0);
                    toolbar.setTitle("List Groups");
                    bottomNavigationView.getMenu().getItem(0).setChecked(true);
                    break;

                case R.id.navigation_current:
                    ViewPager_main.setCurrentItem(1);
                    toolbar.setTitle("Current Lists");
                    bottomNavigationView.getMenu().getItem(1).setChecked(true);
                    break;

                case R.id.navigation_settings:
                    ViewPager_main.setCurrentItem(2);
                    toolbar.setTitle("Settings");
                    bottomNavigationView.getMenu().getItem(2).setChecked(true);
                    break;
            }
            return false;
        }
    }
}
