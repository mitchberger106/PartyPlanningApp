package berger.mitchell.partyplanningapp.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import berger.mitchell.partyplanningapp.R;
import berger.mitchell.partyplanningapp.SharedPref;
import berger.mitchell.partyplanningapp.Sources.GuestListSource;

public class GuestListAdapter extends RecyclerView.Adapter<GuestListAdapter.MyViewHolder>{
    private List<GuestListSource> GuestList;
    private Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView guestName;
        public TextView guestStatus;
        public TextView guestNumber;
        public View mGuestListRow;

        @SuppressLint("CutPasteId")
        public MyViewHolder(View view) {
            super(view);
            guestName = (TextView) view.findViewById(R.id.guestName);
            guestStatus = (TextView) view.findViewById(R.id.status);
            mGuestListRow = view.findViewById(R.id.guestlist_row_layout);
            guestNumber = (TextView) view.findViewById(R.id.guestNumber);
        }
    }

    public GuestListAdapter(List<GuestListSource> GuestList, Context context) {
        this.GuestList = GuestList;
        this.mContext = context;
    }
    public GuestListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.guestlist_row, parent, false);

        return new GuestListAdapter.MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(GuestListAdapter.MyViewHolder holder, int position) {
        final GuestListSource availableGuest = GuestList.get(position);
        final String PartyName = SharedPref.read(SharedPref.Party,"");
        holder.guestName.setText(availableGuest.getName());
        holder.guestStatus.setText(availableGuest.getStatus());
        holder.guestNumber.setText(availableGuest.getNumber());
/*        holder.mGuestListRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference myRef = database.getReference("Parties");
                final DatabaseReference myRef0 = myRef.child(PartyName);
                final DatabaseReference myRef2 = myRef0.child("Guests");
                final DatabaseReference myRef3 = myRef2.child(availableGuest.getName());
                final DatabaseReference myRef5 = myRef3.child("Status");
                if(availableGuest.getStatus().equals("Invited")){
                    myRef5.setValue("Attended");}
                else{
                    myRef5.setValue("Invited");
                }
            }
        });*/
    }

    //Get size of list
    @Override
    public int getItemCount() {
        return GuestList.size();
    }
}
