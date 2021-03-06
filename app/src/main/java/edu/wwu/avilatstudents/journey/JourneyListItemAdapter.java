package edu.wwu.avilatstudents.journey;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by myklhenn on 2/28/17.
 */

class JourneyListItemAdapter extends ArrayAdapter<JourneyListItem> {

    private final Activity context;
    private final ArrayList<JourneyListItem> journeys;

    JourneyListItemAdapter(Activity context, ArrayList<JourneyListItem> journeys) {
        super(context, R.layout.item_journey, journeys);
        this.context = context;
        this.journeys = journeys;
    }

    @NonNull
    @Override
    public View getView(int pos, View view, @NonNull ViewGroup parent) {
        //LayoutInflater inflater = context.getLayoutInflater();
        JourneyListItemViewHolder journeyCard;

        if (view == null) {
            LayoutInflater inf = (LayoutInflater) this.context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            view = inf.inflate(R.layout.item_journey, null);
            // cache view fields into the holder
            journeyCard = new JourneyListItemViewHolder();
            journeyCard.journeyName = (TextView) view.findViewById(R.id.journey_card_name);
            journeyCard.journeyProgress = (ProgressBar) view.findViewById(R.id.journey_card_progress);
            journeyCard.journeyMap = (ImageView) view.findViewById(R.id.journey_card_map);
            // associate the holder with the view for later lookup
            view.setTag(journeyCard);
        } else {
            journeyCard = (JourneyListItemViewHolder) view.getTag();
        }

        journeyCard.journeyName.setText(this.journeys.get(pos).getName());
        journeyCard.journeyProgress.setProgress(this.journeys.get(pos).getProgress());
        journeyCard.journeyMap.setImageResource(this.journeys.get(pos).getMapRes());

        return view;
    }

    @Override
    public JourneyListItem getItem(int pos) {
        return this.journeys.get(pos);
    }
}

class JourneyListItemViewHolder {
    TextView journeyName;
    ProgressBar journeyProgress;
    ImageView journeyMap;
}
