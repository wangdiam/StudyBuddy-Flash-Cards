package com.wangdiam.studybuddycapstoneproject.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wangdiam.studybuddycapstoneproject.R;
import com.wangdiam.studybuddycapstoneproject.models.Card;


public class AllCardsAdapter extends ListAdapter<Card, AllCardsAdapter.CardHolder> {
    private OnCardItemClickListener listener;
    Context context;

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

    public AllCardsAdapter(Context context) {
        super(DIFF_CALLBACK);
        this.context = context;
    }

    public Card getCardAt(int position) {
        return getItem(position);
    }

    @NonNull
    @Override
    public CardHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_full,viewGroup,false);
        return new CardHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CardHolder cardHolder, int i) {
        final Card card = getItem(i);
        cardHolder.mAllCardsQuestionTV.setText(card.getFront());
        cardHolder.mAllCardsSubjectTV.setText(card.getSubjectName());
        cardHolder.mAllCardCV.setBackgroundColor(card.isSelected() ? Color.CYAN : Color.WHITE);
        cardHolder.mAllCardsAnswerTV.setText(card.getBack());
    }


    public class CardHolder extends RecyclerView.ViewHolder {

        TextView mAllCardsQuestionTV, mAllCardsSubjectTV, mAllCardsAnswerTV;
        CardView mAllCardCV;

        public CardHolder(@NonNull final View itemView) {
            super(itemView);
            mAllCardCV = itemView.findViewById(R.id.all_cards_individual_cv);
            mAllCardsAnswerTV = itemView.findViewById(R.id.all_cards_answer_tv);
            mAllCardsQuestionTV = itemView.findViewById(R.id.all_cards_question_tv);
            mAllCardsSubjectTV = itemView.findViewById(R.id.all_cards_subject_tv);
            mAllCardCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onCardItemClicked(getItem(position));
                    }
                }
            });


        }
    }

    public interface OnCardItemClickListener {
        void onCardItemClicked(Card card);
    }

    public void setOnCardItemClickListener(OnCardItemClickListener listener) {
        this.listener = listener;
    }
}
