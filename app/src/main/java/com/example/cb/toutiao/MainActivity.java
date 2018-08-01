package com.example.cb.toutiao;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.example.cb.toutiao.allfragment.HomeFragment;
import com.example.cb.toutiao.allfragment.PhotoFragment;
import com.example.cb.toutiao.allfragment.VideoFragment;
import com.example.cb.toutiao.allfragment.JokeFragment;

import de.hdodenhof.circleimageview.CircleImageView;
import me.shaohui.shareutil.ShareConfig;
import me.shaohui.shareutil.ShareManager;
import me.shaohui.shareutil.ShareUtil;
import me.shaohui.shareutil.share.ShareListener;
import me.shaohui.shareutil.share.SharePlatform;

public class MainActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener, View.OnClickListener {

    private Toolbar mtoolbar;
    private HomeFragment homeFragment;
    private PhotoFragment photoFragment;
    private VideoFragment videoFragment;
    private JokeFragment weiTouTiao;
    //抽屉布局
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    private CircleImageView imageView;
    private View headerlayout;
    private Boolean message;
    private ShareListener mShareListener;
    private BottomSheetDialog dialog;
    private Window window;
    private MyDatabaseHelper dbHelper;
    private Intent intent;
    public static Bundle data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        window = getWindow();
        //获取用户信息
        intent = getIntent();
        // 获取该intent所携带的数据
        data = intent.getExtras();

        //创建数据库
        dbHelper = new MyDatabaseHelper(this, "myDict.db3", 1);
        homeFragment = new HomeFragment();
        photoFragment = new PhotoFragment();
        videoFragment = new VideoFragment();
        weiTouTiao = new JokeFragment();

        //navgationview
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setItemIconTintList(null);

        //绑定headerlayout
        get_info(data);

        //初始化
        initView();
    }

    //获取用户信息并且判断是否登录
    private void get_info(Bundle data) {
        try {
            if(!data.getString("name").equals(null)){
                message = true;
            }
            else{
                message = false;
            }
        }catch (Exception e){
            e.printStackTrace();
            message = false;
        }

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        if (message) {
            navigationView.addHeaderView(layoutInflater.inflate(R.layout.navigation_header, navigationView, false));
            headerlayout = navigationView.getHeaderView(0);
            TextView tv_header = (TextView) headerlayout.findViewById(R.id.tv_header);
            TextView followers = (TextView) headerlayout.findViewById(R.id.followers);
            TextView following = (TextView) headerlayout.findViewById(R.id.following);
            tv_header.setText(data.getString("name"));
        } else {
            navigationView.addHeaderView(layoutInflater.inflate(R.layout.navigation_header_before, navigationView, false));
            headerlayout = navigationView.getHeaderView(0);
            imageView = (CircleImageView) headerlayout.findViewById(R.id.profile_image_before);
            imageView.setOnClickListener(this);
        }
    }

    public void initView() {
        //显示toolbar
        mtoolbar = (Toolbar) findViewById(R.id.toolbar);
        mtoolbar.setTitle("今日头条 - 新闻");
        setSupportActionBar(mtoolbar);
        try {
            if(!data.getString("theme").equals(null)){
                mtoolbar.setBackgroundColor(Color.parseColor(data.getString("theme")));
                window.setStatusBarColor(Color.parseColor(data.getString("theme")));
            }
            else{
                mtoolbar.setBackgroundColor(Color.parseColor("#EE3B3B"));
            }
        }catch (Exception e){
            e.printStackTrace();
            mtoolbar.setBackgroundColor(Color.parseColor("#EE3B3B"));
        }

        //绑定侧边栏
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //实现toolbar和Drawer的联动
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, mtoolbar, R.string.drawer_open, R.string.drawer_close);
        actionBarDrawerToggle.syncState();
        //监听实现动画效果
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        //显示底部导航
        BottomNavigationBar bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        bottomNavigationBar.setBarBackgroundColor("#FCFCFC");
        bottomNavigationBar.addItem(new BottomNavigationItem(R.mipmap.home2, "主页").setInActiveColor(R.color.colorbttonfont).setActiveColorResource(R.color.colorblue))
                .addItem(new BottomNavigationItem(R.mipmap.comment, "段子").setInActiveColor(R.color.colorbttonfont).setActiveColorResource(R.color.colorAccent))
                .addItem(new BottomNavigationItem(R.mipmap.photo, "图片").setInActiveColor(R.color.colorbttonfont).setActiveColorResource(R.color.colororange))
                .addItem(new BottomNavigationItem(R.mipmap.play, "视频").setInActiveColor(R.color.colorbttonfont).setActiveColorResource(R.color.colorpurple2))
                .setFirstSelectedPosition(0)
                .initialise();
        //设置启动页
        setDefaultFragment();
        //底部导航监听事件
        bottomNavigationBar.setTabSelectedListener(this);
        //初始化Listener
        ShareConfig config = ShareConfig.instance()
                .qqId("595470658")
                .weiboId("13575419350")
                .wxId("cb595470658");
        ShareManager.init(config);

        mShareListener = new ShareListener() {
            @Override
            public void shareSuccess() {
                Toast.makeText(MainActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void shareFailure(Exception e) {
                Toast.makeText(MainActivity.this, "分享失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void shareCancel() {
                Toast.makeText(MainActivity.this, "取消分享", Toast.LENGTH_SHORT).show();
            }
        };

        //侧边栏NavgationView的监听
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(false);//设置选项是否选中
                item.setCheckable(false);//选项是否可选
                switch (item.getItemId()) {
                    case R.id.item_setting:
                        if (message) {
                            Intent intent = new Intent(MainActivity.this, UserSetting.class);
                            intent.putExtras(data);
                            startActivity(intent);
                        } else {
                            alert_info();
                        }
                        break;
                    case R.id.item_theme:
                        if (message) {
                            dialog = new BottomSheetDialog(MainActivity.this);
                            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.theme_choose, null);
                            dialog.setContentView(view);
                            dialog.show();
                            theme_choose(view);
                        } else {
                            alert_info();
                        }
                        break;
                    case R.id.item_love:
                        if (message) {
                            Toast.makeText(MainActivity.this, "您没有收藏任何东西！"
                                    , Toast.LENGTH_LONG).show();
                        } else {
                            alert_info();
                        }
                        break;
                    case R.id.item_share:
                        if (message) {
                            dialog = new BottomSheetDialog(MainActivity.this);
                            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.user_share, null);
                            dialog.setContentView(view);
                            dialog.show();
                            user_share(view, dialog);
                        } else {
                            alert_info();
                        }
                        break;
                    case R.id.logout:
                        if (message) {
                            new AlertDialog.Builder(MainActivity.this).setTitle("退出登录").setMessage("确认退出登录吗?").setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    logout();
                                }
                            }).show();
                        } else {
                            alert_info_out();
                        }
                        break;
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });
    }

    //主题选择
    private void theme_choose(View view) {
        view.findViewById(R.id.theme_blue).setOnClickListener(this);
        view.findViewById(R.id.theme_yellow).setOnClickListener(this);
        view.findViewById(R.id.theme_red).setOnClickListener(this);
        view.findViewById(R.id.theme_black).setOnClickListener(this);
        view.findViewById(R.id.theme_purple).setOnClickListener(this);
    }

    //用户分享
    private void user_share(View view, final BottomSheetDialog dialog) {
        view.findViewById(R.id.share_qq).setOnClickListener(this);
        view.findViewById(R.id.share_weixin).setOnClickListener(this);
        view.findViewById(R.id.share_friend).setOnClickListener(this);
        view.findViewById(R.id.share_weibo).setOnClickListener(this);
        view.findViewById(R.id.share_zone).setOnClickListener(this);
    }

    //设置启动页，即主界面
    private void setDefaultFragment() {
        //将用户数据传到home界面
        if(data!=null){
            System.out.println(data.getString("name"));
            homeFragment.setArguments(data);
        }
        else{
            System.out.println("没有用户数据");
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.maindfragment, homeFragment).commit();
    }

    //请先登录的提示
    public void alert_info() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("请您先登录?");
        builder.setTitle("消息提示");
        builder.setPositiveButton("登录",
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.dismiss();
                        Intent intent=new Intent(MainActivity.this,LoginAcitivity.class);
                        finish();
                        startActivity(intent);
                    }
                });
        builder.setNegativeButton("取消",
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
        //new AlertDialog.Builder(MainActivity.this).setTitle("消息提示").setMessage("请您先登录?").setNegativeButton("确定", LoginAcitivity).show();
    }

    //在没登录的情况下点击退出
    public void alert_info_out() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("您还没有登陆噢！");
        builder.setTitle("消息提示");
        builder.setPositiveButton("登录",
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.dismiss();
                        Intent intent=new Intent(MainActivity.this,LoginAcitivity.class);
                        finish();
                        startActivity(intent);
                    }
                });
        builder.setNegativeButton("取消",
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
        //new AlertDialog.Builder(MainActivity.this).setTitle("消息提示").setMessage("您还没有登陆噢！").setNegativeButton("确定", null).show();
    }

    //退出登录
    public void logout() {
        Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Toast.makeText(MainActivity.this, "退出成功", Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }

    //添加搜索控件
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //处理菜单被选中运行后的事件处理（未实现搜索功能）
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //显示这个的搜索绑定
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                System.out.println("open");
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                System.out.println("close");
                return true;
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    //底部导航监听事件
    @Override
    public void onTabSelected(int position) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        //将用户数据传到home界面
        if(data!=null){
            System.out.println(data.getString("name"));
            homeFragment.setArguments(data);
            photoFragment.setArguments(data);
            videoFragment.setArguments(data);
            weiTouTiao.setArguments(data);
        }
        else{
            System.out.println("没有用户数据");
        }
        switch (position) {
            case 0:
                ft.replace(R.id.maindfragment, homeFragment).commit();
                mtoolbar.setTitle("今日头条 - 新闻");
                break;
            case 1:
                ft.replace(R.id.maindfragment, weiTouTiao).commit();
                mtoolbar.setTitle("今日头条 - 段子");
                break;
            case 2:
                ft.replace(R.id.maindfragment, photoFragment).commit();
                mtoolbar.setTitle("今日头条 - 图片");
                break;
            case 3:
                ft.replace(R.id.maindfragment, videoFragment).commit();
                mtoolbar.setTitle("今日头条 - 视频");
                break;
        }
    }


    @Override
    public void onTabUnselected(int position) {

    }

    @Override
    public void onTabReselected(int position) {

    }

    //设置抽屉布局内控件监听
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile_image_before:
                drawerLayout.closeDrawers();//关闭navigationview
                finish();
                startActivity(new Intent(this, LoginAcitivity.class));//启动用户登录界面
                break;
            case R.id.share_qq:
                ShareUtil.shareImage(MainActivity.this, SharePlatform.QQ,
                        "http://shaohui.me/images/avatar.gif", mShareListener);
                dialog.hide();
                break;
            case R.id.share_weixin:
                ShareUtil.shareText(MainActivity.this, SharePlatform.WX, "分享文字", mShareListener);
                dialog.hide();
                break;
            case R.id.share_weibo:
                ShareUtil.shareImage(MainActivity.this, SharePlatform.WEIBO,
                        "http://shaohui.me/images/avatar.gif", mShareListener);
                dialog.hide();
                break;
            case R.id.share_zone:
                ShareUtil.shareMedia(MainActivity.this, SharePlatform.QZONE, "Title", "summary",
                        "http://www.google.com", "http://shaohui.me/images/avatar.gif",
                        mShareListener);
                dialog.hide();
                break;
            case R.id.share_friend:
                ShareUtil.shareText(MainActivity.this, SharePlatform.WX_TIMELINE, "测试分享文字",
                        mShareListener);
                dialog.hide();
                break;
            case R.id.theme_blue:
                mtoolbar.setBackgroundColor(Color.parseColor("#3F51B5"));
                window.setStatusBarColor(Color.parseColor("#3F51B5"));
                updateTheme(data.getString("id"),"#3F51B5");
                break;
            case R.id.theme_yellow:
                mtoolbar.setBackgroundColor(Color.parseColor("#FF7F00"));
                window.setStatusBarColor(Color.parseColor("#FF7F00"));
                updateTheme(data.getString("id"),"#FF7F00");
                break;
            case R.id.theme_red:
                mtoolbar.setBackgroundColor(Color.parseColor("#EE3B3B"));
                window.setStatusBarColor(Color.parseColor("#EE3B3B"));
                updateTheme(data.getString("id"),"#EE3B3B");
                break;
            case R.id.theme_black:
                mtoolbar.setBackgroundColor(Color.parseColor("#000000"));
                window.setStatusBarColor(Color.parseColor("#000000"));
                updateTheme(data.getString("id"),"#000000");
                break;
            case R.id.theme_purple:
                mtoolbar.setBackgroundColor(Color.parseColor("#6A5ACD"));
                window.setStatusBarColor(Color.parseColor("#6A5ACD"));
                updateTheme(data.getString("id"),"#6A5ACD");
                break;
        }
    }

    public void updateTheme(String id,String theme){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("theme",theme);
        db.update("user",values,"id=?",new String[]{id});
        data.putString("theme",theme);
        intent.putExtras(data);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        // 退出程序时关闭MyDatabaseHelper里的SQLiteDatabase
        if (dbHelper != null)
        {
            dbHelper.close();
        }
    }
}
