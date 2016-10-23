package com.oschina.bluelife.newcontact.widget;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by HiWin10 on 2016/10/20.
 */

public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {
    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    abstract void  bind(T item);
}
