package com.tommybear.batcitycrossfit;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by dot on 10/3/2017.
 */

public class punch_type extends AppCompatActivity {

    private ImageButton mPunchCardCF;
    private ImageButton mPunchCardOly;
    private ImageButton mPunchCardCardio;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.punchcardchoices);

        mPunchCardCF = (ImageButton) findViewById(R.id.punchcardcrossfit);
        mPunchCardOly = (ImageButton) findViewById(R.id.punchcardoly);
        mPunchCardCardio = (ImageButton) findViewById(R.id.punchcardrowing);

        mPunchCardCF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PunchCardCF();
            }
        });
        mPunchCardOly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OlyPunchCardCF();
            }
        });
        mPunchCardCardio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CardioPunchCardCF();
            }
        });
    }
    public void PunchCardCF() {
        Intent i = new Intent(this, Punch_Cards.class);
        startActivity(i);
    }
    public void OlyPunchCardCF() {
        Intent i = new Intent(this, olypunchcard.class);
        startActivity(i);
    }
    public void CardioPunchCardCF() {
        Intent i = new Intent(this, cardio_class_punch_card.class);
        startActivity(i);
    }
}
