package com.peixing.myapplication.rxgalleryfinal.presenter;


import com.peixing.myapplication.rxgalleryfinal.view.MediaGridView;

/**
 * Desction:
 * Author:pengjianbo  Dujinyang
 * Date:16/5/14 上午10:53
 */
public interface MediaGridPresenter {

    void setMediaGridView(MediaGridView mediaGridView);

    void getMediaList(String bucketId, int pageSize, int currentOffset);

    void getBucketList();
}
