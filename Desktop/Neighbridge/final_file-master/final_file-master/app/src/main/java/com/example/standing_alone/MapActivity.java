package com.example.standing_alone;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MapActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.together_main);

        // 하단바
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            Intent intent;
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

        findViewById(R.id.homeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.taxiBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TaxiActivity.class);
                startActivity(intent);
            }
        });
    }
}
