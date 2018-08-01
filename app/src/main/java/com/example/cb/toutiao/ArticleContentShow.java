package com.example.cb.toutiao;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class ArticleContentShow extends AppCompatActivity {
    private WebView webView = null;
    private ProgressDialog dialog = null;
    private Intent intent;
    private String share_url;
    private Toolbar toolbar;
    private Bundle data;
    private Window window;

    //@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_content_show);
        window = getWindow();
        intent = getIntent();
        // 获取该intent所携带的数据
        data = intent.getExtras();
        toolbar = (Toolbar) findViewById(R.id.contentToolbar);
        toolbar.setTitle("今日头条 - 文章内容");
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
        setSupportActionBar(toolbar);

        try {
            if(!data.getString("name").equals(null)){
                //设置状态栏颜色
                window.setStatusBarColor(Color.parseColor(data.getString("theme")));
                //设置背景颜色
                toolbar.setBackgroundColor(Color.parseColor(data.getString("theme")));
            }
        }catch (Exception e){
            e.printStackTrace();
            window.setStatusBarColor(Color.parseColor( "#EE3B3B"));
            toolbar.setBackgroundColor(Color.parseColor( "#EE3B3B"));
        }

        //初始化页面
        initViews();

        // 进度条显示网页的加载过程
        download();

        //设置webView能够识别除http、https以外的自定义协议
        WebViewClient webViewClient = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView wv, String url) {
                if(url == null) return false;
                try {
                    if(url.startsWith("weixin://") //微信
                            || url.startsWith("alipays://") //支付宝
                            || url.startsWith("mailto://") //邮件
                            || url.startsWith("tel://")//电话
                            || url.startsWith("dianping://")//大众点评
                            || url.startsWith("snssdk143://")//阳光宽屏网
                        //其他自定义的scheme
                            ) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                        return true;
                    }
                } catch (Exception e) { //防止crash (如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash)
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                        dialog = null;
                    }
                    Toast.makeText(ArticleContentShow.this, "您没有安装该App！"
                            , Toast.LENGTH_LONG).show();
                    return true;//没有安装该app时，返回true，表示拦截自定义链接，但不跳转，避免弹出上面的错误页面
                }

                //处理http和https开头的url
                wv.loadUrl(url);
                return true;
            }
        };
        webView.setWebViewClient(webViewClient);

        // 启用支持JavaScript
        WebSettings webSettings = webView.getSettings();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheEnabled(true);// 应用可以有缓存
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);// 优先使用缓存
        webSettings.setAppCacheEnabled(true);// 缓存最多可以有10M

        // 优先使用缓存优化效率
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
    }

    private void initViews() {
        //隐藏/显示返回箭头
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //设置使用activity的logo还是activity的icontrue为logo
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        webView = (WebView) findViewById(R.id.webView);
        share_url = intent.getStringExtra("share_url");
        webView.loadUrl(share_url);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }

    //设置进度条
    private void download() {
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    // 加载完毕
                    closeDialog(newProgress);
                } else {
                    openDialog(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }

            private void openDialog(int newProgress) {
                if (dialog == null) {
                    dialog = new ProgressDialog(ArticleContentShow.this);
                    dialog.setTitle("正在加载");
                    //ProgressDialog.STYLE_SPINNER 环形精度条
                    //ProgressDialog.STYLE_HORIZONTAL 水平样式的进度条
                    dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    //dialog.setMessage("loading...");
                    dialog.setProgress(newProgress);
                    dialog.show();
                } else {
                    dialog.setProgress(newProgress);
                }
            }

            private void closeDialog(int newProgress) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                    dialog = null;
                }
            }
        });
    }
}
