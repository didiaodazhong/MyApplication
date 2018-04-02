package com.peixing.myapplication.activity

import android.Manifest
import android.annotation.TargetApi
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.hardware.Camera
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.app.hubert.library.HighLight
import com.app.hubert.library.NewbieGuide
import com.bigkoo.pickerview.OptionsPickerView
import com.bigkoo.pickerview.TimePickerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.zxing.activity.CaptureActivity
import com.peixing.myapplication.R
import com.peixing.myapplication.adapter.RecyclerViewAdapter
import com.peixing.myapplication.bean.FilterData
import com.peixing.myapplication.rxgalleryfinal.RxGalleryFinal
import com.peixing.myapplication.rxgalleryfinal.RxGalleryFinalApi
import com.peixing.myapplication.rxgalleryfinal.bean.MediaBean
import com.peixing.myapplication.rxgalleryfinal.imageloader.ImageLoaderType
import com.peixing.myapplication.rxgalleryfinal.rxbus.RxBusResultDisposable
import com.peixing.myapplication.rxgalleryfinal.rxbus.event.BaseResultEvent
import com.peixing.myapplication.rxgalleryfinal.rxbus.event.ImageMultipleResultEvent
import com.peixing.myapplication.rxgalleryfinal.rxbus.event.ImageRadioResultEvent
import com.peixing.myapplication.uikit.PermissionListener
import com.peixing.myapplication.utils.CLDLogger
import com.peixing.myapplication.view.WheelView
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : BaseFragmentActivity() {
    private var tvMain: TextView? = null
    private var spinner1: Spinner? = null
    private var spinner2: Spinner? = null
    private var spinner3: Spinner? = null
    private var button: Button? = null
    private var rcyFilter: RecyclerView? = null
    private var tvExpand: TextView? = null
    private var recyclerView: SwipeMenuRecyclerView? = null
    private var btCircleMenu: Button? = null
    private var checkPwd: CheckBox? = null
    //    private var edtLogin: LoginEditText? = null
    internal var datas = ArrayList<FilterData>()
    private var isExpand = false
    internal var tempDatas = ArrayList<FilterData>()
    private var relPic: RelativeLayout? = null
    private var btChooseVideo: Button? = null
    private var btScanQr: Button? = null
    private var btnCountDown: Button? = null;
    private var btnFingerPrint: Button? = null;
    private var btnSchemeProduct: Button? = null
    private var btnSchemeSpecial: Button? = null
    private var btnSchemeDesign: Button? = null
    private var btnSchemeCategory: Button? = null
    private var btnSchemeWeb: Button? = null
    private var btnContact: Button? = null
    private var btnGetPath: Button? = null;

    private val medias = ArrayList<MediaBean>()
    //打开扫描界面请求码
    private val REQUEST_CODE = 0x01
    //扫描成功返回码
    private val RESULT_OK = 0xA1
    //请求权限数组
    internal var premissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        request()
        button = findViewById<View>(R.id.button) as Button
        tvMain = findViewById<View>(R.id.tv_main) as TextView
        spinner1 = findViewById<View>(R.id.spinner1) as Spinner
        spinner2 = findViewById<View>(R.id.spinner2) as Spinner
        spinner3 = findViewById<View>(R.id.spinner3) as Spinner
        relPic = findViewById<View>(R.id.rel_pic) as RelativeLayout
        tvExpand = findViewById<View>(R.id.tv_expand) as TextView
        rcyFilter = findViewById<View>(R.id.rcy_filter) as RecyclerView
        btnCountDown = findViewById<View>(R.id.btn_count_down) as Button
        btnFingerPrint = findViewById<View>(R.id.btn_finger_print) as Button
        btnSchemeProduct = findViewById<View>(R.id.btn_scheme_product) as Button
        btnSchemeProduct = findViewById<View>(R.id.btn_scheme_product) as Button
        btnSchemeSpecial = findViewById<View>(R.id.btn_scheme_special) as Button
        btnSchemeDesign = findViewById<View>(R.id.btn_scheme_design) as Button
        btnSchemeCategory = findViewById<View>(R.id.btn_scheme_category) as Button
        btnSchemeWeb = findViewById<View>(R.id.btn_scheme_web) as Button
        btnContact = findViewById<View>(R.id.btn_contact) as Button
        btnGetPath = findViewById<View>(R.id.btn_get_path) as Button

        val gridLayoutManager = GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false)

//        edtLogin = findViewById<View>(R.id.edt_login) as LoginEditText
        btScanQr = findViewById<View>(R.id.bt_scan_qr) as Button
        btChooseVideo = findViewById<View>(R.id.bt_choose_video) as Button
        checkPwd = findViewById<View>(R.id.check_pwd) as CheckBox
        recyclerView = findViewById<View>(R.id.recycler_view) as SwipeMenuRecyclerView
        btCircleMenu = findViewById<View>(R.id.bt_circle_menu) as Button
        rcyFilter!!.layoutManager = gridLayoutManager
        rcyFilter!!.setHasFixedSize(true)
        //recyclerview设置manager
        var gridLayout = GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false)
        recyclerView!!.layoutManager = gridLayout
        recyclerView!!.setHasFixedSize(true)

        //动态添加shortcut
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            setupShortcuts()
        }

//        checkPwd!!.setOnCheckedChangeListener { buttonView, isChecked ->
//            //输入框密码切换
//            if (isChecked) {
//                edtLogin!!.transformationMethod = HideReturnsTransformationMethod.getInstance()
//                checkPwd!!.setButtonDrawable(R.drawable.pwd_visible)
//            } else {
//                edtLogin!!.transformationMethod = PasswordTransformationMethod.getInstance()
//                checkPwd!!.setButtonDrawable(R.drawable.pwd_invisible)
//            }
//        }
        for (i in PLANETS.indices) {
            var filter = FilterData()
            filter.id = i
            filter.name = PLANETS[i]
            datas.add(filter)
        }
        val recyclerAdapter = RecyclerViewAdapter(this, datas)
//        rcyFilter!!.adapter = recyclerAdapter
        recyclerView!!.adapter = recyclerAdapter
        btnCountDown!!.setOnClickListener {
            startActivity(Intent(this@MainActivity, CountDownActivity::class.java))
        }

        tvExpand!!.setOnClickListener {
            if (!isExpand) {
                if (datas.size > 6) {
                    tempDatas.clear()
                    for (i in 0..5) {
                        tempDatas.add(datas[i])
                    }
                    recyclerAdapter.setData(tempDatas)
                    isExpand = !isExpand
                    tvExpand!!.text = "扩展"
                    Log.i("MainActivity", "onClick: 收缩")
                }
            } else {
                recyclerAdapter.setData(datas)
                Log.i("MainActivity", "onClick: 扩展")
                isExpand = !isExpand

                tvExpand!!.text = "收缩"
            }
        }
        /*      Glide.with(MainActivity.this)
                      .load("http://img1.cloudokids.cn/content/images/thumbs/0000209_tilly-wrap-top-blue.jpeg")
                      .into(new SimpleTarget<GlideDrawable>() {
                          @Override
                          public void onResourceReady(GlideDrawable resource, GlideAnimation <? super GlideDrawable> glideAnimation) {
                              relPic.setBackground(resource);
                              Log.i("MainActivity", "onResourceReady: 加载图片");
                          }
                      });*/

        /*  Glide.with(this@MainActivity)
                  .load("http://img1.cloudokids.cn/content/images/thumbs/0000209_tilly-wrap-top-blue.jpeg")
                  .into(object : SimpleTarget<GlideDrawable>() {
                      override fun onResourceReady(resource: GlideDrawable, glideAnimation: GlideAnimation<in GlideDrawable>) {
                          relPic!!.setBackground(resource);
                          Log.i("MainActivity", "onResourceReady: 加载图片")
                      }
                  })*/

        Glide.with(this)
                .load("http://img1.cloudokids.cn/content/images/thumbs/0000209_tilly-wrap-top-blue.jpeg")
                .into(object : SimpleTarget<Drawable>() {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
//                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        relPic!!.setBackground(resource);
                        Log.i("MainActivity", "onResourceReady: 加载图片")
                    }
                })
        button!!.setOnClickListener {
            //                startActivity(new Intent(MainActivity.this, CardPageActivity.class));
            startActivity(Intent(this@MainActivity, DrawerActivity::class.java))

            /* PackageManager packageManager = getPackageManager();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("m://cloudoKids:8888/Welcome"));
                List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
                boolean isValid = !activities.isEmpty();
                if (isValid) {
                    Log.i("MainActivity", "onClick: 打开app");
//                    startActivity(intent);
                }
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("m://cloudoKids:8888/Welcome"));
//                startActivity(intent);*/

            val pvTime = TimePickerView.Builder(this@MainActivity, TimePickerView.OnTimeSelectListener { date, v ->
                //选中事件回调
                tvMain!!.text = getTime(date)
            })
                    //不显示秒
                    .setType(booleanArrayOf(true, true, true, true, true, false))
                    //点击区域外消失
                    .setOutSideCancelable(true)
                    //设置label文字大小
                    .setContentSize(15)
                    .setTitleText("开始时间")
                    .setTitleSize(16)
                    //设置循环滚动展示
                    .isCyclic(true)
                    .build()
            pvTime.setDate(Calendar.getInstance())//注：根据需求来决定是否使用该方法（一般是精确到秒的情况），此项可以在弹出选择器的时候重新设置当前时间，避免在初始化之后由于时间已经设定，导致选中时间与当前时间不匹配的问题。
            //                pvTime.show();


            val options1Items = ArrayList<String>()
            for (i in 0..4) {
                options1Items.add("option" + i)
            }
            //                options1Items.add()

            val pvOptions = OptionsPickerView.Builder(this@MainActivity, OptionsPickerView.OnOptionsSelectListener { options1, option2, options3, v ->
                //返回的分别是三个级别的选中位置
                //                        String tx = options1Items.get(options1).getPickerViewText()
                //                                + options2Items.get(options1).get(option2)
                //                                + options3Items.get(options1).get(option2).get(options3).getPickerViewText();
                tvMain!!.text = options1Items[options1]
            })
                    .setSubmitText("确定")//确定按钮文字
                    .setCancelText("取消")//取消按钮文字
                    .setTitleText("城市选择")//标题
                    .setSubCalSize(18)//确定和取消文字大小
                    .setTitleSize(20)//标题文字大小
                    .setTitleColor(Color.BLACK)//标题文字颜色
                    .setSubmitColor(Color.BLUE)//确定按钮文字颜色
                    .setCancelColor(Color.BLUE)//取消按钮文字颜色
                    //                        .setTitleBgColor(0xFF333333)//标题背景颜色 Night mode
                    //                        .setBgColor(0xFF000000)//滚轮背景颜色 Night mode
                    .setContentTextSize(18)//滚轮文字大小
                    .setLinkage(false)//设置是否联动，默认true
                    //                        .setLabels("省", "市", "区")//设置选择的三级单位
                    .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                    .setCyclic(true, false, false)//循环与否
                    .setSelectOptions(1, 1, 1)  //设置默认选中项
                    .setOutSideCancelable(true)//点击外部dismiss default true
                    .isDialog(false)//是否显示为对话框样式
                    .build()

            //                pvOptions.setPicker(options1Items, options2Items, options3Items);//添加数据源
            //                pvOptions.setPicker(options1Items);//添加数据源
            //                pvOptions.show();
            //                Intent intent = new Intent(MainActivity.this, WXEntryActivity.class);
            //                startActivity(intent);

            // 录制
            /*           MediaRecorderConfig config = new MediaRecorderConfig.Buidler()

                        .fullScreen(false)
                        .smallVideoWidth(360)
                        .smallVideoHeight(480)
                        .recordTimeMax(6000)
                        .recordTimeMin(1500)
                        .maxFrameRate(20)
                        .videoBitrate(600000)
                        .captureThumbnailsTime(1)
                        .build();
                MediaRecorderActivity.goSmallVideoRecorder(this, SendSmallVideoActivity.class.getName(), config);*/
            // 选择本地视频压缩
            /* LocalMediaConfig.Buidler buidler = new LocalMediaConfig.Buidler();
                final LocalMediaConfig config = buidler
                        .setVideoPath(path)
                        .captureThumbnailsTime(1)
                        .doH264Compress(new AutoVBRMode())
                        .setFramerate(15)
                        .setScale(1.0f)
                        .build();
                OnlyCompressOverBean onlyCompressOverBean = new LocalMediaCompress(config).startCompress();*/
        }


        tvMain!!.setOnClickListener {
            val outerView = LayoutInflater.from(this@MainActivity).inflate(R.layout.wheel_view, null)
            val wv = outerView.findViewById<View>(R.id.wheel_view) as WheelView
            wv.setOffset(2)
            wv.setItems(Arrays.asList(*PLANETS))
            wv.setSeletion(3)

            wv.onWheelViewListener = object : WheelView.OnWheelViewListener() {
                override fun onSelected(selectedIndex: Int, item: String) {
                    Log.i("MainActivity", "[Dialog]selectedIndex: $selectedIndex, item: $item")
                }
            }

            AlertDialog.Builder(this@MainActivity)
                    .setTitle("WheelView in Dialog")
                    .setView(outerView)
                    .setPositiveButton("OK", null)
                    .show()
        }

        btChooseVideo!!.setOnClickListener {
            RxGalleryFinal.with(this@MainActivity)
                    .imageConfig(Bitmap.Config.ARGB_8888)
                    .imageLoader(ImageLoaderType.GLIDE)
                    .radio()
                    .crop(false)
                    .hideCamera()
                    .subscribe(object : RxBusResultDisposable<ImageRadioResultEvent>() {
                        @Throws(Exception::class)
                        override fun onEvent(imageRadioResultEvent: ImageRadioResultEvent) {
                            var originalPath = imageRadioResultEvent.result.originalPath
                            CLDLogger.I("path--" + originalPath)
                        }
                    }).openGallery()
        }

        /*btChooseVideo!!.setOnClickListener {
            RxGalleryFinal.with(this@MainActivity)
                    .imageConfig(Bitmap.ARGB_4444)
                    .imageLoader(ImageLoaderType.GLIDE)
                    .image()
                    .maxSize(3)
                    .selected(medias)
                    .multiple()
                    .crop()
                    .subscribe(object : RxBusResultSubscriber<ImageMultipleResultEvent>() {
                        @Throws(Exception::class)
                        override fun onEvent(baseResultEvent: ImageMultipleResultEvent) {
                            medias.clear()
                            medias.addAll(baseResultEvent.result)
                            for (i in 0 until baseResultEvent.result.size) {
                                val originalPath = baseResultEvent.result[i].originalPath
                                Log.i("MainActivity", "onEvent:originalPath-- " + originalPath)
                            }

                        }
                    })
                    .openGallery()*/
        /*   RxGalleryFinalApi
                   .getInstance(MainActivity.this)

                   .setType(RxGalleryFinalApi.SelectRXType.TYPE_VIDEO, RxGalleryFinalApi.SelectRXType.TYPE_SELECT_RADIO)
                   .setVDRadioResultEvent(new RxBusResultSubscriber < ImageRadioResultEvent >() {
                       @Override
                       protected void onEvent(ImageRadioResultEvent imageRadioResultEvent) throws Exception {
                           String originalPath = imageRadioResultEvent . getResult ().getOriginalPath();
                           Log.i("MainActivity", "onEvent: originalPath-- " + originalPath);
// 选择本地视频压缩
                           LocalMediaConfig.Buidler buidler = new LocalMediaConfig.Buidler();
                           final LocalMediaConfig config = buidler
                                   .setVideoPath(originalPath)
                                   .captureThumbnailsTime(1)
                                   .doH264Compress(new AutoVBRMode ())
                                   .setFramerate(10)
                                   .setScale(1.0f)
                                   .build();
                           OnlyCompressOverBean onlyCompressOverBean = new LocalMediaCompress(config).startCompress();
                           String videoPath = onlyCompressOverBean . getVideoPath ();
                           Log.i("MainActivity", "onEvent: 压缩后-- " + videoPath);
                           File file = new File(videoPath);
                           FileInputStream fis = new FileInputStream(file);
                           long totalSpace = fis . available ();
                           Log.i("MainActivity", "onEvent: totalSpace-- " + FormetFileSize(totalSpace));
                       }
                   })
                   .open();*/
        /* .setImageRadioResultEvent(new RxBusResultSubscriber<ImageRadioResultEvent>() {
                        @Override
                        protected void onEvent(ImageRadioResultEvent imageRadioResultEvent) throws Exception {
                            String originalPath = imageRadioResultEvent.getResult().getOriginalPath();
                            Log.i("MainActivity", "onEvent: originalPath-- "+originalPath);
                          if (imageRadioResultEvent.getResult().getCropPath() != null) {

                            } else {
//                                    showViewToast("该图片格式不正确,未获得图片链接");
                                Log.i("MainActivity", "onEvent: 该图片格式不正确,未获得图片链接");
                            }
                        }
                    }).*/



        btScanQr!!.setOnClickListener {
            //二维码扫描
            if (isCameraCanUse) {
                val intent = Intent(this@MainActivity, CaptureActivity::class.java)
                Log.i("MainActivity", "qr--跳转扫描页面")
                startActivityForResult(intent, REQUEST_CODE)
            } else {
                Log.i("MainActivity", "onClick: camera不能使用");
            }
        }
        btCircleMenu!!.setOnClickListener { startActivity(Intent(this@MainActivity, CircleActivity::class.java)) }

        //图片引导
        NewbieGuide.with(this)//activity or fragment
                .setLabel("guide1")//Set guide layer labeling to distinguish different guide layers, must be passed! Otherwise throw an error
                .addHighLight(btChooseVideo, HighLight.Type.OVAL)//Add the view that needs to be highlighted
                .setLayoutRes(R.layout.view_guide)//Custom guide layer layout, do not add background color, the boot layer background color is set by setBackgroundColor()
                .show()
        btnFingerPrint!!.setOnClickListener {
            startActivity(Intent(this@MainActivity, FingerPrintActivity::class.java))
        }

        btnSchemeProduct!!.setOnClickListener {
            //            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("cloudokids://com.cloudokids.cn:8086/android?pid=10008"))
//            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("cloudokids://com.cloudokids.cn:8086/android?special=4&&title=带banner专题"))
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("cloudokids://com.cloudokids.cn:8086/android?pid=10003&&title=AKID"))
//            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("cloudokids://com.cloudokids.cn:8086/android?category=66&&title=童鞋"))
            startActivity(intent)
        }
        btnSchemeSpecial!!.setOnClickListener {
            //            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("cloudokids://com.cloudokids.cn:8086/android?pid=10008"))
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("cloudokids://com.cloudokids.cn:8086/android?special=4&&title=带banner专题"))
//            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("cloudokids://com.cloudokids.cn:8086/android?designer=3&&title=AKID"))
//            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("cloudokids://com.cloudokids.cn:8086/android?category=66&&title=童鞋"))
            startActivity(intent)
        }
        btnSchemeCategory!!.setOnClickListener {
            //            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("cloudokids://com.cloudokids.cn:8086/android?pid=10008"))
//            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("cloudokids://com.cloudokids.cn:8086/android?special=4&&title=带banner专题"))
//            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("cloudokids://com.cloudokids.cn:8086/android?designer=3&&title=AKID"))
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("cloudokids://com.cloudokids.cn:8086/android?category=66&&title=童鞋"))
            startActivity(intent)
        }
        btnSchemeDesign!!.setOnClickListener {
            //            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("cloudokids://com.cloudokids.cn:8086/android?pid=10008"))
//            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("cloudokids://com.cloudokids.cn:8086/android?special=4&&title=带banner专题"))
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("cloudokids://com.cloudokids.cn:8086/android?designer=3&&title=AKID"))
//            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("cloudokids://com.cloudokids.cn:8086/android?category=66&&title=童鞋"))
            startActivity(intent)
        }
        btnSchemeWeb!!.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("cloudokids://com.cloudokids.cn:8086/android?category=66&&title=童鞋"))
            startActivity(intent)
        }
        btnContact!!.setOnClickListener {
            val intent = Intent(this@MainActivity, ContactActivity::class.java)
            startActivity(intent)
        }

        btnGetPath!!.setOnClickListener {
            val intent = Intent(this@MainActivity, PathActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //扫描结果回调
        if (resultCode == RESULT_OK) { //RESULT_OK = -1
            if (data != null) {
                val bundle = data.extras
                val scanResult = bundle!!.getString("qr_scan_result")
                //将扫描出的信息显示出来
                //            qrCodeText.setText(scanResult);
                Log.i("MainActivity", "onActivityResult: result-- " + scanResult!!)
            }
        }
    }

    private fun getTime(date: Date): String {//可根据需要自行截取数据显示
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return format.format(date)
    }


    /**
     * 5.0以上动态权限申请
     */
    private fun request() {
        //动态申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestRunPermisssion(premissions, object : PermissionListener {
                override fun onGranted() {
                    Log.i("MainActivity", "onGranted: 授权成功")
                }

                override fun onDenied(deniedPermission: List<String>) {
                    for (str in deniedPermission) {
                        Log.i("MainActivity", "onDenied: --权限--$str--被拒绝了--")
                    }
                }
            })
        }
    }


/* @TargetApi(Build.VERSION_CODES.N_MR1)
 private void setupShortcuts() {
     ShortcutManager mShortcutManager = getSystemService(ShortcutManager.class);

     List<ShortcutInfo> infos = new ArrayList<>();
     for (int i = 0; i < 4; i++) {
         Intent intent = new Intent(this, CircleActivity.class);
         intent.setAction(Intent.ACTION_VIEW);
         intent.putExtra("msg", "我和" + PLANETS[i] + "的对话");

         ShortcutInfo info = new ShortcutInfo.Builder(this, "id" + i)
                 .setShortLabel(PLANETS[i])
                 .setLongLabel("联系人:" + PLANETS[i])
                 .setIcon(Icon.createWithResource(this, R.mipmap.ic_launcher))
                 .setIntent(intent)
                 .build();
         infos.add(info);
//            manager.addDynamicShortcuts(Arrays.asList(info));
     }

     mShortcutManager.setDynamicShortcuts(infos);
 }*/


/* @TargetApi(Build.VERSION_CODES.N_MR1)
 private fun setupShortcuts() {
     val mShortcutManager = getSystemService(ShortcutManager::class.java)
     val infos = ArrayList()
     for (i in 0..3)
     {
         val intent = Intent(this, CircleActivity::class.java)
         intent.setAction(Intent.ACTION_VIEW)
         intent.putExtra("msg", "我和" + PLANETS[i] + "的对话")
         val info = ShortcutInfo.Builder(this, "id" + i)
                 .setShortLabel(PLANETS[i])
                 .setLongLabel("联系人:" + PLANETS[i])
                 .setIcon(Icon.createWithResource(this, R.mipmap.ic_launcher))
                 .setIntent(intent)
                 .build()
         infos.add(info)
         // manager.addDynamicShortcuts(Arrays.asList(info));
     }
     mShortcutManager.setDynamicShortcuts(infos)
 }*/

    @TargetApi(Build.VERSION_CODES.N_MR1)
    private fun setupShortcuts() {
        var shortcutManager: ShortcutManager = getSystemService(ShortcutManager::class.java)
        var infos = ArrayList<ShortcutInfo>()

        for (i in 0..3 step 1) {
            var intent = Intent(this, CircleActivity::class.java)
            intent.setAction(Intent.ACTION_VIEW)
            intent.putExtra("msg", "我和" + PLANETS[i] + "的对话")
            var info: ShortcutInfo = ShortcutInfo.Builder(this, "id" + i)
                    .setShortLabel(PLANETS[i])
                    .setLongLabel("联系人" + PLANETS[i])
                    .setIntent(intent)
                    .setRank(i)
                    .setIcon(Icon.createWithResource(this, R.mipmap.ic_launcher))
                    .build()
            infos.add(info)
//            shortcutManager.addDynamicShortcuts(Arrays.asList(info))
        }
        shortcutManager.setDynamicShortcuts(infos)
    }

    companion object {
        public val PLANETS = arrayOf("Mercury", "Venus", "Earth", "Mars", "Jupiter", "Uranus", "Neptune", "Pluto")

        /**
         * 转换文件大小
         *
         * @param fileS
         * @return
         */
        private fun FormetFileSize(fileS: Long): String {
            val df = DecimalFormat("#.00")
            var fileSizeString = ""
            val wrongSize = "0B"
            if (fileS == 0L) {
                return wrongSize
            }
            if (fileS < 1024) {
                fileSizeString = df.format(fileS.toDouble()) + "B"
            } else if (fileS < 1048576) {
                fileSizeString = df.format(fileS.toDouble() / 1024) + "KB"
            } else if (fileS < 1073741824) {
                fileSizeString = df.format(fileS.toDouble() / 1048576) + "MB"
            } else {
                fileSizeString = df.format(fileS.toDouble() / 1073741824) + "GB"
            }
            return fileSizeString
        }

        val isCameraCanUse: Boolean
            get() {
                var canUse = true
                var mCamera: Camera? = null
                try {
                    mCamera = Camera.open()
                } catch (e: Exception) {
                    canUse = false
                }

                if (canUse) {
                    if (mCamera != null) {
                        mCamera.release()
                        mCamera = null
                    }
                }
                return canUse
            }
    }
}
