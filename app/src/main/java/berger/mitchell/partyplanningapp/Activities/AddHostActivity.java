package berger.mitchell.partyplanningapp.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import berger.mitchell.partyplanningapp.Adapters.LocationAdapter;
import berger.mitchell.partyplanningapp.R;
import berger.mitchell.partyplanningapp.SharedPref;

public class AddHostActivity extends AppCompatActivity {

    AutoCompleteTextView newHost;
    Button addBtn;
    ArrayAdapter<String> adapter;
    TextInputLayout inputLayoutName;
    String hostName, partyName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_host);

        partyName = SharedPref.read(SharedPref.Party,"");

        newHost = findViewById(R.id.input_new_host);
        newHost.setThreshold(1);
        addBtn = findViewById(R.id.btn_add);
        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_host_name);

        final ArrayList<String> users = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Log.d("ChildUid", postSnapshot.child("Uid").getValue().toString());
                    if(!postSnapshot.child("Uid").getValue().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid().toString())){
                        users.add(postSnapshot.getKey().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
// Create the adapter and set it to the AutoCompleteTextView
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, users);
        newHost.setAdapter(adapter);

        newHost.addTextChangedListener(new MyTextWatcher(newHost));

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference("Users").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(hostName)){
                            String uid = dataSnapshot.child(hostName).child("Uid").getValue().toString();
                            FirebaseDatabase.getInstance().getReference("Parties").child(partyName).child("Hosts").child(uid).setValue("True");
                            Toast.makeText(getApplicationContext(), hostName + " added as host!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(AddHostActivity.this, MainActivity.class));
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "User not in database!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });


    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        protected MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_new_host:
                    validateName();
                    hostName = newHost.getText().toString().trim();
                    break;
            }
        }
    }

    private boolean validateName() {
        if (newHost.getText().toString().trim().isEmpty()) {
            newHost.setError(getString(R.string.err_msg_name));
            requestFocus(newHost);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

}
