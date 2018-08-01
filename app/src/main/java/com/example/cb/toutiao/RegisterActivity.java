package com.example.cb.toutiao;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText name;
    private EditText email;
    private EditText password;
    MyDatabaseHelper dbHelper;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_activity);
        //设置沉浸式状态栏
        StatusBarUtil.setTransparent(RegisterActivity.this);
        dbHelper = new MyDatabaseHelper(this, "myDict.db3", 1);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        findViewById(R.id.signin).setOnClickListener(this);
        findViewById(R.id.register).setOnClickListener(this);
        findViewById(R.id.fanhui).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signin:
                intent = new Intent(RegisterActivity.this, LoginAcitivity.class);
                RegisterActivity.this.finish();
                startActivity(intent);
                break;
            case R.id.register:
                register(name.getText().toString(), email.getText().toString(), password.getText().toString());
                break;
            case R.id.fanhui:
                intent = new Intent(RegisterActivity.this, LoginAcitivity.class);
                finish();
                startActivity(intent);
                break;
        }
    }

    private void register(String namestr, String emailstr, String paswordstr) {
        Boolean is_validate = validate(namestr, emailstr, paswordstr);
        if(is_validate){
            String theme = "#EE3B3B";
            insertData(dbHelper.getReadableDatabase(), namestr, emailstr, paswordstr, theme);
            Toast.makeText(RegisterActivity.this, "注册成功!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(RegisterActivity.this, LoginAcitivity.class);
            finish();
            startActivity(intent);
        }
    }

    //该方法用于向表输入数据
    private void insertData(SQLiteDatabase db, String nameS, String emailS
            , String passwordS,String theme)
    {
        db.execSQL("insert into user values(null , ? , ? , ? , ? , null , null , null)"
                , new String[] {nameS,emailS,passwordS,theme});
    }

    //验证字符格式
    public boolean validate(String namestr, String emailstr, String paswordstr) {
        String rex = "[\\w-]+[\\.\\w]*@[\\w]+(\\.[\\w]{2,3})";
        Pattern p = Pattern.compile(rex);
        Matcher m = p.matcher(emailstr);
        if (namestr.length() < 3 || namestr.length() > 20) {
            name.setError("用户名长度为3~20");
            return false;
        }
        if (m.find() == false) {
            email.setError("邮箱地址不正确");
            return false;
        }
        if (paswordstr.length() < 6 || paswordstr.length() > 15) {
            password.setError("密码长度为6~15");
            return false;
        }
        return true;
    }
}
