package com.example.admin.nantuoappdemo.othermanager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.admin.nantuoappdemo.R;
import com.example.admin.nantuoappdemo.activity.ActivityManager;
import com.example.admin.nantuoappdemo.rx.ActivityLifeCycleEvent;
import com.example.admin.nantuoappdemo.utils.StatusBarUtil;

import retrofit2.Response;
import rx.Observable;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

/**
 * Created by admin on 2017/11/6.
 */

public abstract class RxActivity extends BaseActivity {
    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifecycleSubject.onNext(ActivityLifeCycleEvent.CREATE);
        setStatusBar();

        ActivityManager.getInstance().add(this);
        Log.e("add", ActivityManager.getInstance().size() + "");
    }

    //销毁当前的Activity
    public void removeCurrentActivity() {
        ActivityManager.getInstance().removeCurrent();
        Log.e("remove", ActivityManager.getInstance().size() + "");
    }

    /**
     * 设置状态栏颜色
     */
    protected void setStatusBar() {
        StatusBarUtil.setColor(this, getResources().getColor(R.color.color_theme));
    }

    @Override
    protected void onDestroy() {
        lifecycleSubject.onNext(ActivityLifeCycleEvent.DESTROY);

        super.onDestroy();
    }

    @Override
    protected void onStop() {
        lifecycleSubject.onNext(ActivityLifeCycleEvent.STOP);

        super.onStop();
    }

    public <T> Observable.Transformer<Response<T>, Response<T>> bindUntilEvent(@NonNull final ActivityLifeCycleEvent event) {
        return new Observable.Transformer<Response<T>, Response<T>>() {
            @Override
            public Observable<Response<T>> call(Observable<Response<T>> sourceObservable) {
                Observable<ActivityLifeCycleEvent> o = lifecycleSubject.takeFirst(new Func1<ActivityLifeCycleEvent, Boolean>() {
                    @Override
                    public Boolean call(ActivityLifeCycleEvent activityLifeCycleEvent) {
                        return activityLifeCycleEvent.equals(event);
                    }
                });
                return sourceObservable.takeUntil(o);
            }
        };
    }

    public <T> Observable.Transformer<Response<T>, Response<T>> bindUntilEvent() {
        return new Observable.Transformer<Response<T>, Response<T>>() {
            @Override
            public Observable<Response<T>> call(Observable<Response<T>> sourceObservable) {
                Observable<ActivityLifeCycleEvent> o = lifecycleSubject.takeFirst(new Func1<ActivityLifeCycleEvent, Boolean>() {
                    @Override
                    public Boolean call(ActivityLifeCycleEvent activityLifeCycleEvent) {
                        return activityLifeCycleEvent.equals(ActivityLifeCycleEvent.DESTROY);
                    }
                });
                return sourceObservable.takeUntil(o);
            }
        };
    }
}
