package berger.mitchell.partyplanningapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import berger.mitchell.partyplanningapp.Adapters.UpcomingPartyAdapter;
import berger.mitchell.partyplanningapp.R;
import berger.mitchell.partyplanningapp.Sources.UpcomingPartySource;

public class PartyListActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private UpcomingPartyAdapter mAdapter;
    private List<UpcomingPartySource> RecyclerList = new ArrayList<>();
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_main);

        /*if(getIntent().hasExtra("Name")){
            Bundle bundle = getIntent().getExtras();
            String name = bundle.getString("Name");
            String date = bundle.getString("Date");
            RecyclerSource newParty = new RecyclerSource(name,date,"0");
            RecyclerList.add(newParty);
        }*/

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Available Parties");
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle("Available Parties");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PartyListActivity.this, PartyInputActivity.class);
                startActivity(intent);
            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadSessions();
            }
        });


        //NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //navigationView.setNavigationItemSelectedListener(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        //RecyclerSource party = new RecyclerSource(partyName,partyDate,numGuests);
        //RecyclerList.add(party);

        mAdapter = new UpcomingPartyAdapter(RecyclerList, this);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && fab.isShown())
                    fab.hide();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    fab.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        loadSessions();
    }

    public void loadSessions() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Parties");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                RecyclerList.clear();
                for (DataSnapshot Snapshot : dataSnapshot.getChildren()) {
                    String name = Snapshot.child("Name").getValue(String.class);
                    String date = Snapshot.child("Date").getValue(String.class);
                    String numGuests = Snapshot.child("numGuests").getValue(String.class);
                    String location = Snapshot.child("Location").getValue(String.class);
                    String time = Snapshot.child("Time").getValue(String.class);
                    String attended = Snapshot.child("Attended").getValue(String.class);
                    Log.d("hi", "Value is: " + name);
                    UpcomingPartySource party = new UpcomingPartySource(name, date, numGuests, location, time, attended);
                    RecyclerList.add(party);
                }

                class StringDateComparator implements Comparator<UpcomingPartySource>
                {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
                    public int compare(UpcomingPartySource rhs, UpcomingPartySource lhs)
                    {
                        try {
                            return dateFormat.parse(lhs.getDate()).compareTo(dateFormat.parse(rhs.getDate()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return 0;
                    }
                }

                Collections.sort(RecyclerList, new StringDateComparator());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("here", "Failed to read value.", error.toException());
            }
        });
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
