package com.example.standing_alone;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BorrowActivity extends AppCompatActivity {

    private final FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    Intent intent;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow);


        // 하단바
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.first_tab:
                        intent = new Intent(getApplicationContext(), MainActivity.class);
                        //intent3.putExtra("tel",num_textView.getText().toString());
                        //intent3.putExtra("cnum",1234);
                        startActivityForResult(intent, 101);
                        break;
                    case R.id.second_tab:
                        intent = new Intent(getApplicationContext(), BorrowActivity.class);
                        startActivityForResult(intent, 101);
                        break;
                    case R.id.third_tab:
                        intent = new Intent(getApplicationContext(), BorrowActivity.class);
                        startActivityForResult(intent, 101);
                        break;
                    case R.id.fourth_tab:
                        intent = new Intent(getApplicationContext(), MapActivity.class);
                        startActivityForResult(intent, 101);
                        break;
                }
                return false;
            }
        });

        RecyclerView mMainRecyclerView = findViewById(R.id.main_recycler_view);
        findViewById(R.id.main_write_button).setOnClickListener(onClickListener);

        List<Board> mBoardList = new ArrayList<>();
        mStore.collection("posts")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                        for( DocumentChange dc : queryDocumentSnapshots.getDocumentChanges())  {
                            String id = (String) dc.getDocument().getData().get("id");
                            String title=(String) dc.getDocument().get("title");
                            String contents = (String)dc.getDocument().get("contents");
                            String name = (String) dc.getDocument().getData().get("name");
                            Board data = new Board(id, title, contents, name);
                            mBoardList.add(data);
                        }
                    }
                });
        mStore.collection("posts").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())){
                        }
                        MainAdapter mAdapter = new MainAdapter(mBoardList);
                        mMainRecyclerView.setAdapter(mAdapter);
                    }
                });
    }
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.main_write_button:
                    myStartActivity(WritePostActivity.class);
                    break;
            }
        }
    };

    private class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder>{


        private final List<Board> mBoardList;
        public MainAdapter(List<Board> mBoardList) {
            this.mBoardList = mBoardList;
        }

        @NonNull
        @Override
        public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MainViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {
            Board data = mBoardList.get(position);
            holder.mTitleTextView.setText(data.getTitle());
            holder.mNameTextView.setText(data.getName());


        }

        private void setSupportActionBar(Toolbar toolbar) {
        }


        @Override
        public int getItemCount() {
            return mBoardList.size();
        }


        class MainViewHolder extends RecyclerView.ViewHolder{

            private final TextView mTitleTextView;
            private final TextView mNameTextView;

            public MainViewHolder(@NonNull View itemView) {
                super(itemView);

                mTitleTextView = itemView.findViewById(R.id.item_title_text);
                mNameTextView = itemView.findViewById(R.id.item_name_text);



            }
        }
    }
    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivityForResult(intent, 0);
    }
}
