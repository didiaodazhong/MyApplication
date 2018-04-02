package com.peixing.myapplication.rxgalleryfinal.presenter.impl;

import android.content.Context;

import com.peixing.myapplication.rxgalleryfinal.bean.BucketBean;
import com.peixing.myapplication.rxgalleryfinal.bean.MediaBean;
import com.peixing.myapplication.rxgalleryfinal.interactor.MediaBucketFactoryInteractor;
import com.peixing.myapplication.rxgalleryfinal.interactor.MediaSrcFactoryInteractor;
import com.peixing.myapplication.rxgalleryfinal.interactor.impl.MediaBucketFactoryInteractorImpl;
import com.peixing.myapplication.rxgalleryfinal.interactor.impl.MediaSrcFactoryInteractorImpl;
import com.peixing.myapplication.rxgalleryfinal.presenter.MediaGridPresenter;
import com.peixing.myapplication.rxgalleryfinal.view.MediaGridView;

import java.util.List;


/**
 * Desction:
 * Author:pengjianbo  Dujinyang
 * Date:16/5/14 上午10:58
 */
public class MediaGridPresenterImpl implements MediaGridPresenter, MediaSrcFactoryInteractor.OnGenerateMediaListener,
        MediaBucketFactoryInteractor.OnGenerateBucketListener {

    private final MediaSrcFactoryInteractor mediaSrcFactoryInteractor;
    private final MediaBucketFactoryInteractor mediaBucketFactoryInteractor;

    private MediaGridView mediaGridView;

    public MediaGridPresenterImpl(Context context, boolean isImage) {
        this.mediaSrcFactoryInteractor = new MediaSrcFactoryInteractorImpl(context, isImage, this);
        this.mediaBucketFactoryInteractor = new MediaBucketFactoryInteractorImpl(context, isImage, this);
    }

    /**
     * 设置MVP view(操作UI接口)
     */
    @Override
    public void setMediaGridView(MediaGridView mediaGridView) {
        this.mediaGridView = mediaGridView;
    }

    /**
     * 分页获取media
     */
    @Override
    public void getMediaList(String bucketId, int pageSize, int currentOffset) {
        mediaSrcFactoryInteractor.generateMeidas(bucketId, pageSize, currentOffset);
    }

    @Override
    public void getBucketList() {
        mediaBucketFactoryInteractor.generateBuckets();
    }

    /**
     * Media获取事件回调
     */
    @Override
    public void onFinished(String bucketId, int pageSize, int currentOffset, List<MediaBean> list) {
        mediaGridView.onRequestMediaCallback(list);
    }

    /**
     * BUCKET获取事件回调
     */
    @Override
    public void onFinished(List<BucketBean> list) {
        mediaGridView.onRequestBucketCallback(list);
    }
}
