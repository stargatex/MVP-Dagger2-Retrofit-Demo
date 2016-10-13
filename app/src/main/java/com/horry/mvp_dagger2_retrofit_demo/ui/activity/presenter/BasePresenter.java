package com.horry.mvp_dagger2_retrofit_demo.ui.activity.presenter;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.horry.mvp_dagger2_retrofit_demo.data.ModelHelper;
import com.horry.mvp_dagger2_retrofit_demo.data.ModelHelperImpl;
import com.horry.mvp_dagger2_retrofit_demo.data.api.ApiService;
import com.horry.mvp_dagger2_retrofit_demo.model.Response;
import com.horry.mvp_dagger2_retrofit_demo.model.home.Home;
import com.softstao.softstaolibrary.library.mvp.presenter.MvpPresenter;
import com.softstao.softstaolibrary.library.mvp.viewer.BaseViewer;

import javax.inject.Inject;

import retrofit2.Retrofit;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 *  MVP中的Presenter基类
 *  * Created by xuhon on 2016/7/29.
 * @param <V> 继承BaseViewer
 */
public abstract class BasePresenter<V extends BaseViewer> implements MvpPresenter<V> {
    /**
     * MVP中的V
     */
    protected V viewer;

    protected ModelHelperImpl helper;
    protected Integer currentPage = 0;
    /**
     * 构造方法
     * @param viewer
     */
    public BasePresenter(V viewer,ModelHelperImpl helper){
        this.viewer=viewer;
        this.helper =helper;
    }

    /**
     * 订阅对象
     */
    private Subscription subscription;

    /**
     * 注销监听者
     */
    protected void unsubscribe() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        subscription = null;
    }

    /**
     *  进行订阅服务
     * @param observable 通过retrofit.create生成的observable
     * @param action1   实际处理请求的
     * @param pullToRefresh 是否在刷新
     */
    public <T> void subscribe(Observable<Response<T>> observable, Action1<T> action1, final boolean pullToRefresh) {
        if (viewer!=null) {
            viewer.showLoading(pullToRefresh);
        }
        unsubscribe();
        subscription = observable
                .filter(mResponse ->{
                    int error = mResponse.getError();
                    if(error!=0){
                        int messageType = mResponse.getMsg_type();
                        if(messageType == -100){
                            error=-100;
                        }
                        switch (error){
                            case 1:
                                onError(new Throwable(mResponse.getMsg()),pullToRefresh);
                                return false;
                            case -100:
                                viewer.noLogin();
                                return false;
                        }
                    }
                    return true;
                })
                .map(mResponse -> mResponse.getData())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action1,
                        throwable -> {this.onError(throwable,pullToRefresh);},
                        this::onCompleted);
    }


    /**
     * 事件结束时调用
     */
    protected void onCompleted() {
        if (viewer!=null) {
            viewer.showContent();
            viewer.closePtrFrameLayout();
        }
        unsubscribe();
    }

    /**
     * 时间错误调用
     * @param e 错误信息
     * @param pullToRefresh 是否正在刷新
     */
    protected void onError(Throwable e, boolean pullToRefresh) {
        if (viewer!=null) {
            viewer.showError(e, pullToRefresh);
            viewer.closePtrFrameLayout();
        }
        unsubscribe();
    }
}
