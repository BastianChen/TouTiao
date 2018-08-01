package com.example.cb.toutiao;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;
import com.dou361.dialogui.DialogUIUtils;
import com.dou361.dialogui.bean.TieBean;
import com.dou361.dialogui.listener.DialogUIItemListener;
import com.dou361.dialogui.listener.DialogUIListener;
import com.tamic.novate.Novate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UserSetting extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private TextView tvname;
    private TextView tvsig;
    private TextView tvsex;
    private TextView tvbirthday;
    private TimePickerView pvTime;
    private String info;
    private String sex;
    private String birthday;
    public Bundle data;
    private Window window;
    private Intent intent;
    private MyDatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private ContentValues values;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);
        window = getWindow();
        //获取用户信息
        intent = new Intent();
        // 获取该intent所携带的数据
        data = MainActivity.data;
        //创建数据库
        dbHelper = new MyDatabaseHelper(this, "myDict.db3", 1);
        db = dbHelper.getWritableDatabase();
        values = new ContentValues();
        //设置toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("个人设置");
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
        setSupportActionBar(toolbar);

        //初始化Textview
        tvname = (TextView) findViewById(R.id.name);
        tvsig = (TextView) findViewById(R.id.info);
        tvsex = (TextView) findViewById(R.id.sex);
        tvbirthday = (TextView) findViewById(R.id.date);

        //设置状态栏颜色
        window.setStatusBarColor(Color.parseColor(data.getString("theme")));
        //设置背景颜色
        toolbar.setBackgroundColor(Color.parseColor(data.getString("theme")));
        tvname.setText(data.getString("name"));
        tvsig.setText(data.getString("signature"));
        tvsex.setText(data.getString("sex"));
        tvbirthday.setText(data.getString("birthday"));

        //初始化View
        findViewById(R.id.lineinfo).setOnClickListener(this);
        findViewById(R.id.linedate).setOnClickListener(this);
        findViewById(R.id.linename).setOnClickListener(this);
        findViewById(R.id.linesex).setOnClickListener(this);

        //初始化日期选择器
        pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                birthday = format.format(date);
                values.put("birthday",birthday);
                db.update("user",values,"id=?",new String[]{data.getString("id")});
                data.putString("birthday",birthday);
                intent.putExtras(data);
                tvbirthday.setText(data.getString("birthday"));
                Toast.makeText(UserSetting.this, "设置成功", Toast.LENGTH_SHORT).show();
            }
        }).setType(new boolean[]{true, true, true, false, false, false}).isCenterLabel(false).build();
    }

    //视图点击加载
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lineinfo:
                info_choose();
                break;
            case R.id.linedate:
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                try {
                    if(data.getString("birthday")==null){
                        pvTime.setDate(Calendar.getInstance());//注：根据需求来决定是否使用该方法（一般是精确到秒的情况），此项可以在弹出选择器的时候重新设置当前时间，避免在初始化之后由于时间已经设定，导致选中时间与当前时间不匹配的问题。
                    }
                    else{
                        //获取用户生日数据
                        birthday=data.getString("birthday");
                        date = formatter.parse(birthday);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        pvTime.setDate(calendar);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    pvTime.setDate(Calendar.getInstance());
                }
                pvTime.show();
                break;
            case R.id.linename:
                name_choose();
                break;
            case R.id.linesex:
                sex_choose();
                break;
        }
    }

    //信息选择器
    private void info_choose() {
        DialogUIUtils.showAlert(UserSetting.this, null, null, "Your Signature", null, "确定", "取消", false, true, true, new DialogUIListener() {

            @Override
            public void onPositive() {
                System.out.println("yes");
                Toast.makeText(UserSetting.this, "设置成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNegative() {
                System.out.println("cancle");
            }

            public void onGetInput(CharSequence input1, CharSequence input2) {
                info = input1.toString();
                values.put("signature",info);
                db.update("user",values,"id=?",new String[]{data.getString("id")});
                data.putString("signature",info);
                intent.putExtras(data);
                tvsig.setText(data.getString("signature"));
            }

        }).show();
    }

    //名字选择器
    public void name_choose() {
        DialogUIUtils.showAlert(UserSetting.this, null, "用户名："+data.getString("name"), null, null, "返回", null, true, true, true, new DialogUIListener() {

            @Override
            public void onPositive() {
                System.out.println("yes");
            }

            @Override
            public void onNegative() {
                System.out.println("cancle");
            }
        }).show();
    }

    //性别选择器
    public void sex_choose() {
        List<TieBean> gender = new ArrayList<TieBean>();
        gender.add(0, new TieBean("男"));
        gender.add(1, new TieBean("女"));
        DialogUIUtils.showSheet(this, gender, "取消", Gravity.BOTTOM, true, true, new DialogUIItemListener() {
            @Override
            public void onItemClick(CharSequence text, int position) {
                sex = text.toString();
                values.put("sex",sex);
                db.update("user",values,"id=?",new String[]{data.getString("id")});
                data.putString("sex",sex);
                intent.putExtras(data);
                tvsex.setText(data.getString("sex"));
                Toast.makeText(UserSetting.this, "设置成功", Toast.LENGTH_SHORT).show();
            }
        }).show();
    }
}
