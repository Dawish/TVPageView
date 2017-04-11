package com.skw.tvpageview;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.Scroller;

import java.lang.ref.WeakReference;

/**
 * Created by weishukai on 17/2/24.
 */

public class TVPageView extends LinearLayout {

    private String TAG = "TVPageView";

    private final int MAX_SCROLL_TIME = 800;

    private static final int MSG_SCROLL = 0;

    private View firstCompleteView;

    private int mScrollSpeed = 1920;
    protected Scroller mScroller;

    private ScrollHandler mHandler = new ScrollHandler(TVPageView.this);
    ;

    static class ScrollHandler extends Handler {
        private final WeakReference<TVPageView> mOuterClassRef;

        ScrollHandler(TVPageView outerClass) {
            mOuterClassRef = new WeakReference<TVPageView>(outerClass);
        }

        public void handleMessage(Message msg) {
            TVPageView outerClass = mOuterClassRef.get();
            switch (msg.what) {
                case MSG_SCROLL:
                    if (outerClass != null && !outerClass.mScroller.isFinished()) {
                        outerClass.mScroller.computeScrollOffset();
                        outerClass.scrollTo(outerClass.mScroller.getCurrX(), outerClass.mScroller.getCurrY());
                        if (!outerClass.mScroller.isFinished()) {
                            outerClass.mHandler.sendEmptyMessageDelayed(MSG_SCROLL, 1);
                        }
                    }
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    }

    public TVPageView(Context context) {
        super(context);
        init(context, null);
    }

    public TVPageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TVPageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.mScroller = new Scroller(context,
                new AccelerateDecelerateInterpolator());
    }

    /**
     * 设置每一页的layoutid
     *
     * @param layoutIds
     */
    public void setChildLayoutIds(int[] layoutIds) {
        if (layoutIds != null && layoutIds.length > 0) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            for (int layoutId : layoutIds) {
                layoutInflater.inflate(layoutId, this, false);
            }
        } else {
            Log.e(TAG, "必须要有子view");
        }

    }


    @Override
    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
        Log.e(TAG, "requestChildFocus");
        if (firstCompleteView != null && firstCompleteView != child) {
            switch (getOrientation()) {
                case HORIZONTAL:
                    slideToX(child.getLeft());
                    break;
                case VERTICAL:
                    slideToY(child.getTop());
                    break;
            }
        }
        firstCompleteView = child;

    }

    /**
     * 平滑滑动到某处
     *
     * @param x
     */
    public void slideToX(int x) {
        this.slideX(this.getScrollX(), x);
    }

    public void slideToY(int y) {
        this.slideY(this.getScrollY(), y);
    }


    private void slideX(int fromScrollX, int toScrollX) {
        if (fromScrollX == toScrollX) {
            return;
        }

        int dx = toScrollX - fromScrollX;
        int duration = (int) (Math.abs(dx) * 1f / this.mScrollSpeed * 1000);
        if (duration > MAX_SCROLL_TIME) {
            duration = MAX_SCROLL_TIME;
        }

        this.mScroller.forceFinished(true);
        this.mScroller.startScroll(fromScrollX, 0, dx, 0, duration);
        this.mHandler.removeMessages(MSG_SCROLL);
        this.mHandler.sendEmptyMessage(MSG_SCROLL);
    }

    private void slideY(int fromScrollY, int toScrollY) {
        if (fromScrollY == toScrollY) {
            return;
        }

        int dy = toScrollY - fromScrollY;
        int duration = (int) (Math.abs(dy) * 1f / this.mScrollSpeed * 1000);
        if (duration > MAX_SCROLL_TIME) {
            duration = MAX_SCROLL_TIME;
        }

        this.mScroller.forceFinished(true);
        this.mScroller.startScroll(0, fromScrollY, 0, dy, duration);
        this.mHandler.removeMessages(MSG_SCROLL);
        this.mHandler.sendEmptyMessage(MSG_SCROLL);
    }
}
