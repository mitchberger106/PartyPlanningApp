package berger.mitchell.partyplanningapp.Activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import berger.mitchell.partyplanningapp.Adapters.LocationAdapter;
import berger.mitchell.partyplanningapp.R;

public class PartyInputActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {
    private Toolbar toolbar;
    private EditText inputName, inputDate, inputTime;
    private TextInputLayout inputLayoutName, inputLayoutDate, inputLayoutTime, inputLayoutLocation;
    private AutoCompleteTextView inputLocation;
    private Button btnEnter, btnDate;
    final Calendar myCalendar = Calendar.getInstance();
    final Calendar myTime = Calendar.getInstance();
    private static final String TAG = "PartyInputActivity";

    private static final int GOOGLE_API_CLIENT_ID = 0;
    private GoogleApiClient mGoogleApiClient;
    private LocationAdapter mPlaceArrayAdapter;
    private static final LatLngBounds BOUNDS_NC = new LatLngBounds(
            new LatLng(34, -82), new LatLng(37, -74));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partyinput);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("New Party");
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle("New Party");

        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        inputLayoutDate = (TextInputLayout) findViewById(R.id.input_layout_date);
        inputLayoutLocation = (TextInputLayout) findViewById(R.id.input_layout_location);
        inputLayoutTime = (TextInputLayout) findViewById(R.id.input_layout_time);
        inputName = (EditText) findViewById(R.id.input_name);
        inputDate = (EditText) findViewById(R.id.input_date);
        inputTime = (EditText) findViewById(R.id.input_time);
        inputLocation = (AutoCompleteTextView) findViewById(R.id.input_location);
        inputLocation.setThreshold(3);

        btnEnter = (Button) findViewById(R.id.btn_enter);
        btnDate = (Button) findViewById(R.id.btn_enter);

        mGoogleApiClient = new GoogleApiClient.Builder(PartyInputActivity.this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, (GoogleApiClient.OnConnectionFailedListener) this)
                .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) this)
                .build();
        mPlaceArrayAdapter = new LocationAdapter(this, android.R.layout.simple_list_item_1,
                BOUNDS_NC, null);

        inputLocation.setOnItemClickListener(mAutocompleteClickListener);
        inputLocation.setAdapter(mPlaceArrayAdapter);



        inputName.addTextChangedListener(new MyTextWatcher(inputName));
        inputDate.addTextChangedListener(new MyTextWatcher(inputDate));
        inputLocation.addTextChangedListener(new MyTextWatcher(inputLocation));
        inputTime.addTextChangedListener(new MyTextWatcher(inputTime));

        final TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                myTime.set(Calendar.HOUR_OF_DAY, hour);
                myTime.set(Calendar.MINUTE, minute);
                updateTimeLabel();
            }

        };

        inputTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new TimePickerDialog(PartyInputActivity.this, time, myTime
                        .get(Calendar.HOUR_OF_DAY), myTime.get(Calendar.MINUTE), false).show();
            }
        });
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        inputDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(PartyInputActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
                if (inputLayoutTime.isErrorEnabled() == false &&
                        inputLayoutDate.isErrorEnabled() == false && inputLayoutName.isErrorEnabled() == false) {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    String name = inputName.getText().toString();
                    String date = inputDate.getText().toString();
                    final DatabaseReference myRef = database.getReference("Parties");
                    final DatabaseReference myRef0 = myRef.child(name);
                    final DatabaseReference myRef1 = myRef0.child("Name");
                    final DatabaseReference myRef2 = myRef0.child("Date");
                    final DatabaseReference myRef3 = myRef0.child("Location");
                    final DatabaseReference myRef4 = myRef0.child("Time");
                    final DatabaseReference myRef5 = myRef0.child("numGuests");
                    final DatabaseReference myRef6 = myRef0.child("Attended");
                    Log.d("name", name);
                    myRef1.setValue(name);
                    myRef2.setValue(date);
                    myRef3.setValue(inputLocation.getText().toString());
                    myRef4.setValue(inputTime.getText().toString());
                    myRef5.setValue("0");
                    myRef6.setValue("0");
                    Intent intent = new Intent(PartyInputActivity.this, MainActivity.class);
                    //b.putString("Name", inputName.getText().toString());
                    //b.putString("Date", inputDate.getText().toString());
                    //intent.putExtras(b);
                    startActivity(intent);
                }
            }
        });
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            final LocationAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(TAG, "Selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Log.i(TAG, "Fetching details for ID: " + item.placeId);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            CharSequence attributions = places.getAttributions();

            inputLocation.setText(Html.fromHtml(place.getAddress() + ""));


        }
    };

    private void updateLabel() {

        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        inputDate.setText(sdf.format(myCalendar.getTime()));
    }

    private void updateTimeLabel() {
        String myFormat = "h:mm a";
        SimpleDateFormat stz = new SimpleDateFormat(myFormat, Locale.US);

        inputTime.setText(stz.format(myTime.getTime()));
    }
    /*@Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        String time = "You picked the following time: "+hourOfDay+"h"+minute+"m"+second;
        timeTextView.setText(time);
    }*/

    /**
     * Validating form
     */
    private void submitForm() {
        if (!validateName()) {
            return;
        }

        if (!validateDate()) {
            return;
        }

        if (!validateTime()) {
            return;
        }

        if (!validateLocation()) {
            return;
        }

        Toast.makeText(getApplicationContext(), "New Party Created!", Toast.LENGTH_SHORT).show();
    }

    private boolean validateName() {
        if (inputName.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError(getString(R.string.err_msg_name));
            requestFocus(inputName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateDate() {
        String date = inputDate.getText().toString().trim();

        if (date.isEmpty()) {
            inputLayoutDate.setError(getString(R.string.err_msg_date));
            requestFocus(inputDate);
            return false;
        } else {
            inputLayoutDate.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateTime() {
        String date = inputDate.getText().toString().trim();

        if (date.isEmpty()) {
            inputLayoutDate.setError(getString(R.string.err_msg_time));
            requestFocus(inputDate);
            return false;
        } else {
            inputLayoutDate.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateLocation() {
        if (inputLocation.getText().toString().trim().isEmpty()) {
            inputLayoutLocation.setError(getString(R.string.err_msg_location));
            requestFocus(inputLocation);
            return false;
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        Log.i(TAG, "Google Places API connected.");
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
        Log.e(TAG, "Google Places API connection suspended.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

        Toast.makeText(this,
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
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
                case R.id.input_name:
                    validateName();
                    break;
                case R.id.input_date:
                    validateDate();
                    break;
                case R.id.input_time:
                    validateTime();
                case R.id.input_location:
                    validateLocation();
                    break;
            }
        }
    }
}
