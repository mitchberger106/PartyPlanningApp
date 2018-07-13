package berger.mitchell.partyplanningapp.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import berger.mitchell.partyplanningapp.R;
import berger.mitchell.partyplanningapp.SharedPref;
import berger.mitchell.partyplanningapp.Sources.GuestListSource;

public class GuestListAdapter extends RecyclerView.Adapter<GuestListAdapter.MyViewHolder>{
    private List<GuestListSource> GuestList;
    private Context mContext;
    public String PartyName;
    public String guestListName;

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
        PartyName = SharedPref.read(SharedPref.Party,"");
        holder.guestName.setText(availableGuest.getName());
        holder.guestStatus.setText(availableGuest.getStatus());
        holder.guestNumber.setText(availableGuest.getNumber());
        holder.mGuestListRow.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                guestListName = availableGuest.getName();
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("Are you sure you want to delete " + availableGuest.getName() + "?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
                return false;
            }
        });
    }


    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    final DatabaseReference myRef = database.getReference("Parties");
                    final DatabaseReference myRef0 = myRef.child(PartyName);
                    final DatabaseReference myRef2 = myRef0.child("Guests");
                    final DatabaseReference myRef3 = myRef2.child(guestListName);
                    Toast.makeText(mContext, "Removing "+guestListName, Toast.LENGTH_LONG).show();
                    myRef3.removeValue();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };

    //Get size of list
    @Override
    public int getItemCount() {
        return GuestList.size();
    }
}
