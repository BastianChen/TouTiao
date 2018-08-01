package com.example.cb.toutiao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//负责数据库表的创建与管理
public class MyDatabaseHelper extends SQLiteOpenHelper
{
	final String CREATE_TABLE_SQL =
			"create table user(id integer primary key autoincrement , name , email , password , theme" +
					", signature , sex , birthday )";
	public MyDatabaseHelper(Context context, String name, int version)
	{
		super(context, name, null, version);
	}
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		// 第一次使用数据库时自动建表
		db.execSQL(CREATE_TABLE_SQL);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db
			, int oldVersion, int newVersion)
	{
		System.out.println("--------onUpdate Called--------"
				+ oldVersion + "--->" + newVersion);
	}
}
