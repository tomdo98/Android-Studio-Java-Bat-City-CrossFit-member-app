package com.tommybear.batcitycrossfit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
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
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by dot on 9/1/2017.
 */

public class gym_store extends AppCompatActivity {

    //for firebase
    private DatabaseReference mMessagesDatabaseReference;
    private DatabaseReference mMessagesDatabaseReferencePurchases;

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mFirebaseAuth;
    //private MessageAdapter mMessageAdapter;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    public static final String ANONYMOUS = "anonymous";
    public static final int RC_SIGN_IN = 1;
    private String mUsername;
    private ChildEventListener mChildEventListener;
    private String mUserLoggedIn;

//    private ImageButton mButtonAddTshirttocart;
//    private ImageButton mButtonAddFitAid;
//    private ImageButton mButtonDropInFee;
//    private TextView mTextViewShoppingCart;
//    private TextView mTextViewRunningTotal;
//
//    private Button mButtonPurchase;
//
//    private Button mButtonClearList;

    private String mShoppingCart;
    private int intRunningTotal;

    private ListView mMessageListView;
    private StoreItemsAdapter mStoreItemsAdapter;
    private TextView mDescription;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gym_store);



        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mMessagesDatabaseReference = mFirebaseDatabase.getReference();
        mMessagesDatabaseReferencePurchases = mFirebaseDatabase.getReference();
//        mButtonAddTshirttocart = (ImageButton) findViewById(R.id.imageButtonTshirt);
//        mButtonAddFitAid = (ImageButton) findViewById(R.id.imageButtonFitAid);
//        mButtonDropInFee = (ImageButton) findViewById(R.id.imageButtonDropInFee);
//        mTextViewShoppingCart = (TextView) findViewById(R.id.TextViewShoppingCart);
//        mTextViewRunningTotal = (TextView) findViewById(R.id.TextViewRunningTotal);
//        mButtonClearList = (Button) findViewById(R.id.buttonClearList);
//        mButtonPurchase = (Button) findViewById(R.id.buttonPurchase);
        mDescription = (TextView) findViewById(R.id.nameTextView);

        mMessageListView = (ListView) findViewById(R.id.storelistview);

        List<StoreItemsMessage> storeitems = new ArrayList<>();
        mStoreItemsAdapter = new StoreItemsAdapter(this, R.layout.storelayout, storeitems);
        mMessageListView.setAdapter(mStoreItemsAdapter);

        mMessageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Toast.makeText(gym_store.this, mStoreItemsAdapter.getItem(position).getdescription(), Toast.LENGTH_SHORT).show();
                DialogueBox(mStoreItemsAdapter.getItem(position).getdescription(),mStoreItemsAdapter.getItem(position).getprice());
            }
        });

        // Initialize message ListView and its adapter
        // List<FriendlyMessage> friendlyMessages = new ArrayList<>();
        // mMessageAdapter = new MessageAdapter(this, R.layout.item_message, friendlyMessages);
        // mMessageListView.setAdapter(mMessageAdapter);

        intRunningTotal = 0;
        mShoppingCart = "";
        //mTextViewRunningTotal.setText("0");
//        mButtonAddFitAid.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                intRunningTotal = intRunningTotal + 3;
//                mTextViewRunningTotal.setText("$" + String.valueOf(intRunningTotal));
//                mShoppingCart = mShoppingCart + "\n\rFit Aid $3";
//                mTextViewShoppingCart.setText(mShoppingCart);
//            }
//        });

//        mButtonAddTshirttocart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                intRunningTotal = intRunningTotal + 20;
//                mTextViewRunningTotal.setText("$" + String.valueOf(intRunningTotal));
//                mShoppingCart = mShoppingCart + "\n\rTShirt $20";
//                mTextViewShoppingCart.setText(mShoppingCart);
//            }
//        });

//        mButtonDropInFee.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                intRunningTotal = intRunningTotal + 15;
//                mTextViewRunningTotal.setText("$" + String.valueOf(intRunningTotal));
//                mShoppingCart = mShoppingCart + "\n\rDrop in Fee $15";
//                mTextViewShoppingCart.setText(mShoppingCart);
//            }
//        });
//        mButtonClearList.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mShoppingCart = "";
//                intRunningTotal = 0;
//                mTextViewShoppingCart.setText("");
//                mTextViewRunningTotal.setText("0");
//            }
//        });

//        mButtonPurchase.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DialogueBox();
//            }
//        });
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                mUserLoggedIn = user.getEmail();
                //Toast.makeText(Punch_Cards.this, mUserLoggedIn, Toast.LENGTH_SHORT).show();
                if (user != null) {
                    //Toast.makeText(Punch_Cards.this, "You're now signed on to punc_cards", Toast.LENGTH_SHORT).show();
                    onSignedInitialize(user.getDisplayName());
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

            }
        };


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
        mStoreItemsAdapter.clear();
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
        attachDatabaseReadListener();
        //attachDatabaseReadListener();
    }

    public void DialogueBox(final String item, final String price) {
   //     if (intRunningTotal != 0) {
            AlertDialog.Builder b = new AlertDialog.Builder(gym_store.this);
       //     b.setTitle("Are you sure you want to make to buy " + item + " for $" + String.valueOf(intRunningTotal) + "?");
        b.setTitle("Are you sure you want to buy " + item + " for $" + price + "?");
            b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {
                    Purchase(item, price);
                    Toast.makeText(gym_store.this, "Thank you for shopping with us. You will get a receipt once your purchase is processed.", Toast.LENGTH_LONG).show();
                 //   mShoppingCart = "";
                  //  intRunningTotal = 0;
                //    mTextViewShoppingCart.setText("");
                 //   mTextViewRunningTotal.setText("0");
                }
            });
            b.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(gym_store.this, "Purchase Cancelled.", Toast.LENGTH_SHORT).show();
                }
            });
            b.show();

    }

    public void Purchase(String item, String price){

        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMddhma");
        String date1 = format1.format(currentTime);
        gym_store_fb gym_store = new gym_store_fb(mUserLoggedIn, item, date1, price );
        mMessagesDatabaseReferencePurchases.child("purchases").push().setValue(gym_store);
    }

    private void attachDatabaseReadListener() {
        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("store");
        mMessagesDatabaseReferencePurchases = mFirebaseDatabase.getReference();
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    StoreItemsMessage storeItemsMessage = dataSnapshot.getValue(StoreItemsMessage.class);
                    mStoreItemsAdapter.add(storeItemsMessage);
                }

                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                public void onCancelled(DatabaseError databaseError) {
                }
            };
            mMessagesDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }
}
