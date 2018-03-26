package com.peixing.myapplication.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.peixing.myapplication.R;
import com.peixing.myapplication.core.FingerprintCore;
import com.peixing.myapplication.core.KeyguardLockScreenManager;
import com.peixing.myapplication.utils.FingerprintUtil;

public class FingerPrintActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView fingerprintGuide;
    private TextView fingerprintGuideTip;
    private Button fingerprintRecognitionStart;
    private Button fingerprintRecognitionCancel;
    private Button fingerprintRecognitionSysUnlock;
    private Button fingerprintRecognitionSysSetting;
    private FingerprintCore mFingerprintCore;
    private KeyguardLockScreenManager mKeyguardLockScreenManager;
    private Toast mToast;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finger_print);
        initView();
        initListener();
        initFingerPrint();
    }

    private void initFingerPrint() {
        mFingerprintCore = new FingerprintCore(this);
        mFingerprintCore.setFingerprintManager(mResultListener);
        mKeyguardLockScreenManager = new KeyguardLockScreenManager(this);
    }

    private void initView() {
        fingerprintGuide = (ImageView) findViewById(R.id.fingerprint_guide);
        fingerprintGuideTip = (TextView) findViewById(R.id.fingerprint_guide_tip);
        fingerprintRecognitionStart = (Button) findViewById(R.id.fingerprint_recognition_start);
        fingerprintRecognitionCancel = (Button) findViewById(R.id.fingerprint_recognition_cancel);
        fingerprintRecognitionSysUnlock = (Button) findViewById(R.id.fingerprint_recognition_sys_unlock);
        fingerprintRecognitionSysSetting = (Button) findViewById(R.id.fingerprint_recognition_sys_setting);
    }

    private void initListener() {
        fingerprintRecognitionSysSetting.setOnClickListener(this);
        fingerprintRecognitionStart.setOnClickListener(this);
        fingerprintRecognitionCancel.setOnClickListener(this);
        fingerprintRecognitionSysUnlock.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fingerprint_recognition_start:
                startFingerprintRecognition();
                break;
            case R.id.fingerprint_recognition_cancel:
                cancelFingerprintRecognition();
                break;
            case R.id.fingerprint_recognition_sys_setting:
                enterSysFingerprintSettingPage();
                break;
            case R.id.fingerprint_recognition_sys_unlock:
                startFingerprintRecognitionUnlockScreen();
                break;
            default:
                break;
        }
    }

    /**
     * 开始指纹识别
     */
    private void startFingerprintRecognition() {
        if (mFingerprintCore.isSupport()) {
            if (!mFingerprintCore.isHasEnrolledFingerprints()) {
                //没有录入指纹
                toastTipMsg(R.string.fingerprint_recognition_not_enrolled);
                FingerprintUtil.openFingerPrintSettingPage(this);
                return;
            }
            //指纹识别开启,可以验证
            toastTipMsg(R.string.fingerprint_recognition_tip);
            fingerprintGuideTip.setText(R.string.fingerprint_recognition_tip);
            fingerprintGuide.setBackgroundResource(R.drawable.fingerprint_guide);
            if (mFingerprintCore.isAuthenticating()) {
                toastTipMsg(R.string.fingerprint_recognition_authenticating);
            } else {
                mFingerprintCore.startAuthenticate();
            }
        } else {
            //硬件不支持
            toastTipMsg(R.string.fingerprint_recognition_not_support);
            fingerprintGuideTip.setText(R.string.fingerprint_recognition_tip);
        }
    }

    /**
     * 指纹解锁屏幕
     */
    private void startFingerprintRecognitionUnlockScreen() {
        if (mKeyguardLockScreenManager == null) {
            return;
        }
        if (!mKeyguardLockScreenManager.isOpenLockScreenPwd()) {
            //系统没有设置锁屏密码
            toastTipMsg(R.string.fingerprint_not_set_unlock_screen_pws);
            FingerprintUtil.openFingerPrintSettingPage(this);
            return;
        }
        mKeyguardLockScreenManager.showAuthenticationScreen(this);
    }

    /**
     * 取消指纹解锁
     */
    private void cancelFingerprintRecognition() {
        if (mFingerprintCore.isAuthenticating()) {
            mFingerprintCore.cancelAuthenticate();
            resetGuideViewState();
        }
    }

    /**
     * 打开系统设置界面
     */
    private void enterSysFingerprintSettingPage() {
        FingerprintUtil.openFingerPrintSettingPage(this);
    }

    private FingerprintCore.IFingerprintResultListener mResultListener = new FingerprintCore.IFingerprintResultListener() {
        @Override
        public void onAuthenticateSuccess() {
            //识别成功
            toastTipMsg(R.string.fingerprint_recognition_success);
            resetGuideViewState();
        }

        @Override
        public void onAuthenticateFailed(int helpId) {
            //识别失败
            toastTipMsg(R.string.fingerprint_recognition_failed);
            fingerprintGuideTip.setText(R.string.fingerprint_recognition_failed);
        }

        @Override
        public void onAuthenticateError(int errMsgId) {
            //识别错误,请稍后重试
            resetGuideViewState();
            toastTipMsg(R.string.fingerprint_recognition_error);
        }

        @Override
        public void onStartAuthenticateResult(boolean isSuccess) {

        }
    };

    private void toastTipMsg(int messageId) {
        if (mToast == null) {
            mToast = Toast.makeText(this, messageId, Toast.LENGTH_SHORT);
        }
        mToast.setText(messageId);
        mToast.cancel();
        mHandler.removeCallbacks(mShowToastRunnable);
        mHandler.postDelayed(mShowToastRunnable, 0);
    }

    private void toastTipMsg(String message) {
        if (mToast == null) {
            mToast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        }
        mToast.setText(message);
        mToast.cancel();
        mHandler.removeCallbacks(mShowToastRunnable);
        mHandler.postDelayed(mShowToastRunnable, 200);
    }

    private Runnable mShowToastRunnable = new Runnable() {
        @Override
        public void run() {
            mToast.show();
        }
    };

    private void resetGuideViewState() {
        fingerprintGuideTip.setText(R.string.fingerprint_recognition_guide_tip);
        fingerprintGuide.setBackgroundResource(R.drawable.fingerprint_normal);
    }

    @Override
    protected void onDestroy() {
        if (mFingerprintCore != null) {
            mFingerprintCore.onDestroy();
            mFingerprintCore = null;
        }
        if (mKeyguardLockScreenManager != null) {
            mKeyguardLockScreenManager.onDestroy();
            mKeyguardLockScreenManager = null;
        }
        mResultListener = null;
        mShowToastRunnable = null;
        mToast = null;
        super.onDestroy();
    }
}
