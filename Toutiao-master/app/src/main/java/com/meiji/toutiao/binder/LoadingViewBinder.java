package com.meiji.toutiao.binder;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.meiji.toutiao.R;
import com.meiji.toutiao.bean.LoadingBean;
import com.meiji.toutiao.util.SettingUtil;

import me.drakeet.multitype.ItemViewBinder;

/**
 * Created by Meiji on 2017/6/8.
 */

public class LoadingViewBinder extends ItemViewBinder<LoadingBean, LoadingViewBinder.ViewHolder> {

    @NonNull
    @Override
    protected LoadingViewBinder.ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View view = inflater.inflate(R.layout.item_loading, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull LoadingBean item) {
        int color = SettingUtil.getInstance().getColor();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Drawable wrapDrawable = DrawableCompat.wrap(holder.progressBar.getIndeterminateDrawable());
            DrawableCompat.setTint(wrapDrawable, color);
            holder.progressBar.setIndeterminateDrawable(DrawableCompat.unwrap(wrapDrawable));
        } else {
            holder.progressBar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ProgressBar progressBar;

        ViewHolder(View itemView) {
            super(itemView);
            this.progressBar = itemView.findViewById(R.id.progress_footer);
        }
    }
}
