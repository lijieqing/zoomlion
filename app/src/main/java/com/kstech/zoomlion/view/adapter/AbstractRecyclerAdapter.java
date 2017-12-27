package com.kstech.zoomlion.view.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView Adapter 具有添加footer和header的功能
 */
public abstract class AbstractRecyclerAdapter<T> extends RecyclerView.Adapter<AbstractRecyclerAdapter.MyHolder> {
    /**
     * 当前RecyclerView
     */
    private RecyclerView mRecyclerView;
    /**
     * 展示的数据
     */
    private List<T> data = new ArrayList<>();
    /**
     * 上下文对象
     */
    private Context mContext;
    /**
     * FooterView
     */
    private View VIEW_FOOTER;
    /**
     * HeaderView
     */
    private View VIEW_HEADER;
    /**
     * 普通Item Type
     */
    private static int TYPE_NORMAL = 1000;
    /**
     * 头部Item Type
     */
    private static int TYPE_HEADER = 1001;
    /**
     * 底部Item Type
     */
    private static int TYPE_FOOTER = 1002;

    public AbstractRecyclerAdapter(List<T> data, Context mContext) {
        this.data = data;
        this.mContext = mContext;
    }

    /**
     * 回调方法，子类实现此方法用新的布局填充ViewHolder并返回
     *
     * @return 填充好的ViewHolder
     */
    protected abstract AbstractRecyclerAdapter.MyHolder onCreateNormalViewHolder(ViewGroup parent);

    /**
     * 创建view holder时根据viewType来创建
     *
     * @param parent   parent
     * @param viewType item的类型
     * @return 创建好的ViewHolder
     */
    @Override
    public AbstractRecyclerAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER) {
            return new MyHolder(VIEW_FOOTER);
        } else if (viewType == TYPE_HEADER) {
            return new MyHolder(VIEW_HEADER);
        } else {
            return onCreateNormalViewHolder(parent);
        }
    }

    /**
     * 回调方法，子类实现为item布局设置基本数据
     *
     * @param datas    数据集合
     * @param holder   ViewHolder 对象
     * @param position 当前位置
     */
    protected abstract void onBindNormalViewHolder(List<T> datas, MyHolder holder, int position);

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        //不是头部和底部类型的item进行赋值
        if (!isHeaderView(position) && !isFooterView(position)) {
            //如果当前item集合存在header 所有元素的position 减一
            if (haveHeaderView()) position--;
            onBindNormalViewHolder(data, holder, position);
        }
    }


    @Override
    public int getItemCount() {
        int count = (data == null ? 0 : data.size());

        //存在底部或者头部item 总数分别加一
        if (VIEW_FOOTER != null) {
            count++;
        }
        if (VIEW_HEADER != null) {
            count++;
        }

        return count;
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderView(position)) {
            return TYPE_HEADER;
        } else if (isFooterView(position)) {
            return TYPE_FOOTER;
        } else {
            return TYPE_NORMAL;
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        try {
            if (mRecyclerView == null && mRecyclerView != recyclerView) {
                mRecyclerView = recyclerView;
            }
            ifGridLayoutManager();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据布局生成view
     *
     * @param layoutId 布局文件ID
     * @return 生成好的view
     */
    protected View getLayout(ViewGroup parent, int layoutId) {
        View v = LayoutInflater.from(mContext).inflate(layoutId, null);
        if (v.getParent() != null) {
            ViewGroup vg = (ViewGroup) v.getParent();
            vg.removeView(v);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            parent.addView(v, params);
        }
        return v;
    }

    /**
     * 添加HeaderView
     *
     * @param headerView HeaderView
     */
    public void addHeaderView(View headerView) {
        if (haveHeaderView()) {
            throw new IllegalStateException("hearview has already exists!");
        } else {
            //避免出现宽度自适应
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            headerView.setLayoutParams(params);
            VIEW_HEADER = headerView;
            ifGridLayoutManager();
            notifyItemInserted(0);
        }

    }

    /**
     * 添加FooterView
     *
     * @param footerView FooterView
     */
    public void addFooterView(View footerView) {
        if (haveFooterView()) {
            throw new IllegalStateException("footerView has already exists!");
        } else {
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            footerView.setLayoutParams(params);
            VIEW_FOOTER = footerView;
            ifGridLayoutManager();
            notifyItemInserted(getItemCount() - 1);
        }
    }

    /**
     * 移除FooterView
     */
    public void removeFooterView() {
        if (haveFooterView()) {
            VIEW_FOOTER = null;
        }
        notifyItemRemoved(getItemCount() - 1);
    }

    /**
     * 处理gridLayoutManager 设置Header或Footer的宽度为grid一行的宽度
     */
    private void ifGridLayoutManager() {
        if (mRecyclerView == null) return;
        final RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager.SpanSizeLookup originalSpanSizeLookup =
                    ((GridLayoutManager) layoutManager).getSpanSizeLookup();
            ((GridLayoutManager) layoutManager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return (isHeaderView(position) || isFooterView(position)) ?
                            ((GridLayoutManager) layoutManager).getSpanCount() :
                            1;
                }
            });
        }
    }

    /**
     * 根据HeaderView是否为空 来判断有无HeaderView
     *
     * @return 有无HeaderView
     */
    private boolean haveHeaderView() {
        return VIEW_HEADER != null;
    }

    /**
     * 根据FooterView是否为空 来判断有无FooterView
     *
     * @return 有无FooterView
     */
    public boolean haveFooterView() {
        return VIEW_FOOTER != null;
    }

    /**
     * 判断position位置的view是否为HeaderView
     * 只有当position为0 并且HeaderView不为空的情况下
     *
     * @param position 当前位置
     * @return 是否为HeaderView
     */
    private boolean isHeaderView(int position) {
        return haveHeaderView() && position == 0;
    }

    /**
     * 判断position位置的view是否为FooterView
     * 只有当position为最后一个 并且FooterView不为空的情况下
     *
     * @param position 当前位置
     * @return 是否为FooterView
     */
    private boolean isFooterView(int position) {
        return haveFooterView() && position == getItemCount() - 1;
    }


    public static class MyHolder extends RecyclerView.ViewHolder {
        public MyHolder(View itemView) {
            super(itemView);
        }
    }

}
