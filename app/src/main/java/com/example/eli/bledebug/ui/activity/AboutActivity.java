package com.example.eli.bledebug.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableRow;

import com.example.eli.bledebug.R;
import com.example.eli.bledebug.model.SaveObservable;
import com.example.eli.bledebug.ui.base.BaseActivity;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Eli on 2017/2/15.
 */

public class AboutActivity extends BaseActivity {
    @BindView(R.id.row_git)
    TableRow rowGit;
    @BindView(R.id.row_csdn)
    TableRow rowCsdn;
    @BindView(R.id.row_share)
    TableRow rowShare;
    @BindView(R.id.row_ping)
    TableRow rowPing;
    @BindView(R.id.img_wx)
    ImageView imageView;
    Bitmap image;
    SaveObservable saveObservable;

    @Override
    protected int initLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    protected void initView() {
        //Toolbar上显示menu菜单
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        //增加返回按钮
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                saveObservable.saveImageView(image, AboutActivity.this);
                return true;
            }
        });
    }

    @Override
    protected void initData() {
        image = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        saveObservable = new SaveObservable(image, this);
    }

    public void toShare(String plainContent) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, plainContent);
        intent.setType("text/plain");

        this.startActivity(Intent.createChooser(intent, "分享到:"));
    }

    public void toPingFen() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void toWeb(int state) {
        Intent intent = new Intent(this, WebActivity.class);
        intent.putExtra("url", state);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.row_git, R.id.row_csdn, R.id.row_share, R.id.row_ping})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.row_git:
                toWeb(1);
                break;
            case R.id.row_csdn:
                toWeb(2);
                break;
            case R.id.row_share:
                toShare("ble");
                break;
            case R.id.row_ping:
                toPingFen();
                break;
        }
    }
}
