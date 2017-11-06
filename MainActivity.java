package com.tommybear.batcitycrossfit;

import android.content.ClipData;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Html;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
//import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
//import com.google.android.gms.ads.formats.NativeAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

//import java.io.IOException;
//import java.io.InputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.net.URLConnection;

import java.util.Calendar;

import com.facebook.FacebookSdk;

import static com.tommybear.batcitycrossfit.R.id.textView;
import static java.lang.Boolean.TRUE;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mChatPhotosStorageReference;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
  //  private WebView mWebView;

  //  private Button mbuttonFirebase;
  //  private Button mbuttonReadData;
  //  private Button mbuttonDeleteData;
 //   private Button mbuttonUpdateData;
 //   private Button mbuttonSwitchscreens;

    private TextView mShowBalance;

    //private MenuItem mFireCheckIn;
    // private Button mButtonSend;

  //  private EditText mCheckInDate;
  //  private EditText mName;
  //  private EditText mLatitude;
  //  private EditText mLongitude;
 //   private EditText mTime;

    public static final String ANONYMOUS = "anonymous";
    public static final int RC_SIGN_IN = 1;

    private String mUsername;
  //  private TextView mWod;
    private ImageButton mCheckInButton;
    private ImageButton mInGymPurchase;
    private ImageButton mPunchCards;
    private ImageButton mLiveChatButton;

    private ImageButton mSendMessageButton;
    private String mUserLoggedIn;
    private String mDisplayName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsername = ANONYMOUS;

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        //mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("checkins");
        mMessagesDatabaseReference = mFirebaseDatabase.getReference();
        //mFirebaseStorage = FirebaseStorage.getInstance();
        //mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

     //   mWod = (TextView) findViewById(R.id.wod);
    //    mWebView = (WebView) findViewById(R.id.webview);
     //   mWebView.loadUrl("http://batcitycrossfit.com/wod2.html");
     //   mWebView.getSettings().setJavaScriptEnabled(TRUE);

        mCheckInButton = (ImageButton) findViewById(R.id.CheckInButton);
        mInGymPurchase = (ImageButton) findViewById(R.id.InGymPurchaseButton);
        mPunchCards = (ImageButton) findViewById(R.id.PunchCardButton);
        mLiveChatButton = (ImageButton) findViewById(R.id.LiveChatButton);
        mSendMessageButton = (ImageButton) findViewById(R.id.SendAMessageButton);
        mShowBalance = (TextView) findViewById(R.id.TextShowBalance) ;

        mLiveChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LiveChat();
            }
        });
        mCheckInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QRCheckIn();
            }
        });
        mInGymPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GymStore();
            }
        });
        mPunchCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PunchCardsOpen();
            }
        });

        mSendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Comments();
            }
        });

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // Toast.makeText(MainActivity.this, "You're now singed in. Welcome to FriendliyChat!", Toast.LENGTH_SHORT).show();
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
                getcurrentbalance();
            }
        };

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //  Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                //      .setAction("Action", null).show();
//                Comments();
//            }
//        });
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();
//
      //  NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
        if (id == R.id.sign_out) {
            AuthUI.getInstance().signOut(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.mm_checkin) {
            QRCheckIn();

        } else if (id == R.id.mm_in_gym_purchases) {
            GymStore();
        } else if (id == R.id.mm_punch_card) {
            PunchCardsOpen();
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
        //detachDatabaseReadListener();
        //mMessageAdapter.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    public void PunchCardsOpen() {
       // Intent i = new Intent(this, Punch_Cards.class);
        Intent i = new Intent(this, punch_type.class);
        startActivity(i);
    }

    public void QRCheckIn() {
        Intent i = new Intent(this, QRCheckIn.class);
        startActivity(i);
    }

    public void GymStore() {
        Intent i = new Intent(this, gym_store.class);
        // Intent i = new Intent(this, batcitycomments.class);
        startActivity(i);
    }

    public void Comments() {
        Intent i = new Intent(this, batcitycomments.class);
        startActivity(i);
    }

    public void LiveChat() {
        Intent i = new Intent(this, ActivityTwo.class);
         startActivity(i);
      //  InputStream getInput;
        //getInput = OpenHttpConnection("http://batcitycrossfit.com/wod2.html");
      //  Toast.makeText(MainActivity.this, "output " + getInput.toString(), Toast.LENGTH_SHORT).show();
    }

    public void getcurrentbalance()
    {
        Calendar now = Calendar.getInstance();

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
                                String weekofyear = postSnapshot.child("weekofyear").getValue().toString();
                                String timesthisweek = postSnapshot.child("timesthisweek").getValue().toString();
                                String xaweek = postSnapshot.child("xaweek").getValue().toString();
                                String messagetoclient = postSnapshot.child("messagetoclient").getValue().toString();
                                if (weekofyear.equals(String.valueOf(now.get(Calendar.WEEK_OF_YEAR))))
                                {
                                    times = timesthisweek;
                                }
                                else
                                {
                                    times = "0";
                                }
                                mShowBalance.setText(" Your current check in balance is :" + String.valueOf(balance) + ".\n You last checked in on " + lastcheckedin + ".\n You have checked in " + times + " times this week. \n You are on the " + xaweek + " plan. \n Messages: " + messagetoclient );
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
