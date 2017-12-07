package com.kstech.zoomlion.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

/**
 * ClearFocusByDownEditView 是为了实现在编辑模式下，点击虚拟键盘收回键实现 焦点取消
 * 测试得知，点击收回键时 会回调onKeyPreIme方法
 * Created by lijie on 2017/12/7.
 */
public class ClearFocusByDownEditView extends android.support.v7.widget.AppCompatEditText {
    /**
     * Instantiates a new Clear focus by down edit view.
     *
     * @param context the context
     */
    public ClearFocusByDownEditView(Context context) {
        super(context);
    }

    /**
     * Instantiates a new Clear focus by down edit view.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public ClearFocusByDownEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Instantiates a new Clear focus by down edit view.
     *
     * @param context      the context
     * @param attrs        the attrs
     * @param defStyleAttr the def style attr
     */
    public ClearFocusByDownEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        this.clearFocus();
        return super.onKeyPreIme(keyCode, event);
    }
}
