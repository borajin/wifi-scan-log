package com.example.wifiscanlog;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static final int PERMISSION_REQUEST_CODE = 1;
    private final String[] PERMISSIONS = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.ACCESS_COARSE_LOCATION"};

    //GPS
    private LocationManager locationManager;

    private ViewPager vp;
    private Button scanViewBtn;
    private Button listViewBtn;

    private ArrayList<String> scanItems;
    private pagerAdapter pa;

    private ScanFragment scanFragment;
    private ListFragment listFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();
        checkNetwork();
        GPSCheck();

        setUI();
    }

    private void setUI() {
        scanItems = new ArrayList<String>();

        vp = findViewById(R.id.vp);
        scanViewBtn = findViewById(R.id.scanViewBtn);
        listViewBtn = findViewById(R.id.listViewBtn);
        pa = new pagerAdapter(getSupportFragmentManager());

        vp.setAdapter(pa);
        vp.setOffscreenPageLimit(1);
        vp.setCurrentItem(0);

        scanViewBtn.setOnClickListener(movePageListener);
        scanViewBtn.setTag(0);
        listViewBtn.setOnClickListener(movePageListener);
        listViewBtn.setTag(1);

        scanViewBtn.setSelected(true);

        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position == 0) {
                    scanViewBtn.setSelected(true);
                    listViewBtn.setSelected(false);
                } else {
                    scanViewBtn.setSelected(false);
                    listViewBtn.setSelected(true);
                    listFragment.refresh();
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    View.OnClickListener movePageListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int tag = (int) v.getTag();

            if(tag == 0) {
                scanViewBtn.setSelected(true);
                listViewBtn.setSelected(false);
            } else {
                scanViewBtn.setSelected(false);
                listViewBtn.setSelected(true);
                listFragment.refresh();
            }
            vp.setCurrentItem(tag);
        }
    };



    private class pagerAdapter extends FragmentStatePagerAdapter {
        public pagerAdapter(androidx.fragment.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public androidx.fragment.app.Fragment getItem(int position) {
            switch (position) {
                case 0:
                    scanFragment = new ScanFragment();
                    return scanFragment;
                case 1:
                    listFragment = new ListFragment();
                    return listFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    private void checkPermission() {
        if (!hasPermissions(PERMISSIONS)) {
            requestNecessaryPermissions(PERMISSIONS);
        } else {
            //이미 사용자에게 퍼미션 허가를 받았음

        }
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions,
                                           int[] grantResults) {
        switch (permsRequestCode) {

            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean readAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (!readAccepted || !writeAccepted) {
                            return;
                        }
                    }
                }
                break;
        }
    }

    private boolean hasPermissions(String[] permissions) {
        int res = 0;
        //스트링 배열에 있는 퍼미션들의 허가 상태 여부 확인
        for (String perms : permissions) {
            res = this.checkCallingOrSelfPermission(perms);
            if (!(res == PackageManager.PERMISSION_GRANTED)) {
                //퍼미션 허가 안된 경우
                AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
                localBuilder.setTitle("권한 설정")
                        .setMessage("권한 거절로 인해 일부기능이 제한됩니다.")
                        .setPositiveButton("권한 설정하러 가기", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                                try {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                            .setData(Uri.parse("package:" + getPackageName()));
                                    startActivity(intent);
                                } catch (ActivityNotFoundException e) {
                                    e.printStackTrace();
                                    Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                                    startActivity(intent);
                                }
                            }
                        })
                        .setNegativeButton("취소하기", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                                Toast.makeText(getApplication(), "권한을 허용하지 않으셨습니다.", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        .create()
                        .show();
                return false;
            }

        }
        //퍼미션이 허가된 경우
        return true;
    }

    private void requestNecessaryPermissions(String[] permissions) {
        //마시멜로( API 23 )이상에서 런타임 퍼미션(Runtime Permission) 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, PERMISSION_REQUEST_CODE);
        }
    }

    private boolean checkNetwork() {
        ConnectivityManager manager = (ConnectivityManager) this.getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null) {
            int type = networkInfo.getType();
            if (type == ConnectivityManager.TYPE_MOBILE) {//쓰리지나 LTE로 연결된것(모바일을 뜻한다.)
                return true;
            } else if (type == ConnectivityManager.TYPE_WIFI) {//와이파이 연결된것
                return true;
            }
        }

        return false;
    }

    private void GPSCheck() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //GPS 설정화면으로 이동
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            startActivity(intent);
        }
    }
}