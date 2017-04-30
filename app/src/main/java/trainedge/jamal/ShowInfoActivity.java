package trainedge.jamal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static trainedge.jamal.ScanActivity.LOCATION_ID;

public class ShowInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private FloatingActionButton fab;
    private ImageView appBarImage;
    private CollapsingToolbarLayout toolbarLayout;
    private TextView tvAddress;
    private TextView tvlat;
    private TextView tvlng;
    private Button btnGoogle;
    private Button btnWiki;
    private Button btnViewImages;
    private Button btnMap;
    private ProgressDialog dialog;
    private String locationId;
    private String photo2;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_info);
        bindViews();
        initViews();
        getIntentInfo();
        setupListeners();
    }

    private void getIntentInfo() {
        if (getIntent() != null) {
            if (getIntent().hasExtra(LOCATION_ID)) {
                locationId = getIntent().getStringExtra(LOCATION_ID);
                loadDataFromFirebase(locationId);

            }
        } else {

        }
    }

    private void loadDataFromFirebase(String locationId) {
        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading");
        dialog.setCancelable(false);
        dialog.show();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("location").child(locationId);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    String address = dataSnapshot.child("address").getValue(String.class);
                    String latitude = dataSnapshot.child("latitude").getValue(String.class);
                    String longitude = dataSnapshot.child("longitude").getValue(String.class);
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String photo1 = dataSnapshot.child("photo_url_1").getValue(String.class);
                    String photo2 = dataSnapshot.child("photo_url_2").getValue(String.class);
                    String web = dataSnapshot.child("web").getValue(String.class);
                    updateUI(address, latitude, longitude, name, photo1, photo2, web);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (databaseError == null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(ShowInfoActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void updateUI(String address, String latitude, String longitude, String name, String photo1, String photo2, String web) {
        dialog.dismiss();
        try {
            this.name = name;
            tvAddress.setText(address);
            tvlat.setText(latitude);
            toolbarLayout.setTitle(name);
            tvlng.setText(longitude);
            Picasso.with(this).setIndicatorsEnabled(true);
            Picasso.with(this).load(photo1).error(R.drawable.logo).into(appBarImage);
            btnWiki.setTag(web);
            this.photo2 = photo2;

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        updateUserHistory();
    }

    private void updateUserHistory() {
        try {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference history = FirebaseDatabase.getInstance().getReference("history").child(uid);
            HashMap<String, Object> historyInfo = new HashMap<String, Object>();
            historyInfo.put("id", locationId);
            historyInfo.put("name", name);
            historyInfo.put("timestamp", System.currentTimeMillis());
            historyInfo.put("photo", photo2);
            history.push().setValue(historyInfo);
        } catch (NullPointerException e) {
            Toast.makeText(this, "user details could not be fetched", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "could not save in history", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void setupListeners() {
        fab.setOnClickListener(this);
        btnGoogle.setOnClickListener(this);
        btnMap.setOnClickListener(this);
        btnViewImages.setOnClickListener(this);
        btnWiki.setOnClickListener(this);
    }

    private void initViews() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void bindViews() {
        appBarImage = (ImageView) findViewById(R.id.app_bar_image);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbarLayout);
        tvAddress = (TextView) findViewById(R.id.tvAddress);
        tvlat = (TextView) findViewById(R.id.tvlongitude);
        tvlng = (TextView) findViewById(R.id.tvlatitude);
        btnGoogle = (Button) findViewById(R.id.btnGoogle);
        btnWiki = (Button) findViewById(R.id.btnWiki);
        btnViewImages = (Button) findViewById(R.id.btnViewImages);
        btnMap = (Button) findViewById(R.id.btnMap);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnGoogle:
                launchIntent("https://www.google.co.in/search?q=" + toolbarLayout.getTitle());
                break;
            case R.id.btnMap:
                launchOnGoogleMaps();
                break;
            case R.id.btnViewImages:
                launchIntent("https://www.google.com/search?tbm=isch&q=" + toolbarLayout.getTitle());
                break;
            case R.id.btnWiki:
                launchIntent(btnWiki.getTag().toString());
                break;
            case R.id.fab:
                shareMyInfo();
                break;
        }
    }

    private void shareMyInfo() {
        String msg = "I am at " + toolbarLayout.getTitle() + "\n ";
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Share with frds"));
    }

    private void launchOnGoogleMaps() {
        Uri gmmIntentUri = Uri.parse("google.streetview:cbll=" + tvlat.getText().toString() + "," + tvlng.getText().toString());
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    private void launchIntent(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    // Returns the URI path to the Bitmap displayed in specified ImageView
    public Uri getLocalBitmapUri(ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable) {
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }
}
