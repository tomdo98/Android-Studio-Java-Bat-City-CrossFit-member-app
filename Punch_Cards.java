package com.tommybear.batcitycrossfit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.util.Calendar;
import java.util.Map;

import static android.R.attr.value;
import static com.tommybear.batcitycrossfit.R.mipmap.redeemed;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import android.os.Vibrator;

/**
 * Created by dot on 8/20/2017.
 */

public class Punch_Cards extends AppCompatActivity {
    private DatabaseReference mMessagesDatabaseReference;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mFirebaseAuth;
    private MessageAdapter mMessageAdapter;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    public static final String ANONYMOUS = "anonymous";
    public static final int RC_SIGN_IN = 1;
    private String mUsername;
    private ChildEventListener mChildEventListener;


   // private TextView mTextViewPunchesLeft;
    //private Button mButtonRechardCard;
    private ImageButton mReloadCard;
    //private Button mButtonCheckin;
    private ImageButton mPunchButton;
    private ImageView mRing;

    private String mFound;

    private ArrayList mArrayPunches = new ArrayList<>();

    private String mUserLoggedIn;

    CustomDialogueClass dialogue;
    FireMisslesDialogueFragment firemissles;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.punch_cards);


        mUsername = ANONYMOUS;
      //  mTextViewPunchesLeft = (TextView) findViewById(R.id.TextViewPunchesLeft);
        //mButtonRechardCard = (Button) findViewById(R.id.buttonrechargecard);
        mReloadCard = (ImageButton) findViewById(R.id.reloadbutton);
       // mButtonCheckin = (Button) findViewById(R.id.buttoncheckin);
        mPunchButton = (ImageButton) findViewById(R.id.punchbutton) ;
        mRing = (ImageView) findViewById(R.id.imagering);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        //mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("checkins");
        mMessagesDatabaseReference = mFirebaseDatabase.getReference();

        //final List<PunchCardsFB> punchcardsFB = new ArrayList<>();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                //Toast.makeText(Punch_Cards.this, mUserLoggedIn, Toast.LENGTH_SHORT).show();
                if (user != null) {
                    //Toast.makeText(Punch_Cards.this, "You're now signed on to punc_cards", Toast.LENGTH_SHORT).show();
                    //onSignedInitialize(user.getDisplayName());
                    mUserLoggedIn = user.getEmail();
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
            }
        };

        mReloadCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogueBox();

            }
        });

        mPunchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder b = new AlertDialog.Builder(Punch_Cards.this);
                b.setTitle("Are you sure you want to punch in?");
                b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(Punch_Cards.this, "Thanks for punching in!", Toast.LENGTH_LONG).show();

                        Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        // Vibrate for 500 milliseconds
                        vib.vibrate(50);
                        mMessagesDatabaseReference
                                .child("punch_cards")
                                .orderByChild("username")
                                .equalTo(mUserLoggedIn)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                            //Toast.makeText(Punch_Cards.this, "=" + postSnapshot.child("username").getValue() + "=" + postSnapshot.getKey(), Toast.LENGTH_SHORT).show();
                                            //PunchCardsFB punchCardsFB1 = dataSnapshot.getValue(PunchCardsFB.class);
                                            Date currentTime = Calendar.getInstance().getTime();
                                            SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMddhma");
                                            String date1 = format1.format(currentTime);
                                            int punches_left = Integer.parseInt(postSnapshot.child("punchesleft").getValue().toString());
                                            punches_left = punches_left - 1;
                                            PunchCardsFB punchCardsFB2 = new PunchCardsFB(mUserLoggedIn, String.valueOf(punches_left), date1);
                                            mMessagesDatabaseReference.child("punch_cards").child(postSnapshot.getKey()).setValue(punchCardsFB2);
                                            mark_redeemed(String.valueOf(punches_left));

                                            card_stat_fb card_stat_fb = new card_stat_fb(mUserLoggedIn, "punch_in", date1);
                                            mMessagesDatabaseReference.child("card_stat").push().setValue(card_stat_fb);
                                        }

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                    }
                });
                b.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(Punch_Cards.this, "Punch Cancelled.", Toast.LENGTH_SHORT).show();
                    }
                });
                b.show();
            }
        });


    }

    public void SetCards() {

            mMessagesDatabaseReference
                    .child("punch_cards")
                    .orderByChild("username")
                    .equalTo(mUserLoggedIn)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()) {
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    String punchcount = postSnapshot.child("punchesleft").getValue().toString();
                                    mark_redeemed(punchcount);
                                    //mReloadCard.setEnabled(FALSE);
                                    //mReloadCard.setImageResource(R.mipmap.disabledreload);
                                }
                            }
                            else
                            {
                                mReloadCard.setEnabled(TRUE);
                                mReloadCard.setImageResource(R.mipmap.reload2);
                                mPunchButton.setEnabled(FALSE);
                                mPunchButton.setImageResource(R.mipmap.grayboxingglove);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


    }

    public void mark_redeemed(String redeemed) {
        if (redeemed.equals("0")) {
            mRing.setImageResource(R.mipmap.ring9);
            mReloadCard.setEnabled(TRUE);
            mReloadCard.setImageResource(R.mipmap.reload2);
            mPunchButton.setEnabled(FALSE);
            mPunchButton.setImageResource(R.mipmap.grayboxingglove);
        }
        if (redeemed.equals("1")) {
            mRing.setImageResource(R.mipmap.ring8);
            mReloadCard.setEnabled(FALSE);
            mReloadCard.setImageResource(R.mipmap.disabledreload);
        }
        if (redeemed.equals("2")) {
            mRing.setImageResource(R.mipmap.ring7);
            mReloadCard.setEnabled(FALSE);
            mReloadCard.setImageResource(R.mipmap.disabledreload);
        }
        if (redeemed.equals("3")) {
            mRing.setImageResource(R.mipmap.ring6);
            mReloadCard.setEnabled(FALSE);
            mReloadCard.setImageResource(R.mipmap.disabledreload);
        }
        if (redeemed.equals("4")) {
            mRing.setImageResource(R.mipmap.ring5);
            mReloadCard.setEnabled(FALSE);
            mReloadCard.setImageResource(R.mipmap.disabledreload);
        }
        if (redeemed.equals("5")) {
            mRing.setImageResource(R.mipmap.ring4);
            mReloadCard.setEnabled(FALSE);
            mReloadCard.setImageResource(R.mipmap.disabledreload);
        }
        if (redeemed.equals("6")) {
            mRing.setImageResource(R.mipmap.ring3);
            mReloadCard.setEnabled(FALSE);
            mReloadCard.setImageResource(R.mipmap.disabledreload);
        }
        if (redeemed.equals("7")) {
            mRing.setImageResource(R.mipmap.ring2);
            mReloadCard.setEnabled(FALSE);
            mReloadCard.setImageResource(R.mipmap.disabledreload);

        }
        if (redeemed.equals("8")) {
            mRing.setImageResource(R.mipmap.ring1);
            mReloadCard.setEnabled(FALSE);
            mReloadCard.setImageResource(R.mipmap.disabledreload);
        }
        if (redeemed.equals("9")) {
            mRing.setImageResource(R.mipmap.ring0);
            mReloadCard.setEnabled(FALSE);
            mReloadCard.setImageResource(R.mipmap.disabledreload);
            mPunchButton.setEnabled(TRUE);
            mPunchButton.setImageResource(R.mipmap.boxingcardbutton);
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

    public void DialogueBox() {
        AlertDialog.Builder b = new AlertDialog.Builder(Punch_Cards.this);
        b.setTitle("Are you sure you want to buy/recharge your punch card? The price is $90 for nine visits.");
        b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                RechargeCard();
            }
        });
        b.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        b.show();
    }

    public void RechargeCard()
    {
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMddhma");
        final String date1 = format1.format(currentTime);
        card_stat_fb card_stat_fb = new card_stat_fb(mUserLoggedIn, "recharge_card", date1);
        mMessagesDatabaseReference.child("card_stat").push().setValue(card_stat_fb);
        mMessagesDatabaseReference
                .child("punch_cards")
                .orderByChild("username")
                .equalTo(mUserLoggedIn)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                PunchCardsFB punchCardsFB2 = new PunchCardsFB(mUserLoggedIn, "9", date1);
                                mMessagesDatabaseReference.child("punch_cards").child(postSnapshot.getKey()).setValue(punchCardsFB2);
                                mReloadCard.setEnabled(FALSE);
                                mReloadCard.setImageResource(R.mipmap.disabledreload);
                                mPunchButton.setEnabled(TRUE);
                                mPunchButton.setImageResource(R.mipmap.boxingcardbutton);
                                mark_redeemed("9");
                            }
                        }
                        else
                        {
                            PunchCardsFB punchCardsFB2 = new PunchCardsFB(mUserLoggedIn, "9", date1);
                            mMessagesDatabaseReference.child("punch_cards").push().setValue(punchCardsFB2);
                            mReloadCard.setEnabled(FALSE);
                            mReloadCard.setImageResource(R.mipmap.disabledreload);
                            mPunchButton.setEnabled(TRUE);
                            mPunchButton.setImageResource(R.mipmap.boxingcardbutton);
                            mark_redeemed("9");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }


}