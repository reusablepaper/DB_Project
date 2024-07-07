package org.techtown.databasetest;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;


public class MainActivity extends AppCompatActivity {
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy - MM - dd");

    public static SQLiteDatabase database;
    private static final String DATABASE_NAME = "app_database";
    private static final int DATABASE_VERSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        database = dbHelper.getWritableDatabase();
        database=openOrCreateDatabase("testdatabase.db",MODE_PRIVATE,null);

        Button serchButton = findViewById(R.id.searchButton);
        Button nearButton = findViewById(R.id.nearButton);

        serchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(),SearchActivity.class);
                startActivity(intent);

            }
        });
        nearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(),CurrentLocationActivity.class);
                startActivity(intent);
            }
        });

    }
    // SQLiteOpenHelper 클래스
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(MainActivity context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // 테이블 생성은 이미 되어있다고 가정
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // 테이블 업그레이드 로직
        }
    }
}
