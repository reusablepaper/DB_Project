package org.techtown.databasetest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import android.Manifest;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NearActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    private NaverMap naverMap;
    public boolean isListEmpty =true;
    public boolean isUpdate=false;

    Button backButton;
    String collage = "";
    String building = "";
    String dayOfWeek = "";
    String startTime = "";
    String endTime = "";
    FragmentManager fm;

    LatLng currentLocation;
    List<Triple<Double,String,String>> nearbyBuildings=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_near);

        fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.map);

        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
        FragmentTransaction transaction = fm.beginTransaction();
        getCurrentLocation();

        nearbyBuilding(nearbyBuildings);




        //요일
        dayOfWeek = getCurrentDayOfWeek();
        //시작 시간과 종료 시간 설정
        long currentTimeMillis = System.currentTimeMillis();
        long minutes = currentTimeMillis / (1000 * 60); // 밀리초를 분으로 변환
        long hours = minutes / 60; // 분을 시간으로 변환
        long currentHour = hours % 24; // 24시간 형식으로 변환
        long currentMinute = minutes % 60;
        if(currentMinute>30)
        {
            startTime=String.valueOf(currentHour)+":30";
            endTime=String.valueOf(currentHour+1)+":30";
        }
        else
        {
            startTime=String.valueOf(currentHour)+":00";
            endTime=String.valueOf(currentHour+1)+":00";
        }




        backButton=findViewById(R.id.backToMainButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        
        collage=nearbyBuildings.get(0).getSecond();
        building=nearbyBuildings.get(0).getThird();
        nearbyBuildings.remove(0);
        setBundle();


    }


    private void nearbyBuilding(List<Triple<Double, String, String>> nearbyBuildings) {
        //현재위치를 기준으로 모든 건물의 거리를 비교해 리스트로 저장한다
        Cursor cursor=MainActivity.database.rawQuery(
                "SELECT collage, building, latitude,longitude FROM building_coordinate",null );
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String collage = cursor.getString(cursor.getColumnIndex("collage"));
                String building = cursor.getString(cursor.getColumnIndex("building"));
                Double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
                Double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
                Double distance=currentLocation.distanceTo(new LatLng(latitude,longitude));
                nearbyBuildings.add(new  Triple<>(distance,collage,building));
            }
            cursor.close();
        }
        //정렬해준다
        Collections.sort(nearbyBuildings, new Comparator<Triple<Double, String, String>>() {
            @Override
            public int compare(Triple<Double, String, String> t1, Triple<Double, String, String> t2) {
                return t1.getFirst().compareTo(t2.getFirst());
            }
        });

    }

    private void getCurrentLocation() {
        // Intent를 통해 전달받은 데이터 확인
        Intent intent = getIntent();
        Double latitude;
        Double longitude;
        latitude=intent.getDoubleExtra("latitude",0);
        longitude= intent.getDoubleExtra("longitude",0);
        currentLocation=new LatLng(latitude,longitude);
    }

    private void setBundle() {
        // this->result list fragment 데이터 전달
        startTime="15:30";
        endTime="16:30";
        Bundle bundle = new Bundle();
        bundle.putString("collage", collage);
        bundle.putString("building", building);
        bundle.putString("dayOfWeek", dayOfWeek);
        bundle.putString("startTime", startTime);
        bundle.putString("endTime", endTime);
        bundle.putString("activityName","NearActivity");
        // ResultListFragment 생성 및 데이터 전달
        ResultListFragment resultListFragment = new ResultListFragment();
        resultListFragment.setArguments(bundle);

        // 프래그먼트 트랜잭션 수행
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.listFragment, resultListFragment);
        transaction.commit();
    }
    private void toast(String data) {
        Toast.makeText(getApplicationContext(),data,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated()) { // 권한 거부됨
                naverMap.setLocationTrackingMode(LocationTrackingMode.None);
            }
            return;
        }

        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);
    }


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

    private String getCurrentDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        // 요일을 문자열로 변환
        String dayOfWeekString = "";
        switch (dayOfWeek) {
            case Calendar.SUNDAY:
                dayOfWeekString = "일요일";
                break;
            case Calendar.MONDAY:
                dayOfWeekString = "월요일";
                break;
            case Calendar.TUESDAY:
                dayOfWeekString = "화요일";
                break;
            case Calendar.WEDNESDAY:
                dayOfWeekString = "수요일";
                break;
            case Calendar.THURSDAY:
                dayOfWeekString = "목요일";
                break;
            case Calendar.FRIDAY:
                dayOfWeekString = "금요일";
                break;
            case Calendar.SATURDAY:
                dayOfWeekString = "토요일";
                break;
        }
        return dayOfWeekString;
    }



    private  void log(String data){
        Log.d("test",data);
    }

    // synchronized 메서드로 동기화
    public synchronized void setDataReceived(boolean data) {
        this.isListEmpty = data;
    }

    // synchronized 메서드로 동기화
    public synchronized boolean isDataReceived() {
        return this.isListEmpty;
    }
}

