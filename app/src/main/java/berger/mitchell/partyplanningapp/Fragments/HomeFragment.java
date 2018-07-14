package berger.mitchell.partyplanningapp.Fragments;


import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.constraint.solver.widgets.Snapshot;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import berger.mitchell.partyplanningapp.Adapters.GuestListAdapter;
import berger.mitchell.partyplanningapp.Adapters.PartyInfoAdapter;
import berger.mitchell.partyplanningapp.R;
import berger.mitchell.partyplanningapp.SharedPref;
import berger.mitchell.partyplanningapp.Sources.PartyInfoSource;

import static android.support.constraint.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements OnMapReadyCallback  {

    SharedPref mPref;
    SupportMapFragment mapFragment;
    GoogleMap map;
    String address;
    private RecyclerView mRecyclerView;
    private PartyInfoAdapter mAdapter;
    private List<PartyInfoSource> RecyclerList = new ArrayList<>();
    private int attended = 0;
    String name;
    private Context mContext;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        rootView.setTag(TAG);
        address = SharedPref.read(SharedPref.Address, "");
        setRetainInstance(true);
        mRecyclerView = rootView.findViewById(R.id.home_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new PartyInfoAdapter(RecyclerList, mContext);

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.setNestedScrollingEnabled(false);

        name = SharedPref.read(SharedPref.Party,"");
        loadSession();
        return rootView;
    }

    @Override
    public void onActivityCreated( Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        if(map != null) {
            Log.d("setUpMap", "Map not null");
            map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            LatLng partyLoc = getLatLngfromAddress(address, this.getContext());
            map.addMarker(new MarkerOptions().position(partyLoc).title(address));
            CameraPosition cameraPosition = new CameraPosition.Builder().target(partyLoc).zoom(18.0f).build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
            map.moveCamera(cameraUpdate);
        }
        else{
            Log.d("setUpMap", "Map null");
        }
    }
    private LatLng getLatLngfromAddress(String address, Context context) {
        Geocoder gc = new Geocoder(context);
        if(gc.isPresent()){
            try {
                List<Address> list = gc.getFromLocationName(address, 1);
                Address res = list.get(0);
                double lat = res.getLatitude();
                double lng = res.getLongitude();
                return new LatLng(lat,lng);
            }
            catch (Exception ex){
                Log.e("Error", ex.toString());
            }
        }
        return null;
    }

    private void loadSession(){
        final String partyName = name;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Parties").child(partyName);
        getAttended();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                RecyclerList.clear();
                    String name = dataSnapshot.child("Name").getValue(String.class);
                    String date = dataSnapshot.child("Date").getValue(String.class);
                    String numGuests = dataSnapshot.child("numGuests").getValue(String.class);
                    String location = dataSnapshot.child("Location").getValue(String.class);
                    String time = dataSnapshot.child("Time").getValue(String.class);
                    String attend = dataSnapshot.child("Attended").getValue(String.class);
                    PartyInfoSource party = new PartyInfoSource(name, date, numGuests, location, time, attend);
                        RecyclerList.add(party);
                        mAdapter.notifyDataSetChanged();
                    }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("here", "Failed to read value.", error.toException());
            }
        });
    }
    private void getAttended() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Parties").child(name).child("Guests");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                attended = 0;
                DatabaseReference myRef1 = database.getReference("Parties").child(name).child("Attended");
                myRef1.setValue(Integer.toString(attended));
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot Snapshot : dataSnapshot.getChildren()) {
                    String status = Snapshot.child("Status").getValue(String.class);
                    if (status != null) {
                        if (status.equals("Attended")) {
                            attended++;
                            myRef1 = database.getReference("Parties").child(name).child("Attended");
                            myRef1.setValue(Integer.toString(attended));
                        }
                    }
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
