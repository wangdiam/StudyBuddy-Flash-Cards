package com.wangdiam.studybuddycapstoneproject.ui.activities;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;
import com.wangdiam.studybuddycapstoneproject.R;
import com.wangdiam.studybuddycapstoneproject.ui.adapters.ReviewModeAdapter;
import com.wangdiam.studybuddycapstoneproject.models.Card;
import com.wangdiam.studybuddycapstoneproject.models.Subject;
import com.wangdiam.studybuddycapstoneproject.viewmodels.CardViewModel;
import com.wangdiam.studybuddycapstoneproject.viewmodels.SubjectViewModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.wangdiam.studybuddycapstoneproject.ui.activities.AllSubjectsActivity.ADAPTER_POSITION;
import static com.wangdiam.studybuddycapstoneproject.ui.activities.AllSubjectsActivity.SUBJECT_ID;
import static com.wangdiam.studybuddycapstoneproject.ui.activities.AllSubjectsActivity.SUBJECT;

public class ReviewModeActivity extends AppCompatActivity {
    public static final String CARD_ID = "CARD_ID";
    CardViewModel cardViewModel;
    SubjectViewModel subjectViewModel;
    ReviewModeAdapter reviewModeAdapter;
    SlidrInterface slidrInterface;
    ArrayList<Card> retrievedCards;
    String subjectName;
    int count=0;
    @BindView(R.id.no_subject_cards_tv) TextView noSubjectCardsTV;
    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.review_mode_rv) RecyclerView recyclerView;
    @BindView(R.id.fab) FloatingActionButton fab;
    public static final int CREATE_NEW_SUBJECT_CARD = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_mode);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setTitle("All " + getIntent().getStringExtra(SUBJECT) + " Cards");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        slidrInterface = Slidr.attach(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewModeAdapter = new ReviewModeAdapter(this);
        recyclerView.setAdapter(reviewModeAdapter);
        recyclerView.setHasFixedSize(true);

        cardViewModel = ViewModelProviders.of(this).get(CardViewModel.class);
        Subject subject = new Subject(getIntent().getStringExtra(SUBJECT));
        subject.setId(getIntent().getLongExtra(SUBJECT_ID,0));
        subjectViewModel = ViewModelProviders.of(this).get(SubjectViewModel.class);
        MutableLiveData<DataSnapshot> mutableSubjectsLiveData = subjectViewModel.getUpdatedSubjectNameWithId(getIntent().getLongExtra(SUBJECT_ID,0));
        mutableSubjectsLiveData.observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(@Nullable DataSnapshot dataSnapshot) {
                subjectName = dataSnapshot.getValue().toString();
            }
        });

        MutableLiveData<DataSnapshot> mutableLiveData = cardViewModel.getAllCardsForSubjectLiveData(subject);
        mutableLiveData.observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(@Nullable DataSnapshot dataSnapshot) {
                retrievedCards = new ArrayList<>();
                for (DataSnapshot card: dataSnapshot.getChildren()) {
                    try {
                        Card currentCard = card.getValue(Card.class);
                        retrievedCards.add(currentCard);
                    } catch (Exception e) {

                    }
                }
                if (retrievedCards.size() == 0) {
                    noSubjectCardsTV.setVisibility(View.VISIBLE);
                } else {
                    noSubjectCardsTV.setVisibility(View.INVISIBLE);
                }
                final Context context = recyclerView.getContext();
                final LayoutAnimationController controller =
                        AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_from_bottom);
                if (count == 0) recyclerView.setLayoutAnimation(controller);
                reviewModeAdapter.submitList(retrievedCards);
                if (count == 0) recyclerView.scheduleLayoutAnimation();
                count++;
            }
        });

        reviewModeAdapter.setOnCardItemClickListener(new ReviewModeAdapter.OnCardItemClickListener() {
            @Override
            public void onCardItemClicked(Card card) {
                Intent intent = new Intent(ReviewModeActivity.this, SpecificReviewModeCardActivity.class);
                intent.putExtra(CARD_ID,card.getId());
                intent.putExtra(SUBJECT_ID,getIntent().getLongExtra(SUBJECT_ID,0));
                intent.putExtra(SUBJECT,getIntent().getStringExtra(SUBJECT));
                startActivity(intent);
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

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                onItemRemove(viewHolder,recyclerView);
            }
        }).attachToRecyclerView(recyclerView);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReviewModeActivity.this,CreateEditCardActivity.class);
                intent.putExtra(SUBJECT,getIntent().getStringExtra(SUBJECT));
                intent.putExtra(ADAPTER_POSITION,getIntent().getIntExtra(ADAPTER_POSITION,0));
                startActivityForResult(intent,CREATE_NEW_SUBJECT_CARD);
            }
        });
    }

    public void onItemRemove(final RecyclerView.ViewHolder viewHolder, final RecyclerView recyclerView) {
        final int adapterPosition = viewHolder.getAdapterPosition();
        final Card card = reviewModeAdapter.getCardAt(adapterPosition);
        Snackbar snackbar = Snackbar
                .make(recyclerView, "Card removed", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int mAdapterPosition = viewHolder.getAdapterPosition();
                        cardViewModel.insertCard(card);
                        recyclerView.scrollToPosition(mAdapterPosition);
                    }
                });
        snackbar.show();
        cardViewModel.deleteCard(reviewModeAdapter.getCardAt(viewHolder.getAdapterPosition()));

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_NEW_SUBJECT_CARD && resultCode == RESULT_OK) {
        } else {
        }
    }
}
