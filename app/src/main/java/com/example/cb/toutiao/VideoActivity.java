package com.example.cb.toutiao;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.dou361.ijkplayer.widget.PlayerView;
import com.tamic.novate.Novate;

import okhttp3.MediaType;

public class VideoActivity extends AppCompatActivity {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private View rootView;
    private PlayerView player;
    private Intent intent;
    private Toolbar toolbar;
    private SharedPreferences sp;
    private String json;
    private Novate novate;
    private String share_url;
    private String mainurl;
    private String video_title;
    private WebView webView = null;
    private ProgressDialog dialog = null;
    private Bundle data;
    private Window window;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_content_show);
        window = getWindow();
        intent = getIntent();
        // 获取该intent所携带的数据
        data = intent.getExtras();
        toolbar = (Toolbar) findViewById(R.id.contentToolbar);
        toolbar.setTitle("今日头条 - 视频内容");
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

//        data = getIntent();
//        share_url = data.getStringExtra("share_url");
//        video_title=data.getStringExtra("video_title");

        //createThread();

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
        getSupportActionBar().setDisplayShowHomeEnabled(true);//隐藏/显示返回箭头
        getSupportActionBar().setDisplayUseLogoEnabled(true);//设置使用activity的logo还是activity的icontrue为logo
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
                    dialog = new ProgressDialog(VideoActivity.this);
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

//    private void playVideo(String url) {
//        rootView = getLayoutInflater().from(this).inflate(R.layout.simple_player_view_player, null);
//        setContentView(rootView);
//        player = new PlayerView(this)
//                .setTitle(video_title)
//                .hideSteam(true)
//                .setScaleType(PlayStateParams.f4_3)
//                .hideMenu(true)
//                .forbidTouch(false)
//                .showThumbnail(new OnShowThumbnailListener() {
//                    @Override
//                    public void onShowThumbnail(ImageView ivThumbnail) {
//                        Glide.with(VideoActivity.this)
//                                .load("http://pic2.nipic.com/20090413/406638_125424003_2.jpg")
//                                .error(R.color.colorPrimary)
//                                .into(ivThumbnail);
//                    }
//                })
//                .setPlaySource(url)
//                .startPlay();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (player != null) {
//            player.onPause();
//        }
//        /**demo的内容，恢复系统其它媒体的状态*/
//        //MediaUtils.muteAudioFocus(mContext, true);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (player != null) {
//            player.onDestroy();
//        }
//    }
//
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        if (player != null) {
//            player.onConfigurationChanged(newConfig);
//        }
//    }
//
//    @Override
//    public void onBackPressed() {
//        if (player != null && player.onBackPressed()) {
//            return;
//        }
//        super.onBackPressed();
//    }
//
//    //创建线程
//    public void createThread() {
//        new Thread() {
//            public void run() {
//                postJson();
//            }
//        }.start();
//    }
//
//    //获取数据
//    private void postJson() {
//        //申明给服务端传递一个json串
//        //创建一个OkHttpClient对象
//        OkHttpClient okHttpClient = new OkHttpClient();
//        //创建一个RequestBody(参数1：数据类型 参数2传递的json串)
//        RequestBody requestBody = RequestBody.create(JSON, "joke");
//        //创建一个请求对象
//        Request request = new Request.Builder()
//                .url(share_url)
//                .header("User-Agent", Constant.USER_AGENT_MOBILE)
//                .addHeader("Accept", "Accept:text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
//                .post(requestBody)
//                .build();
//        //发送请求获取响应
//        try {
//            Response response = okHttpClient.newCall(request).execute();
//            //判断请求是否成功
//            if (response.isSuccessful()) {
//                String content = response.body().string();
//                Message msg = new Message();
//                msg.obj = content;
//                handler.sendMessage(msg);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    //数据处理
//    Handler handler = new Handler() {
//        public void handleMessage(Message msg) {
//            final String str = msg.obj + "";
//            try {
//                JSONObject jsonObject = new JSONObject(parseHTML(str));
//                String path = doLoadVideoData(jsonObject.optString("videoid"));
//                novate.get(path.substring(path.indexOf("video")), null, new BaseSubscriber<ResponseBody>() {
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onNext(ResponseBody responseBody) {
//                        try {
//                            String string = new String(responseBody.bytes());
//                            JSONObject jp = new JSONObject(string);
//                            JSONObject jp1 = jp.optJSONObject("data");
//                            JSONObject jp2 = jp1.optJSONObject("video_list");
//                            JSONObject jp3 = jp2.optJSONObject("video_1");
//                            String base64 = jp3.optString("main_url");
//                            mainurl = (new String(Base64.decode(base64.getBytes(), Base64.DEFAULT)));
//                            playVideo(mainurl);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//
//        }
//    };
//
//    private String parseHTML(String HTML) {
//        boolean flag = false;
//        Document doc = Jsoup.parse(HTML);
//        // 取得所有的script tag
//        Elements scripts = doc.getElementsByTag("script");
//        for (Element e : scripts) {
//            // 过滤字符串
//            String script = e.toString();
//            if (script.contains("var player")) {
//                // 只取得script的內容
//                script = e.childNode(0).toString();
////                System.out.println(script);
//                // 取得JS变量数组
//                String[] vars = script.split("var ");
//                // 取得单个JS变量
//                for (String var : vars) {
//                    // 取到满足条件的JS变量
//                    if (var.contains("player=")) {
//                        int start = var.indexOf("=");
//                        int end = var.lastIndexOf(";");
//                        json = var.substring(start + 1, end + 1);
////                        System.out.println(json);
//                    }
//                }
//            }
//        }
//        return json;
//    }
//
//    public String doLoadVideoData(String videoid) {
//        String url = getVideoContentApi(videoid);
//        return url;
//    }
//
//    private static String getVideoContentApi(String videoid) {
//        String VIDEO_HOST = "http://ib.365yg.com";
//        String VIDEO_URL = "/video/urls/v/1/toutiao/mp4/%s?r=%s";
//        String r = getRandom();
//        String s = String.format(VIDEO_URL, videoid, r);
//        // 将/video/urls/v/1/toutiao/mp4/{videoid}?r={Math.random()} 进行crc32加密
//        CRC32 crc32 = new CRC32();
//        crc32.update(s.getBytes());
//        String crcString = crc32.getValue() + "";
//        String url = VIDEO_HOST + s + "&s=" + crcString;
//        return url;
//    }
//
//    private static String getRandom() {
//        Random random = new Random();
//        StringBuilder result = new StringBuilder();
//        for (int i = 0; i < 16; i++) {
//            result.append(random.nextInt(10));
//        }
//        return result.toString();
//    }
}
