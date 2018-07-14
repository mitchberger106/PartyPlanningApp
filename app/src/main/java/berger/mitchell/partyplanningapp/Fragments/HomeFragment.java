package berger.mitchell.partyplanningapp.Fragments;


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

import java.io.IOException;
import java.util.List;

import berger.mitchell.partyplanningapp.R;
import berger.mitchell.partyplanningapp.SharedPref;

import static android.support.constraint.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements OnMapReadyCallback  {

    SharedPref mPref;
    SupportMapFragment mapFragment;
    GoogleMap map;
    String address;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        rootView.setTag(TAG);
        address = SharedPref.read(SharedPref.Address, "");
        setRetainInstance(true);
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
            Log.d("Present", "Present");
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
}
