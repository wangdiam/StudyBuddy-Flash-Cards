package com.wangdiam.studybuddycapstoneproject.ui.activities;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.wangdiam.studybuddycapstoneproject.R;
import com.wangdiam.studybuddycapstoneproject.models.Card;
import com.wangdiam.studybuddycapstoneproject.models.Subject;
import com.wangdiam.studybuddycapstoneproject.viewmodels.CardViewModel;
import com.wangdiam.studybuddycapstoneproject.viewmodels.SubjectViewModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.wangdiam.studybuddycapstoneproject.ui.activities.AllCardsActivity.FROM_ALL_CARDS;
import static com.wangdiam.studybuddycapstoneproject.ui.activities.AllSubjectsActivity.ADAPTER_POSITION;
import static com.wangdiam.studybuddycapstoneproject.ui.activities.AllSubjectsActivity.SUBJECT;
import static com.wangdiam.studybuddycapstoneproject.ui.activities.AllSubjectsActivity.SUBJECT_ID;
import static com.wangdiam.studybuddycapstoneproject.ui.activities.ReviewModeActivity.CARD_ID;

public class CreateEditCardActivity extends AppCompatActivity {
    @BindView(R.id.select_subject_spinner) Spinner spinner;
    SubjectViewModel subjectViewModel;
    CardViewModel cardViewModel;
    @BindView(R.id.card_front_et) EditText cardFrontET;
    @BindView(R.id.card_back_et) EditText cardBackET;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.toolbar) Toolbar toolbar;
    ArrayList<Subject> subjects = new ArrayList<>();
    Card card;
    Integer currentSpinnerIndex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_card);
        ButterKnife.bind(this);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "Please Sign In", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        setSupportActionBar(toolbar);
        if (getIntent().getLongExtra(CARD_ID,0) != 0) setTitle("Edit Card");
        else setTitle("Add New Card");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        cardViewModel = ViewModelProviders.of(this).get(CardViewModel.class);
        subjectViewModel = ViewModelProviders.of(this).get(SubjectViewModel.class);
        MutableLiveData<DataSnapshot> dataSnapshotMutableLiveData = subjectViewModel.getAllSubjectsLiveData();
        dataSnapshotMutableLiveData.observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(@Nullable DataSnapshot dataSnapshot) {
                subjects = new ArrayList<>();
                spinnerAdapter.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Subject subject = snapshot.getValue((Subject.class));
                    subjects.add(subject);
                    spinnerAdapter.add(subject.getName());
                }
                Intent intent = getIntent();
                if (intent != null) {
                    MutableLiveData<DataSnapshot> mutableLiveData = cardViewModel.getCardWithID(getIntent().getLongExtra(CARD_ID,0),getIntent().getLongExtra(SUBJECT_ID,0));
                    mutableLiveData.observe(CreateEditCardActivity.this, new Observer<DataSnapshot>() {
                        @Override
                        public void onChanged(@Nullable DataSnapshot dataSnapshot) {
                            card = dataSnapshot.getValue(Card.class);
                            cardFrontET.setText(card != null?card.getFront():"");
                            cardBackET.setText(card != null?card.getBack():"");
                        }
                    });

                    String compareValue = intent.getStringExtra(SUBJECT);
                    if (compareValue != null && currentSpinnerIndex == null) currentSpinnerIndex = getIndex(spinner,compareValue);
                    else if (currentSpinnerIndex == null)currentSpinnerIndex = 0;
                    spinner.setSelection(currentSpinnerIndex,true);
                }

            }
        });
        spinnerAdapter.notifyDataSetChanged();








        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!cardFrontET.getText().toString().trim().equals("")&&!cardBackET.getText().toString().trim().equals("")&& spinner.getSelectedItem() != null) {
                    Card tempCard = new Card(cardFrontET.getText().toString(),cardBackET.getText().toString(),subjects.get(spinner.getSelectedItemPosition()).getId(),subjects.get(spinner.getSelectedItemPosition()).getName());
                    if (getIntent().getLongExtra(CARD_ID,0) != 0 && getIntent().getStringExtra(FROM_ALL_CARDS) == null) {
                        tempCard.setId(card.getId());
                        cardViewModel.deleteCard(card);
                        cardViewModel.insertCard(tempCard);
                        Toast.makeText(CreateEditCardActivity.this, "Card has been updated", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CreateEditCardActivity.this, ReviewModeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(SUBJECT_ID, getIntent().getLongExtra(SUBJECT_ID, 0));
                        intent.putExtra(SUBJECT, getIntent().getStringExtra(SUBJECT));
                        startActivity(intent);
                    } else if (getIntent().getStringExtra(FROM_ALL_CARDS) != null) {
                        tempCard.setId(card.getId());
                        cardViewModel.deleteCard(card);
                        cardViewModel.insertCard(tempCard);
                        Toast.makeText(CreateEditCardActivity.this, "Card has been updated", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CreateEditCardActivity.this, AllCardsActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(SUBJECT_ID, getIntent().getLongExtra(SUBJECT_ID, 0));
                        intent.putExtra(SUBJECT, getIntent().getStringExtra(SUBJECT));
                        startActivity(intent);
                    } else {
                        cardViewModel.insertCard(tempCard);
                        setResult(RESULT_OK);
                        Toast.makeText(CreateEditCardActivity.this, "Card has been added to the deck", Toast.LENGTH_SHORT).show();
                    }
                    currentSpinnerIndex = spinner.getSelectedItemPosition();
                } else {
                    Toast.makeText(CreateEditCardActivity.this, "Please input all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!cardBackET.getText().toString().trim().equals("") || !cardFrontET.getText().toString().trim().equals("")) {
            new AlertDialog.Builder(this)
                    .setTitle("Leaving")
                    .setMessage("Are you sure you want to leave this page? Any changes will be discarded.")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        } else {
            finish();
        }
    }

    private int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getAdapter().getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }

        return 0;
    }
}
