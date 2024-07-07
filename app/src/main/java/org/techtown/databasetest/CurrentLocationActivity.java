package org.techtown.databasetest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.naver.maps.geometry.LatLng;

import org.techtown.databasetest.NearActivity;

public class CurrentLocationActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private boolean isLocationPermissionGranted = false;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // 위치 권한 확인 및 요청
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 권한이 없으면 권한 요청
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            // 위치 권한이 있으면 현재 위치 가져오기
            isLocationPermissionGranted = true;
            getCurrentLocation();
        }
    }

    private void getCurrentLocation() {
        if (isLocationPermissionGranted) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();
                                LatLng currentLocation = new LatLng(latitude, longitude);

                                // NearActivity로 위치 전달

                                Intent intent = new Intent(CurrentLocationActivity.this, NearActivity.class);
                                intent.putExtra("latitude",latitude);
                                intent.putExtra("longitude",longitude);
                                //intent.putExtra("currentLocation", currentLocation.toString());


                                startActivity(intent);
                                finish(); // 현재 액티비티 종료
                            } else {
                                Toast.makeText(CurrentLocationActivity.this, "위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한 허용됨
                isLocationPermissionGranted = true;
                getCurrentLocation();
            } else {
                // 권한 거부됨
                Toast.makeText(this, "위치 권한을 허용해야 위치 정보를 얻을 수 있습니다.", Toast.LENGTH_SHORT).show();
                finish(); // 액티비티 종료
            }
        }
    }
}
