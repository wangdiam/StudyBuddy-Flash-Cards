package com.wangdiam.studybuddycapstoneproject.ui.adapters;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.wangdiam.studybuddycapstoneproject.R;
import com.wangdiam.studybuddycapstoneproject.models.Subject;

import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;

public class SubjectAdapter extends ListAdapter<Subject,SubjectAdapter.SubjectHolder> {
    private OnItemClickListener listener;
    public static final DiffUtil.ItemCallback<Subject> SUBJECT_DIFF_CALLBACK = new DiffUtil.ItemCallback<Subject>() {
        @Override
        public boolean areItemsTheSame(@NonNull Subject subject, @NonNull Subject t1) {
            return subject.getId().equals(t1.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Subject subject, @NonNull Subject t1) {
            return subject.getName().equals(t1.getName()) && subject.getCardCount().equals(t1.getCardCount());
        }
    };

    public SubjectAdapter() {
        super(SUBJECT_DIFF_CALLBACK);

    }


    @NonNull
    @Override
    public SubjectHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.subject_item,viewGroup,false);
        return new SubjectHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectHolder subjectHolder, int i) {
        Subject subject = getItem(i);
        subjectHolder.subjectNameTV.setText(subject.getName());
        subjectHolder.noOfCardsTV.setText(String.format("%s",subject.getCardCount()));
    }


    public Subject getSubjectAt(int position) {
        return getItem(position);
    }


    public class SubjectHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, MenuItem

            .OnMenuItemClickListener{

        TextView subjectNameTV,noOfCardsTV;
        CardView subjectCV;

        public SubjectHolder(@NonNull View itemView) {
            super(itemView);
            subjectNameTV = itemView.findViewById(R.id.subject_name_tv);
            noOfCardsTV = itemView.findViewById(R.id.cards_left_tv);
            subjectCV = itemView.findViewById(R.id.subject_cv);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(itemView.getContext());
            if (!prefs.getBoolean("firstTimeSubjects", false)) {
                new GuideView.Builder(itemView.getContext())
                        .setTitle("Subject deck")
                        .setContentText("Click on the deck to choose a study mode\n\nLong press the card for more options")
                        .setTargetView(subjectCV)
                        .setDismissType(DismissType.anywhere)
                        .build()
                        .show();



                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("firstTimeSubjects", true);
                editor.commit();
            }

            itemView.setOnCreateContextMenuListener(this);
            subjectCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClicked(getItem(position),position);
                    }
                }
            });
        }


        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (listener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    switch (item.getItemId()) {
                        case 1:
                            listener.onEditClicked(position);
                            return true;
                        case 2:
                            listener.onDeleteClicked(position);
                            return true;
                        default:
                            return false;
                    }
                }
            }
            return false;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select Action");
            MenuItem edit = menu.add(Menu.NONE,1,1,"Edit subject name");
            MenuItem delete = menu.add(Menu.NONE,2,2,"Delete entire subject deck");

            edit.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);
        }
    }

    public interface OnItemClickListener {
        void onItemClicked(Subject subject, int position);

        void onEditClicked(int position);

        void onDeleteClicked(int position);
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
