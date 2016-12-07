// Created by odavison
package ca.dal.cs.scavenger;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.ArrayList;

// Class handling the display of a Challenge in a recyclerview item
// Implements the ViewHolder pattern where a few layouts are reused as the user
// scrolls through a list of items.
class ChallengeAdapter extends RecyclerView.Adapter<ChallengeAdapter.ChallengeViewHolder> {

    private ArrayList<Challenge> mChallengeList;
    private ItemOnClickListener mItemOnClickListener;

    ChallengeAdapter(ArrayList<Challenge> challengeList, ItemOnClickListener itemOnClickListener) {
        this.mChallengeList = challengeList;
        this.mItemOnClickListener = itemOnClickListener;
    }

    @Override
    public int getItemCount() {
        return mChallengeList.size();
    }

    // Setup elements of the ViewHolder that do not change as the user scrolls
    @Override
    public ChallengeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View challengeCard = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.challenge_card, parent, false);

        ChallengeViewHolder challengeViewHolder = new ChallengeViewHolder(challengeCard);
        challengeViewHolder.setItemOnClickListener(mItemOnClickListener);
        return challengeViewHolder;
    }

    // Setup elements of the ViewHolder that depend on the Challenge it currently displays
    @Override
    public void onBindViewHolder(ChallengeViewHolder challengeViewHolder, int i) {
        Challenge challenge = mChallengeList.get(i);
        Context context = challengeViewHolder.itemView.getContext();

        challengeViewHolder.vDescription.setText(challenge.description);
        LoadVisual.withContext(context)
                .fromSource(challenge)
                .into(challengeViewHolder.vImage);

        if (challenge.is_complete) {
            challengeViewHolder.vCompleted.setVisibility(View.VISIBLE);
        } else {
            challengeViewHolder.vCompleted.setVisibility(View.INVISIBLE);
        }

        if (challenge.is_verified) {
            challengeViewHolder.vVerified.setVisibility(View.VISIBLE);
        } else {
            challengeViewHolder.vVerified.setVisibility(View.INVISIBLE);
        }
    }

    // View class for a single item in the recyclerview
    static class ChallengeViewHolder extends RecyclerView.ViewHolder {
        ImageView vImage;
        TextView vDescription;
        ImageView vCompleted;
        ImageView vVerified;
        private ItemOnClickListener itemOnClickListener;

        ChallengeViewHolder(View v) {
            super(v);
            vImage = (ImageView) v.findViewById(R.id.challenge_image);
            vDescription = (TextView) v.findViewById(R.id.description);
            vCompleted = (ImageView) v.findViewById(R.id.completed);
            vCompleted.setImageDrawable(new IconicsDrawable(v.getContext())
                    .icon(GoogleMaterial.Icon.gmd_check)
                    .color(Color.GREEN)
            );
            vVerified = (ImageView) v.findViewById(R.id.verified);
            vVerified.setImageDrawable(new IconicsDrawable(v.getContext())
                    .icon(GoogleMaterial.Icon.gmd_check)
                    .color(Color.GREEN)
            );

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemOnClickListener.itemClicked(view, getAdapterPosition());
                }
            });
            v.setLongClickable(true);
            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    return itemOnClickListener.itemLongClicked(view, getAdapterPosition());
                }
            });
        }

        void setItemOnClickListener(ItemOnClickListener itemOnClickListener) {
            this.itemOnClickListener = itemOnClickListener;
        }
    }
}
