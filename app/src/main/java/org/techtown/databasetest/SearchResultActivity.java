package org.techtown.databasetest;


import android.Manifest;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;

import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;

import java.text.SimpleDateFormat;
import java.util.Date;
public class SearchResultActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    private NaverMap naverMap;

    TextView showSettingTextView;

    String collage;
    String building;
    String dayOfWeek;
    String startTime;
    String endTime;
    Date date;


    FragmentManager fm;
    SimpleDateFormat dateFormat=MainActivity.dateFormat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_result);

        fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.map);

        //search->this 데이터 받아오기
        getBundle();
        // this->result list fragment 데이터 전달
        setBundle();


        showSettingTextView=findViewById(R.id.showSettingTextView);
        showSettingTextView.setText("날짜: "+ dateFormat.format(date)+" "+dayOfWeek+"요일\n" +startTime+"~"+endTime+" 빈 강의실 결과");
        Button backButton = findViewById(R.id.backToSearchButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);


    }



    private void getBundle() {
        Intent intent = getIntent();
        collage = intent.getStringExtra("collage");
        building = intent.getStringExtra("building");
        dayOfWeek= intent.getStringExtra("dayOfWeek");
        startTime =  intent.getStringExtra("startTime");
        endTime =  intent.getStringExtra("endTime");
        date = (Date) intent.getSerializableExtra("date"); // Date 객체 받기
    }

    private void setBundle() {
        // this->result list fragment 데이터 전달
        Bundle bundle = new Bundle();
        bundle.putString("collage", collage);
        bundle.putString("building", building);
        bundle.putString("dayOfWeek", dayOfWeek);
        bundle.putString("startTime", startTime);
        bundle.putString("endTime", endTime);
        bundle.putString("activityName","");

        // ResultListFragment 생성 및 데이터 전달
        ResultListFragment resultListFragment = new ResultListFragment();
        resultListFragment.setArguments(bundle);

        // 프래그먼트 트랜잭션 수행
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.listFragment, resultListFragment);
        transaction.commit();
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;

        LatLng fixedLocation = new LatLng(35.8468171, 127.1299221); // 고정할 위치의 위도와 경도
        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(fixedLocation);
        naverMap.moveCamera(cameraUpdate);


        LatLng latLng = getBuildingLocation();
        if (latLng != null) {
            showMarker(latLng);
        } else {
            Log.e("SearchResultActivity", "Building location not found or invalid coordinates!");
        }

    }

    public LatLng getBuildingLocation() {
        Cursor cursor = MainActivity.database.rawQuery(
                "SELECT latitude, longitude FROM building_coordinate WHERE collage = ? AND building = ?",
                new String[]{collage, building});

        LatLng latLng = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude"));
                double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"));
                latLng = new LatLng(latitude, longitude);
            }
            cursor.close();
        }
        return latLng;
    }
    private void showMarker(LatLng location) {
        Marker marker = new Marker();
        marker.setPosition(location);
        marker.setMap(naverMap);

        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(location);
        naverMap.moveCamera(cameraUpdate);
    }

}
