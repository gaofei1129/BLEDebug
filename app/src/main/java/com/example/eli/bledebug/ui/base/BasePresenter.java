package com.example.eli.bledebug.ui.base;

import rx.Subscription;

/**
 * Created by Eli on 2017/2/14.
 */

public class BasePresenter<V> {
    public V mView;
    protected Subscription mSubscription;

    public void attach(V view) {
        mView = view;
    }

    public void detach() {
        mView = null;
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
    }
}
