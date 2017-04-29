package trainedge.jamal;

import android.app.ProgressDialog;
import android.os.Bundle;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

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
                String locationId = getIntent().getStringExtra(LOCATION_ID);
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
            tvAddress.setText(address);
            tvlat.setText(latitude);
            tvlng.setText(longitude);
            Picasso.with(this).setIndicatorsEnabled(true);
            Picasso.with(this).load(photo1).error(R.drawable.logo).into(appBarImage);

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                launchIntent("https://www.google.co.in/?q=bara%20imabara");
                break;
            case R.id.btnMap:
                break;
            case R.id.btnViewImages:
                break;
            case R.id.btnWiki:
                break;
        }
    }

    private void launchIntent(String url) {

    }
}
