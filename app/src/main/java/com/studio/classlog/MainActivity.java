package com.studio.classlog;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Context mContext;

    private DatabaseReference mDatabaseUsers, mUsersCategory;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private String user_id, studentDept, studentYear, category;


    /************************onCreate method life cycle*************************/
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "eventLog: onCreate: Starting Main Activity.");
        mContext = MainActivity.this;

        mAuth = FirebaseAuth.getInstance();

        //Check if user is logged in.
        //if not navigate to the login activity.
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.d(TAG, "eventLog: Checking user's log-in status.");
                if (firebaseAuth.getCurrentUser() == null){

                    Log.d(TAG, "eventLog: onAuthStateChanged: Navigating user to the login activity.");
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                }else {
                    user_id = mAuth.getCurrentUser().getUid();
                    Log.d(TAG, "eventLog: onAuthStateChanged: Current User ID: " +user_id);
                }
            }
        };

        Log.d(TAG, "eventLog: Setting up Users: Instantiating. ");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");


    }


    @Override
    protected void onStart() {
        super.onStart();

        //Check user's authentication status.
        mAuth.addAuthStateListener(mAuthListener);

        //checkUserExist();
        setCategory();

    }

    private void setCategory() {

        Log.d(TAG, "eventLog: Getting user's category. ");
        mUsersCategory = FirebaseDatabase.getInstance().getReference().child("Users");

        mUsersCategory.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot ds: dataSnapshot.getChildren()){

                    if (ds.getKey().equals(user_id)){

                        Log.d(TAG, "eventLog: Getting user info: "+ds);
                        Log.d(TAG, "eventLog: User info: "+ds.child("department") +" " +ds.child("year"));
                        Toast.makeText(mContext, "Hello, " +ds.child("username").getValue(String.class).toString(), Toast.LENGTH_LONG).show();

                        studentDept = ds.child("department").getValue(String.class).toString();
                        studentYear = ds.child("year").getValue(String.class).toString();

                        Log.d(TAG, "eventLog: Variable Stored: "+studentDept +studentYear);

                        category = studentDept + studentYear;
                        Log.d(TAG, "eventLog: User Category: " +category);

                        BlogFragment fragment = new BlogFragment();
                        Bundle data = new Bundle();
                        data.putString("data", category);
                        fragment.setArguments(data);

                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                        Log.d(TAG, "eventLog: Navigating user to the Blog Fragment: " +category);
                        fragmentTransaction.add(R.id.fragmentContainer, fragment);
                        fragmentTransaction.commit();

                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    //Menu options.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.signOut){
            mAuth.signOut();
        }

        if (item.getItemId() == R.id.settings){
            Intent intent = new Intent(mContext, AccountSetup.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    /************************Checking if user has completed account setup.*************************/
    private void checkUserExist() {

        if (mAuth.getCurrentUser() != null) {

            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot ds: dataSnapshot.getChildren()){

                        Log.d(TAG, "eventLog: checking if user has completed account setup.");
                        if (ds.getKey().equals(user_id)){


                            if (!ds.child(user_id).hasChild("username")) {

                                Log.d(TAG, "eventLog: onDataChange: Navigating user to update account.");
                                Toast.makeText(mContext, "Please complete your account setup to login.", Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(mContext, AccountSetup.class);
                                startActivity(intent);

                                }
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

    }

}