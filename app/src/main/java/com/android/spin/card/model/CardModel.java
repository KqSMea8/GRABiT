package com.android.spin.card.model;

import com.android.base.module.http.callback.ChildSubscriber;
import com.android.base.module.http.callback.OnNetRequestListener;
import com.android.base.module.http.callback.ShowApiListResponse;
import com.android.base.module.http.callback.ShowApiResponse;
import com.android.base.util.SignHelper;
import com.android.spin.card.entity.CardItemEntity;
import com.android.spin.card.service.CardRetrofitService;
import com.android.spin.common.entity.LoginResultEntity;
import com.android.spin.logreg.entity.RegisterResultEntity;
import com.android.spin.logreg.service.LogregRetrofitService;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 作者：yangqiyun
 * 时间：2017/8/2
 * 1120389276@qq.com
 * 描述：
 */

public class CardModel {

    /**
     * 获取优惠券列表
     * @param params
     * @param listener
     */
    public void getUserCoupons(Map<String,Object> params, OnNetRequestListener listener){
        Observable<ShowApiResponse<ShowApiListResponse<CardItemEntity>>> observable = CardRetrofitService.getInstance().
                createShowApi().getUserCoupons(CardRetrofitService.getCacheControl(), SignHelper.addSign(params));

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ChildSubscriber<ShowApiResponse<ShowApiListResponse<CardItemEntity>>>(listener) {
                });
    }

    /**
     * 领取优惠券
     * @param params
     * @param listener
     */
    public void postUserCoupons(Map<String,Object> params, OnNetRequestListener listener){
        Observable<ShowApiResponse<Object>> observable = CardRetrofitService.getInstance().
                createShowApi().postUserCoupons(CardRetrofitService.getCacheControl(), SignHelper.addSign(params));

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ChildSubscriber<ShowApiResponse<Object>>(listener) {
                });
    }
}
