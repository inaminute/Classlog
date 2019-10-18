package com.studio.classlog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AccountSetup extends AppCompatActivity {

    private static final String TAG = "AccountSetup";

    private static final int GALLERY_REQUEST = 1;

    private Context mContext;

    private ImageView profilePicture;
    private EditText profileName;
    private Spinner deptSpinner;
    private Spinner yearSpinner;
    private Button updateButton;

    private String selectedDept, selectedYear;

    private Uri mImageUri = null;

    private ProgressDialog mProgress;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private StorageReference mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setup);

        mContext = AccountSetup.this;

        mProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mStorage = FirebaseStorage.getInstance().getReference().child("profile_picture");

        profilePicture = (ImageView) findViewById(R.id.profilePictureImgVw);
        profileName = (EditText) findViewById(R.id.profileNameTxtVw);

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_REQUEST);
            }
        });

        deptSpinner = (Spinner) findViewById(R.id.spinner);
        yearSpinner = (Spinner) findViewById(R.id.spinner2);


        ArrayAdapter<String> deptAdapter = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.dept_list));

        deptAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        deptSpinner.setAdapter(deptAdapter);

        ArrayAdapter<String> yearAdapter =  new ArrayAdapter<String>(mContext,
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.year_list));

        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);

        deptSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDept = deptSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedYear = yearSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        updateButton = (Button) findViewById(R.id.updateBtn);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateAccount();

            }
        });
    }




    private void updateAccount() {

        final String user_id = mAuth.getCurrentUser().getUid();
        final String username = profileName.getText().toString();


        if (!TextUtils.isEmpty(username) && mImageUri != null){

            if (!(selectedDept.equals("Select Department")) && !(selectedYear.equals("Select Year")) ){

                mProgress.setMessage("Finishing account setup.");
                mProgress.show();

                StorageReference filepath = mStorage.child(mImageUri.getLastPathSegment());

                filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        String downloadUri = taskSnapshot.getDownloadUrl().toString();

                        mDatabase.child(user_id).child("profile_picture").setValue(downloadUri);
                        mDatabase.child(user_id).child("username").setValue(username);
                        mDatabase.child(user_id).child("department").setValue(selectedDept);
                        mDatabase.child(user_id).child("year").setValue(selectedYear);

                        mProgress.dismiss();

                        Intent intent = new Intent(mContext, MainActivity.class);
                        startActivity(intent);
                        finish();

                    }
                });

            }else {
                Toast.makeText(mContext, "Please choose a Department and class to complete your account setup", Toast.LENGTH_LONG)
                        .show();
            }


        } else {

            Toast.makeText(mContext, "Please enter all your details to complete your account setup.", Toast.LENGTH_LONG).show();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){

            mImageUri = data.getData();

            profilePicture.setImageURI(mImageUri);
        }

    }

    @Override
    public void onBackPressed() {
        Toast.makeText(mContext, "Please complete your account setup.", Toast.LENGTH_LONG).show();
    }

}
