package berger.mitchell.partyplanningapp.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import berger.mitchell.partyplanningapp.R;
import berger.mitchell.partyplanningapp.Sources.PartyInfoSource;

/**
 * Created by mberger on 5/27/17.
 */

public class PartyInfoAdapter extends RecyclerView.Adapter<PartyInfoAdapter.MyViewHolder> {
    private List<PartyInfoSource> RecyclerList;
    private Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView partyName;
        public TextView partyDate;
        public TextView partyTime;
        public TextView partyLocation;
        public TextView partyGuests;
        public View mPartyRow;

        public MyViewHolder(View view) {
            super(view);
            partyName = (TextView) view.findViewById(R.id.name);
            partyDate = (TextView) view.findViewById(R.id.date);
            partyTime = (TextView) view.findViewById(R.id.time);
            partyLocation = (TextView) view.findViewById(R.id.location);
            partyGuests = (TextView) view.findViewById(R.id.numGuests);
            mPartyRow = view.findViewById(R.id.info_row_layout);
        }
    }

    public PartyInfoAdapter(List<PartyInfoSource> RecyclerList, Context context) {
        this.RecyclerList = RecyclerList;
        this.mContext = context;
    }
    public PartyInfoAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.info_row, parent, false);

        return new PartyInfoAdapter.MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(PartyInfoAdapter.MyViewHolder holder, int position) {
        final PartyInfoSource availableParty = RecyclerList.get(position);
        holder.partyName.setText("Party Name: " + availableParty.getName());
        holder.partyDate.setText("Start Date: " + availableParty.getDate());
        holder.partyTime.setText("Start Time: " + availableParty.getTime());
        holder.partyLocation.setText("Party Location: " + availableParty.getLocation());
        holder.partyGuests.setText("Attended Guests: " + availableParty.getAttended() + "/" + availableParty.getGuests());
    }

    //Get size of list
    @Override
    public int getItemCount() {
        return RecyclerList.size();
    }
}