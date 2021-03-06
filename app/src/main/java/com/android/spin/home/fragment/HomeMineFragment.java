package com.android.spin.home.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.base.base.BaseHeaderTitleBarView;
import com.android.base.base.MvpFragment;
import com.android.base.base.application.BaseApplication;
import com.android.base.mvp.view.IView;
import com.android.base.util.AppLanguageManager;
import com.android.base.util.AppManagerUtil;
import com.android.base.util.ToastUtil;
import com.android.base.util.image.GlideCatchUtil;
import com.android.base.util.image.GlideUtil;
import com.android.spin.R;
import com.android.spin.common.AgreementActivity;
import com.android.spin.common.selector.view.CircleImageView;
import com.android.spin.common.util.Constant;
import com.android.spin.db.UserManager;
import com.android.spin.event.AddCardEvent;
import com.android.spin.event.UpdateUserInfoEvent;
import com.android.spin.home.HomeActivity;
import com.android.spin.logreg.LoginActivity;
import com.android.spin.mine.PhoneActivity;
import com.android.spin.mine.UserInfoActivity;
import com.android.spin.mine.presenter.MinePresenter;
import com.android.spin.util.DialogUtil;
import com.android.spin.util.facebook.FaceBookLogin;
import com.android.spin.util.facebook.FacebookUser;
import com.android.spin.util.string.FormatStringUtil;
import com.android.spin.view.MineTabItemView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.orhanobut.logger.Logger;
import com.taobao.uikit.feature.view.TRecyclerView;
import com.taobao.uikit.feature.view.TTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：yangqiyun
 * 时间：2017/7/21
 * 1120389276@qq.com
 * 描述：个人中心
 */
public class HomeMineFragment extends MvpFragment<IView, MinePresenter> implements View.OnClickListener, IView {

    @Bind(R.id.btb_top_bar)
    BaseHeaderTitleBarView btbTopBar;
    @Bind(R.id.ttv_mine_name)
    TTextView ttvMineName;
    @Bind(R.id.ll_mine_info)
    LinearLayout llMineInfo;
    @Bind(R.id.miv_mine_clear_cache)
    MineTabItemView mivMineClearCache;
    @Bind(R.id.miv_mine_set_language)
    MineTabItemView mivMineSetLanguage;
    @Bind(R.id.miv_mine_mobile)
    MineTabItemView mivMineMobile;
    @Bind(R.id.miv_mine_version)
    MineTabItemView mivMineVersion;
    @Bind(R.id.miv_mine_agreement)
    MineTabItemView mivMineAgreement;
    @Bind(R.id.miv_mine_about_grabit)
    MineTabItemView mivMineAboutGrabit;
    @Bind(R.id.tv_bind_face_book)
    TTextView tvBindFaceBook;
    @Bind(R.id.tv_face_book_login)
    TTextView tvFaceBookLogin;

    private final static int TYPE_REQUEST_LOGOUT = 0x00;
    private final static int TYPE_REQUEST_BIND = 0x01;
    private final static int TYPE_REQUEST_UNBIND = 0x02;
    @Bind(R.id.cimg_user_avatar)
    CircleImageView cimgUserAvatar;

    private int[] languageIcons = {R.mipmap.icon_language_hk, R.mipmap.icon_language_cn, R.mipmap.icon_language_en};

    @Override
    public MinePresenter initPresenter() {
        return new MinePresenter();
    }

    public HomeMineFragment() {
        // Required empty public constructor
    }

    public static HomeMineFragment newInstance() {
        HomeMineFragment fragment = new HomeMineFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_home_mine;
    }

    private void initHeaderView() {
        btbTopBar.setHeaderTitle(getString(R.string.text_mine_title));
        btbTopBar.setTitleColor(getResources().getColor(R.color.app_color_191917));

    }

    private void initFacebookStatus(){
        try {
            if(TextUtils.isEmpty(UserManager.getInstance().getUser().getFb_openid())){
                tvBindFaceBook.setText(getString(R.string.text_mine_bind_face_book));
            }else{
                tvBindFaceBook.setText(getString(R.string.text_mine_unbind_face_book));
            }
        }catch (Exception e){

        }
    }


    /**
     * 更新用户信息
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateUserInfo(UpdateUserInfoEvent event) {
        initDatas();

        initFacebookStatus();

        initItem();
    }

    @Override
    public void initView() {
        initHeaderView();

        initItem();

        initDatas();

        initFacebookStatus();
    }

    @OnClick({R.id.miv_mine_clear_cache, R.id.miv_mine_set_language,
            R.id.miv_mine_mobile, R.id.miv_mine_version,
            R.id.miv_mine_agreement, R.id.miv_mine_about_grabit,
            R.id.tv_bind_face_book, R.id.tv_face_book_login, R.id.ll_mine_info})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_mine_info:
                //个人资讯
                UserInfoActivity.star(getActivity());
                break;
            case R.id.miv_mine_clear_cache:
                //清理缓存
                showClearCacheDialog();
                break;
            case R.id.miv_mine_set_language:
                //设置语言
                showSelectLanguageDialog();
                break;
            case R.id.miv_mine_mobile:
                //我的电话
                PhoneActivity.star(getActivity(),UserManager.getInstance().getUser().getPhone());
                break;
            case R.id.miv_mine_version:
                //当前版本
                break;
            case R.id.miv_mine_agreement:
                //服务条款
                if (AppLanguageManager.isLanguageEN()) {
                    AgreementActivity.star(getActivity(), Constant.URL_SERVICE_AGREEMENT_EN);
                } else {
                    AgreementActivity.star(getActivity(), Constant.URL_SERVICE_AGREEMENT_CN);
                }

                break;
            case R.id.miv_mine_about_grabit:
                //关于grabit
                AgreementActivity.star(getActivity(), Constant.URL_ABOUT_GRABIT);
                break;
            case R.id.tv_bind_face_book:
                //绑定facebook
                if(TextUtils.isEmpty(UserManager.getInstance().getUser().getFb_openid())){

                    FaceBookLogin mFaceBookLogin = ((HomeActivity)getActivity()).getFaceBookLogin();
                    mFaceBookLogin.setFacebookListener(new FaceBookLogin.FacebookListener() {
                        @Override
                        public void facebookLoginSuccess(FacebookUser user) {
                            Map<String,Object> params = new HashMap<>();
                            params.put("access_token",user.getToken());
                            getPresenter().doFacebookBind(params,user.getId(),TYPE_REQUEST_BIND);
                            showLoadDialog();
                        }
                        @Override
                        public void facebookLoginFail() {
                            ToastUtil.shortShow("无法打开facebook");
                        }
                    });
                    mFaceBookLogin.login();
                }else{
                    getPresenter().doFacebookUnBind(null,TYPE_REQUEST_UNBIND);
                    showLoadDialog();
                }
                break;
            case R.id.tv_face_book_login:
                //退出登陆
                showLogoutDialog();
                break;
        }
    }

    private void initDatas() {
        try {
            ttvMineName.setText(UserManager.getInstance().getUser().getName());
            GlideUtil.defualtLoad(getActivity(), UserManager.getInstance().getUser().getAvatar(),
                    R.mipmap.icon_mine_header,cimgUserAvatar);
        }catch (Exception e){}
    }

    /**
     * 初始化选项卡
     */
    private void initItem() {
        try{
            mivMineVersion.setValue("V" + AppManagerUtil.getVersionName(getActivity()));
            mivMineClearCache.setValue(GlideCatchUtil.getInstance().getCacheSize());
            mivMineMobile.setValue(FormatStringUtil.getPhone(UserManager.getInstance().getUser().getPhone()));
        }catch (Exception e){}
        initLanguage();
    }

    /**
     * 显示当前选中的语言
     */
    private void initLanguage() {
        if (AppLanguageManager.isLanguageCN()) {
            mivMineSetLanguage.setValue("中国简体");
            mivMineSetLanguage.setValueLeftDrawable(R.mipmap.icon_language_cn);
        } else if (AppLanguageManager.isLanguageHK()) {
            mivMineSetLanguage.setValue("香港繁體");
            mivMineSetLanguage.setValueLeftDrawable(R.mipmap.icon_language_hk);
        } else if (AppLanguageManager.isLanguageEN()) {
            mivMineSetLanguage.setValue("English");
            mivMineSetLanguage.setValueLeftDrawable(R.mipmap.icon_language_en);
        }
    }

    /**
     * 清除缓存提示
     */
    private void showClearCacheDialog() {
        DialogUtil.getStandDialog(getActivity(), null, getString(R.string.text_clear_cache_hint)
                , getString(R.string.dialog_btn_cancel), getString(R.string.dialog_btn_confirm),
                true, new DialogUtil.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog, View view, int position) {
                        switch (position) {
                            case 1:
                                //清除缓存
                                GlideCatchUtil.getInstance().cleanCatchDisk();
                                GlideCatchUtil.getInstance().clearCacheDiskSelf();
                                GlideCatchUtil.getInstance().clearCacheMemory();
                                mivMineClearCache.setValue(GlideCatchUtil.getInstance().getCacheSize());
                                break;
                        }
                    }
                }).show();
    }

    /**
     * 提示登出
     */
    private void showLogoutDialog() {
        DialogUtil.getStandDialog(getActivity(), null, getString(R.string.dialog_content_logout_hint)
                , getString(R.string.dialog_btn_cancel), getString(R.string.dialog_btn_confirm),
                true, new DialogUtil.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog, View view, int position) {
                        switch (position) {
                            case 1:
                                //退出登陆请求
                                showLoadDialog();
                                getPresenter().doLogout(TYPE_REQUEST_LOGOUT);
                                break;
                        }
                    }
                }).show();
    }

    /**
     * 选择语言
     */
    Dialog mdialog;

    private void showSelectLanguageDialog() {
        if (mdialog == null) {
            mdialog = DialogUtil.getListDialog(getActivity(), getString(R.string.dialog_select_language_title), languageIcons,
                    getResources().getStringArray(R.array.list_language), new TRecyclerView.OnItemClickListener() {
                        @Override
                        public void onItemClick(TRecyclerView parent, View view, int position, long id) {
                            mdialog.dismiss();
                            switch (position) {
                                case 0:
                                    AppLanguageManager.setLanguageHk();
                                    break;
                                case 1:
                                    AppLanguageManager.setLanguageCN();
                                    break;
                                case 2:
                                    AppLanguageManager.setLanguageEN();
                                    break;
                            }
//                            Toast.makeText(getActivity(),"正在重启应用。。。",Toast.LENGTH_LONG).show();
                            showWillRestar();
                        }
                    });
        }
        mdialog.show();
    }

    /**
     * 显示需要重启
     */
    private void showWillRestar(){
        DialogUtil.getStandDialog(getActivity(), null,
                getString(R.string.text_mine_change_langua_hint), getString(R.string.dialog_btn_cancel), getString(R.string.dialog_btn_confirm), true, new DialogUtil.OnClickListener() {
            @Override
            public void onClick(Dialog dialog, View view, int position) {
                if(position == 1){
                    getActivity().recreate();
                    BaseApplication.mAppHandle.doAppRestart();
                }
            }
        }).show();
    }

    @Override
    public void onFail(String code, int type) {

    }

    @Override
    public void onComplete(int type) {
        dismissLoadDialog();
    }

    @Override
    public void onSuccess(Object data, int type) {
        switch (type) {
            case TYPE_REQUEST_LOGOUT:
                //登出成功
                UserManager.getInstance().logout();
                LoginActivity.star(getActivity());
                finishActivity();
                break;
            case TYPE_REQUEST_BIND:
                initFacebookStatus();
                ToastUtil.shortShow(getString(R.string.text_mine_bind_face_book_hint));
                break;
            case TYPE_REQUEST_UNBIND:
                initFacebookStatus();
                ToastUtil.shortShow(getString(R.string.text_mine_unbind_face_book_hint));
                break;
        }
    }

}
