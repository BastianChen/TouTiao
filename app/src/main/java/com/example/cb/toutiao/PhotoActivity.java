package com.example.cb.toutiao;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import okhttp3.MediaType;

public class PhotoActivity extends AppCompatActivity {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static String PATH;
    private Intent intent;
    private String json;
    private int now;
    private String image_list;
    private Toolbar toolbar;
    private ViewPager viewPager;
    private ImageView[] imageViews;
    private List<String> imgIdArray;
    private TextView tv_count;
    private TextView tv_save;
    private SharedPreferences sp;
    private WebView webView = null;
    private String share_url;
    private ProgressDialog dialog = null;
    private Bundle data;
    private Window window;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_content_show);
        window = getWindow();
        intent = getIntent();
        // 获取该intent所携带的数据
        data = intent.getExtras();
        toolbar = (Toolbar) findViewById(R.id.contentToolbar);
        toolbar.setTitle("今日头条 - 图片内容");
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
                    Toast.makeText(PhotoActivity.this, "您没有安装该App！"
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
//        tv_count = (TextView) findViewById(R.id.image_count);
//        tv_save = (TextView) findViewById(R.id.image_save);
//        sp=getSharedPreferences("user_auth", Activity.MODE_PRIVATE);
//        toolbar.setBackgroundColor(Color.parseColor(sp.getString("theme", "#3F51B5")));

        //初始化整个图片当前的位置
//        now = 1;
//        //设置viewpager
//        viewPager = (ViewPager) findViewById(R.id.viewPager);
//        imgIdArray = new ArrayList<String>();
//        data = getIntent();
//        PATH = data.getStringExtra("share_url");
        //启动运行
        //createThread();

//        //点击保存
//        tv_save.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String url = imgIdArray.get(now - 1);
//                ImageLoader imageLoader = ImageLoader.getInstance();
//                Bitmap bmp = imageLoader.loadImageSync(url);
//                Boolean is_save = new ImgUtils().saveImageToGallery(PhotoActivity.this, bmp);
//                if (is_save) {
//                    Toast.makeText(PhotoActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
//                }else{
//                    Toast.makeText(PhotoActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
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
                    dialog = new ProgressDialog(PhotoActivity.this);
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

//    public void createThread() {
//        new Thread() {
//            @Override
//            public void run() {
//                postJson();
//            }
//        }.start();
//    }
//
//    private void postJson() {
//        //申明给服务端传递一个json串
//        //创建一个OkHttpClient对象
//        OkHttpClient okHttpClient = new OkHttpClient();
//        //创建一个RequestBody(参数1：数据类型 参数2传递的json串)
//        RequestBody requestBody = RequestBody.create(JSON, "photo");
//        //创建一个请求对象
//        Request request = new Request.Builder()
//                .url(PATH)
//                .header("User-Agent", Constant.USER_AGENT_MOBILE)
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
//
//    }
//
//    Handler handler = new Handler() {
//        public void handleMessage(Message msg) {
//            String str = msg.obj + "";
//            //System.out.println("+++++++++++++++++++++++++++++++++"+str);
//            JSONObject root = null;
//            //image_list = parseHTML(str);
//            image_list = "{\\\"count\\\":4,\\\"sub_images\\\":[{\\\"url\\\":\\\"http:\\\\/\\\\/p3.pstatp.com\\\\/origin\\\\/pgc-image\\\\/1529270873576bfea3fc6e1\\\",\\\"width\\\":900,\\\"url_list\\\":[{\\\"url\\\":\\\"http:\\\\/\\\\/p3.pstatp.com\\\\/origin\\\\/pgc-image\\\\/1529270873576bfea3fc6e1\\\"},{\\\"url\\\":\\\"http:\\\\/\\\\/pb9.pstatp.com\\\\/origin\\\\/pgc-image\\\\/1529270873576bfea3fc6e1\\\"},{\\\"url\\\":\\\"http:\\\\/\\\\/pb1.pstatp.com\\\\/origin\\\\/pgc-image\\\\/1529270873576bfea3fc6e1\\\"}],\\\"uri\\\":\\\"origin\\\\/pgc-image\\\\/1529270873576bfea3fc6e1\\\",\\\"height\\\":617},{\\\"url\\\":\\\"http:\\\\/\\\\/p3.pstatp.com\\\\/origin\\\\/pgc-image\\\\/15292708729599c9b914fc4\\\",\\\"width\\\":560,\\\"url_list\\\":[{\\\"url\\\":\\\"http:\\\\/\\\\/p3.pstatp.com\\\\/origin\\\\/pgc-image\\\\/15292708729599c9b914fc4\\\"},{\\\"url\\\":\\\"http:\\\\/\\\\/pb9.pstatp.com\\\\/origin\\\\/pgc-image\\\\/15292708729599c9b914fc4\\\"},{\\\"url\\\":\\\"http:\\\\/\\\\/pb1.pstatp.com\\\\/origin\\\\/pgc-image\\\\/15292708729599c9b914fc4\\\"}],\\\"uri\\\":\\\"origin\\\\/pgc-image\\\\/15292708729599c9b914fc4\\\",\\\"height\\\":900},{\\\"url\\\":\\\"http:\\\\/\\\\/p1.pstatp.com\\\\/origin\\\\/pgc-image\\\\/1529270873360c5759e6c5c\\\",\\\"width\\\":900,\\\"url_list\\\":[{\\\"url\\\":\\\"http:\\\\/\\\\/p1.pstatp.com\\\\/origin\\\\/pgc-image\\\\/1529270873360c5759e6c5c\\\"},{\\\"url\\\":\\\"http:\\\\/\\\\/pb3.pstatp.com\\\\/origin\\\\/pgc-image\\\\/1529270873360c5759e6c5c\\\"},{\\\"url\\\":\\\"http:\\\\/\\\\/pb9.pstatp.com\\\\/origin\\\\/pgc-image\\\\/1529270873360c5759e6c5c\\\"}],\\\"uri\\\":\\\"origin\\\\/pgc-image\\\\/1529270873360c5759e6c5c\\\",\\\"height\\\":600},{\\\"url\\\":\\\"http:\\\\/\\\\/p9.pstatp.com\\\\/origin\\\\/pgc-image\\\\/15292708738308bead8d3c1\\\",\\\"width\\\":900,\\\"url_list\\\":[{\\\"url\\\":\\\"http:\\\\/\\\\/p9.pstatp.com\\\\/origin\\\\/pgc-image\\\\/15292708738308bead8d3c1\\\"},{\\\"url\\\":\\\"http:\\\\/\\\\/pb1.pstatp.com\\\\/origin\\\\/pgc-image\\\\/15292708738308bead8d3c1\\\"},{\\\"url\\\":\\\"http:\\\\/\\\\/pb3.pstatp.com\\\\/origin\\\\/pgc-image\\\\/15292708738308bead8d3c1\\\"}],\\\"uri\\\":\\\"origin\\\\/pgc-image\\\\/15292708738308bead8d3c1\\\",\\\"height\\\":634}],\\\"max_img_width\\\":900,AAA             \\\"labels\\\":[\\\"\\\\u8db3\\\\u7403\\\",\\\"\\\\u4e16\\\\u754c\\\\u676f\\\",\\\"\\\\u4fc4\\\\u7f57\\\\u65af\\\\u4e16\\\\u754c\\\\u676f\\\",\\\"\\\\u56fd\\\\u9645\\\\u8db3\\\\u7403\\\",\\\"\\\\u4f53\\\\u80b2\\\"],\\\"sub_abstracts\\\":[\\\"\\\\u5f53\\\\u5730\\\\u65f6\\\\u95f42018\\\\u5e746\\\\u670817\\\\u65e5\\\\uff0c2018\\\\u4fc4\\\\u7f57\\\\u65af\\\\u4e16\\\\u754c\\\\u676f\\\\u5c0f\\\\u7ec4\\\\u8d5b\\\\uff0c\\\\u5df4\\\\u897f\\\\u5bf9\\\\u9635\\\\u745e\\\\u58eb\\\\u7684\\\\u6bd4\\\\u8d5b\\\\u4e2d\\\\u51fa\\\\u5e2d\\\\u4e89\\\\u8bae\\\\u4e00\\\\u5e55\\\\u3002\\\\uff08\\\\u56fe\\\\u7247\\\\u7f72\\\\u540d\\\\uff1a \\\\u4e1c\\\\u65b9IC\\\\uff09    \\\\\\\"\\\",\\\"\\\\u963f\\\\u574e\\\\u5409\\\\u7981\\\\u533a\\\\u5185\\\\u62b1\\\\u6454\\\\u70ed\\\\u82cf\\\\u65af\\\\u672a\\\\u5224\\\\u70b9 \\\\u3002\\\\uff08\\\\u56fe\\\\u7247\\\\u7f72\\\\u540d\\\\uff1a \\\\u4e1c\\\\u65b9IC\\\\uff09]}";
//            //System.out.println("+++++++++++++++++++++++++++++++++"+image_list);
//            try {
//                root = new JSONObject(image_list);
//                JSONArray ary = root.getJSONArray("sub_images");
//                for (int i = 0; i < ary.length() - 1; i++) {
//                    JSONObject root1 = ary.getJSONObject(i);
//                    imgIdArray.add(i, root1.optString("url"));
//                }
//                imageViews = new ImageView[imgIdArray.size()];
//                tv_count.setText(1 + "/" + imageViews.length);
//                //设置图片
//                for (int i = 0; i < imageViews.length; i++) {
//                    ImageView imageView = new ImageView(PhotoActivity.this);
//                    imageViews[i] = imageView;
//                    new ImgLoader(PhotoActivity.this).disPlayimg(imgIdArray.get(i), imageView);
//                }
//                viewPager.setAdapter(new MyAdapter());
//                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//                    @Override
//                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                    }
//
//                    @Override
//                    public void onPageSelected(int position) {
//                        System.out.println(position);
//                        now = position + 1;
//                        tv_count.setText(now + "/" + imageViews.length);
//                    }
//
//                    @Override
//                    public void onPageScrollStateChanged(int state) {
//
//                    }
//                });
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//    };
//
//    private String parseHTML(String HTML) {
//        boolean flag = false;
//        Document doc = Jsoup.parse(HTML);
//        System.out.println("+++++++++++++++++++++++++++++++++"+doc);
//        // 取得所有的script tag
//        Elements scripts = doc.getElementsByTag("script");
//        System.out.println("+11111111111111111111111111111111111++++"+scripts);
//        for (Element e : scripts) {
//            // 过滤字符串
//            String script = e.toString();
//            System.out.println("+++2222222222222222222222222222+++++++++++"+script);
//            if (script.contains("var BASE_DATA = {")) {
//                // 只取得script的內容
//                script = e.childNode(0).toString();
//                System.out.println("33333333333333333333333333333333+"+script);
//                // 取得JS变量数组
//                String[] vars = script.split("var ");
//                // 取得单个JS变量
//                for (String var : vars) {
//                    // 取到满足条件的JS变量
//                    if (var.contains("BASE_DATA.galleryInfo = ")) {
//                        System.out.println("444444444444444444444444444444"+var);
//                        int start = var.indexOf("JSON.parse("+"'");
//                        int end = var.lastIndexOf("siblingList;");
//                        System.out.println("555555555555555555555555555555"+start);
//                        System.out.println("555555555555555555555555555555"+end);
//                        json = var.substring(start + 1, end - 1);
//                        System.out.println("555555555555555555555555555555"+json);
//                    }
//                }
//            }
//        }
//        System.out.println("+++++++++++++++++++++++++++++++++"+json);
//        return json;
//    }
//
//    public class MyAdapter extends PagerAdapter {
//        @Override
//        public int getCount() {
//            return imageViews.length;
//        }
//
//        @Override
//        public boolean isViewFromObject(View view, Object object) {
//            return view == object;
//        }
//
//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
//            ((ViewPager) container).removeView(imageViews[position]);
//        }
//
//        @Override
//        public Object instantiateItem(ViewGroup container, int position) {
//            try {
//                ((ViewPager) container).addView(imageViews[position], 0);
//            } catch (Exception e) {
//                //handler something
//            }
//            return imageViews[position];
//        }
//    }
}
