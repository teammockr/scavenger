package ca.dal.cs.scavenger;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.ArrayList;

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

    @Override
    public ChallengeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View challengeCard = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.challenge_card, parent, false);

        ChallengeViewHolder challengeViewHolder = new ChallengeViewHolder(challengeCard);
        challengeViewHolder.setItemOnClickListener(mItemOnClickListener);
        return challengeViewHolder;
    }

    @Override
    public void onBindViewHolder(ChallengeViewHolder challengeViewHolder, int i) {
        Challenge challenge = mChallengeList.get(i);
        Context context = challengeViewHolder.itemView.getContext();

        challengeViewHolder.vDescription.setText(challenge.description);
        if (challenge.imageURIString.isEmpty()) {
            challengeViewHolder.vImage.setImageDrawable(new IconicsDrawable(context)
                    .icon(GoogleMaterial.Icon.gmd_broken_image));
        } else {
            challengeViewHolder.vImage.setImageURI(Uri.parse(challenge.imageURIString));
        }
    }

    static class ChallengeViewHolder extends RecyclerView.ViewHolder {
        ImageView vImage;
        TextView vDescription;
        private ItemOnClickListener itemOnClickListener;

        ChallengeViewHolder(View v) {
            super(v);
            vImage = (ImageView) v.findViewById(R.id.challenge_image);
            vDescription = (TextView) v.findViewById(R.id.description);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemOnClickListener.itemClicked(view, getAdapterPosition());
                }
            });
        }

        void setItemOnClickListener(ItemOnClickListener itemOnClickListener) {
            this.itemOnClickListener = itemOnClickListener;
        }
    }
}
