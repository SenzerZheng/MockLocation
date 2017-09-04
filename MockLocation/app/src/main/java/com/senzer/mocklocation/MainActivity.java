package com.senzer.mocklocation;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Date;

import static android.location.LocationManager.GPS_PROVIDER;
import static com.senzer.mocklocation.PermissionDetector.REQUEST_LOCATION;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private LocationManager locationManager;
    boolean hasAddTestProvider = false;

    EditText etLng, etLat;
    TextView tvStart, tvStop, tvGetLocation, tvShowLocation;

    double lat = 30.637545;         // 纬度
    double lng = 104.088119;        // 经度

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(GPS_PROVIDER,
                2000,
                10.0f,
                new MyLocationListener());

        if (PermissionDetector.verifyLocationPermissions(this)) {
            initMockConfig();
        }

        etLat = (EditText) findViewById(R.id.et_latitude);
        etLng = (EditText) findViewById(R.id.et_longitude);
        tvStart = (TextView) findViewById(R.id.tv_boot);
        tvStop = (TextView) findViewById(R.id.tv_stop);
        tvGetLocation = (TextView) findViewById(R.id.tv_get_location);
        tvShowLocation = (TextView) findViewById(R.id.tv_show_location);

        tvStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strLat = etLat.getText().toString();
                String strLng = etLng.getText().toString();

                lat = TextUtils.isEmpty(strLat) ? lat : Double.parseDouble(strLat);
                lng = TextUtils.isEmpty(strLng) ? lng : Double.parseDouble(strLng);

                new Thread(new RunnableMockLocation()).start();
            }
        });

        tvStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopMockLocation();
            }
        });

        tvGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int granted = ActivityCompat.checkSelfPermission(
                        MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
                if (granted != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                Location location = locationManager.getLastKnownLocation(GPS_PROVIDER);
                if (null == location) {
                    return;
                }

                String coordinate = location.getLatitude() + ", " + location.getLongitude();
                tvShowLocation.setText("getLastKnownLocation: " + coordinate);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_LOCATION:

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {  // permission granted

                    initMockConfig();
                }
                break;

            default:
                break;
        }
    }

    private void initMockConfig() {
        boolean canMockPosition = (Settings.Secure.getInt(getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION, 0) != 0)
                || Build.VERSION.SDK_INT > 22;
        if (canMockPosition && hasAddTestProvider == false) {
            try {
                LocationProvider provider = locationManager.getProvider(GPS_PROVIDER);
                if (provider != null) {
                    locationManager.addTestProvider(
                            provider.getName()
                            , provider.requiresNetwork()
                            , provider.requiresSatellite()
                            , provider.requiresCell()
                            , provider.hasMonetaryCost()
                            , provider.supportsAltitude()
                            , provider.supportsSpeed()
                            , provider.supportsBearing()
                            , provider.getPowerRequirement()
                            , provider.getAccuracy());
                } else {
                    locationManager.addTestProvider(
                            GPS_PROVIDER
                            , true, true, false, false, true, true, true
                            , Criteria.POWER_HIGH, Criteria.ACCURACY_FINE);
                }
                locationManager.setTestProviderEnabled(GPS_PROVIDER, true);
                locationManager.setTestProviderStatus(GPS_PROVIDER, LocationProvider.AVAILABLE, null, System.currentTimeMillis());

                // 模拟位置可用
                hasAddTestProvider = true;
                canMockPosition = true;
            } catch (SecurityException e) {
                canMockPosition = false;
                e.printStackTrace();
            }
        }
    }

    private class RunnableMockLocation implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000);

                    if (hasAddTestProvider == false) {
                        continue;
                    }

                    Log.i(TAG, "启动模拟位置");
                    try {
                        // 模拟位置（addTestProvider成功的前提下）
                        String providerStr = GPS_PROVIDER;
                        Location mockLocation = new Location(providerStr);
                        mockLocation.setLatitude(lat);      // 维度（度）
                        mockLocation.setLongitude(lng);     // 经度（度）
                        mockLocation.setAltitude(30);       // 高程（米）
                        mockLocation.setBearing(180);       // 方向（度）
                        mockLocation.setSpeed(10);          // 速度（米/秒）
                        mockLocation.setAccuracy(0.1f);     // 精度（米）
                        mockLocation.setTime(new Date().getTime());  // 本地时间
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            mockLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
                        }
                        locationManager.setTestProviderLocation(providerStr, mockLocation);
                    } catch (Exception e) {
                        // 防止用户在软件运行过程中关闭模拟位置或选择其他应用
                        stopMockLocation();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 停止模拟位置，以免启用模拟数据后无法还原使用系统位置
     * 若模拟位置未开启，则removeTestProvider将会抛出异常；
     * 若已addTestProvider后，关闭模拟位置，未removeTestProvider将导致系统GPS无数据更新；
     */
    public void stopMockLocation() {
        Log.i(TAG, "停止模拟位置");

        if (hasAddTestProvider) {
            try {
                locationManager.removeTestProvider(GPS_PROVIDER);
            } catch (Exception ex) {
                // 若未成功addTestProvider，或者系统模拟位置已关闭则必然会出错
                ex.printStackTrace();
            }
            hasAddTestProvider = false;
        }
    }

    class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            String coordinate = location.getLatitude() + ", " + location.getLongitude();
            tvShowLocation.setText("onLocationChanged: " + coordinate);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
        }
    }
}
