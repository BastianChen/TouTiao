package com.example.cb.toutiao.alladapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cb.toutiao.Interface.OnItemClickListener;
import com.example.cb.toutiao.R;
import com.example.cb.toutiao.allbean.RecommendBean;
import com.example.cb.toutiao.extra.ImgLoader;

import java.util.Collections;
import java.util.List;

//在RecyclerView中加载RecommendAdapter并设置相关的标题、信息源以及图片
public class RecommendAdapter extends RecyclerView.Adapter {
    private List<RecommendBean> lists;
    private OnItemClickListener onItemClickListener;
    private Context context;

    public RecommendAdapter(List<RecommendBean> lists, Context context) {
        this.lists = lists;
        this.context=context;
    }

    public void add(List<RecommendBean> newlist) {
        Collections.addAll(newlist);
    }

    //点击接口
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    //有图片的模板
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView hotTitle;
        private TextView hotExtra;
        private ImageView hotImg1;
        private ImageView hotImg2;
        private ImageView hotImg3;
        private OnItemClickListener onItemClickListener;

        public ViewHolder(View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            hotTitle = (TextView) itemView.findViewById(R.id.hot_title);
            hotExtra = (TextView) itemView.findViewById(R.id.hot_extra);
            hotImg1 = (ImageView) itemView.findViewById(R.id.hot_image);
            hotImg2 = (ImageView) itemView.findViewById(R.id.hot_image2);
            hotImg3 = (ImageView) itemView.findViewById(R.id.hot_image3);

            //设置点击事件
            this.onItemClickListener = onItemClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onClick(v, getLayoutPosition());
            }
        }
    }

    //没有图片的模板
    class NoImageView extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView hotTitle;
        private TextView hotExtra;
        private OnItemClickListener onItemClickListener = null;

        public NoImageView(View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            hotTitle = (TextView) itemView.findViewById(R.id.hot_title);
            hotExtra = (TextView) itemView.findViewById(R.id.hot_extra);

            //点击事件
            this.onItemClickListener = onItemClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onClick(v, getLayoutPosition());
            }
        }
    }

    //根据Adapter中List中recommendBean中的hasimg属性来判断有无图片
    @Override
    public int getItemViewType(int position) {
        //判断有没有图片
        if (lists.get(position).isHasimg()) {
            return 1;
        }
        return 0;
    }

    //根据有无图片来设置加载相应的布局文件
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //判断有图片
        if (viewType == 1) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recommend_home_has_image, parent, false);
            return new ViewHolder(view, onItemClickListener);
        }
        //判断没有图片
        if (viewType == 0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recommend_home_no_image, parent, false);
            return new NoImageView(view, onItemClickListener);
        }
        return null;
    }

    //根据返回的Holder来设置资讯列表中资讯的题目以及资讯源以及是否有图片
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //有图片的Holder
        if (holder instanceof ViewHolder) {
            ViewHolder vh = (ViewHolder) holder;
            vh.hotTitle.setText(lists.get(position).getTitle());
            vh.hotExtra.setText(lists.get(position).getSource());
            //设置图片
            if (lists.get(position).getImg() != null) {
                new ImgLoader(context).disPlayimg(lists.get(position).getImg(),vh.hotImg1);
                new ImgLoader(context).disPlayimg(lists.get(position).getImg2(),vh.hotImg2);
                new ImgLoader(context).disPlayimg(lists.get(position).getImg3(),vh.hotImg3);
            }
        }
        //没有图片的Holder
        if (holder instanceof NoImageView) {
            NoImageView vh = (NoImageView) holder;
            vh.hotTitle.setText(lists.get(position).getTitle());
            vh.hotExtra.setText(lists.get(position).getSource());
        }
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }
}
