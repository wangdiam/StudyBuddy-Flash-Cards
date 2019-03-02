package com.wangdiam.studybuddycapstoneproject.ui.activities;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;

import com.google.firebase.database.DataSnapshot;
import com.wangdiam.studybuddycapstoneproject.R;
import com.wangdiam.studybuddycapstoneproject.models.Card;
import com.wangdiam.studybuddycapstoneproject.ui.adapters.SpecificCardReviewModeAdapter;
import com.wangdiam.studybuddycapstoneproject.viewmodels.CardViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.wangdiam.studybuddycapstoneproject.ui.activities.AllCardsActivity.FROM_ALL_CARDS;
import static com.wangdiam.studybuddycapstoneproject.ui.activities.AllSubjectsActivity.SUBJECT;
import static com.wangdiam.studybuddycapstoneproject.ui.activities.AllSubjectsActivity.SUBJECT_ID;
import static com.wangdiam.studybuddycapstoneproject.ui.activities.ReviewModeActivity.CARD_ID;

public class SpecificReviewModeCardActivity extends AppCompatActivity {
    @BindView(R.id.fab_review_mode) FloatingActionButton fab;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.specific_review_mode_card_vp) ViewPager viewPager;
    int[] colorIntArray = {R.color.showAnswer,R.color.showQuestion};
    int[] iconIntArray = {R.drawable.ic_keyboard_arrow_right_black_24dp,R.drawable.ic_keyboard_arrow_left_black_24dp};
    CardViewModel cardViewModel;
    Card card;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_review_mode_card);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setTitle("Review Mode");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        cardViewModel = ViewModelProviders.of(this).get(CardViewModel.class);
        viewPager.setOffscreenPageLimit(1);
        SpecificCardReviewModeAdapter adapter = new SpecificCardReviewModeAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem((viewPager.getCurrentItem() == 0) ? 1 : 0,true);
            }
        });
        MutableLiveData<DataSnapshot> mutableLiveData = cardViewModel.getCardWithID(getIntent().getLongExtra(CARD_ID, 0), getIntent().getLongExtra(SUBJECT_ID, 0));
        mutableLiveData.observe(SpecificReviewModeCardActivity.this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(@Nullable DataSnapshot dataSnapshot) {
                card = dataSnapshot.getValue(Card.class);
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                animateFab(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }



    protected void animateFab(final int position) {
        fab.clearAnimation();
        // Scale down animation
        ScaleAnimation shrink =  new ScaleAnimation(1f, 0.0f, 1f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        shrink.setDuration(200);     // animation duration in milliseconds
        shrink.setInterpolator(new DecelerateInterpolator());
        shrink.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Change FAB color and icon
                fab.setBackgroundTintList(getResources().getColorStateList(colorIntArray[position]));
                fab.setImageDrawable(getResources().getDrawable(iconIntArray[position], null));

                // Scale up animation
                ScaleAnimation expand =  new ScaleAnimation(0.0f, 1f, 0.0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                expand.setDuration(150);     // animation duration in milliseconds
                expand.setInterpolator(new AccelerateDecelerateInterpolator());
                fab.startAnimation(expand);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        fab.startAnimation(shrink);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_review,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete) {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Card")
                    .setMessage("Are you sure you want to delete this card? This action is irreversible.")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cardViewModel.deleteCard(card);
                            finish();
                        }
                    })
            .setNegativeButton("No",null)
            .show();
        } else if (id == R.id.action_edit) {
            Intent intent = new Intent(SpecificReviewModeCardActivity.this,CreateEditCardActivity.class);
            intent.putExtra(SUBJECT_ID,card.getSubjectId());
            intent.putExtra(CARD_ID,card.getId());
            intent.putExtra(SUBJECT,card.getSubjectName());
            intent.putExtra(FROM_ALL_CARDS,getIntent().getStringExtra(FROM_ALL_CARDS));
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
