package com.android.base.module.base;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by codeest on 2016/8/2.
 * Presenter基类
 */
public interface BasePresenter<T extends BaseView>{

    void attachView(T view);

    void detachView();

    AppCompatActivity getActivity();
}