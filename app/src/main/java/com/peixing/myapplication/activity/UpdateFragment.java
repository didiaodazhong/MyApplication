package com.peixing.myapplication.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.peixing.myapplication.R;

/**
 * Created by xuxiaohe on 2018/4/10.
 */

@SuppressLint("ValidFragment")
public class UpdateFragment extends DialogFragment implements View.OnClickListener {
    private Button btnCancel;
    private Button btnEnsure;
    private DownLoadFragmentListener downLoadFragmentListener;
    private Context context;
    private LinearLayout linFragmentContainer;
    private TextView txtFragmentMessage;
    private boolean hadIntercept;

    public UpdateFragment(Context context) {
        this.context = context;
    }

    public void setDownLoadFragmentListener(DownLoadFragmentListener downLoadFragmentListener) {
        this.downLoadFragmentListener = downLoadFragmentListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_update);
        linFragmentContainer = (LinearLayout) dialog.findViewById(R.id.lin_fragment_container);
        txtFragmentMessage = (TextView) dialog.findViewById(R.id.txt_fragment_message);
        btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
        btnEnsure = (Button) dialog.findViewById(R.id.btn_ensure);
        btnCancel.setOnClickListener(this);
        btnEnsure.setOnClickListener(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (hadIntercept) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return true;
                }
            }
        });
        return dialog;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                downLoadFragmentListener.cancel();
                dismiss();
                break;
            case R.id.btn_ensure:
                downLoadFragmentListener.ensure();
//                dismiss();
                break;
            default:
                break;
        }
    }

    public void setHadIntercept(boolean hadIntercept) {
        this.hadIntercept = hadIntercept;
    }

    public void setProgress(String progress) {
        if (txtFragmentMessage != null) {
            txtFragmentMessage.setText(progress);
        }
    }
}
