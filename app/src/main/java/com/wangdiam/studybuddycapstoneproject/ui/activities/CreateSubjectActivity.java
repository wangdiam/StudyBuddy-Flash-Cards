package com.wangdiam.studybuddycapstoneproject.ui.activities;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.wangdiam.studybuddycapstoneproject.R;
import com.wangdiam.studybuddycapstoneproject.models.Card;
import com.wangdiam.studybuddycapstoneproject.models.Subject;
import com.wangdiam.studybuddycapstoneproject.viewmodels.CardViewModel;
import com.wangdiam.studybuddycapstoneproject.viewmodels.SubjectViewModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.wangdiam.studybuddycapstoneproject.ui.activities.AllSubjectsActivity.CARD_COUNT;
import static com.wangdiam.studybuddycapstoneproject.ui.activities.AllSubjectsActivity.SUBJECT;
import static com.wangdiam.studybuddycapstoneproject.ui.activities.AllSubjectsActivity.SUBJECT_ID;

public class CreateSubjectActivity extends AppCompatActivity {
    SubjectViewModel subjectViewModel;
    CardViewModel cardViewModel;
    @BindView(R.id.add_subject_et) EditText mAddSubjectET;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_subject);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (getIntent().getLongExtra(SUBJECT_ID,0) != 0) {
            setTitle("Edit Subject Name");
            mAddSubjectET.setText(getIntent().getStringExtra(SUBJECT));
        } else {
            setTitle("Add New Subject");
        }
        subjectViewModel = ViewModelProviders.of(this).get(SubjectViewModel.class);
        cardViewModel = ViewModelProviders.of(this).get(CardViewModel.class);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mAddSubjectET.getText().toString().trim().equals("")) {
                    if (getIntent().getLongExtra(SUBJECT_ID,0) == 0) {
                        final Subject subject = new Subject(mAddSubjectET.getText().toString());
                        MutableLiveData<DataSnapshot> mutableLiveData = subjectViewModel.getAllSubjectsLiveData();
                        mutableLiveData.observe(CreateSubjectActivity.this, new Observer<DataSnapshot>() {
                            @Override
                            public void onChanged(@Nullable DataSnapshot dataSnapshot) {
                                boolean nameExists = false;
                                for (DataSnapshot subject: dataSnapshot.getChildren()) {
                                    String subjectName = subject.getValue(Subject.class).getName();
                                    if (subjectName.equals(mAddSubjectET.getText().toString())) {
                                        nameExists = true;
                                    }
                                }
                                if (!nameExists) subjectViewModel.insertSubject(subject);
                            }
                        });
                        setResult(RESULT_OK);
                        finish();
                        Toast.makeText(CreateSubjectActivity.this, "Subject has been added", Toast.LENGTH_SHORT).show();
                    } else {
                        Long subjectId = getIntent().getLongExtra(SUBJECT_ID,0);
                        final Subject subject = new Subject(mAddSubjectET.getText().toString());
                        subject.setId(subjectId);
                        subject.setCardCount(getIntent().getLongExtra(CARD_COUNT,0));
                        subjectViewModel.updateSubject(subject);

                        MutableLiveData<DataSnapshot> mutableLiveData = cardViewModel.getAllCardsForSubjectLiveData(subject);
                        mutableLiveData.observe(CreateSubjectActivity.this, new Observer<DataSnapshot>() {
                            @Override
                            public void onChanged(@Nullable DataSnapshot dataSnapshot) {
                                for (DataSnapshot card: dataSnapshot.getChildren()) {
                                    try {
                                        Card currentCard = card.getValue(Card.class);
                                        cardViewModel.updateCardSubjectName(currentCard,subject);
                                    } catch (Exception e) {

                                    }
                                }
                            }
                        });
                        setResult(RESULT_OK);
                        finish();
                        Toast.makeText(CreateSubjectActivity.this, "Subject name has been updated", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
