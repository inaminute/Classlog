package com.studio.classlog;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class PostView extends AppCompatActivity {

    private static final String TAG = "PostView";

    private Context mContext;

    private String mPostKey, user_id;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private ImageView mImage;
    private TextView username, title, description;
    private Button postRemoveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_view);
        Log.d(TAG, "onCreate: Starting Single Post View Activity.");

        mContext = PostView.this;

        mPostKey = getIntent().getExtras().getString("post_id");
        Log.d(TAG, "eventLog: Post ID " +mPostKey);

        mImage = (ImageView) findViewById(R.id.postViewImage);
        username = (TextView) findViewById(R.id.postViewUsername);
        title = (TextView) findViewById(R.id.postViewTitle);
        description = (TextView) findViewById(R.id.postViewDesc);

        postRemoveBtn = (Button) findViewById(R.id.postRemoveBtn);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blogs");

        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();


        mDatabase.child(mPostKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String post_title = (String) dataSnapshot.child("title").getValue();
                String post_desc = (String) dataSnapshot.child("description").getValue();
                String post_image = (String) dataSnapshot.child("image").getValue();
                String post_username = (String) dataSnapshot.child("username").getValue();
                String post_uid = (String) dataSnapshot.child("uid").getValue();

                username.setText(post_username);
                title.setText(post_title);
                description.setText(post_desc);

                Picasso.with(mContext).load(post_image).into(mImage);

                if (user_id.equals(post_uid)){

                    postRemoveBtn.setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        postRemoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDatabase.child(mPostKey).removeValue();

                finish();

            }
        });
    }
}
