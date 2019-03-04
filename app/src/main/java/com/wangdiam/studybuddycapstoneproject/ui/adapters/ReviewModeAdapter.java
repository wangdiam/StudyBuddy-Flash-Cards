package com.wangdiam.studybuddycapstoneproject.ui.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wangdiam.studybuddycapstoneproject.R;
import com.wangdiam.studybuddycapstoneproject.models.Card;
import com.wangdiam.studybuddycapstoneproject.utils.ToastUtils;

import rm.com.longpresspopup.LongPressPopup;
import rm.com.longpresspopup.LongPressPopupBuilder;
import rm.com.longpresspopup.PopupInflaterListener;
import rm.com.longpresspopup.PopupOnHoverListener;
import rm.com.longpresspopup.PopupStateListener;
import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener;


public class ReviewModeAdapter extends ListAdapter<Card, ReviewModeAdapter.CardHolder>{
    private OnCardItemClickListener listener;
    Context context;
    RelativeLayout popupView;

    public static final DiffUtil.ItemCallback<Card> DIFF_CALLBACK = new DiffUtil.ItemCallback<Card>() {


        @Override
        public boolean areItemsTheSame(@NonNull Card card, @NonNull Card t1) {
            return card.getId().equals(t1.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Card card, @NonNull Card t1) {
            return card.getFront().equals(t1.getFront())  && card.getBack().equals(t1.getBack() ) && card.getSubjectId().equals(t1.getSubjectId());
        }
    };

    public ReviewModeAdapter(Context context) {
        super(DIFF_CALLBACK);
        this.context = context;
    }

    public Card getCardAt(int position) {
        return getItem(position);
    }

    @NonNull
    @Override
    public CardHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_item,viewGroup,false);
        return new CardHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CardHolder cardHolder, int i) {
        final Card card = getItem(i);
        cardHolder.mReviewModeQuestionTV.setText(card.getFront());
        cardHolder.mReviewModeSubjectTV.setText(card.getSubjectName());
        cardHolder.mReviewModeCV.setBackgroundColor(card.isSelected() ? Color.CYAN : Color.WHITE);
        cardHolder.onBind();

    }


    public class CardHolder extends RecyclerView.ViewHolder implements PopupInflaterListener, View.OnClickListener, PopupOnHoverListener, PopupStateListener {

        TextView mReviewModeQuestionTV, mReviewModeSubjectTV;
        TextView popUpAnswerTV,seeAllAnswerTV;
        CardView mReviewModeCV;

        public CardHolder(@NonNull final View itemView) {
            super(itemView);
            mReviewModeCV = itemView.findViewById(R.id.review_mode_individual_cv);
            mReviewModeQuestionTV = itemView.findViewById(R.id.review_mode_question_tv);
            mReviewModeSubjectTV = itemView.findViewById(R.id.review_mode_subject_tv);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(itemView.getContext());
            if (!prefs.getBoolean("firstTimeReviewMode", false)) {
                new GuideView.Builder(itemView.getContext())
                        .setTitle("Content card")
                        .setContentText("Click on the card to see detailed answers\n\nLong press the card to peek at the answer\n\nSwipe the card to delete it")
                        .setTargetView(mReviewModeCV)
                        .setDismissType(DismissType.anywhere)
                        .build()
                        .show();



                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("firstTimeReviewMode", true);
                editor.commit();
            }
            mReviewModeCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onCardItemClicked(getItem(position));
                    }
                }
            });


        }


        public void onBind() {
            LongPressPopup popup = new LongPressPopupBuilder(context)
                    .setTarget(mReviewModeCV)
                    .setPopupView(R.layout.popup_layout,this)
                    .setLongPressDuration(250)
                    .setDismissOnLongPressStop(true)
                    .setDismissOnTouchOutside(false)
                    .setDismissOnBackPressed(false)
                    .setCancelTouchOnDragOutsideView(true)
                    .setLongPressReleaseListener(this)
                    .setOnHoverListener(this)
                    .setPopupListener(this)
                    .setAnimationType(LongPressPopup.ANIMATION_TYPE_FROM_CENTER)
                    .build();
            popup.register();
        itemView.setOnClickListener(this);
        }

        @Override
        public void onViewInflated(@Nullable String popupTag, View root) {
            popUpAnswerTV = root.findViewById(R.id.popup_answer);
            seeAllAnswerTV = root.findViewById(R.id.see_more_tv);
            seeAllAnswerTV.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if ((seeAllAnswerTV != null && v.getId() == seeAllAnswerTV.getId())||v.getId() == itemView.getId()) {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onCardItemClicked(getItem(position));
                }
            }
        }

        @Override
        public void onHoverChanged(View view, boolean isHovered) {
            if (isHovered) {
                if (view.getId() == seeAllAnswerTV.getId()) {
                    ToastUtils.showLocalizedToast(view.getContext(),"See full answer",view);
                    Vibrator vibrator = (Vibrator) view.getContext().getSystemService(Context.VIBRATOR_SERVICE);
                    if (vibrator.hasVibrator()) {
                        vibrator.vibrate(100);
                    }
                }
            }
        }

        @Override
        public void onPopupShow(@Nullable String popupTag) {
            if (popUpAnswerTV != null) {
                popUpAnswerTV.setText(getItem(getAdapterPosition()).getBack());
            }
        }

        @Override
        public void onPopupDismiss(@Nullable String popupTag) {
        }
    }

    public interface OnCardItemClickListener {
        void onCardItemClicked(Card card);
    }

    public void setOnCardItemClickListener(OnCardItemClickListener listener) {
        this.listener = listener;
    }
}
