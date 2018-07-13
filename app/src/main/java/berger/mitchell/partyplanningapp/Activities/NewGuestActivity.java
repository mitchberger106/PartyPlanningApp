package berger.mitchell.partyplanningapp.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import berger.mitchell.partyplanningapp.R;
import berger.mitchell.partyplanningapp.SharedPref;

/**
 * Created by mberger on 6/28/17.
 */

public class NewGuestActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText inputName, inputNumber;
    private TextInputLayout inputLayoutName, inputLayoutNumber;
    private Button btnEnter;
    final public static int SEND_SMS = 101;
    EditText editText;
    String EditTextValue ;
    Thread thread ;
    public final static int QRcodeWidth = 500 ;
    Bitmap bitmap ;
    final public static int WRITE_EXTERNAL_STORAGE = 101;
     String PartyName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newguest);

        PartyName = SharedPref.read(SharedPref.Party,"");

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Guest");

        inputLayoutName = (TextInputLayout) findViewById(R.id.guest_name);
        inputName = (EditText) findViewById(R.id.input_name);
        inputLayoutNumber = (TextInputLayout) findViewById(R.id.guest_number);
        inputNumber = (EditText) findViewById(R.id.input_number);
        btnEnter = (Button) findViewById(R.id.btn_enter);


        inputName.addTextChangedListener(new MyTextWatcher(inputName));
        inputNumber.addTextChangedListener(new MyTextWatcher(inputNumber));

        btnEnter.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                submitForm();
                if (inputLayoutNumber.isErrorEnabled() == false && inputLayoutName.isErrorEnabled() == false)
                {

                    checkAndroidVersion();

                    shareBitmap();

                    //Intent intent = new Intent(NewGuestActivity.this, GuestListActivity.class);
                    //startActivity(intent);
                }
            }
        });
    }

    public void checkAndroidVersion(){
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(NewGuestActivity.this, android.Manifest.permission.SEND_SMS);
            int checkWritePhonePermission = ContextCompat.checkSelfPermission(NewGuestActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(NewGuestActivity.this,new String[]{android.Manifest.permission.SEND_SMS},SEND_SMS);
                return;
            }
            else if(checkWritePhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(NewGuestActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},WRITE_EXTERNAL_STORAGE);
                return;
            }else
            {
                generateCode();
            }
        } else {
            generateCode();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // permission was granted
            generateCode();
        } else {
            // permission denied
        }
        return;

    }

    public void send(){
        // get the phone number from the phone number text field
        String phoneNumber = "+1" + inputNumber.getText().toString();
        // get the message from the message text box
        String msg = "You've been invited!";

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, msg, null, null);
    }

    /**
     * Validating form
     */
    private void submitForm() {
        if (!validateName()) {
            return;
        }

        if (!validateNumber()) {
            return;
        }

    }

    private boolean validateName() {
        /*if (inputName.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError(getString(R.string.err_msg_name));
            requestFocus(inputName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }*/

        return true;
    }

    private boolean validateNumber() {
        String number = inputNumber.getText().toString().trim();

        /*if (number.length() != 10) {
            inputLayoutNumber.setError(getString(R.string.err_msg_number));
            requestFocus(inputNumber);
            return false;
        } else {
            inputLayoutNumber.setErrorEnabled(false);
        }*/

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
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
                case R.id.input_number:
                    validateNumber();
                    break;
            }
        }
    }
    public void generateCode(){
        EditTextValue = SharedPref.read(SharedPref.Name,"");

        try {
            bitmap = TextToImageEncode(EditTextValue);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
    Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.mdtp_transparent_black):getResources().getColor(R.color.mdtp_white);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }
    private void shareBitmap () {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/LatestShare.png";
        OutputStream out = null;
        File file = new File(path);
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            file.setReadable(true, false);
            out.close();
            Log.d("NewGuestActivity", "try case");
        } catch (Exception e) {
            e.printStackTrace();
        }
        path = file.getPath();
        Uri bmpUri = FileProvider.getUriForFile(NewGuestActivity.this, getString(R.string.file_provider_authority), file);
                //Uri.parse("file://" + path);
        if(bmpUri == null){
            Log.d("NewGuestActivity", "uri is null");
        }
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("image/png");
        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
        shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share with"));
        sendToFirebase();
    }

    public void sendToFirebase(){
        String name = inputName.getText().toString();
        String number = inputNumber.getText().toString();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("Parties");
        final DatabaseReference myRef0 = myRef.child(PartyName);
        final DatabaseReference myRef2 = myRef0.child("Guests");
        final DatabaseReference myRef3 = myRef2.child(name);
        final DatabaseReference myRef4 = myRef3.child("Number");
        final DatabaseReference myRef5 = myRef3.child("Status");
        final DatabaseReference myRef6 = myRef3.child("Name");
        myRef4.setValue(number);
        myRef5.setValue("Invited");
        myRef6.setValue(name);

        SharedPref.init(getApplicationContext());
        SharedPref.write(SharedPref.Name, name);
    }
}
