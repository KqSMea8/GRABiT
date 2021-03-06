package com.android.spin.common;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.base.base.BaseUIActivity;
import com.android.spin.R;
import com.android.spin.common.delegate.CommonShareDelegate;
import com.android.spin.shop.entity.ShopProductItemEntity;
import com.android.spin.util.image.BlurBuilder;

public class CommonShareActivity extends BaseUIActivity<CommonShareDelegate> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static void star(Context mcontext,ShopProductItemEntity entity){
        Intent intent = new Intent(mcontext,CommonShareActivity.class);
        intent.putExtra("url",entity);
        mcontext.startActivity(intent);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public int getLayoutStyle() {
        return TITLEBARSTYLE_NOT;
    }

    @Override
    public void initView() {

        setMainBg(Color.TRANSPARENT);
    }

    @Override
    public void initHeaderView() {

    }

    @Override
    protected Class getDelegateClass() {
        return CommonShareDelegate.class;
    }

    @Override
    public void finish() {
//        defaultFinishNotActivityHelper();
        BlurBuilder.recycle();
        overridePendingTransition(android.view.animation.Animation.INFINITE,
                android.view.animation.Animation.INFINITE);
        super.finish();
    }
}
