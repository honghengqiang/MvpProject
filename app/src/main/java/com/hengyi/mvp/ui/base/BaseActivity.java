package com.hengyi.mvp.ui.base;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hengyi.mvp.MyApp;
import com.hengyi.mvp.R;
import com.hengyi.mvp.ui.inter.IBase;
import com.jaeger.library.StatusBarUtil;
import com.trello.rxlifecycle2.LifecycleTransformer;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.bingoogolapple.swipebacklayout.BGASwipeBackHelper;

/**
 * Created by Administrator on 2018/1/15.
 */

public abstract class BaseActivity<T1 extends BaseContract.BasePresenter> extends SupportActivity implements IBase,BaseContract.BaseView,BGASwipeBackHelper.Delegate{

    protected View mRootView;
    protected Dialog mLoadingDialog = null;
    Unbinder unbinder;

    @Inject
    protected T1 mPresenter;

    protected BGASwipeBackHelper mSwipeBackHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        initSwipeBackFinish();
        super.onCreate(savedInstanceState);
        mRootView = createView(null,null,savedInstanceState);
        setContentView(mRootView);
        initInjector(MyApp.getInstance().getApplicationComponent());
        attachView();
        bindView(mRootView, savedInstanceState);
        initStateView();
        initData();
    }

    private void initStateView() {

    }

    private void attachView() {
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(getContentLayout(), container);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public View getView() {
        return mRootView;
    }

    /**
     * 初始化滑动返回。在 super.onCreate(savedInstanceState) 之前调用该方法
     */
    private void initSwipeBackFinish() {
        mSwipeBackHelper = new BGASwipeBackHelper(this, this);

        // 「必须在 Application 的 onCreate 方法中执行 BGASwipeBackHelper.init 来初始化滑动返回」
        // 下面几项可以不配置，这里只是为了讲述接口用法。

        // 设置滑动返回是否可用。默认值为 true
        mSwipeBackHelper.setSwipeBackEnable(true);
        // 设置是否仅仅跟踪左侧边缘的滑动返回。默认值为 true
        mSwipeBackHelper.setIsOnlyTrackingLeftEdge(true);
        // 设置是否是微信滑动返回样式。默认值为 true
        mSwipeBackHelper.setIsWeChatStyle(true);
        // 设置阴影资源 id。默认值为 R.drawable.bga_sbl_shadow
        mSwipeBackHelper.setShadowResId(R.drawable.bga_sbl_shadow);
        // 设置是否显示滑动返回的阴影效果。默认值为 true
        mSwipeBackHelper.setIsNeedShowShadow(true);
        // 设置阴影区域的透明度是否根据滑动的距离渐变。默认值为 true
        mSwipeBackHelper.setIsShadowAlphaGradient(true);
        // 设置触发释放后自动滑动返回的阈值，默认值为 0.3f
        mSwipeBackHelper.setSwipeBackThreshold(0.3f);
    }

    /**
     * 设置状态栏颜色
     *
     * @param color
     */
    protected void setStatusBarColor(@ColorInt int color) {
        setStatusBarColor(color, StatusBarUtil.DEFAULT_STATUS_BAR_ALPHA);
    }

    /**
     * 设置状态栏颜色
     *
     * @param color
     * @param statusBarAlpha 透明度
     */
    public void setStatusBarColor(@ColorInt int color, @IntRange(from = 0, to = 255) int statusBarAlpha) {
        StatusBarUtil.setColorForSwipeBack(this, color, statusBarAlpha);
    }


    @Override
    public boolean isSupportSwipeBack() {
        return true;
    }

    @Override
    public void onSwipeBackLayoutSlide(float slideOffset) {

    }

    @Override
    public void onSwipeBackLayoutCancel() {

    }

    @Override
    public void onSwipeBackLayoutExecuted() {
        mSwipeBackHelper.swipeBackward();
    }

    @Override
    public void onBackPressed() {
        // 正在滑动返回的时候取消返回按钮事件
        if (mSwipeBackHelper.isSliding()) {
            return;
        }
        mSwipeBackHelper.backward();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void showSuccess() {

    }

    @Override
    public void showFaild() {

    }

    @Override
    public void showNoNet() {

    }

    @Override
    public <T> LifecycleTransformer<T> bindToLife() {
        return this.<T>bindToLifecycle();
    }
}
