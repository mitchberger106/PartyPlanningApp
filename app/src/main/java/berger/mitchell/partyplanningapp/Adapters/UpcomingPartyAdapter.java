package berger.mitchell.partyplanningapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import berger.mitchell.partyplanningapp.Activities.MainActivity;
import berger.mitchell.partyplanningapp.R;
import berger.mitchell.partyplanningapp.SharedPref;
import berger.mitchell.partyplanningapp.Sources.UpcomingPartySource;

public class UpcomingPartyAdapter extends RecyclerView.Adapter<UpcomingPartyAdapter.MyViewHolder> {
    private List<UpcomingPartySource> RecyclerList;
    private Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView partyName;
        public View mPartyRow;
        public TextView partyDate;
        public TextView numGuests;

        public MyViewHolder(View view) {
            super(view);
            partyName = (TextView) view.findViewById(R.id.name);
            partyDate = (TextView) view.findViewById(R.id.date);
            numGuests = (TextView) view.findViewById(R.id.guests);
            mPartyRow = view.findViewById(R.id.partyinfo_row_layout);
        }
    }

    public UpcomingPartyAdapter(List<UpcomingPartySource> RecyclerList, Context context) {
        this.RecyclerList = RecyclerList;
        this.mContext = context;
    }
    public UpcomingPartyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.party_info_row, parent, false);

        return new UpcomingPartyAdapter.MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(UpcomingPartyAdapter.MyViewHolder holder, int position) {
        final UpcomingPartySource availableParty = RecyclerList.get(position);
        holder.partyName.setText(availableParty.getName());
        holder.partyDate.setText("Date: " + availableParty.getDate());
        holder.numGuests.setText("Number of guests: " + availableParty.getGuests());
        holder.mPartyRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPref.init(mContext);
                SharedPref.write(SharedPref.Party,availableParty.getName());
                SharedPref.write(SharedPref.Address, availableParty.getLocation());
                Intent intent = new Intent(mContext, MainActivity.class);
                mContext.startActivity(intent);}
        });
    }

    //Get size of list
    @Override
    public int getItemCount() {
        return RecyclerList.size();
    }
}
