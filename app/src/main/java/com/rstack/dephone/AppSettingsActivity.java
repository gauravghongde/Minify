package com.rstack.dephone;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.List;

public class AppSettingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager recyclerViewLayoutManager;
    Intent webintent;

    DrawerLayout drawer;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                drawerView.bringToFront();
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        recyclerView = findViewById(R.id.recycler_view);

        // Passing the column number 1 to show online one column in each row.
        recyclerViewLayoutManager = new GridLayoutManager(AppSettingsActivity.this, 1);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        adapter = new AppsAdapter(AppSettingsActivity.this, new ApkInfoExtractor(AppSettingsActivity.this).GetAllInstalledApkInfo());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        drawer.closeDrawer(GravityCompat.START);
        switch(id){
            case R.id.nav_home:
                Intent settingsPage = new Intent(AppSettingsActivity.this,MainActivity.class);
                startActivity(settingsPage);
                finish();
                break;
            case R.id.nav_feedback:
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"777gaurav.g7@gmail.com"});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Minify - Feedback");
                emailIntent.setType("text/plain");
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "\n");
                final PackageManager pm = AppSettingsActivity.this.getPackageManager();
                final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
                ResolveInfo best = null;
                for(final ResolveInfo info : matches)
                    if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
                        best = info;
                if (best != null)
                    emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
                AppSettingsActivity.this.startActivity(emailIntent);
                break;
            case R.id.nav_share:
                String textToSend;
                textToSend = "Hey There! I am using this awesome app called 'Minify'. " +
                        "It helps to reduce Smartphone usage, by providing App Usage Alerts and Usage Statistics. " +
                        "We can set Timers on every app and restrict our daily usage. You could save a lot of precious time " +
                        "and focus on essentials, Download it now! \n\nGet the App from - " +
                        "https://play.google.com/store/apps/details?id=com.rstack.dephone&hl=en";
                Intent intentShare = new Intent();
                intentShare.setAction(Intent.ACTION_SEND);
                intentShare.putExtra(Intent.EXTRA_TEXT, textToSend);
                intentShare.setType("text/plain");
                startActivity(Intent.createChooser(intentShare,"Share via - "));
                break;
            case R.id.nav_git:
                webintent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/gauravghongde/Minify"));
                startActivity(webintent);
                break;
            case R.id.nav_website:
                webintent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://gauravghongde.github.io/portfolio/"));
                startActivity(webintent);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent settingsPage = new Intent(AppSettingsActivity.this,MainActivity.class);
        startActivity(settingsPage);
        finish();
        super.onBackPressed();
    }
}
