package com.wangdiam.studybuddycapstoneproject.ui.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.wangdiam.studybuddycapstoneproject.BuildConfig;
import com.wangdiam.studybuddycapstoneproject.R;
import com.wangdiam.studybuddycapstoneproject.models.Subject;
import com.wangdiam.studybuddycapstoneproject.services.ReminderJobService;
import com.wangdiam.studybuddycapstoneproject.ui.adapters.SubjectAdapter;
import com.wangdiam.studybuddycapstoneproject.viewmodels.CardViewModel;
import com.wangdiam.studybuddycapstoneproject.viewmodels.SubjectViewModel;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Optional;
import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;

import static com.wangdiam.studybuddycapstoneproject.application.StudyBuddyApp.REMINDER_JOB_SERVICE;
import static com.wangdiam.studybuddycapstoneproject.ui.activities.AllSubjectsActivity.ADAPTER_POSITION;
import static com.wangdiam.studybuddycapstoneproject.ui.activities.AllSubjectsActivity.CARD_COUNT;
import static com.wangdiam.studybuddycapstoneproject.ui.activities.AllSubjectsActivity.SUBJECT;
import static com.wangdiam.studybuddycapstoneproject.ui.activities.AllSubjectsActivity.SUBJECT_ID;


public class LandingActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;

    @BindView(R.id.hello_user_tv) TextView mHelloUserTV;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.cards_to_study_tv) TextView cardsToStudyTV;
    CardViewModel cardViewModel;
    SubjectViewModel subjectViewModel;
    @BindView(R.id.go_to_cards_btn) Button beginStudyingButton;
    @BindView(R.id.landing_rv)
    RecyclerView recyclerView;
    MutableLiveData<DataSnapshot> mutableLiveData;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.no_subject_deck_tv) TextView noSubjectDecksTV;
    private HeaderViewHolder mHeaderViewHolder;
    MutableLiveData<DataSnapshot> mutableSubjectsLiveData;
    ArrayList<Subject> subjects = new ArrayList<>();
    @BindView(R.id.adView) AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        setTitle("Study Buddy");
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            sendToStart();
            return;
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean("firstTime", false)) {
            new GuideView.Builder(this)
                    .setTitle("Add card/subject button")
                    .setContentText("Adds a card or subject deck")
                    .setTargetView(fab)
                    .setDismissType(DismissType.anywhere)
                    .build()
                    .show();


            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.commit();
        }

        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Calendar now = Calendar.getInstance();
        Calendar fivePMAfternoon = Calendar.getInstance();
        fivePMAfternoon.set(Calendar.HOUR, 5);
        fivePMAfternoon.set(Calendar.MINUTE, 0);
        fivePMAfternoon.set(Calendar.SECOND, 0);
        fivePMAfternoon.set(Calendar.MILLISECOND, 0);
        fivePMAfternoon.set(Calendar.AM_PM, Calendar.PM);
        long diff = fivePMAfternoon.getTimeInMillis() - now.getTimeInMillis();
        System.out.println(diff);
        if (diff < 0) {
            fivePMAfternoon.add(Calendar.DAY_OF_MONTH,1);
            diff = fivePMAfternoon.getTimeInMillis() - now.getTimeInMillis();
        }
        System.out.println(diff);
        int startSeconds = (int) (diff/1000);
        int endSeconds = startSeconds + 300;
        Job reminderJob = dispatcher.newJobBuilder()
                .setService(ReminderJobService.class)
                .setLifetime(Lifetime.FOREVER)
                .setTrigger(Trigger.executionWindow(startSeconds,endSeconds))
                .setReplaceCurrent(true)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setRecurring(true)
                .setTag(REMINDER_JOB_SERVICE)
                .build();

        dispatcher.mustSchedule(reminderJob);



        AdRequest.Builder builder = new AdRequest.Builder();
        AdRequest adRequest = builder.build();
        mAdView.loadAd(adRequest);
        cardViewModel = ViewModelProviders.of(this).get(CardViewModel.class);
        subjectViewModel = ViewModelProviders.of(this).get(SubjectViewModel.class);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final SubjectAdapter subjectAdapter = new SubjectAdapter();
        recyclerView.setAdapter(subjectAdapter);
        recyclerView.setHasFixedSize(true);
        subjectViewModel = ViewModelProviders.of(this).get(SubjectViewModel.class);
        mutableSubjectsLiveData = subjectViewModel.getAllSubjectsLiveData();




        mutableSubjectsLiveData.observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(@Nullable DataSnapshot dataSnapshot) {
                subjects = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Subject subject = snapshot.getValue(Subject.class);
                    subjects.add(subject);
                }
                if (subjects.size() == 0) {
                    noSubjectDecksTV.setVisibility(View.VISIBLE);
                } else {
                    noSubjectDecksTV.setVisibility(View.INVISIBLE);
                }
                final Context context = recyclerView.getContext();
                final LayoutAnimationController controller =
                        AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_from_bottom);
                subjects = new ArrayList<>(subjects.subList(0,(subjects.size() > 3) ? 3 : subjects.size()));
                recyclerView.setLayoutAnimation(controller);
                subjectAdapter.submitList(subjects);
                recyclerView.scheduleLayoutAnimation();
            }
        });

        subjectAdapter.setOnItemClickListener(new SubjectAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(Subject subject, int position) {
                subject = subjectAdapter.getSubjectAt(position);
                Intent intent = new Intent(LandingActivity.this, ModeChooserActivity.class);
                intent.putExtra(SUBJECT, subject.getName());
                intent.putExtra(ADAPTER_POSITION, position);
                intent.putExtra(SUBJECT_ID, subject.getId());
                startActivity(intent);
            }

            @Override
            public void onEditClicked(int position) {
                Intent intent = new Intent(LandingActivity.this,CreateSubjectActivity.class);
                Subject subject = subjectAdapter.getSubjectAt(position);
                intent.putExtra(SUBJECT,subject.getName());
                intent.putExtra(ADAPTER_POSITION, position);
                intent.putExtra(SUBJECT_ID,subject.getId());
                intent.putExtra(CARD_COUNT, subject.getCardCount());
                startActivity(intent);
            }

            @Override
            public void onDeleteClicked(final int position) {
                AlertDialog.Builder b = new AlertDialog.Builder(LandingActivity.this);
                b.setTitle("Delete")
                        .setMessage("Are you sure you want to delete this subject? This action is irreversible")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                subjectViewModel.deleteSubject(subjectAdapter.getSubjectAt(position));
                            }
                        })
                        .setNegativeButton("NO", null)
                        .show();
            }
        });

        MutableLiveData<DataSnapshot> cardCountLiveData = cardViewModel.getAllCardsLiveData();
        cardCountLiveData.observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(@Nullable DataSnapshot dataSnapshot) {
                Integer total = 0;
                for (DataSnapshot subject:dataSnapshot.getChildren()) {
                    total += Integer.parseInt(subject.child("cardCount").getValue().toString());
                }
                if (total != 0) cardsToStudyTV.setText(String.format("You have %s %s in total. Start studying now!", total.toString(), (total == 1) ? "card" : "cards"));
                else cardsToStudyTV.setText(getString(R.string.no_cards_message));
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] options = {"Add a new subject", "Add a new card"};

                AlertDialog.Builder builder = new AlertDialog.Builder(LandingActivity.this);
                builder.setTitle("Select an option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Intent intent = new Intent(LandingActivity.this, CreateSubjectActivity.class);
                            startActivity(intent);
                        } else if (which == 1) {
                            Intent intent = new Intent(LandingActivity.this, CreateEditCardActivity.class);
                            startActivity(intent);
                        }
                    }
                });
                builder.show();
            }
        });

        beginStudyingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LandingActivity.this,AllSubjectsActivity.class);
                startActivity(intent);
            }
        });



        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        mHeaderViewHolder = new HeaderViewHolder(header);
        if (mAuth.getCurrentUser() != null) {
            mutableLiveData = cardViewModel.getDisplayName();
            mutableLiveData.observe(LandingActivity.this, new Observer<DataSnapshot>() {
                @Override
                public void onChanged(@Nullable DataSnapshot dataSnapshot) {
                    try {
                        mHelloUserTV.setText(String.format("Hello, %s", dataSnapshot.getValue()));
                        mHeaderViewHolder.mHeaderUserNameTV.setText(dataSnapshot.getValue().toString());
                        mHeaderViewHolder.mHeaderUserEmailTV.setText(mAuth.getCurrentUser().getEmail());
                    } catch (Exception e) {
                        mHelloUserTV.setText(String.format("Hello, %s", mAuth.getCurrentUser().getDisplayName()));
                        mHeaderViewHolder.mHeaderUserEmailTV.setText(mAuth.getCurrentUser().getEmail());
                        mHeaderViewHolder.mHeaderUserNameTV.setText(mAuth.getCurrentUser().getDisplayName());
                    }

                }
            });
        }
    }



    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.landing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_decks) {
            drawer.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(LandingActivity.this,AllSubjectsActivity.class);
            startActivity(intent);
            // Handle the camera action
        } else if (id == R.id.nav_cards) {
            drawer.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(LandingActivity.this,AllCardsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Get StudyBuddy now!");
            sharingIntent.putExtra(Intent.EXTRA_TEXT,"Make memorizing easier and download StudyBuddy today!");
            startActivity(Intent.createChooser(sharingIntent, getString(R.string.action_share)));
        } else if (id == R.id.nav_settings) {
            drawer.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        navigationView.setCheckedItem(R.id.nav_home);

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            sendToStart();
        }
    }

    private void sendToStart() {
        Intent intent = new Intent(this,WelcomeActivity.class);
        startActivity(intent);
        finish();
    }

    protected static class HeaderViewHolder {

        @BindView(R.id.header_user_name_tv)
        protected TextView mHeaderUserNameTV;

        @BindView(R.id.header_user_email_tv)
        protected TextView mHeaderUserEmailTV;

        HeaderViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
