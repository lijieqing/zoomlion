package com.kstech.zoomlion.view.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.kstech.zoomlion.R;
import com.kstech.zoomlion.view.adapter.AbstractRecyclerAdapter;

/**
 * 继承自SwipeRefreshLayout,从而实现滑动到底部时上拉加载更多的功能.
 */
public class RefreshLayout extends SwipeRefreshLayout {

    /**
     * 滑动到最下面时的上拉操作
     */

    private int mTouchSlop;
    /**
     * RecyclerView实例
     */
    private RecyclerView mRecycler;

    /**
     * 上拉监听器, 到了最底部的上拉加载操作
     */
    private OnLoadListener mOnLoadListener;

    /**
     * RecyclerView的加载中footer
     */
    private View mRecyclerViewFooter;

    /**
     * 按下时的y坐标
     */
    private int mYDown;
    /**
     * 抬起时的y坐标, 与mYDown一起用于滑动到底部时判断是上拉还是下拉
     */
    private int mLastY;
    /**
     * 是否在加载中 ( 上拉加载更多 )
     */
    private boolean isLoading = false;

    public RefreshLayout(Context context) {
        this(context, null);
    }

    public RefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        mRecyclerViewFooter = LayoutInflater.from(context).inflate(R.layout.view_footer, null,
                false);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        // 初始化RecyclerView对象
        if (mRecycler == null) {
            getRecyclerView();
        }
    }

    /**
     * 获取RecyclerView对象
     */
    private void getRecyclerView() {
        int childs = getChildCount();
        if (childs > 0) {
            View childView = getChildAt(0);
            if (childView instanceof RecyclerView) {
                mRecycler = (RecyclerView) childView;
                // 设置滚动监听器给RecyclerView, 使得滚动的情况下也可以自动加载
                mRecycler.addOnScrollListener(new OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        // 滚动时到了最底部也可以加载更多
                        if (canLoad()) {
                            loadData();
                        }
                    }
                });
                Log.d(VIEW_LOG_TAG, "### 找到RecyclerView");
            }
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // 按下
                mYDown = (int) event.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:
                // 移动
                mLastY = (int) event.getRawY();
                break;

            case MotionEvent.ACTION_UP:
                // 抬起
                if (canLoad()) {
                    loadData();
                }
                break;
            default:
                break;
        }

        return super.dispatchTouchEvent(event);
    }

    /**
     * 是否可以加载更多, 条件是到了最底部, listview不在加载中, 且为上拉操作.
     *
     * @return
     */
    private boolean canLoad() {
        return isBottom() && !isLoading && isPullUp();
    }

    /**
     * 判断是否到了最底部
     */
    private boolean isBottom() {
        if (mRecycler != null && mRecycler.getAdapter() != null) {
            int totalItem = mRecycler.getAdapter().getItemCount();
            int visibaleItem = mRecycler.getChildCount();
            //当全部item数量大于可见item数量时，才可上拉加载
            if (totalItem > visibaleItem) {
                //全部item集合中最后一个item的
                int lastItemPosition = mRecycler.getAdapter().getItemCount() - 1;
                //当前可见item集合中的最后一个
                int visibleLastPos = mRecycler.getChildCount() - 1;
                //根据visibleLastPos获取到对应的view
                View lastView = mRecycler.getChildAt(visibleLastPos);
                //获取lastView在全部item集合中的位置
                int visibleLastPosition = mRecycler.getChildAdapterPosition(lastView);

                //当lastItemPosition和visibleLastPosition相等时，认为滑到底部
                return visibleLastPosition == lastItemPosition;
            }
        }
        return false;
    }

    /**
     * 是否是上拉操作
     *
     * @return
     */
    private boolean isPullUp() {
        return (mYDown - mLastY) >= mTouchSlop;
    }

    /**
     * 如果到了最底部,而且是上拉操作.那么执行onLoad方法
     */
    private void loadData() {
        if (mOnLoadListener != null) {
            // 设置状态
            setLoading(true);
            //
            mOnLoadListener.onLoad();
        }
    }

    /**
     * @param loading
     */
    public void setLoading(boolean loading) {
        isLoading = loading;
        AbstractRecyclerAdapter adapter = (AbstractRecyclerAdapter) mRecycler.getAdapter();
        if (isLoading) {
            adapter.addFooterView(mRecyclerViewFooter);
        } else {
            adapter.removeFooterView();
            mYDown = 0;
            mLastY = 0;
        }
    }

    /**
     * @param loadListener
     */
    public void setOnLoadListener(OnLoadListener loadListener) {
        mOnLoadListener = loadListener;
    }

    /**
     * 加载更多的监听器
     *
     * @author mrsimple
     */
    public interface OnLoadListener {
        void onLoad();
    }
}