package com.studio.classlog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * Created by Studio on 9/18/2017.
 */

public class BlogFragment extends Fragment {

    private static final String TAG = "BlogFragment";

    public FloatingActionButton fab;

    private RecyclerView mBlogList;

    private DatabaseReference mDatabase, mDatabaseUsers, mUsersCategory, mUserLikes;
    private FirebaseAuth mAuth;

    private boolean likeStaus = false;

    private String category, user_id;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_blog_view, container, false);

        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();

        category = getArguments().getString("data");
        Log.d(TAG, "eventLog: Getting user category: " +category);

        Log.d(TAG, "eventLog: Setting up Blog: Instantiating. ");
        mDatabase = FirebaseDatabase.getInstance().getReference().child(category);

        Log.d(TAG, "eventLog: Setting up Likes: Instantiating. ");
        mUserLikes = FirebaseDatabase.getInstance().getReference().child("Likes");

        mBlogList = (RecyclerView) view.findViewById(R.id.blogList);
        mBlogList.setHasFixedSize(true);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        mBlogList.setLayoutManager(mLayoutManager);

        fab = (FloatingActionButton) view.findViewById(R.id.fabBtn);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "eventLog: onClick: Navigating user to the Post Activity.");
                Intent intent = new Intent(getActivity(), PostActivity.class);
                intent.putExtra("category", category);
                startActivity(intent);
            }
        });

        return view;

    }

    @Override
    public void onStart() {
        super.onStart();

        //Initialzie Recycler Adapter to display the post's from the database.
        FirebaseRecyclerAdapter<Blog, BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(

                Blog.class,
                R.layout.blog_row,
                BlogViewHolder.class,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, Blog model, int position) {

                final String post_key = getRef(position).getKey();

                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDescription());
                viewHolder.setImage(getActivity().getApplicationContext(), model.getImage());
                viewHolder.setUsername(model.getUsername());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(), post_key, Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(getActivity(), PostView.class);
                        intent.putExtra("post_id", post_key);
                        startActivity(intent);
                    }
                });

                viewHolder.setLikeBtn(post_key);

                viewHolder.likes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        likeStaus = true;

                        mUserLikes.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (likeStaus) {

                                    if (dataSnapshot.child(post_key).hasChild(user_id)) {

                                        mUserLikes.child(post_key).child(user_id).removeValue();
                                        likeStaus = false;

                                    } else {

                                        mUserLikes.child(post_key).child(user_id).setValue("Liked");
                                        likeStaus = false;
                                    }

                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
            }
        };

        mBlogList.setAdapter(firebaseRecyclerAdapter);
    }

    /***************************BlogView Holder Class*************************************************/
    public static class BlogViewHolder extends RecyclerView.ViewHolder {

        View mView;

        ImageButton likes;

        DatabaseReference mUserLikes;
        FirebaseAuth mAuth;

        public BlogViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            likes = (ImageButton) mView.findViewById(R.id.likeBtn);

            mUserLikes = FirebaseDatabase.getInstance().getReference().child("Likes");
            mAuth = FirebaseAuth.getInstance();
        }

        public void setLikeBtn(final String post_key){

            mUserLikes.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())){

                        likes.setImageResource(R.drawable.ic_thumbup_liked);

                    } else {

                        likes.setImageResource(R.drawable.ic_thumbsup);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        public void setUsername(String username){

            TextView postUsername = (TextView) mView.findViewById(R.id.cardViewPostAuthor);
            postUsername.setText(username);

        }

        public void setImage(Context ctx, String image){

            ImageView post_image = (ImageView) mView.findViewById(R.id.cardViewImage);
            Picasso.with(ctx).load(image).into(post_image);
        }

        public void setTitle(String title){

            TextView postTitle = (TextView) mView.findViewById(R.id.cardViewPostTitle);
            postTitle.setText(title);

        }

        public void setDesc(String desc){

            TextView postDesc = (TextView) mView.findViewById(R.id.cardViewPostDesc);
            postDesc.setText(desc);
        }

    }
}
