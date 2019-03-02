package com.wangdiam.studybuddycapstoneproject.ui.activities;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;
import com.wangdiam.studybuddycapstoneproject.R;
import com.wangdiam.studybuddycapstoneproject.models.Card;
import com.wangdiam.studybuddycapstoneproject.ui.adapters.AllCardsAdapter;
import com.wangdiam.studybuddycapstoneproject.ui.adapters.ReviewModeAdapter;
import com.wangdiam.studybuddycapstoneproject.viewmodels.CardViewModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.wangdiam.studybuddycapstoneproject.ui.activities.AllSubjectsActivity.SUBJECT;
import static com.wangdiam.studybuddycapstoneproject.ui.activities.AllSubjectsActivity.SUBJECT_ID;
import static com.wangdiam.studybuddycapstoneproject.ui.activities.ReviewModeActivity.CARD_ID;

public class AllCardsActivity extends AppCompatActivity {
    @BindView(R.id.all_cards_rv) RecyclerView recyclerView;
    CardViewModel cardViewModel;
    ArrayList<Card> retrievedCards;
    @BindView(R.id.no_subject_cards_in_all_tv) TextView noSubjectCardsInAllTV;
    AllCardsAdapter allCardsAdapter;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.toolbar) Toolbar toolbar;
    SlidrInterface slidrInterface;
    public static String FROM_ALL_CARDS = "FROMALLCARDS";
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_cards);
        ButterKnife.bind(this);
        slidrInterface = Slidr.attach(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("All Cards");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cardViewModel = ViewModelProviders.of(this).get(CardViewModel.class);
        allCardsAdapter = new AllCardsAdapter(this);
        recyclerView.setAdapter(allCardsAdapter);
        recyclerView.setHasFixedSize(true);

        MutableLiveData<DataSnapshot> mutableLiveData = cardViewModel.getAllCardsLiveData();
        mutableLiveData.observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(@Nullable DataSnapshot dataSnapshot) {
                retrievedCards = new ArrayList<>();
                for (DataSnapshot subject: dataSnapshot.getChildren()) {
                    for (DataSnapshot card: subject.getChildren()) {
                        try {
                            Card currentCard = card.getValue(Card.class);
                            retrievedCards.add(currentCard);
                        } catch (Exception e) {

                        }
                    }
                }
                if (retrievedCards.size() == 0) {
                    noSubjectCardsInAllTV.setVisibility(View.VISIBLE);
                } else {
                    noSubjectCardsInAllTV.setVisibility(View.INVISIBLE);
                }
                final Context context = recyclerView.getContext();
                final LayoutAnimationController controller =
                        AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_from_bottom);

                if (count == 0) recyclerView.setLayoutAnimation(controller);
                allCardsAdapter.submitList(retrievedCards);
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

        allCardsAdapter.setOnCardItemClickListener(new AllCardsAdapter.OnCardItemClickListener() {
            @Override
            public void onCardItemClicked(Card card) {
                Intent intent = new Intent(AllCardsActivity.this, SpecificReviewModeCardActivity.class);
                intent.putExtra(FROM_ALL_CARDS,"fromallcardsactivity");
                intent.putExtra(CARD_ID,card.getId());
                intent.putExtra(SUBJECT_ID,card.getSubjectId());
                intent.putExtra(SUBJECT,getIntent().getStringExtra(SUBJECT));
                startActivity(intent);
            }
        });

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
                String[] options = {"Add a new subject", "Add a new card"};

                AlertDialog.Builder builder = new AlertDialog.Builder(AllCardsActivity.this);
                builder.setTitle("Select an option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Intent intent = new Intent(AllCardsActivity.this, CreateSubjectActivity.class);
                            startActivity(intent);
                        } else if (which == 1) {
                            Intent intent = new Intent(AllCardsActivity.this, CreateEditCardActivity.class);
                            startActivity(intent);
                        }
                    }
                });
                builder.show();
            }
        });
    }





    public void onItemRemove(final RecyclerView.ViewHolder viewHolder, final RecyclerView recyclerView) {
        final int adapterPosition = viewHolder.getAdapterPosition();
        final Card card = allCardsAdapter.getCardAt(adapterPosition);
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
        cardViewModel.deleteCard(allCardsAdapter.getCardAt(viewHolder.getAdapterPosition()));

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
