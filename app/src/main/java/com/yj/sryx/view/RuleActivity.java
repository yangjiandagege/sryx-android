package com.yj.sryx.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.yj.sryx.R;
import com.yj.sryx.SryxApp;
import com.yj.sryx.utils.LogUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by eason.yang on 2017/7/15.
 */

public class RuleActivity extends BaseActivity {
    @Bind(R.id.wv_game_rule)
    WebView wvGameRule;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(SryxApp.sActivityThemeMap.get(this.getClass().getSimpleName()).getStyleId());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rule);
        ButterKnife.bind(this);
        initWebView();
    }

    private void initWebView() {
        //声明WebSettings子类
        WebSettings webSettings = wvGameRule.getSettings();

        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true);
        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

        //缩放操作
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件

        //其他细节操作
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式

        wvGameRule.loadUrl("https://www.ywwxmm.cn/image/game_rule.html");

        //设置不用系统浏览器打开,直接显示在当前Webview
        wvGameRule.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }

    //销毁Webview
    @Override
    protected void onDestroy() {
        if (wvGameRule != null) {
            wvGameRule.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            wvGameRule.clearHistory();

            ((ViewGroup) wvGameRule.getParent()).removeView(wvGameRule);
            wvGameRule.destroy();
            wvGameRule = null;
        }
        super.onDestroy();
    }
}
