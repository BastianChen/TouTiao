package com.example.cb.toutiao.allfragment;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cb.toutiao.alladapter.MyFragmentPagerAdapter;
import com.example.cb.toutiao.R;

public class HomeFragment extends Fragment {
    private View rootView;
    private MyFragmentPagerAdapter adapter;

    public HomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        initView(rootView);
        return rootView;
    }

    public void initView(View rootView) {
        //得到从MainActivity中传来的数据
        Bundle bundle = getArguments();
        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);
        //利用getChildFragmentManager获得子Fragment
        adapter = new MyFragmentPagerAdapter(getChildFragmentManager(), getContext(),bundle);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(7);

        //TabLayout
        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tablayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        //显示当前那个标签页
//        viewPager.setCurrentItem(1);
        //TabLayout绑定ViewPager滑动
        tabLayout.setupWithViewPager(viewPager);
    }
}
