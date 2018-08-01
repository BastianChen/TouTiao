package com.example.cb.toutiao.alladapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.cb.toutiao.allfragment.PageFragment;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    private String[] titles = new String[]{"推荐","热点","娱乐","科技","体育","军事","游戏"};
    public int COUNT = titles.length;
    private Context context;
    private Bundle bundle;

    public MyFragmentPagerAdapter(FragmentManager fm, Context context, Bundle bundle) {
        super(fm);
        this.context = context;
        this.bundle = bundle;
    }

    //加载文章列表界面
    @Override
    public Fragment getItem(int position) {
        PageFragment pageFragment = new PageFragment();
        pageFragment.setArguments(bundle);
        return pageFragment;
        //return PageFragment.newInstance();
    }

    @Override
    public int getCount() {
        return COUNT;
    }

    //设置Tab中页面标题
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
