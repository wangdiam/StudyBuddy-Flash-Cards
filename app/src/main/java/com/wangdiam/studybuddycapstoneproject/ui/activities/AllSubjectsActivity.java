package com.wangdiam.studybuddycapstoneproject.ui.activities;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;
import com.wangdiam.studybuddycapstoneproject.R;
import com.wangdiam.studybuddycapstoneproject.ui.adapters.SubjectAdapter;
import com.wangdiam.studybuddycapstoneproject.models.Subject;
import com.wangdiam.studybuddycapstoneproject.ui.adapters.SubjectAdapter.OnItemClickListener;
import com.wangdiam.studybuddycapstoneproject.viewmodels.SubjectViewModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AllSubjectsActivity extends AppCompatActivity implements OnItemClickListener{
    SubjectViewModel subjectViewModel;
    SubjectAdapter subjectAdapter;
    @BindView(R.id.no_subject_deck_tv) TextView noSubjectDecksTV;
    @BindView(R.id.all_subjects_rv) RecyclerView recyclerView;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.toolbar) Toolbar toolbar;
    SlidrInterface slidrInterface;
    public static final String SUBJECT = "SUBJECT";
    public static final String SUBJECT_ID = "SUBJECT_ID";
    public static final String ADAPTER_POSITION = "ADAPTER_POSITION";
    public static final String CARD_COUNT = "CARD_COUNT";
    public static final int CREATE_NEW_SUBJECT_DECK = 2;
    MutableLiveData<DataSnapshot> mutableSubjectsLiveData;
    ArrayList<Subject> subjects = new ArrayList<>();
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_subjects);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        setTitle("All Subject Decks");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        slidrInterface = Slidr.attach(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        subjectAdapter = new SubjectAdapter();
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

                if (count == 0) recyclerView.setLayoutAnimation(controller);
                subjectAdapter.submitList(subjects);
                if (count == 0) recyclerView.scheduleLayoutAnimation();
                count++;
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && fab.getVisibility() == View.VISIBLE) {
                    fab.hide();
                } else if (dy < 0 && fab.getVisibility() != View.VISIBLE) {
                    fab.show();
                }
            }});

        subjectAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClicked(Subject subject, int position) {
                subject = subjectAdapter.getSubjectAt(position);
                Intent intent = new Intent(AllSubjectsActivity.this,ModeChooserActivity.class);
                intent.putExtra(SUBJECT,subject.getName());
                intent.putExtra(ADAPTER_POSITION, position);
                intent.putExtra(SUBJECT_ID,subject.getId());
                startActivity(intent);
            }

            @Override
            public void onEditClicked(int position) {
                Intent intent = new Intent(AllSubjectsActivity.this,CreateSubjectActivity.class);
                Subject subject = subjectAdapter.getSubjectAt(position);
                intent.putExtra(SUBJECT,subject.getName());
                intent.putExtra(ADAPTER_POSITION, position);
                intent.putExtra(SUBJECT_ID,subject.getId());
                intent.putExtra(CARD_COUNT, subject.getCardCount());
                startActivity(intent);
            }

            @Override
            public void onDeleteClicked(final int position) {
                AlertDialog.Builder b = new AlertDialog.Builder(AllSubjectsActivity.this);
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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] options = {"Add a new subject", "Add a new card"};

                AlertDialog.Builder builder = new AlertDialog.Builder(AllSubjectsActivity.this);
                builder.setTitle("Select an option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Intent intent = new Intent(AllSubjectsActivity.this, CreateSubjectActivity.class);
                            startActivity(intent);
                        } else if (which == 1) {
                            Intent intent = new Intent(AllSubjectsActivity.this, CreateEditCardActivity.class);
                            startActivity(intent);
                        }
                    }
                });
                builder.show();
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_NEW_SUBJECT_DECK && resultCode == RESULT_OK) {

        } else {
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onItemClicked(Subject subject, int position) {

    }

    @Override
    public void onEditClicked(int position) {

    }

    @Override
    public void onDeleteClicked(int position) {

    }
}
