package com.kstech.zoomlion.view.decor;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

/**
 * 列表分组 悬浮效果
 * 实现依赖ItemDecoration 给需要展示 组布局的item设置偏移量
 * 将组布局绘制到对应位置
 */
public class GroupItemDecor extends RecyclerView.ItemDecoration {
    private int mGroupHeight = 60;
    private Paint mGroupPaint;
    //设置悬浮条的坐标
    private int left, right, bottom, top;
    private DecorHelper helper;

    public GroupItemDecor(DecorHelper helper) {
        super();
        this.helper = helper;
        mGroupPaint = new Paint();
        mGroupPaint.setColor(Color.YELLOW);
        mGroupPaint.setAntiAlias(true);
    }

    private boolean isFirstInGroup(int pos) {
        if (pos == 0) {
            return true;
        } else {
            String prevGroup = helper.getGroupName(pos - 1);
            String curGroup = helper.getGroupName(pos);
            return !TextUtils.equals(prevGroup, curGroup);
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        //获取当前可见item 数量
        int childCount = parent.getChildCount();
        //获取全部item 数量 与资源个数是一致的
        int itemCount = state.getItemCount();
        Log.e("DataBindingActivity", "getChildCount():" + childCount + "||||getItemCount:" + itemCount);
        //获取坐标点
        left = parent.getLeft() + parent.getPaddingLeft();
        right = parent.getRight() - parent.getPaddingRight();

        Log.e("DataBindingActivity", "left():" + left + "||||right:" + right);
        String preGroupName;
        String curGroupName = null;

        //遍历每一个可见的 item
        for (int i = 0; i < childCount; i++) {
            //获取一个可见的item
            View view = parent.getChildAt(i);
            //获取到该item在全部item中的位置
            int position = parent.getChildAdapterPosition(view);
            Log.e("DataBindingActivity", "getChildAdapterPosition():" + position + "||||i:" + i);

            preGroupName = curGroupName;
            curGroupName = helper.getGroupName(position);

            //当上一个item的组名称 与当前item组名称相同时 直接进行下一次循环 不会向下进行
            if (TextUtils.equals(preGroupName, curGroupName)) {
                continue;
            }

            //当组名称不同时，向下进行

            //获得当前item的底部坐标 Y方向
            int viewBottom = view.getBottom();

            //计算当前item的顶部位置，并将其设置为悬浮条的底部坐标
            //当小于 悬浮条的高度时 使用悬浮条的高度
            //此时并没有判断与邻近item的区别
            bottom = Math.max(mGroupHeight, view.getTop());

            //当前item不是最后一项时 进入
            if (position + 1 < itemCount) {
                String nextGroupName = helper.getGroupName(position + 1);
                //当 下一个item的组名称与当前item不同 并且 当前item的底部坐标小于到顶部的位置
                if (!nextGroupName.equals(curGroupName) && viewBottom < bottom) {
                    //更改bottom的值
                    //此时最顶部悬浮窗就会跟着当前item一起滑动
                    //此时的Decor是将当前item遮住的
                    bottom = viewBottom;
                }
            }

            //计算顶部坐标
            top = bottom - mGroupHeight;

            //开始绘制
            c.drawRect(left, top, right, bottom, mGroupPaint);

            mGroupPaint.setStyle(Paint.Style.STROKE);
            mGroupPaint.setColor(Color.DKGRAY);
            c.drawRect(left, top, right, bottom, mGroupPaint);

            mGroupPaint.setStyle(Paint.Style.FILL);

            Paint.FontMetrics fm = mGroupPaint.getFontMetrics();
            //文字竖直居中显示
            mGroupPaint.setColor(Color.RED);
            float baseLine = bottom - (mGroupHeight - (fm.bottom - fm.top)) / 2 - fm.bottom;
            c.drawText(curGroupName, left + 50, baseLine, mGroupPaint);

            mGroupPaint.setColor(Color.YELLOW);
        }

    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int pos = parent.getChildAdapterPosition(view);
        //当前item 与上一个item 不是同一组时 将当前item偏移
        if (isFirstInGroup(pos)) {
            outRect.top = mGroupHeight;
        }
    }

    public interface DecorHelper{
        String getGroupName(int position);
    }
}