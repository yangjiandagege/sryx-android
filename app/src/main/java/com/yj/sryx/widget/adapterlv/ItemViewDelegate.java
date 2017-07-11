package com.yj.sryx.widget.adapterlv;


/**
 * Created by yangjian on 16/9/10.
 */
public interface ItemViewDelegate<T> {

    public abstract int getItemViewLayoutId();

    public abstract boolean isForViewType(T item, int position);

    public abstract void convert(ViewHolder holder, T t, int position);
}
