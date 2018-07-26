package berger.mitchell.partyplanningapp.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import berger.mitchell.partyplanningapp.Fragments.GuestListFragment;
import berger.mitchell.partyplanningapp.Fragments.HomeFragment;
import berger.mitchell.partyplanningapp.Fragments.ProfileFragment;
import berger.mitchell.partyplanningapp.Fragments.ScanFragment;
import berger.mitchell.partyplanningapp.R;

public class MainActivity extends AppCompatActivity {
    private Fragment homeFragment;
    private Fragment guestListFragment;
    private Fragment scanFragment;
    private Fragment profileFragment;
    private Toolbar toolbar;
    BottomNavigationView navigation;
    private Boolean isRunning;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_sign_out) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you wish to sign out?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    auth.signOut();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            setSupportActionBar(toolbar);
            switch (item.getItemId()) {
                case R.id.navigation_home:

                    transaction.replace(R.id.fragment_container, homeFragment);
                    getSupportActionBar().setTitle("Party info");
                    break;
                case R.id.navigation_guestlist:
                    transaction.replace(R.id.fragment_container, new GuestListFragment());
                    getSupportActionBar().setTitle("Guest List");
                    break;
                case R.id.navigation_scan:
                    transaction.replace(R.id.fragment_container, scanFragment);
                    getSupportActionBar().setTitle("Scan");
                    break;
                case R.id.navigation_profile:
                    transaction.replace(R.id.fragment_container, profileFragment);
                    getSupportActionBar().setTitle("Profile");
                    break;
            }
            transaction.commit();
            return true;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        homeFragment = new HomeFragment();
        guestListFragment = new GuestListFragment();
        scanFragment = new ScanFragment();
        profileFragment = new ProfileFragment();
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);

        auth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        setSupportActionBar(toolbar);
        transaction.add(R.id.fragment_container, homeFragment);
        transaction.commit();
        toolbar.setTitle("Party Info");
    }

    @Override
    protected void onPause(){
        super.onPause();
        isRunning = false;
    }

    @Override
    protected void onResume(){
        super.onResume();
        isRunning = true;
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }
}
