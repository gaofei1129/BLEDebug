package com.example.eli.bledebug.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Eli on 2017/2/16.
 */

public class SaveObservable implements Observable.OnSubscribe<String> {

    private Bitmap drawingCache = null;
    private Context mContext;

    public SaveObservable(Bitmap drawingCache, Context context) {
        this.drawingCache = drawingCache;
        this.mContext = context;
    }

    @Override
    public void call(Subscriber<? super String> subscriber) {
        if (drawingCache == null) {
            subscriber.onError(new NullPointerException("imageview的bitmap获取为null,请确认imageview显示图片了"));
        } else {
            try {
                File imageFile = new File(Environment.getExternalStorageDirectory(), "saveImageview.jpg");
                FileOutputStream outStream;
                outStream = new FileOutputStream(imageFile);
                drawingCache.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                subscriber.onNext(Environment.getExternalStorageDirectory().getPath());
                subscriber.onCompleted();
                outStream.flush();
                outStream.close();
                // 最后通知图库更新
                mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(imageFile.getPath()))));
            } catch (IOException e) {
                subscriber.onError(e);
            }
        }
    }

    public class SaveSubscriber extends Subscriber<String> {

        @Override
        public void onCompleted() {
            Toast.makeText(mContext.getApplicationContext(), "保存成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(Throwable e) {
            Log.i(getClass().getSimpleName(), e.toString());
            Toast.makeText(mContext.getApplicationContext(), "保存失败——> " + e.toString(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNext(String s) {
            Toast.makeText(mContext.getApplicationContext(), "保存路径为：-->  " + s, Toast.LENGTH_SHORT).show();
        }
    }


    public void saveImageView(Bitmap drawingCache,Context context) {
        Observable.create(new SaveObservable(drawingCache,context))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SaveSubscriber());
    }

    /**
     * 某些机型直接获取会为null,在这里处理一下防止国内某些机型返回null
     */
    private Bitmap getViewBitmap(View view) {
        if (view == null) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
}
