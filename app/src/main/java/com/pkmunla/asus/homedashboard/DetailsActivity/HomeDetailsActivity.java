package com.pkmunla.asus.homedashboard.DetailsActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.pkmunla.asus.homedashboard.R;

public class HomeDetailsActivity extends AppCompatActivity {

    TextView mTitleTV,mDescTV,mLocationTV,mUstadTV,mMasjidTV;
    ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_details);

        //Action bar
        ActionBar actionBar = getSupportActionBar();

        //Action bar title
        actionBar.setTitle("DETAILS KAJIAN");

        //set back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        //inisialisasi view
        mTitleTV = findViewById(R.id.TitleTV);
        mDescTV = findViewById(R.id.descriptionTV);
        mLocationTV = findViewById(R.id.LocationTV);
        mUstadTV = findViewById(R.id.ustadTV);
        mMasjidTV = findViewById(R.id.MasjidTV);
        mImageView = findViewById(R.id.ImageView);

        //get data from intent
        byte [] bytes = getIntent().getByteArrayExtra("image");
        String judul = getIntent().getStringExtra("title");
        String lokasi = getIntent().getStringExtra("location");
        String deskripsi = getIntent().getStringExtra("description");
        String masjid = getIntent().getStringExtra("masjid");
        String ustadz = getIntent().getStringExtra("ustad");
        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        //set data to view
        mTitleTV.setText(judul);
        mDescTV.setText(deskripsi);
        mLocationTV.setText(lokasi);
        mUstadTV.setText(ustadz);
        mMasjidTV.setText(masjid);
        mImageView.setImageBitmap(bmp);



    }

    //handle onBackPressed (go to previous activity

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
