package com.wangdiam.studybuddycapstoneproject.ui.fragments;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.wangdiam.studybuddycapstoneproject.BuildConfig;
import com.wangdiam.studybuddycapstoneproject.R;
import com.wangdiam.studybuddycapstoneproject.models.Card;
import com.wangdiam.studybuddycapstoneproject.viewmodels.CardViewModel;

import static com.wangdiam.studybuddycapstoneproject.ui.activities.AllSubjectsActivity.SUBJECT_ID;
import static com.wangdiam.studybuddycapstoneproject.ui.activities.ReviewModeActivity.CARD_ID;
import static com.wangdiam.studybuddycapstoneproject.ui.adapters.SpecificCardReviewModeAdapter.POSITION;


public class SpecificReviewModeCardFragment extends Fragment {

    CardViewModel cardViewModel;
    Card card;
    int position;
    private AdView adView;
    TextView reviewModeSideTV,reviewModeContentTV;
    private MutableLiveData<DataSnapshot> mutableLiveData;

    public SpecificReviewModeCardFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(POSITION);
            cardViewModel = ViewModelProviders.of(this).get(CardViewModel.class);
            mutableLiveData = cardViewModel.getCardWithID(
                    getActivity().getIntent().getLongExtra(CARD_ID,0),
                    getActivity().getIntent().getLongExtra(SUBJECT_ID,0));

        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review_mode,container,false);
        reviewModeSideTV = view.findViewById(R.id.review_mode_side_tv);
        reviewModeContentTV = view.findViewById(R.id.review_mode_content_tv);
        adView = view.findViewById(R.id.adView);
        AdRequest.Builder builder = new AdRequest.Builder();
        AdRequest adRequest = builder.build();
        adRequest.isTestDevice(getContext());
        adView.loadAd(adRequest);
        mutableLiveData.observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(@Nullable DataSnapshot dataSnapshot) {
                card = dataSnapshot.getValue(Card.class);
                switch (position){
                    case 0:
                        reviewModeContentTV.setText(card!=null?card.getFront():"");
                        reviewModeSideTV.setText(getString(R.string.front));
                        break;
                    case 1:
                        reviewModeContentTV.setText(card!=null?card.getBack():"");
                        reviewModeSideTV.setText(getString(R.string.back));
                        break;
                    default:
                        break;

                }
            }
        });


        return view;
    }

}
