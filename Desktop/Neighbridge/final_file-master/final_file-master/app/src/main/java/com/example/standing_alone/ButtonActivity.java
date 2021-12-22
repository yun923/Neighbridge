package com.example.standing_alone;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ButtonActivity extends AppCompatActivity  {

    private static final String TAG = "ButtonActivity";

    Toolbar toolbar;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.button_layout);

        // 상단바
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowCustomEnabled(true);
        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김


        // 하단바
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.first_tab:
                        intent = new Intent(ButtonActivity.this, MainActivity.class);
                        //intent3.putExtra("tel",num_textView.getText().toString());
                        //intent3.putExtra("cnum",1234);
                        startActivityForResult(intent, 101);
                        break;
                    case R.id.second_tab:
                        intent = new Intent(ButtonActivity.this, BorrowActivity.class);
                        startActivityForResult(intent, 101);
                        break;
                    case R.id.third_tab:
                        intent = new Intent(ButtonActivity.this, BorrowActivity.class);
                        startActivityForResult(intent, 101);
                        break;
                    case R.id.fourth_tab:
                        intent = new Intent(ButtonActivity.this, MapActivity.class);
                        startActivityForResult(intent, 101);
                        break;
                }
                return false;
            }
        });

    }


    @Override
    public void onBackPressed()
    {
        finish();
    }


    public void startToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }
}
