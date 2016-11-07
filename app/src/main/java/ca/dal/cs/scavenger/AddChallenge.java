package ca.dal.cs.scavenger;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AddChallenge extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_challenge);

        ArrayList<Challenge> challenges = new ArrayList<>();
        Drawable drawable = Drawable.createFromPath("unnamed.png");
        challenges.add(new Challenge(drawable, "title", "description"));
        challenges.add(new Challenge(drawable, "title", "description"));
        challenges.add(new Challenge(drawable, "title", "description"));
        challenges.add(new Challenge(drawable, "title", "description"));

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.challenges);
        recyclerView.hasFixedSize();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(new ChallengeAdapter(challenges));
    }
}

class ChallengeAdapter  extends RecyclerView.Adapter<ChallengeAdapter.ChallengeViewHolder> {

    private List<Challenge> challengeList;

    ChallengeAdapter(List<Challenge> challengeList) {
        this.challengeList = challengeList;
    }

    @Override
    public int getItemCount() {
        return challengeList.size();
    }

    @Override
    public ChallengeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View challengeView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.challenge_card, parent, false);

        return new ChallengeViewHolder(challengeView);
    }

    @Override
    public void onBindViewHolder(ChallengeViewHolder challengeViewHolder, int i) {
        Challenge challenge = challengeList.get(i);
        challengeViewHolder.vTitle.setText(challenge.title);
        challengeViewHolder.vDescription.setText(challenge.description);
        challengeViewHolder.vImage.setImageDrawable(challenge.challengeDrawable);
    }

    static class ChallengeViewHolder extends RecyclerView.ViewHolder {
        ImageView vImage;
        TextView vTitle;
        TextView vDescription;

        ChallengeViewHolder(View v) {
            super(v);
            vImage = (ImageView) v.findViewById(R.id.challengeImage);
            vTitle = (TextView) v.findViewById(R.id.title);
            vDescription = (TextView) v.findViewById(R.id.description);
        }
    }
}
