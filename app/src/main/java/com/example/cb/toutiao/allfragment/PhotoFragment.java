package com.example.cb.toutiao.allfragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cb.toutiao.Interface.OnItemClickListener;
import com.example.cb.toutiao.PathRandom;
import com.example.cb.toutiao.PhotoActivity;
import com.example.cb.toutiao.R;
import com.example.cb.toutiao.alladapter.PhotoAdapter;
import com.example.cb.toutiao.allbean.PhotoBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PhotoFragment extends Fragment {

    private static String PATH = new PathRandom().getPhotoPath();
    private List<PhotoBean> newList;
    private PhotoAdapter adapter;
    private RecyclerView rv;
    private PhotoBean mainAdapterBean;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View view;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private Bundle bundle;

    public PhotoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_photo, container, false);
        initView();
        return view;
    }

    private void initView() {
        bundle = getArguments();
        newList = new ArrayList<PhotoBean>();
        adapter = new PhotoAdapter(newList, getContext());
        rv = (RecyclerView) view.findViewById(R.id.rv);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        swipeRefreshLayout.setRefreshing(true);
        createThread();
        seeRefresh();
    }

    //设置刷新
    public void seeRefresh() {
        // 设置下拉进度的主题颜色
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,R.color.colorblue);
        //监听刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                PATH = new PathRandom().getPhotoPath();
                createThread();
            }
        });
    }

    //创建进程
    public void createThread() {
        new Thread() {
            public void run() {
                postJson();
            }
        }.start();
    }

    //获取数据
    private void postJson() {
        //申明给服务端传递一个json串
        //创建一个OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        //创建一个RequestBody(参数1：数据类型 参数2传递的json串)
        RequestBody requestBody = RequestBody.create(JSON, "photo");
        //创建一个请求对象
        Request request = new Request.Builder()
                .url(PATH)
//                .header("User-Agent", Constant.USER_AGENT_MOBILE)
                .post(requestBody)
                .build();
        //发送请求获取响应
        try {
            Response response = okHttpClient.newCall(request).execute();
            //判断请求是否成功
            if (response.isSuccessful()) {
                String content = response.body().string();
                Message msg = new Message();
                msg.obj = content;
                handler.sendMessage(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //数据处理
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            String str = msg.obj + "";
            JSONObject root = null;
            try {
                root = new JSONObject(str);
                JSONArray ary = root.getJSONArray("data");
                for (int i = 0; i < ary.length() - 1; i++) {
                    JSONObject root1 = ary.getJSONObject(i);
                    mainAdapterBean = new PhotoBean();
                    mainAdapterBean.setTitle(root1.optString("title"));
                    mainAdapterBean.setTag(root1.optString("chinese_tag"));
                    mainAdapterBean.setSource(root1.optString("source"));
//                    JSONArray img_url = root1.getJSONArray("image_list");
//                    for(int j = 0; j < img_url.length() - 1; j++){
//                        JSONObject root2 = img_url.getJSONObject(j);
//                        JSONArray img_url2 = root2.getJSONArray("url_list");
//                        mainAdapterBean.setImg1(img_url2.getJSONObject(0).optString("url"));
//                    }
                    mainAdapterBean.setImg1(root1.optString("image_url"));
                    mainAdapterBean.setGallary_image_count(root1.optString("gallary_image_count"));
                    mainAdapterBean.setGroup_id(root1.optString("group_id"));
                    System.out.println(PATH);
                    newList.add(0, mainAdapterBean);
                }
                adapter.add(newList);
                //设置点击事件
                adapter.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        Intent i = new Intent(getContext(), PhotoActivity.class);
                        System.out.println("http://www.toutiao.com/i" + newList.get(position).getGroup_id());
                        i.putExtra("share_url", "http://www.toutiao.com/i" + newList.get(position).getGroup_id());
                        if(bundle!=null){
                            i.putExtras(bundle);
                            startActivity(i);
                        }
                        else{
                            startActivity(i);
                        }
                    }
                });
                rv.setLayoutManager(new LinearLayoutManager(getContext()));
                rv.setAdapter(adapter);
                swipeRefreshLayout.setRefreshing(false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
}
