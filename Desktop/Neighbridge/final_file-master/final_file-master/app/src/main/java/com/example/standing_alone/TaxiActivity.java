package com.example.standing_alone;

import android.app.Dialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;





public class TaxiActivity extends AppCompatActivity implements Overlay.OnClickListener, OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    //지자기, 가속도 센서를 활용해 위치를 반환하는 구현체
    // 구글 플레이서비스의 FusedLocationProviderClient도 사용한다.
    //FusedLocationProviderClient란 통합 위치 제공자와 상호 작용하기 위한 기본 진입점.
    //요약하면 센서들을 활용해서 위치를 반환하는 클래스다
    private FusedLocationSource locationSource;
    private NaverMap naverMap;
    private Geocoder geocoder;
    EditText editText;
    Button Button;
    TextView startPosition, endPosition, editTextTextMultiLine;
    private InfoWindow mInfoWindow;

    Dialog write_new_text; //커스텀 다이얼로그
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taxi_main);

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

        // 새 글 작성 다이얼로그
        write_new_text = new Dialog(TaxiActivity.this);       // Dialog 초기화
        write_new_text.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        write_new_text.setContentView(R.layout.write_new_text);             // xml 레이아웃 파일과 연결

        // 버튼: 커스텀 다이얼로그 띄우기
        findViewById(R.id.write_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog01(); // 아래 showDialog01() 함수 호출
            }
        });

        editText = findViewById(R.id.SearchText);
        Button = findViewById(R.id.search_button);
        //지도 사용권한을 받아 온다.
        locationSource =
                new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);


        FragmentManager fragmentManager = getSupportFragmentManager();
        MapFragment mapFragment= (MapFragment) fragmentManager.findFragmentById(R.id.map);

        if(mapFragment==null){
            mapFragment = MapFragment.newInstance();
            fragmentManager.beginTransaction().add(R.id.map, mapFragment).commit();
        }
        //getMapAsync를 호출하여 비동기로 onMapReady콜백 메서드 호출
        //onMapReady에서 NaverMap객체를 받음
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,  @NonNull int[] grantResults) {

        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated()) { // 권한 거부됨


            }
//            naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
            return;
        }
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);
    }
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        geocoder = new Geocoder(this);
        naverMap.setLocationSource(locationSource);
        naverMap.getUiSettings().setLocationButtonEnabled(true);
        LatLng initialPosition = new LatLng(37.5659353775169, 126.977031771199);
        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(initialPosition);
        naverMap.moveCamera(cameraUpdate);
///
        mInfoWindow = new InfoWindow();
        mInfoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(this) {
            @NonNull
            @Override
            public CharSequence getText(@NonNull InfoWindow infoWindow){
                Marker marker = infoWindow.getMarker();
                return "출발지: " + startPosition.getText().toString() + "\n" + "도착지: " + endPosition.getText().toString() + "\n" + "추가사항: " + editTextTextMultiLine.getText().toString();
            }
        });


        // 버튼 이벤트
        Button.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                freeActiveMarkers();
                String str=editText.getText().toString();
                List<Address> addressList = null;
                try {
                    // editText에 입력한 텍스트(주소, 지역, 장소 등)을 지오 코딩을 이용해 변환
                    addressList = geocoder.getFromLocationName(
                            str, // 주소
                            10); // 최대 검색 결과 개수
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println(addressList.get(0).toString());
                // 콤마를 기준으로 split
                String []splitStr = addressList.get(0).toString().split(",");
                String address = splitStr[0].substring(splitStr[0].indexOf("\"") + 1,splitStr[0].length() - 2); // 주소
                System.out.println(address);

                String latitude = splitStr[10].substring(splitStr[10].indexOf("=") + 1); // 위도
                String longitude = splitStr[12].substring(splitStr[12].indexOf("=") + 1); // 경도
                System.out.println(latitude);
                System.out.println(longitude);

                // 좌표(위도, 경도) 생성
                LatLng point = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                // 마커 생성
                Marker marker = new Marker();
                marker.setPosition(point);
                // 마커 추가
                marker.setMap(naverMap);
                activeMarkers.add(marker);

                // 해당 좌표로 화면 줌
//                naverMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point,15));

                CameraUpdate cameraUpdate = CameraUpdate.scrollTo(point);
                naverMap.moveCamera(cameraUpdate);
            }
        });

    }


    @Override
    public boolean onClick(@NonNull Overlay overlay) {
        Marker marker = (Marker) overlay;
        mInfoWindow.open(marker);

        return false;
    }

    // 마커 정보 저장시킬 변수들 선언
    private Vector<LatLng> markersPosition;
    private Vector<Marker> activeMarkers;

    // 현재 카메라가 보고있는 위치
    public LatLng getCurrentPosition(NaverMap naverMap) {
        CameraPosition cameraPosition = naverMap.getCameraPosition();
        return new LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude);
    }


    // 지도상에 표시되고있는 마커들 지도에서 삭제
    private void freeActiveMarkers() {
        if (activeMarkers == null) {
            activeMarkers = new Vector<Marker>();
            return;
        }
        for (Marker activeMarker: activeMarkers) {
            activeMarker.setMap(null);
        }
        activeMarkers = new Vector<Marker>();
    }



    // write_new_text을 디자인하는 함수
    public void showDialog01(){
        write_new_text.show(); // 다이얼로그 띄우기

        startPosition = (EditText) write_new_text.findViewById(R.id.startPosition);
        endPosition = (EditText) write_new_text.findViewById(R.id.endPosition);
        editTextTextMultiLine = (EditText) write_new_text.findViewById(R.id.editTextTextMultiLine);

        //아니오(취소) 버튼
        Button noBtn = write_new_text.findViewById(R.id.noBtn);
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                write_new_text.dismiss(); // 다이얼로그 닫기
            }
        });


        // 네(등록) 버튼

        write_new_text.findViewById(R.id.yesBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 마커 리셋
                freeActiveMarkers();

                // 사용자가 입력한 장소 저장할 ArrayList
                ArrayList<String> str = new ArrayList<String>();
                // 사용자가 입력한 장소의 좌표 저장할 ArrayList
                ArrayList<LatLng> pointList = new ArrayList<>();

                str.add(startPosition.getText().toString());
                str.add(endPosition.getText().toString());

                for(int i=0; i < str.size(); i++) {
                    List<Address> addressList = null;
                    try {
                        // editText에 입력한 텍스트(주소, 지역, 장소 등)을 지오 코딩을 이용해 변환
                        addressList = geocoder.getFromLocationName(
                                str.get(i), // 주소
                                10); // 최대 검색 결과 개수
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    System.out.println(addressList.get(0).toString());
                    // 콤마를 기준으로 split
                    String[] splitStr = addressList.get(0).toString().split(",");
                    String address = splitStr[0].substring(splitStr[0].indexOf("\"") + 1, splitStr[0].length() - 2); // 주소
                    System.out.println(address);

                    String latitude = splitStr[10].substring(splitStr[10].indexOf("=") + 1); // 위도
                    String longitude = splitStr[12].substring(splitStr[12].indexOf("=") + 1); // 경도
                    System.out.println(latitude);
                    System.out.println(longitude);

                    // 좌표(위도, 경도) 생성
                    LatLng point = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                    pointList.add(point);
                    // 마커 생성
                    Marker marker = new Marker();
                    marker.setWidth(100);
                    marker.setHeight(100);
                    marker.setIcon(OverlayImage.fromResource(R.drawable.ic_baseline_place_24));
                    marker.setPosition(point);
                    // 마커 추가
                    marker.setMap(naverMap);
                    marker.setOnClickListener(TaxiActivity.this::onClick);
                    activeMarkers.add(marker);
                }

                // 출발지 화면 비춤
                CameraPosition cameraPosition = new CameraPosition(pointList.get(0), 14);
                naverMap.setCameraPosition(cameraPosition);

                // 다이얼로그 닫기
                write_new_text.dismiss();
            }
        });
    }
}
