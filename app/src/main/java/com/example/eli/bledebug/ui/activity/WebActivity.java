package com.example.eli.bledebug.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.eli.bledebug.R;
import com.example.eli.bledebug.ui.base.BaseActivity;
import com.umeng.analytics.MobclickAgent;
import com.wang.avi.AVLoadingIndicatorView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Eli on 2017/2/15.
 */

public class WebActivity extends BaseActivity {
    @BindView(R.id.main_toolbar)
    Toolbar mainToolbar;
    @BindView(R.id.webView)
    WebView webView;
    int state; //1、Github 2、CSDN
    String loadUrl;
    @BindView(R.id.avi)
    AVLoadingIndicatorView avi;

    //https://github.com/gaofei1129
    //http://blog.csdn.net/gao_fei1129/
    @Override
    protected int initLayoutId() {
        return R.layout.activity_web;
    }

    @Override
    protected void initView() {
        //Toolbar上显示menu菜单
        setSupportActionBar(mainToolbar);
        //增加返回按钮
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //WebView加载web资源
        webView.loadUrl(loadUrl);

        //启用支持javascript
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        //优先使用缓存
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //判断页面加载过程
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // TODO Auto-generated method stub
                if (newProgress == 100) {
                    // 网页加载完成
                    //Log.e("setWebChromeClient", "网页加载完成...");
                    stopAnim();
                } else {
                    // 加载中
                    //Log.e("setWebChromeClient", "加载中...");
                    startAnim();
                }

            }
        });
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        state = intent.getIntExtra("url", 1);
        if (state == 1) {
            loadUrl = "https://github.com/gaofei1129";
            mainToolbar.setTitle("Github");
        } else {
            loadUrl = "http://blog.csdn.net/gao_fei1129/";
            mainToolbar.setTitle("CSDN");
        }
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

    //改写物理按键——返回的逻辑,希望浏览的网页后退而不是退出浏览器
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) {
                webView.goBack();//返回上一页面
                return true;
            } else {
                System.exit(0);//退出程序
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    void startAnim() {
        avi.show();
        // or avi.smoothToShow();
    }

    void stopAnim() {
        avi.hide();
        // or avi.smoothToHide();
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
