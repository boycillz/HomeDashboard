package com.pkmunla.asus.homedashboard.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.pkmunla.asus.homedashboard.R;

public class ProfileActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
