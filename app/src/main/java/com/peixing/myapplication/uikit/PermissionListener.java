package com.peixing.myapplication.uikit;

import java.util.List;

/**
 * Created by xuxiaohe on 2017/6/24.
 * 已授权、未授权的接口回调
 */

public interface PermissionListener {
    void onGranted();//已授权

    void onDenied(List<String> deniedPermission);//未授权

}
