package com.wangdiam.studybuddycapstoneproject.ui.activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;
import com.wangdiam.studybuddycapstoneproject.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener;

import static com.wangdiam.studybuddycapstoneproject.ui.activities.AllSubjectsActivity.SUBJECT_ID;
import static com.wangdiam.studybuddycapstoneproject.ui.activities.AllSubjectsActivity.SUBJECT;

public class ModeChooserActivity extends AppCompatActivity {
    @BindView(R.id.review_mode_cv) CardView reviewModeCV;
    @BindView(R.id.test_mode_cv) CardView testModeCV;
    @BindView(R.id.subject_mode_chooser_tv) TextView subjectChosenTV;
    private SlidrInterface slidrInterface;
    @BindView(R.id.mode_chooser_review_ll) LinearLayout modeChooserReviewLL;
    @BindView(R.id.toolbar) Toolbar toolbar;
    public static final String SHARED_ELEMENT = "SHARED_ELEMENT";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_chooser);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Choose A Mode");

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean("firstTimeModeChooser", false)) {
            new GuideView.Builder(this)
                    .setTitle("Review Mode")
                    .setContentText("Review cards in chronological order with access to answers")
                    .setTargetView(reviewModeCV)
                    .setDismissType(DismissType.anywhere)
                    .setGuideListener(new GuideListener() {
                        @Override
                        public void onDismiss(View view) {
                            new GuideView.Builder(view.getContext())
                                    .setTitle("Test Mode")
                                    .setContentText("Quiz yourself in this mode by answering your questions in random order")
                                    .setTargetView(testModeCV)
                                    .setDismissType(DismissType.anywhere)
                                    .build()
                                    .show();
                        }
                    })
                    .build()
                    .show();




            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTimeModeChooser", true);
            editor.commit();
        }
        slidrInterface = Slidr.attach(this);
        subjectChosenTV.setText(String.format("Current Subject: %s", getIntent().getStringExtra(SUBJECT)));
        reviewModeCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ModeChooserActivity.this, ReviewModeActivity.class);
                intent.putExtra(SUBJECT_ID,getIntent().getLongExtra(SUBJECT_ID,0));
                intent.putExtra(SUBJECT,getIntent().getStringExtra(SUBJECT));
                startActivity(intent);
            }
        });

        testModeCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(ModeChooserActivity.this,TestModeActivity.class);
                intent.putExtra(SUBJECT,getIntent().getStringExtra(SUBJECT));
                intent.putExtra(SUBJECT_ID,getIntent().getLongExtra(SUBJECT_ID,0));
                startActivity(intent);*/
                Snackbar.make(v, "Coming soon", Snackbar.LENGTH_LONG)
                        .setAction("Okay", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
