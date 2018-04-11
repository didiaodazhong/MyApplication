package com.peixing.myapplication.downloader;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.peixing.myapplication.R;
import com.peixing.myapplication.activity.DownLoadActivity;
import com.peixing.myapplication.utils.CLDLogger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by snail
 * on 2017/12/7.
 * Todo 下载的服务
 */

public class DownloadService extends Service {

    //定义notify的id，避免与其它的notification的处理冲突
    private static final int NOTIFY_ID = 0;
    private static final String CHANNEL = "update";
    long[] pattern = {0};
    private DownloadBinder binder = new DownloadBinder();
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private DownloadCallback callback;
    //定义个更新速率，避免更新通知栏过于频繁导致卡顿
    private float rate = .0f;
    /**
     * 把处理结果放回ui线程
     */
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    callback.onPrepare();
                    break;

                case 1:
                    mNotificationManager.cancel(NOTIFY_ID);
                    callback.onFail((String) msg.obj);
                    stopSelf();
                    break;

                case 2: {
                    int progress = (int) msg.obj;
                    callback.onProgress(progress);
                    CLDLogger.I("--正在下载--" + progress + "%");
                    mBuilder.setContentTitle("正在下载：新版本...")
                            .setContentText(String.format(Locale.CHINESE, "%d%%", progress))
                            .setProgress(100, progress, false)
                            .setWhen(System.currentTimeMillis());
                    Notification notification = mBuilder.build();
                    notification.flags = Notification.FLAG_AUTO_CANCEL;
                    mNotificationManager.notify(NOTIFY_ID, notification);
                }
                break;

                case 3: {
                    callback.onComplete((File) msg.obj);
                    //app运行在界面,直接安装
                    //否则运行在后台则通知形式告知完成
                    if (onFront()) {
                        mNotificationManager.cancel(NOTIFY_ID);
                    } else {
                        Intent intent = installIntent(msg.obj.toString());
                        PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext()
                                , 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        initNotification(pIntent, getPackageName(), "下载完成，点击安装");
                    }
                    stopSelf();
                }
                break;
            }
            return false;
        }
    });


    @Override
    public void unbindService(ServiceConnection conn) {
        super.unbindService(conn);
        mNotificationManager.cancelAll();
        mNotificationManager = null;
        mBuilder = null;
    }

    /**
     * 开始下载apk
     */
    public void downApk(String url, DownloadCallback callback) {
        CLDLogger.I("downApk--url--" + url);
        this.callback = callback;
        if (TextUtils.isEmpty(url)) {
            complete("下载路径错误");
            return;
        }
//        setNotification();
        Intent intent = new Intent(getApplicationContext(), DownLoadActivity.class);
        PendingIntent pi = PendingIntent.getActivities(getApplicationContext(), 0, new Intent[]{intent}, PendingIntent.FLAG_CANCEL_CURRENT);

        initNotification(pi, "正在下载", "正在连接服务器");
        handler.sendEmptyMessage(0);
        Request request = new Request.Builder().url(url).build();
        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message message = Message.obtain();
                message.what = 1;
                message.obj = e.getMessage();
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.body() == null) {
                    Message message = Message.obtain();
                    message.what = 1;
                    message.obj = "下载错误";
                    handler.sendMessage(message);
                    return;
                }
                InputStream is = null;
                byte[] buff = new byte[2048];
                int len;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    File file = createFile();
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buff)) != -1) {
                        fos.write(buff, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        if (rate != progress) {
                            Message message = Message.obtain();
                            message.what = 2;
                            message.obj = progress;
                            handler.sendMessage(message);
                            rate = progress;
                        }
                    }
                    fos.flush();
                    Message message = Message.obtain();
                    message.what = 3;
                    message.obj = file.getAbsoluteFile();
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (is != null)
                            is.close();
                        if (fos != null)
                            fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 下载完成
     */
    private void complete(String msg) {
        if (mBuilder != null) {
            mBuilder.setContentTitle("新版本").setContentText(msg);
            Notification notification = mBuilder.build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            mNotificationManager.notify(NOTIFY_ID, notification);
//            initNotification(null, "新版本", msg);
        }

        stopSelf();
    }

    @RequiresApi(api = 26)
    private void initNotification(PendingIntent pIntent, String title, String content) {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("1", "Update", NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(true);
            channel.setShowBadge(true);
            channel.setLightColor(Color.GREEN);
//            channel.setVibrationPattern(new long[]{0, 0, 0, 0, 0});
            mNotificationManager.createNotificationChannel(channel);
        }
        mBuilder = new NotificationCompat.Builder(this, "1");
        if (pIntent != null) {
            mBuilder.setContentIntent(pIntent)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setProgress(0, 0, false)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                    .setOngoing(true)
                    .setAutoCancel(true)
                    .setSound(null)
                    // .setVibrate(new long[]{0, 0, 0, 0, 0})
                    .setWhen(System.currentTimeMillis());
        } else {
            mBuilder.setContentTitle(title)
                    .setContentText(content)
                    .setProgress(0, 0, false)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                    .setOngoing(true)
                    .setAutoCancel(true)
                    .setSound(null)
                    // .setVibrate(new long[]{0, 0, 0, 0, 0})
                    .setWhen(System.currentTimeMillis());
        }
        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(NOTIFY_ID, notification);
    }

    /**
     * 创建通知栏
     */
   /* @RequiresApi(api = 26)
    private void setNotification() {
        if (mNotificationManager == null)
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("1", "Update", NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(true);
            channel.setShowBadge(true);
            channel.setLightColor(Color.GREEN);
//            channel.setVibrationPattern(new long[]{0, 0, 0, 0, 0});
            mNotificationManager.createNotificationChannel(channel);
        }
        mBuilder = new NotificationCompat.Builder(this, "1");

        Intent intent = new Intent(getApplicationContext(), DownLoadActivity.class);
        PendingIntent pi = PendingIntent.getActivities(getApplicationContext(), 0, new Intent[]{intent}, PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentTitle("开始下载")
                .setContentText("正在连接服务器")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setOngoing(true)
                .setAutoCancel(true)
                .setSound(null)
                // .setVibrate(new long[]{0, 0, 0, 0, 0})
                .setContentIntent(pi)
                .setWhen(System.currentTimeMillis());
        mNotificationManager.notify(NOTIFY_ID, mBuilder.build());
    }*/

    /**
     * 路径为根目录
     * 创建文件名称为 updateDemo.apk
     */
    private File createFile() {
        String root = Environment.getExternalStorageDirectory().getPath();
        File file = new File(root, "/Download/updateDemo.apk");
        if (file.exists())
            file.delete();
        try {
            file.createNewFile();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 是否运行在用户前面
     */
    private boolean onFront() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null || appProcesses.isEmpty())
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(getPackageName()) &&
                    appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

    /**
     * 安装
     * 7.0 以上记得配置 fileProvider
     */
    private Intent installIntent(String path) {
        try {
            File file = new File(path);
            String authority = getApplicationContext().getPackageName() + ".fileProvider";
            Uri fileUri = FileProvider.getUriForFile(getApplicationContext(), authority, file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(fileUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            }
            return intent;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 销毁时清空一下对notify对象的持有
     */
    @Override
    public void onDestroy() {
        mNotificationManager = null;
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    /**
     * 定义一下回调方法
     */
    public interface DownloadCallback {
        void onPrepare();

        void onProgress(int progress);

        void onComplete(File file);

        void onFail(String msg);
    }

    /**
     * 和activity通讯的binder
     */
    public class DownloadBinder extends Binder {
        public DownloadService getService() {
            return DownloadService.this;
        }
    }
}
