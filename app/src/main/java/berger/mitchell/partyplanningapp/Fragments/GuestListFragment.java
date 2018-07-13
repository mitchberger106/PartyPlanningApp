package berger.mitchell.partyplanningapp.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import berger.mitchell.partyplanningapp.Adapters.GuestListAdapter;
import berger.mitchell.partyplanningapp.R;
import berger.mitchell.partyplanningapp.SharedPref;
import berger.mitchell.partyplanningapp.Sources.GuestListSource;

public class GuestListFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private GuestListAdapter mAdapter;
    private List<GuestListSource> GuestList = new ArrayList<>();
    private String PartyName;
    private static final String TAG = "GuestListFragment";
    private Context mContext;
    private Toolbar toolbar;

    public GuestListFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }


    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_guest_list, container, false);
        rootView.setTag(TAG);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new GuestListAdapter(GuestList, mContext);

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        PartyName = SharedPref.read(SharedPref.Party, "");

/*        toolbar = (android.support.v7.widget.Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Guest List");*/

        prepareGuestData();

        return rootView;

    }

    private void prepareGuestData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Parties").child(PartyName).child("Guests");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                GuestList.clear();
                for (DataSnapshot Snapshot : dataSnapshot.getChildren()) {
                    String name = Snapshot.child("Name").getValue(String.class);
                    String status = Snapshot.child("Status").getValue(String.class);
                    String number = Snapshot.child("Number").getValue(String.class);
                    Log.d("hi", "Value is: " + name);
                    GuestListSource guest = new GuestListSource(name, status, number);
                    GuestList.add(guest);
                    mAdapter.notifyDataSetChanged();

                    /*if(status != null) {
                        if (status.equals("Attended")) {
                            mCheckBox.setChecked(true);
                        }
                    }*/

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef0 = database.getReference("Parties");
                    final DatabaseReference myRef1 = myRef0.child(PartyName);
                    final DatabaseReference myRef2 = myRef1.child("numGuests");
                    myRef2.setValue(Integer.toString(GuestList.size()));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("here", "Failed to read value.", error.toException());
            }
        });
    }
}