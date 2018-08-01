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
import android.widget.TextView;

import com.example.cb.toutiao.ArticleContentShow;
import com.example.cb.toutiao.Interface.OnItemClickListener;
import com.example.cb.toutiao.PathRandom;
import com.example.cb.toutiao.R;
import com.example.cb.toutiao.alladapter.RecommendAdapter;
import com.example.cb.toutiao.allbean.RecommendBean;

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

//爬取今日头条的文章
public class PageFragment extends Fragment {


    public static final String ARGS_PAGE = "args_page";
    private TextView tv;
    private static String PATH = new PathRandom().getHomePath();

    private List<RecommendBean> newList;
    private RecommendAdapter adapter;
    private RecyclerView rv;
    private RecommendBean recommendBean;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View view;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");//数据类型为json格式
    private Bundle bundle;


    public static PageFragment newInstance() {
        PageFragment fragment = new PageFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_page, container, false);
        initView();
        return view;
    }

    public void initView() {
        bundle = getArguments();
        newList = new ArrayList<RecommendBean>();
        adapter = new RecommendAdapter(newList, getContext());
        rv = (RecyclerView) view.findViewById(R.id.rv);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        swipeRefreshLayout.setRefreshing(true);
        createThread();
        seeRefresh();//设置刷新
    }

    //创建线程
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
        RequestBody requestBody = RequestBody.create(JSON, "article");
        //创建一个请求对象
        Request request = new Request.Builder()
                .url(PATH)
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

    //设置刷新
    public void seeRefresh() {
        // 设置下拉进度的主题颜色
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,R.color.colorblue);
        //监听刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                PATH = new PathRandom().getHomePath();
                createThread();
            }
        });
    }

    //数据处理
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            String str = msg.obj + "";
            try {
                JSONObject root = new JSONObject(str);
                //JSONObject 获取jsonArray
                JSONArray ary = root.getJSONArray("data");
                for (int i = 0; i < ary.length() - 1; i++) {
                    JSONObject root1 = ary.getJSONObject(i);
                    recommendBean = new RecommendBean();
                    recommendBean.setTitle(root1.optString("title"));
                    recommendBean.setCommentCouont(root1.optString("comments_count"));
                    recommendBean.setSource(root1.optString("source"));
                    //recommendBean.setShare_url(root1.optString("share_url"));
                    recommendBean.setGroupId(root1.optString("source_url"));
                    JSONArray img_url = root1.optJSONArray("image_list");
                    System.out.println(PATH);
                    //判断资讯是否有图片
                    if (img_url != null && img_url.length() > 0) {
                        recommendBean.setImg(img_url.getJSONObject(0).optString("url"));
                        recommendBean.setImg2(img_url.getJSONObject(1).optString("url"));
                        recommendBean.setImg3(img_url.getJSONObject(2).optString("url"));
                        recommendBean.setHasimg(true);
                    } else {
                        recommendBean.setHasimg(false);
                    }
                    newList.add(0, recommendBean);
                }
                adapter.add(newList);
                //设置点击事件
                adapter.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        Intent i = new Intent(getContext(), ArticleContentShow.class);
                        i.putExtra("share_url", "http://www.toutiao.com" + newList.get(position).getGroupId());
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
