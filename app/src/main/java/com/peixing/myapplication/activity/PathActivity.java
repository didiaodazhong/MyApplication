package com.peixing.myapplication.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.peixing.myapplication.BuildConfig;
import com.peixing.myapplication.GlideApp;
import com.peixing.myapplication.R;
import com.peixing.myapplication.adapter.PathAdapter;
import com.peixing.myapplication.rxgalleryfinal.RxGalleryFinal;
import com.peixing.myapplication.rxgalleryfinal.imageloader.ImageLoaderType;
import com.peixing.myapplication.rxgalleryfinal.rxbus.RxBusResultDisposable;
import com.peixing.myapplication.rxgalleryfinal.rxbus.event.ImageRadioResultEvent;
import com.peixing.myapplication.utils.BitmapUtils;
import com.peixing.myapplication.utils.CLDLogger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class PathActivity extends AppCompatActivity {
    private TextView txtPath;
    //    private String locationProvider;
    private Double lat;
    private Double lng;
    private LocationManager mlocationmanager;
    private String provider;
    private List<GpsSatellite> numSatelliteList = new ArrayList<>();
    private int mSatelliteNum;
    private Location location;
    private Button btnCamera;
    private BottomNavigationBar bottomNavigationBar;
    private BottomNavigationItem explore;
    private ImageView imgPath;
    private RecyclerView recyclerPath;
    private PathAdapter pathAdapter;
    private ArrayList<String> paths;
    private Button bottomSheet;
    private BottomFragment bottomFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path);
        txtPath = (TextView) findViewById(R.id.txt_path);
        btnCamera = (Button) findViewById(R.id.btn_camera);
        bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        imgPath = (ImageView) findViewById(R.id.img_path);
        recyclerPath = (RecyclerView) findViewById(R.id.recycler_path);
        recyclerPath.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerPath.setHasFixedSize(true);
        bottomSheet = (Button) findViewById(R.id.bottom_sheet);
//        CLDLogger.I("--获得根目录/data 内部存储路径path--" + Environment.getDataDirectory().getPath());
        Log.i("PathActivity", "--获得根目录/data 内部存储路径path--" + Environment.getDataDirectory().getPath());
        CLDLogger.I(" 获得缓存目录/cache--path--" + Environment.getDownloadCacheDirectory().getPath());
        CLDLogger.I(" 获得SD卡目录/mnt/sdcard（获取的是手机外置sd卡的路径）--path--" + Environment.getExternalStorageDirectory().getPath());
        CLDLogger.I("- 获得系统目录/system-path--" + Environment.getRootDirectory().getPath());
//        txtPath.setText("--获得根目录/data 内部存储路径path--" + Environment.getDataDirectory().getPath());

        CLDLogger.I("返回通过Context.openOrCreateDatabase 创建的数据库文件path--" + this.getApplicationContext().getDatabasePath(BuildConfig.APPLICATION_ID));
        CLDLogger.I("用于获取APP的cache目录 /data/data/<application package>/cache目录--" + this.getApplicationContext().getCacheDir().getPath());
        CLDLogger.I("用于获取APP的在SD卡中的cache目录/mnt/sdcard/Android/data/<application package>/cache--" + this.getApplicationContext().getExternalCacheDir().getPath());
        CLDLogger.I("用于获取APP的files目录 /data/data/<application package>/files--" + this.getApplicationContext().getFilesDir().getPath());
        CLDLogger.I("用于获取APPSDK中的obb目录/mnt/sdcard/Android/obb/<application package> --" + this.getApplicationContext().getObbDir().getPath());
        CLDLogger.I("用于获取APP的所在包目录--" + this.getApplicationContext().getPackageName());
        CLDLogger.I("来获得当前应用程序对应的 apk 文件的路径--" + this.getApplicationContext().getPackageCodePath());
        CLDLogger.I("获取该程序的安装包路径--" + this.getApplicationContext().getPackageResourcePath());
//获取地理位置管理器
        mlocationmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        mlocationmanager.setTestProviderEnabled("gps", true);
//获取所有可用的位置提供器

       /* List<String> prividerLists = mlocationmanager.getProviders(true);
        if (prividerLists.contains(LocationManager.GPS_PROVIDER)) {
            locationProvider = LocationManager.GPS_PROVIDER;
        } else if (prividerLists.contains(LocationManager.NETWORK_PROVIDER)) {
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else {
            ToastUtils.showToast(this, "没有可用的位置提供器", 1);
            Intent i = new Intent();
            i.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(i);
            return;
        }*/

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(false);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        provider = mlocationmanager.getBestProvider(criteria,
                true);
       /* location = mlocationmanager.getLastKnownLocation(provider);
        if (location != null) {
            updateWithNewLocation(location);
            if (countDownTimer != null) {
                countDownTimer.start();
            }
            //showLocation(location);
        } else {
            txtPath.setText("无法获取当前位置");
        }*/
        try {
            // 获取系统服务
            mlocationmanager = (LocationManager) getSystemService(LOCATION_SERVICE);
            // 判断GPS是否可以获取地理位置，如果可以，展示位置，
            // 如果GPS不行，再判断network，还是获取不到，那就报错
            if (locationInitByGPS() || locationInitByNETWORK()) {
                // 上面两个只是获取经纬度的，获取经纬度location后，再去调用谷歌解析来获取地理位置名称
                updateWithNewLocation(location);
            } else {
                txtPath.setText("获取地理位置失败，上次的地理位置为：" + location);
            }
        } catch (Exception e) {
            txtPath.setText("获取地理位置失败，上次的地理位置为：" + location);
        }

//监视地理位置变化
//        mlocationmanager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 0, locationListener);
//        mlocationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, locationListener);
//        mlocationmanager.requestLocationUpdates(provider, 3000, 0, locationListener);
//        mlocationmanager.addGpsStatusListener(statusListener);
//        mlocationmanager.addNmeaListener(nmeaListener);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(Environment.getExternalStorageDirectory(), "/temp/" + System.currentTimeMillis() + ".jpg");
                if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
                Uri imageUri = Uri.fromFile(file);
                Intent intent = new Intent();
//设置Action为拍照
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
//将拍取的照片保存到指定URI
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, 1);
            }
        });
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_DEFAULT);
        explore = new BottomNavigationItem(R.mipmap.explore, "发现");
        bottomNavigationBar.addItem(new BottomNavigationItem(R.mipmap.buyer, "买手"))
                .addItem(new BottomNavigationItem(R.mipmap.category, "分类"))
                .addItem(explore)
                .addItem(new BottomNavigationItem(R.mipmap.designer, "品牌"))
                .addItem(new BottomNavigationItem(R.mipmap.personal, "我的"))
                .initialise();
        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                if (position == 3) {
                    bottomNavigationBar.removeItem(explore);
                }
                CLDLogger.I("onTabSelected" + position);
            }

            @Override
            public void onTabUnselected(int position) {

                CLDLogger.I("onTabUnselected" + position);
            }

            @Override
            public void onTabReselected(int position) {
                CLDLogger.I("onTabReselected" + position);
            }
        });

         /*      Glide.with(MainActivity.this)
                      .load("http://img1.cloudokids.cn/content/images/thumbs/0000209_tilly-wrap-top-blue.jpeg")
                      .into(new SimpleTarget<GlideDrawable>() {
                          @Override
                          public void onResourceReady(GlideDrawable resource, GlideAnimation <? super GlideDrawable> glideAnimation) {
                              relPic.setBackground(resource);
                              Log.i("MainActivity", "onResourceReady: 加载图片");
                          }
                      });*/
        String url = "http://img1.cloudokids.cn/content/images/thumbs/0000209_tilly-wrap-top-blue.jpeg";
        BitmapUtils.loadBitmap(this, url, false, imgPath);

        GlideApp.with(this)
                .asBitmap()
                .load("")
                .apply(new RequestOptions().centerCrop())
                .thumbnail(0.5f)

                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                    }
                });
        Glide.with(this)
                .load("")
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {

                    }
                });

        paths = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            paths.add(url);
        }

        pathAdapter = new PathAdapter(this, paths);
        recyclerPath.setAdapter(pathAdapter);

        bottomFragment = new BottomFragment(this, pathAdapter);


        imgPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RxGalleryFinal.with(PathActivity.this)
                        .imageConfig(Bitmap.Config.ARGB_8888)
                        .imageLoader(ImageLoaderType.GLIDE)
                        .image()
                        .crop(false)
                        .radio()
                        .subscribe(new RxBusResultDisposable<ImageRadioResultEvent>() {

                                       @Override
                                       protected void onEvent(ImageRadioResultEvent imageRadioResultEvent) throws Exception {

                                       }
                                   }
                        ).openGallery();
            }
        });

        bottomSheet.setOnClickListener((View view) -> {
            bottomFragment.show(getSupportFragmentManager(),"bottom");
        });
    }

    private void updateWithNewLocation(Location location) {
        String coordinate;
        String addressStr = "no address \n";
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            //double lat = 39.25631486;
            //double lng = 115.63478961;
            coordinate = "Latitude：" + lat + "\nLongitude：" + lng;
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(lat,
                        lng, 1);
                StringBuilder sb = new StringBuilder();
                if (addresses.size() > 0) {
                    Address address = addresses.get(0);
                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                        sb.append(address.getAddressLine(i)).append(" ");
                    }
                /*sb.append(address.getCountryName());
                Log.i("location", "address.getCountryName()==" + address.getCountryName());//国家名*/
                    sb.append(address.getLocality()).append(" ");
                    Log.i("location", "address.getLocality()==" + address.getLocality());//城市名
                    sb.append(address.getSubLocality());
                    Log.i("location", "address.getSubLocality()=2=" + address.getSubLocality());//---区名

                    addressStr = sb.toString();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //如果用户没有允许app访问位置信息 则默认取上海松江经纬度的数据
            lat = 39.25631486;
            lng = 115.63478961;
            coordinate = "no coordinate!\n";
        }
        Log.i("location", "经纬度为===" + coordinate);
        Log.i("location", "地址为====" + addressStr);
        txtPath.setText("当前位置" + addressStr + "");
        Toast.makeText(this, "当前位置" + addressStr + "", Toast.LENGTH_SHORT).show();
    }


    /**
     * LocationListern监听器
     * 参数：地理位置提供器、监听位置变化的时间间隔、位置变化的距离间隔、LocationListener监听器
     */
    LocationListener locationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle arg2) {

        }

        @Override
        public void onProviderEnabled(String provider) {
            CLDLogger.I("onProviderEnabled--provide--" + provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            CLDLogger.I("onProviderDisabled--provider--" + provider);
            updateWithNewLocation(null);
        }

        @Override
        public void onLocationChanged(Location location) {
            //如果位置发生变化,重新显示
            updateWithNewLocation(location);
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(provider)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
//            mlocationmanager.requestLocationUpdates(provider, 3000, 0, locationListener);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mlocationmanager != null) {
            mlocationmanager.removeUpdates(locationListener);
//            mlocationmanager.setTestProviderEnabled("gps", false);
            countDownTimer.cancel();
        }
    }

    CountDownTimer countDownTimer = new CountDownTimer(10000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            if (mlocationmanager != null) {
                mlocationmanager.removeUpdates(locationListener);
            }
        }
    };

    private final GpsStatus.Listener statusListener = new GpsStatus.Listener() {
        @Override
        public void onGpsStatusChanged(int event) {
            // GPS状态变化时的回调，获取当前状态
            if (ActivityCompat.checkSelfPermission(PathActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            GpsStatus status = mlocationmanager.getGpsStatus(null);
            // 获取卫星状态相关数据
            GetGPSStatus(event, status);
        }
    };

    private final GpsStatus.NmeaListener nmeaListener = new GpsStatus.NmeaListener() {
        @Override
        public void onNmeaReceived(long timestamp, String nmea) {
            GetNMEAInfo(nmea);
        }
    };

    private void GetGPSStatus(int event, GpsStatus status) {
        if (status == null) {
            Toast.makeText(PathActivity.this, "GetGPSStatus null", Toast.LENGTH_SHORT).show();
        } else if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
            int maxSatellites = status.getMaxSatellites();
            Iterator<GpsSatellite> it = status.getSatellites().iterator();
            numSatelliteList.clear();

            int count = 0;
            while (it.hasNext() && count <= maxSatellites) {
                GpsSatellite s = it.next();
                CLDLogger.I("--GetGPSStatus--getAzimuth--" + s.getAzimuth() + "--getElevation--" + s.getElevation() + "--getPrn--" + s.getPrn() + "--getSnr--" + s.getSnr());
                numSatelliteList.add(s);
                count++;
            }
            mSatelliteNum = numSatelliteList.size();
        } else if (event == GpsStatus.GPS_EVENT_STARTED) {
            Toast.makeText(PathActivity.this, "GPS_EVENT_STARTED", Toast.LENGTH_SHORT).show();
        } else if (event == GpsStatus.GPS_EVENT_STOPPED) {
            Toast.makeText(PathActivity.this, "Gps_EVENT_STOPPED", Toast.LENGTH_SHORT).show();
        }
    }

    private void GetNMEAInfo(String nmea) {
        if (nmea.contains("$GPRMC")) {
            txtPath.append(nmea + "    ");
        }
    }

    // GPS去获取location经纬度
    public boolean locationInitByGPS() {
        // 没有GPS，直接返回
        if (!mlocationmanager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return false;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return true;
        }
//        mlocationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
//                1000, 0, locationListener);
        location = mlocationmanager
                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            return true;//设置location成功，返回true
        } else {
            return false;
        }
    }

    // network去获取location经纬度
    public boolean locationInitByNETWORK() {
        // 没有NETWORK，直接返回
        if (!mlocationmanager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            return false;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return true;
        }
//        mlocationmanager.requestLocationUpdates(
//                LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
        location = mlocationmanager
                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            if (data != null) {
                String path = data.getData().getPath();
                CLDLogger.I("--path--" + path);
            }
        }
    }
}
