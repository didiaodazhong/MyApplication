package com.peixing.myapplication.activity;

import android.app.AlertDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.peixing.myapplication.R;
import com.peixing.myapplication.downloader.DownloadService;
import com.peixing.myapplication.utils.CLDLogger;
import com.peixing.myapplication.utils.ToastUtils;

import java.io.File;
import java.io.IOException;

public class DownLoadActivity extends AppCompatActivity implements DownLoadFragmentListener {
    private static final int INSTALL_PACKAGES_REQUESTCODE = 101;
    public String apkFile;
    DownloadService myService;
    private Button btnCheckVersion;
    private ServiceConnection conn;
    private TextView txtProgress;
    private UpdateFragment updateFragment;
    private boolean isDownload;
    private boolean isBind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_load);
        btnCheckVersion = (Button) findViewById(R.id.btn_check_version);
        txtProgress = (TextView) findViewById(R.id.txt_progress);
        btnCheckVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (updateFragment == null) {
                    updateFragment = new UpdateFragment(DownLoadActivity.this);
                    updateFragment.setDownLoadFragmentListener(DownLoadActivity.this);
                }
                updateFragment.show(getSupportFragmentManager(), "update");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBind) {
            unbindService(conn);
            isBind = false;
        }
    }

    @Override
    public void ensure() {
        final String url = "http://ois3rbpsm.bkt.clouddn.com/android.apk";
        if (conn == null) {
            conn = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    DownloadService.DownloadBinder binder = (DownloadService.DownloadBinder) service;
                    myService = binder.getService();
                    myService.downApk(url, new DownloadService.DownloadCallback() {

                        @Override
                        public void onPrepare() {
                            isDownload = true;
                        }

                        @Override
                        public void onProgress(int progress) {
                            CLDLogger.I("--下载进度:--" + progress + "%");
                            txtProgress.setText("下载进度:" + progress + "%");
                        }

                        @Override
                        public void onComplete(File file) {
                            isDownload = false;
                            setPermission(file.getPath());
                            CLDLogger.I("下载完成,文件路径为:" + file.getPath());
                            txtProgress.setText("下载完成,文件路径为:" + file.getPath());
                            apkFile = file.getPath();
                            if (Build.VERSION.SDK_INT >= 26) {
                                boolean b = getPackageManager().canRequestPackageInstalls();
                                if (b) {
                                    installApk(file);
                                    //安装应用的逻辑(写自己的就可以)
                                } else {
                                    //请求安装未知应用来源的权限
                                    AlertDialog.Builder builder = new AlertDialog.Builder(DownLoadActivity.this);
                                    builder.setTitle("提示");
                                    builder.setMessage("“安装应用需要打开未知来源权限，请去设置中开启权限");
                                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                                            startActivityForResult(intent, INSTALL_PACKAGES_REQUESTCODE);
                                        }
                                    });
                                    builder.setNegativeButton("取消", null);
                                    builder.show();
                                }
                            } else {
                                installApk(file);
                            }
                        }

                        @Override
                        public void onFail(String msg) {
                            isDownload = false;
                            CLDLogger.I("下载失败:" + msg);
                            txtProgress.setText("下载失败:" + msg);
                        }
                    });
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    //意味中断，较小发生，酌情处理
                    CLDLogger.I("链接失败");
                }
            };
        }
        Intent intent = new Intent(DownLoadActivity.this, DownloadService.class);
        isBind = bindService(intent, conn, Service.BIND_AUTO_CREATE);
    }

    @Override
    public void cancel() {
        ToastUtils.showMiddleToast(this, "您取消了应用更新");
    }

    /**
     * 提升读写权限
     *
     * @param filePath 文件路径
     * @return
     * @throws IOException
     */
    public static void setPermission(String filePath) {
        String command = "chmod " + "777" + " " + filePath;
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void installApk(File file) {
        try {
//                                String authority = getPackageName() + ".fileProvider";
            String authority = "com.peixing.myapplication.fileProvider";
            Uri fileUri = FileProvider.getUriForFile(DownLoadActivity.this, authority, file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(fileUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            }
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == INSTALL_PACKAGES_REQUESTCODE) {
            if (Build.VERSION.SDK_INT >= 26) {
                boolean b = getPackageManager().canRequestPackageInstalls();
                CLDLogger.I("====" + b);
                if (b) {
                    installApk(new File(apkFile));
                    //安装应用的逻辑(写自己的就可以)
                }
            } else {
                installApk(new File(apkFile));
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!isDownload) {
            super.onBackPressed();
        } else {
            ToastUtils.showMiddleToast(this, "当前正在下载应用,请稍后退出页面");
        }
    }
}
