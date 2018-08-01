package com.example.cb.toutiao;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;

public class LoginAcitivity extends AppCompatActivity implements View.OnClickListener {

    private EditText email;
    private EditText password;
    MyDatabaseHelper dbHelper;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_acitivity);
        //设置沉浸式状态栏
        StatusBarUtil.setTransparent(LoginAcitivity.this);
        dbHelper = new MyDatabaseHelper(this, "myDict.db3", 1);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        findViewById(R.id.register).setOnClickListener(this);
        findViewById(R.id.fanhui).setOnClickListener(this);
        findViewById(R.id.login).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register:
                intent = new Intent(LoginAcitivity.this, RegisterActivity.class);
                finish();
                startActivity(intent);
                break;
            case R.id.fanhui:
                intent = new Intent(LoginAcitivity.this, MainActivity.class);
                finish();
                startActivity(intent);
                break;
            case R.id.login:
                //查询用户并进行登录
                login(email.getText().toString(), password.getText().toString());
                break;
        }
    }

    private void login(String emailS, String passwordS) {
        Boolean is_validate = validate(emailS, passwordS);
        if(is_validate){
            // 执行查询
            Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                    "select * from user where name = ? and password = ?",
                    new String[] {emailS,passwordS});

            if(cursor.moveToFirst()==false){
                new AlertDialog.Builder(LoginAcitivity.this).setTitle("消息提示").setMessage("用户不存在!").setNegativeButton("确定", null).show();
            }
            else if(cursor.moveToFirst()==true) {
                // 显示提示信息
                Toast.makeText(LoginAcitivity.this, "登录成功！"
                        , Toast.LENGTH_LONG).show();
                // 创建一个Bundle对象
                Bundle data = new Bundle();
                data.putString("id",cursor.getString(0));
                data.putString("name",cursor.getString(1) );
                data.putString("email",cursor.getString(2) );
                data.putString("password",cursor.getString(3) );
                data.putString("theme",cursor.getString(4) );
                data.putString("signature",cursor.getString(5) );
                data.putString("sex",cursor.getString(6) );
                data.putString("birthday",cursor.getString(7) );
                // 创建一个Intent
                Intent intent = new Intent(LoginAcitivity.this, MainActivity.class);
                intent.putExtras(data);
                //关闭当前Activity
                finish();
                // 启动Activity
                startActivity(intent);
            }
        }
    }

    //验证字符格式
    private Boolean validate(String emailstr, String passwordstr) {
//        String rex = "[\\w-]+[\\.\\w]*@[\\w]+(\\.[\\w]{2,3})";
//        Pattern p = Pattern.compile(rex);
//        Matcher m = p.matcher(emailstr);
//        if (m.find() == false) {
//            email.setError("邮箱地址不正确");
//            return false;
//        }
        if (passwordstr.length() < 6 || passwordstr.length() > 15) {
            password.setError("密码长度为6~15");
            return false;
        }
        return true;
    }
}
