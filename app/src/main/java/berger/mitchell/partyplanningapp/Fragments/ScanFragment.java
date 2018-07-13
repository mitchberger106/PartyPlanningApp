package berger.mitchell.partyplanningapp.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import berger.mitchell.partyplanningapp.R;
import berger.mitchell.partyplanningapp.SharedPref;

import static android.support.constraint.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScanFragment extends Fragment {

    String GuestName;
    String name;
    Button scanButton;

    public ScanFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_scan, container, false);
        rootView.setTag(TAG);
        name = SharedPref.read(SharedPref.Party, "");
        Log.d("ScanFragment", name);
        scanButton = rootView.findViewById(R.id.scnButton);
        scanButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                scanBarcode();
            }
        });
        // Inflate the layout for this fragment
        return rootView;
    }

    void scanBarcode(){
        IntentIntegrator.forSupportFragment(this).initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Log.d("MainActivity", "Cancelled scan");
                Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Log.d("MainActivity", "Scanned");
                GuestName = result.getContents();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference ref = database.getReference("Parties").child(name).child("Guests");
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d("ScanFragment", "value: " + dataSnapshot.getChildrenCount());
                        if(!dataSnapshot.hasChild(GuestName)) {
                            if(result != null) {
                                Toast.makeText(getActivity(), result.getContents() + " not invited", Toast.LENGTH_LONG).show();
                            }
                        }
                        else {
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            final int[] value = new int[1];
                            DatabaseReference myRef = database.getReference("Parties").child(name).child("Guests").child(GuestName).child("Status");
                            myRef.setValue("Attended");
                            Toast.makeText(getActivity(), "Welcome: " + result.getContents(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                //formatTxt.setText(result.getContents());
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
