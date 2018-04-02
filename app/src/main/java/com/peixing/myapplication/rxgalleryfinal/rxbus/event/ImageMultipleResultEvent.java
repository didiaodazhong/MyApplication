package com.peixing.myapplication.rxgalleryfinal.rxbus.event;

import com.peixing.myapplication.rxgalleryfinal.bean.MediaBean;

import java.util.List;



/**
 * Desction:
 * Author:pengjianbo  Dujinyang
 * Date:16/8/1 下午10:52
 */
public class ImageMultipleResultEvent implements BaseResultEvent {
    private final List<MediaBean> mediaResultList;

    public ImageMultipleResultEvent(List<MediaBean> list) {
        this.mediaResultList = list;
    }

    public List<MediaBean> getResult() {
        return mediaResultList;
    }
}
