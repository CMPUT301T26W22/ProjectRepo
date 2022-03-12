package com.example.qrhunter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.digest.DigestUtils;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class UserQRInfoActivity extends BaseNavigatableActivity {
    ListView commentList;
    ArrayList<Comment> commentDataList;
    ArrayAdapter<Comment> commentAdapter;
    ImageButton back;
    Button delete;
    EditText addComment;
    ImageButton sendComment;
    FirebaseFirestore db;


    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_user_qrinfo;
    }

    @Override
    protected int getSelectedItemId() {
        return R.id.profile;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        commentList = findViewById(R.id.commentList);
        commentDataList = new ArrayList<>();

        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("comments");
        final String TAG = "UserQRInfoActivity";


        back = findViewById(R.id.backQR);
        addComment = findViewById(R.id.addComment);
        sendComment = findViewById(R.id.sendComment);

        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String commentData = addComment.getText().toString();
                String user = "test1";
                String uniqueQR = "fnefnioerfnoiFEFSEFfesfs";
                HashMap<String, String> comment = new HashMap<>();
                if (commentData != "") {
                    commentDataList.add(new Comment(user, commentData));
                    comment.put("Comment Data", commentData);
                    comment.put("User", user);

                    commentAdapter.notifyDataSetChanged();
                    addComment.setText("");
                }
                collectionReference.document(uniqueQR).set(comment);
            }
        });

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                // Clear the old list
                commentDataList.clear();
                for(QueryDocumentSnapshot doc: value)
                {
                    String user = (String) doc.getData().get("User");
                    String commentData = (String) doc.getData().get("Comment_Data");
                    commentDataList.add(new Comment(user, commentData)); // Adding the users and comments from FireStore
                }
                commentAdapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched from the cloud
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        commentAdapter = new CommentListAdapter(this, commentDataList);
        commentList.setAdapter(commentAdapter);
    }
}