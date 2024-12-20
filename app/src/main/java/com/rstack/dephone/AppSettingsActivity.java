package com.rstack.dephone;


import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AppSettingsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager recyclerViewLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_settings);

        recyclerView = findViewById(R.id.recycler_view);

        // Passing the column number 1 to show online one column in each row.
        recyclerViewLayoutManager = new GridLayoutManager(AppSettingsActivity.this, 1);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        ApkInfoExtractor apkInfoExtractor = new ApkInfoExtractor(this);
        List<String> allInstalledAppPackageNames = apkInfoExtractor.getAllInstalledAppPackageNames();
        adapter = new AppsAdapter(this, allInstalledAppPackageNames);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent mainActivityIntent = new Intent(AppSettingsActivity.this, MainActivity.class);
        startActivity(mainActivityIntent);
        finish();
    }
}
