package com.tommybear.batcitycrossfit;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import android.Manifest;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.tommybear.batcitycrossfit.barcode.BarcodeCaptureActivity;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

//for maps
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.tommybear.batcitycrossfit.R.id.map;
import static com.tommybear.batcitycrossfit.R.id.rewardsview;
import static com.tommybear.batcitycrossfit.R.id.time;

//import com.facebook.FacebookSdk;

//import static android.R.attr.data;

public class QRCheckIn extends AppCompatActivity implements OnMapReadyCallback, OnCameraIdleListener {
    private static final String LOG_TAG = QRCheckIn.class.getSimpleName();
    private static final int BARCODE_READER_REQUEST_CODE = 1;

    //private ProgressDialog progressDialog;
    private ImageView mRewardsImage;
    //private TextView locationText;

  //  private TextView mResultTextView;
 //   private Button mButtonAddress;

    //for location
    private static final String TAG = QRCheckIn.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
    private static final String LOCATION_ADDRESS_KEY = "location-address";
    private FusedLocationProviderClient mFusedLocationClient;
    private Location mLastLocation;
    private boolean mAddressRequested;
    private String mAddressOutput;
    private ImageButton mScanBarCodeButton;
    //  private AddressResultReceiver mResultReceiver;
    private TextView mLocationAddressTextView;
 //   private ProgressBar mProgressBar;

    private TextView mBalanceCheck;
 //   private Button mFetchAddressButton;

    private GoogleMap mMap;

    //for firebase
    private DatabaseReference mMessagesDatabaseReference;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mFirebaseAuth;
    //private MessageAdapter mMessageAdapter;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    public static final String ANONYMOUS = "anonymous";
    public static final int RC_SIGN_IN = 1;
    private String mUsername;
    private ChildEventListener mChildEventListener;
    private String mUserLoggedIn;

    private String mAddressLine;
    private String mQrcode;
    private String mRewards;
    private String mDisplayName;
    private TextView mTextBalance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrcheckin);
        //for map
        mMap = null;
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(map);
        mapFragment.getMapAsync(this);
        //for map
        mRewardsImage = (ImageView) findViewById(rewardsview);
        //forlocation
        //  mResultReceiver = new AddressResultReceiver(new Handler());

        mLocationAddressTextView = (TextView) findViewById(R.id.location_address_view);
        mScanBarCodeButton  = (ImageButton) findViewById(R.id.scan_barcode_button);

     //   mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mBalanceCheck = (TextView) findViewById(R.id.checkinstats);
      //  mFetchAddressButton = (Button) findViewById(R.id.fetch_address_button);
        mAddressRequested = false;
        mAddressOutput = "";
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mMessagesDatabaseReference = mFirebaseDatabase.getReference();
        mTextBalance = (TextView) findViewById(R.id.textviewbalance);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                //Toast.makeText(Punch_Cards.this, mUserLoggedIn, Toast.LENGTH_SHORT).show();
                if (user != null) {
                    //Toast.makeText(Punch_Cards.this, "You're now signed on to punc_cards", Toast.LENGTH_SHORT).show();
                    //onSignedInitialize(user.getDisplayName());
                    mUserLoggedIn = user.getEmail();
                    mDisplayName = user.getDisplayName();
                } else {
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(
                                            AuthUI.EMAIL_PROVIDER)
                                    .build(),
                            RC_SIGN_IN);

                }
                SetCards();
                getcurrentbalance();
              //  getcurrentbalance();
            }
        };


//        mFetchAddressButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                fetchAddressButtonHandler(v);
//                onClampToAdelaide(v);
//            }
//        });

      //  mResultTextView = (TextView) findViewById(R.id.result_textview);
    //    mButtonAddress = (Button) findViewById(R.id.ButtonAddress);



        //scan bar code button
        mScanBarCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetQRCode();
                fetchAddressButtonHandler(view);

                Intent intent = new Intent(getApplicationContext(), BarcodeCaptureActivity.class);
                startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);

            }
        });

//        mButtonAddress.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        map.setOnCameraIdleListener(this);
    }

    public void onCameraIdle() {
        //   mCameraTextView.setText(mMap.getCameraPosition().toString());
    }

    private boolean checkReady() {
        if (mMap == null) {
            //Toast.makeText(this, R.string.map_not_ready, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void onClampToAdelaide() {


        if (!checkReady()) {
            return;
        }
        LatLngBounds ADELAIDE = new LatLngBounds(
                //     new LatLng(-35.0, 138.58), new LatLng(-34.9, 138.61));
                new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
        CameraPosition ADELAIDE_CAMERA = new CameraPosition.Builder()
                //.target(new LatLng(-34.92873, 138.59995)).zoom(20.0f).bearing(0).tilt(0).build();
                .target(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())).zoom(18.0f).bearing(0).tilt(0).build();
        //mLastLocation.getLatitude()
        mMap.setMaxZoomPreference(18.0f);
        mMap.setLatLngBoundsForCameraTarget(ADELAIDE);
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(ADELAIDE_CAMERA));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        onClampToAdelaide();

        //final String mRewards;
        if (requestCode == BARCODE_READER_REQUEST_CODE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Point[] p = barcode.cornerPoints;
                    final String mbarcode = barcode.displayValue;
                    mMessagesDatabaseReference
                            .child("SignInRunningBalance")
                            .orderByChild("username")
                            .equalTo(mUserLoggedIn)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Calendar now = Calendar.getInstance();
                                    if (dataSnapshot.exists()) {
                                        if(mbarcode.equals(mQrcode)) {
                                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                Date currentTime = Calendar.getInstance().getTime();
                                                SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMddhma");
                                                //SimpleDateFormat flat_date = new SimpleDateFormat("yyyyMMdd");
                                                SimpleDateFormat flat_date = new SimpleDateFormat("MM/dd/yyyy");
                                                String flat_date_output = flat_date.format(currentTime);
                                                String date1 = format1.format(currentTime);
                                                String weekofyear = postSnapshot.child("weekofyear").getValue().toString();
                                                String active = postSnapshot.child("active").getValue().toString();
                                                String xaweek = postSnapshot.child("xaweek").getValue().toString();
                                                String lastupdated = postSnapshot.child("last_updated").getValue().toString();
                                                int timesthisweek = Integer.parseInt(postSnapshot.child("timesthisweek").getValue().toString());
                                                if (lastupdated.equals(flat_date_output))
                                                {
                                                    Toast.makeText(QRCheckIn.this, "You have already checked in today", Toast.LENGTH_LONG).show();
                                                }
                                                else {
                                                    if (weekofyear.equals(String.valueOf(now.get(Calendar.WEEK_OF_YEAR))))
                                                    {

                                                        if (xaweek.equals("3X") && timesthisweek >= 3)
                                                        {
                                                            Toast.makeText(QRCheckIn.this, "You can not check in any more times this week. You are signed up for 3x a week.", Toast.LENGTH_LONG).show();
                                                        }
                                                        else if (!"A".equals(active) && !"a".equals(active))
                                                        {
                                                            Toast.makeText(QRCheckIn.this, "Your account is currently not active or there is a problem with your billing.", Toast.LENGTH_LONG).show();
                                                        }
                                                        else
                                                        {
                                                            timesthisweek = timesthisweek + 1;
                                                            int balance = Integer.parseInt(postSnapshot.child("balance").getValue().toString());
                                                            balance = balance + 1;
                                                            Date currentTime2 = Calendar.getInstance().getTime();
                                                            //SimpleDateFormat date_one = new SimpleDateFormat("yyyyMMdd");
                                                            SimpleDateFormat date_one = new SimpleDateFormat("MM/dd/yyyy");
                                                            SimpleDateFormat hours_one = new SimpleDateFormat("ha");
                                                            final String hours_out = hours_one.format(currentTime2);
                                                            final String date_out = date_one.format(currentTime2);
                                                            String combo_date = date_out + "_" + hours_out;
                                                            SignInRunningBalance signInRunningBalance = new SignInRunningBalance(mUserLoggedIn, flat_date_output, String.valueOf(balance),xaweek,"A","",String.valueOf(now.get(Calendar.WEEK_OF_YEAR)),String.valueOf(timesthisweek));
                                                            mMessagesDatabaseReference.child("SignInRunningBalance").child(postSnapshot.getKey()).setValue(signInRunningBalance);
                                                            SignIn signin = new SignIn(mUserLoggedIn, date1, mAddressLine, mbarcode, String.valueOf(balance), combo_date);
                                                            mMessagesDatabaseReference.child("SignIn").push().setValue(signin);
                                                            getcurrentbalance();
                                                            ClassRunningTotals();
                                                            Toast.makeText(QRCheckIn.this, "You have checked in today successfully.", Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                    else
                                                    {
                                                        int balance = Integer.parseInt(postSnapshot.child("balance").getValue().toString());
                                                        balance = balance + 1;
                                                        Date currentTime2 = Calendar.getInstance().getTime();
                                                        //SimpleDateFormat date_one = new SimpleDateFormat("yyyyMMdd");
                                                        SimpleDateFormat date_one = new SimpleDateFormat("MM/dd/yyyy");
                                                        SimpleDateFormat hours_one = new SimpleDateFormat("ha");
                                                        final String hours_out = hours_one.format(currentTime2);
                                                        final String date_out = date_one.format(currentTime2);
                                                        String combo_date = date_out + "_" + hours_out;
                                                        SignInRunningBalance signInRunningBalance = new SignInRunningBalance(mUserLoggedIn, flat_date_output, String.valueOf(balance),xaweek,"A","",String.valueOf(now.get(Calendar.WEEK_OF_YEAR)),"1");
                                                        mMessagesDatabaseReference.child("SignInRunningBalance").child(postSnapshot.getKey()).setValue(signInRunningBalance);
                                                        SignIn signin = new SignIn(mUserLoggedIn, date1, mAddressLine, mbarcode, String.valueOf(balance), combo_date);
                                                        mMessagesDatabaseReference.child("SignIn").push().setValue(signin);

                                                        getcurrentbalance();
                                                        ClassRunningTotals();
                                                        Toast.makeText(QRCheckIn.this, "You have checked in today successfully.", Toast.LENGTH_LONG).show();
                                                    }

                                                }
                                            }
                                        }
                                        else
                                        {
                                            Toast.makeText(QRCheckIn.this, "The QR code you are scanning does not match they Bat City Key QR Code", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });


                } else ;//mResultTextView.setText(R.string.no_barcode_captured);
            } else Log.e(LOG_TAG, String.format(getString(R.string.barcode_error_format),
                    CommonStatusCodes.getStatusCodeString(resultCode)));
        } else super.onActivityResult(requestCode, resultCode, data);
    }

//    public void LocationFinder(View view){
//
//    }



    @Override
    public void onStart() {
        super.onStart();

        if (!checkPermissions()) {
            requestPermissions();
        } else {
            getAddress();
        }
    }



    /**
     * Runs when user clicks the Fetch Address button.
     */
    @SuppressWarnings("unused")
    public void fetchAddressButtonHandler(View view) {

        if (mLastLocation != null) {
            //  Toast.makeText(QRCheckIn.this,mLastLocation.toString(), Toast.LENGTH_SHORT).show();
            startIntentService();
            return;
        }

        // If we have not yet retrieved the user location, we process the user's request by setting
        // mAddressRequested to true. As far as the user is concerned, pressing the Fetch Address button
        // immediately kicks off the process of getting the address.
        mAddressRequested = true;
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    private void startIntentService() {

        List<Address> addresses = null;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String errorMessage = "";
        try {
            // Using getFromLocation() returns an array of Addresses for the area immediately
            // surrounding the given latitude and longitude. The results are a best guess and are
            // not guaranteed to be accurate.
            addresses = geocoder.getFromLocation(
                    mLastLocation.getLatitude(),
                    mLastLocation.getLongitude(),
                    // In this sample, we get just a single address.
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            errorMessage = getString(R.string.service_not_available);
            Log.e(TAG, errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = getString(R.string.invalid_lat_long_used);
            Log.e(TAG, errorMessage + ". " +
                    "Latitude = " + mLastLocation.getLatitude() +
                    ", Longitude = " + mLastLocation.getLongitude(), illegalArgumentException);
        }
        if (addresses == null || addresses.size()  == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.no_address_found);
                Log.e(TAG, errorMessage);
            }
            // deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<>();

            // Fetch the address lines using {@code getAddressLine},
            // join them, and send them to the thread. The {@link android.location.address}
            // class provides other options for fetching address details that you may prefer
            // to use. Here are some examples:
            // getLocality() ("Mountain View", for example)
            // getAdminArea() ("CA", for example)
            // getPostalCode() ("94043", for example)
            // getCountryCode() ("US", for example)
            // getCountryName() ("United States", for example)
            for(int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            Log.i(TAG, getString(R.string.address_found));
            //deliverResultToReceiver(Constants.SUCCESS_RESULT,
            //   TextUtils.join(System.getProperty("line.separator"), addressFragments));
            //  Toast.makeText(QRCheckIn.this,TextUtils.join(System.getProperty("line.separator"), addressFragments), Toast.LENGTH_SHORT).show();
            mLocationAddressTextView.setText(TextUtils.join(System.getProperty("line.separator"), addressFragments));
            mAddressLine = TextUtils.join(System.getProperty("line.separator"), addressFragments);
        }
    }

    /**
     * Gets the address for the last known location.
     */
    @SuppressWarnings("MissingPermission")
    private void getAddress() {
        //    Toast.makeText(QRCheckIn.this,"get address", Toast.LENGTH_SHORT).show();
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location == null) {
                            Log.w(TAG, "onSuccess:null");
                            return;
                        }

                        mLastLocation = location;

                        // Determine whether a Geocoder is available.
                        if (!Geocoder.isPresent()) {
                            showSnackbar(getString(R.string.no_geocoder_available));
                            return;
                        }

                        // If the user pressed the fetch address button before we had the location,
                        // this will be set to true indicating that we should kick off the intent
                        // service after fetching the location.
                        if (mAddressRequested) {
                            startIntentService();
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "getLastLocation:onFailure", e);
                    }
                });
    }



    /**
     * Toggles the visibility of the progress bar. Enables or disables the Fetch Address button.
     */
//    private void updateUIWidgets() {
//        if (mAddressRequested) {
//            mProgressBar.setVisibility(ProgressBar.VISIBLE);
//            mFetchAddressButton.setEnabled(false);
//        } else {
//            mProgressBar.setVisibility(ProgressBar.GONE);
//            mFetchAddressButton.setEnabled(true);
//        }
//    }

    /**
     * Shows a toast with the given text.
     */
    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save whether the address has been requested.
        savedInstanceState.putBoolean(ADDRESS_REQUESTED_KEY, mAddressRequested);

        // Save the address string.
        savedInstanceState.putString(LOCATION_ADDRESS_KEY, mAddressOutput);
        super.onSaveInstanceState(savedInstanceState);
    }



    /**
     * Shows a {@link Snackbar} using {@code text}.
     *
     * @param text The Snackbar text.
     */
    private void showSnackbar(final String text) {
        View container = findViewById(android.R.id.content);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");

            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(QRCheckIn.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });

        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(QRCheckIn.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                getAddress();
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }

    private void onSignedOutCleanup() {
        mUsername = ANONYMOUS;
        //mMessageAdapter.clear();
    }

    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        detachDatabaseReadListener();
        //mMessageAdapter.clear();
    }

    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mMessagesDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    private void onSignedInitialize(String username) {
        mUsername = username;
        //attachDatabaseReadListener();
    }

    public void InitialSignIn()
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR,-1);
        Date currentTime = cal.getTime();

        SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMddhma");
        final String date1 = format1.format(currentTime);
        Date currentTime2 = cal.getInstance().getTime();
        //SimpleDateFormat date2 = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat date2 = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat hours = new SimpleDateFormat("ha");
        //SimpleDateFormat flat_date = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat flat_date = new SimpleDateFormat("MM/dd/yyyy");
        String flat_date_output = flat_date.format(currentTime);
        final String h2 = hours.format(currentTime2);
        final String dateout = date2.format(currentTime2);
        String combo_date = dateout + "_" + h2;
        Calendar now = Calendar.getInstance();


        SignIn signin = new SignIn(mUserLoggedIn, date1, "location","initial","0",combo_date);
        mMessagesDatabaseReference.child("SignIn").push().setValue(signin);
        SignInRunningBalance signInRunningBalance = new SignInRunningBalance(mUserLoggedIn, flat_date_output, "0","3X","A","",String.valueOf(now.get(Calendar.WEEK_OF_YEAR)),"0");
        mMessagesDatabaseReference.child("SignInRunningBalance").push().setValue(signInRunningBalance);
        showrewards(0);
        mTextBalance.setText("Your current check in balance is : 0");
    }

    public void SetCards() {
        mMessagesDatabaseReference
                .child("SignInRunningBalance")
                .orderByChild("username")
                .equalTo(mUserLoggedIn)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                           //Toast.makeText(QRCheckIn.this, "found 999", Toast.LENGTH_SHORT).show();
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                            }
                        }
                        else
                        {
                           // Toast.makeText(QRCheckIn.this, "not found 111", Toast.LENGTH_SHORT).show();
                            InitialSignIn();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    public void GetQRCode() {
        mMessagesDatabaseReference
                .child("qrcode")
                .orderByChild("code")
                .equalTo("valid")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {

                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                                mQrcode = postSnapshot.child("passcode").getValue().toString();
                                //mQrcode = qrcode;
                               // Toast.makeText(QRCheckIn.this, mQrcode, Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {

                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    public void ClassRunningTotals() {
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat hours = new SimpleDateFormat("ha");
        final String date1 = format1.format(currentTime);
        final String h2 = hours.format(currentTime);

        mMessagesDatabaseReference
                .child("class_running_total")
                .orderByChild("date_class_hour")
                .equalTo(date1+ "_" + h2)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {

                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                int count = Integer.parseInt(postSnapshot.child("count").getValue().toString());
                                String classmates =postSnapshot.child("classmates").getValue().toString();
                                classmates = classmates + "," + mDisplayName;
                                        count = count + 1;
                                Class_rt_fb class_rt_fb = new Class_rt_fb(date1+ "_" + h2, String.valueOf(count), classmates);
                                mMessagesDatabaseReference.child("class_running_total").child(postSnapshot.getKey()).setValue(class_rt_fb);
                            }
                        }
                        else
                        {
                            insertClassTotal();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    public void insertClassTotal()
    {
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat hours = new SimpleDateFormat("ha");
        String date1 = format1.format(currentTime);
        String h2 = hours.format(currentTime);

        Class_rt_fb class_rt_fb = new Class_rt_fb(date1 + "_" + h2, "1", mDisplayName);
        mMessagesDatabaseReference.child("class_running_total").push().setValue(class_rt_fb);
    }

    public void showrewards(int balance)
    {
        int mLeft;


            if (balance < 6) {
                mLeft = 6 - balance;
                mRewards = "You are " + String.valueOf(mLeft) + " workouts away from earning a Koozie!";
                mRewardsImage.setImageResource(R.mipmap.koozie);
            }
            if (balance == 6) {
                mLeft = 15 - balance;
                mRewards = "You just earned a Koozie! Go get it! You are " + String.valueOf(mLeft) + " workouts away from earning a water bottle!";
                mRewardsImage.setImageResource(R.mipmap.koozie);
            }
            if (balance > 6 && balance < 15) {
                mLeft = 15 - balance;
                mRewards = "You are " + String.valueOf(mLeft) + " workouts away from earning a water bottle!";
                mRewardsImage.setImageResource(R.mipmap.waterbottle);
            }
            if (balance == 15) {
                mLeft = 21 - balance;
                mRewards = "You just earned a water bottle! Go get it! You are " + String.valueOf(mLeft) + " workouts away from earning a t-shirt!";
                mRewardsImage.setImageResource(R.mipmap.waterbottle);
            }
            if (balance > 15 && balance < 21) {
                mLeft = 21 - balance;
                mRewards = "You are " + String.valueOf(mLeft) + " workouts away from earning a t-shirt!";
                mRewardsImage.setImageResource(R.mipmap.tshirt);
            }
            if (balance == 21) {
                mLeft = 40 - balance;
                mRewards = "You just earned a T-shirt! Go get it! You are " + String.valueOf(mLeft) + " workouts away from earning a pair of sunglasses!";
                mRewardsImage.setImageResource(R.mipmap.tshirt);
            }
            if (balance > 21 && balance < 40) {
                mLeft = 40 - balance;
                mRewards = "You are " + String.valueOf(mLeft) + " workouts away from earning a pair of sunglasses!";
                mRewardsImage.setImageResource(R.mipmap.sunglasses);
            }
            if (balance == 40) {
                mLeft = 60 - balance;
                mRewards = "You just earned a pair of sunglasses! Go get it! You are " + String.valueOf(mLeft) + " workouts away from earning a baseball cap!";
                mRewardsImage.setImageResource(R.mipmap.sunglasses);
            }
            if (balance > 40 && balance < 60) {
                mLeft = 60 - balance;
                mRewards = "You are " + String.valueOf(mLeft) + " workouts away from earning a baseball cap!";
                mRewardsImage.setImageResource(R.mipmap.baseballcap);
            }
            if (balance == 60) {
                mLeft = 80 - balance;
                mRewards = "You just earned a baseball cap! Go get it! You are " + String.valueOf(mLeft) + " workouts away from earning a tote bag!";
                mRewardsImage.setImageResource(R.mipmap.baseballcap);
            }
            if (balance > 60 && balance < 80) {
                mLeft = 80 - balance;
                mRewards = "You are " + String.valueOf(mLeft) + " workouts away from earning a tote bag!";
                mRewardsImage.setImageResource(R.mipmap.bag);
            }
            if (balance == 80) {
                mLeft = 100 - balance;
                mRewards = "You just earned a totebag! Go get it! You are " + String.valueOf(mLeft) + " workouts away from earning a free month of CrossFit!";
                mRewardsImage.setImageResource(R.mipmap.bag);
            }
            if (balance > 80 && balance < 100) {
                mLeft = 100 - balance;
                mRewards = "You are " + String.valueOf(mLeft) + " workouts away from earning a free month of CrossFit!";
                mRewardsImage.setImageResource(R.mipmap.barbell);
            }
            if (balance == 100) {
                mLeft = 200 - balance;
                mRewards = "You just earned a free month of CrossFit!";
                mRewardsImage.setImageResource(R.mipmap.barbell);
            }
            if (balance > 100 && balance < 200) {
                mLeft = 200 - balance;
                mRewards = "You are " + String.valueOf(mLeft) + " workouts away from earning a free month of CrossFit!";
                mRewardsImage.setImageResource(R.mipmap.barbell);
            }
            if (balance == 200) {
                mLeft = 300 - balance;
                mRewards = "You just earned a free month of CrossFit!";
                mRewardsImage.setImageResource(R.mipmap.barbell);
            }
            if (balance > 200 && balance < 300) {
                mLeft = 300 - balance;
                mRewards = "You are " + String.valueOf(mLeft) + " workouts away from earning a free month of CrossFit!";
                mRewardsImage.setImageResource(R.mipmap.barbell);
            }
            if (balance == 300) {
                mLeft = 400 - balance;
                mRewards = "You just earned a free month of CrossFit!";
                mRewardsImage.setImageResource(R.mipmap.barbell);
            }
            if (balance > 300 && balance < 400) {
                mLeft = 400 - balance;
                mRewards = "You are " + String.valueOf(mLeft) + " workouts away from earning a free month of CrossFit!";
                mRewardsImage.setImageResource(R.mipmap.barbell);
            }
            if (balance == 400) {
                mLeft = 500 - balance;
                mRewards = "You just earned a free month of CrossFit!";
                mRewardsImage.setImageResource(R.mipmap.barbell);
            }
            if (balance > 400 && balance < 500) {
                mLeft = 500 - balance;
                mRewards = "You are " + String.valueOf(mLeft) + " workouts away from earning a free month of CrossFit!";
                mRewardsImage.setImageResource(R.mipmap.barbell);
            }
            if (balance == 500) {
                mLeft = 600 - balance;
                mRewards = "You just earned a free month of CrossFit!";
                mRewardsImage.setImageResource(R.mipmap.barbell);
            }
            if (balance > 500 && balance < 600) {
                mLeft = 600 - balance;
                mRewards = "You are " + String.valueOf(mLeft) + " workouts away from earning a free month of CrossFit!";
                mRewardsImage.setImageResource(R.mipmap.barbell);
            }
            if (balance == 600) {
                mLeft = 700 - balance;
                mRewards = "You just earned a free month of CrossFit!";
                mRewardsImage.setImageResource(R.mipmap.barbell);
            }
            if (balance > 600 && balance < 700) {
                mLeft = 700 - balance;
                mRewards = "You are " + String.valueOf(mLeft) + " workouts away from earning a free month of CrossFit!";
                mRewardsImage.setImageResource(R.mipmap.barbell);
            }
            if (balance == 700) {
                mLeft = 800 - balance;
                mRewards = "You just earned a free month of CrossFit!";
                mRewardsImage.setImageResource(R.mipmap.barbell);
            }
            if (balance > 700 && balance < 800) {
                mLeft = 800 - balance;
                mRewards = "You are " + String.valueOf(mLeft) + " workouts away from earning a free month of CrossFit!";
                mRewardsImage.setImageResource(R.mipmap.barbell);
            }
            if (balance == 800) {
                mLeft = 900 - balance;
                mRewards = "You just earned a free month of CrossFit!";
                mRewardsImage.setImageResource(R.mipmap.barbell);
            }
            if (balance > 800 && balance < 900) {
                mLeft = 900 - balance;
                mRewards = "You are " + String.valueOf(mLeft) + " workouts away from earning a free month of CrossFit!";
                mRewardsImage.setImageResource(R.mipmap.barbell);
            }
            if (balance == 900) {
                mLeft = 1000 - balance;
                mRewards = "You just earned a free month of CrossFit!";
                mRewardsImage.setImageResource(R.mipmap.barbell);
            }
            if (balance > 900 && balance < 1000) {
                mLeft = 1000 - balance;
                mRewards = "You are " + String.valueOf(mLeft) + " workouts away from earning a free month of CrossFit!";
                mRewardsImage.setImageResource(R.mipmap.barbell);
            }
            if (balance == 1000) {
                mRewards = "You just earned a free month of CrossFit!";
                mRewardsImage.setImageResource(R.mipmap.barbell);
            }
            if (balance > 1000 && balance < 1100) {
                mLeft = 1100 - balance;
                mRewards = "You are " + String.valueOf(mLeft) + " workouts away from earning a free month of CrossFit!";
                mRewardsImage.setImageResource(R.mipmap.barbell);
            }
            if (balance == 1100) {
                mRewards = "You just earned a free month of CrossFit!";
                mRewardsImage.setImageResource(R.mipmap.barbell);
            }
            if (balance > 1100 && balance < 1200) {
                mLeft = 1200 - balance;
                mRewards = "You are " + String.valueOf(mLeft) + " workouts away from earning a free month of CrossFit!";
                mRewardsImage.setImageResource(R.mipmap.barbell);
            }
            if (balance == 1200) {
                mRewards = "You just earned a free month of CrossFit!";
                mRewardsImage.setImageResource(R.mipmap.barbell);
            }
            if (balance > 1200 && balance < 1300) {
                mLeft = 1300 - balance;
                mRewards = "You are " + String.valueOf(mLeft) + " workouts away from earning a free month of CrossFit!";
                mRewardsImage.setImageResource(R.mipmap.barbell);
            }
            if (balance == 1300) {
                mRewards = "You just earned a free month of CrossFit!";
                mRewardsImage.setImageResource(R.mipmap.barbell);
            }
            if (balance > 1300 && balance < 1400) {
                mLeft = 1400 - balance;
                mRewards = "You are " + String.valueOf(mLeft) + " workouts away from earning a free month of CrossFit!";
                mRewardsImage.setImageResource(R.mipmap.barbell);
            }
            if (balance == 1400) {
                mRewards = "You just earned a free month of CrossFit!";
                mRewardsImage.setImageResource(R.mipmap.barbell);
            }
            if (balance > 1400 && balance < 1500) {
                mLeft = 1500 - balance;
                mRewards = "You are " + String.valueOf(mLeft) + " workouts away from earning a free month of CrossFit!";
                mRewardsImage.setImageResource(R.mipmap.barbell);
            }
            if (balance == 1500) {
                mRewards = "You just earned a free month of CrossFit!";
                mRewardsImage.setImageResource(R.mipmap.barbell);
            }
        mBalanceCheck.setText(mRewards);
    }
    public void getcurrentbalance()
    {
        mMessagesDatabaseReference
                .child("SignInRunningBalance")
                .orderByChild("username")
                .equalTo(mUserLoggedIn)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String times;
                        if (dataSnapshot.exists()) {

                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                Calendar now = Calendar.getInstance();

                                int balance = Integer.parseInt(postSnapshot.child("balance").getValue().toString());
                                String lastcheckedin = postSnapshot.child("last_updated").getValue().toString();
                                String timesthisweek = postSnapshot.child("timesthisweek").getValue().toString();
                                String weekofyear = postSnapshot.child("weekofyear").getValue().toString();
                                if (weekofyear.equals(String.valueOf(now.get(Calendar.WEEK_OF_YEAR))))
                                {
                                    times = timesthisweek;
                                }
                                else
                                {
                                    times = "0";
                                }
                                mTextBalance.setText(" Your current check in balance is :" + String.valueOf(balance) + ".\n You last checked in " + lastcheckedin + ".\n You have checked in " + times + " time this week." );
                                showrewards(balance);
                                //   Toast.makeText(QRCheckIn.this, "bar code match", Toast.LENGTH_SHORT).show();
                                //create Calendar instance

                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}

