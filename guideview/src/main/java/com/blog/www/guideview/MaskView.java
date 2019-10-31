package com.blog.www.guideview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by binIoter
 */

class MaskView extends ViewGroup {

    /**
     * 高亮区域
     */
    private List<RectF> mTargetRectList = new ArrayList<>();

    /**
     * 蒙层区域
     */
    private final RectF mOverlayRect = new RectF();

    /**
     * 中间变量
     */
    private final RectF mChildTmpRect = new RectF();

    /**
     * 蒙层背景画笔
     */
    private final Paint mFullingPaint;

    private int mPadding = 0;

    private int mPaddingLeft = 0;

    private int mPaddingTop = 0;

    private int mPaddingRight = 0;

    private int mPaddingBottom = 0;

    private HashMap<View, Integer> mComponentViewTargetViewMap;

    /**
     * 是否覆盖目标区域
     */
    private boolean mOverlayTarget = false;

    /**
     * 圆角大小
     */
    private int mCorner = 0;

    /**
     * 目标区域样式，默认为矩形
     */
    private List<Integer> mStyleList = new ArrayList<>();

    /**
     * 挖空画笔
     */
    private Paint mEraser;

    /**
     * 橡皮擦Bitmap
     */
    private Bitmap mEraserBitmap;

    /**
     * 橡皮擦Cavas
     */
    private Canvas mEraserCanvas;

    private boolean ignoreRepadding;

    private int mInitHeight;

    private int mChangedHeight = 0;

    private boolean mFirstFlag = true;

    private List<Float> mRectLeftList;

    private List<Float> mRectRightList;

    private List<Float> mRectTopList;

    private List<Float> mRectBottomList;

    private float mMostLeft;

    private float mMostRight;

    private float mMostTop;

    private float mMostBottom;

    public MaskView(Context context) {
        this(context, null, 0);
    }

    public MaskView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaskView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //自我绘制
        setWillNotDraw(false);

        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getRealMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        mOverlayRect.set(0, 0, width, height);
        mEraserBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mEraserCanvas = new Canvas(mEraserBitmap);
        mFullingPaint = new Paint();
        mEraser = new Paint();
        mEraser.setColor(0xFFFFFFFF);
        //图形重叠时的处理方式，擦除效果
        mEraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        //位图抗锯齿设置
        mEraser.setFlags(Paint.ANTI_ALIAS_FLAG);
        mComponentViewTargetViewMap = new HashMap<>();
        mRectLeftList = new ArrayList<>(mTargetRectList.size());
        mRectRightList = new ArrayList<>(mTargetRectList.size());
        mRectTopList = new ArrayList<>(mTargetRectList.size());
        mRectBottomList = new ArrayList<>(mTargetRectList.size());
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        try {
            clearFocus();
            mEraserCanvas.setBitmap(null);
            mEraserBitmap = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int w = MeasureSpec.getSize(widthMeasureSpec);
        final int h = MeasureSpec.getSize(heightMeasureSpec);
        if (mFirstFlag) {
            mInitHeight = h;
            mFirstFlag = false;
        }
        if (mInitHeight > h) {
            mChangedHeight = h - mInitHeight;
        } else if (mInitHeight < h) {
            mChangedHeight = h - mInitHeight;
        } else {
            mChangedHeight = 0;
        }
        setMeasuredDimension(w, h);
        mOverlayRect.set(0, 0, w, h);
        resetOutPath();

        final int count = getChildCount();
        View child;
        for (int i = 0; i < count; i++) {
            child = getChildAt(i);
            if (child != null) {
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        final float density = getResources().getDisplayMetrics().density;
        View child;
        for (int i = 0; i < count; i++) {
            child = getChildAt(i);
            if (child == null) {
                continue;
            }
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (lp == null) {
                continue;
            }
            if (lp.positionPattern == LayoutParams.GLOBAL_POSITION_PATTERN) { //基于全局高亮区域定位
                globalPosition(lp, child);
            } else { //分别定位
                if (mComponentViewTargetViewMap != null && mComponentViewTargetViewMap.size() > 0) {
                    if (mComponentViewTargetViewMap.containsKey(child)) {
                        singlePosition(lp, child, mComponentViewTargetViewMap.get(child));
                    } else { //单独定位 但没有成功传入需要依赖定位的基准高亮区域，让其全局定位
                        globalPosition(lp, child);
                    }
                }
            }
            //额外的xy偏移
            mChildTmpRect.offset((int) (density * lp.offsetX + 0.5f),
                    (int) (density * lp.offsetY + 0.5f));
            child.layout((int) mChildTmpRect.left, (int) mChildTmpRect.top,
                    (int) mChildTmpRect.right,
                    (int) mChildTmpRect.bottom);
        }
    }

    private void singlePosition(LayoutParams lp, View child, int i) {
        switch (lp.targetAnchor) {
            case LayoutParams.ANCHOR_LEFT://左
                mChildTmpRect.right = mTargetRectList.get(i).left;
                mChildTmpRect.left = mChildTmpRect.right - child.getMeasuredWidth();
                singleVerticalChildPositionLayout(child, mChildTmpRect, lp, i);
                break;
            case LayoutParams.ANCHOR_TOP://上
                mChildTmpRect.bottom = mTargetRectList.get(i).top;
                mChildTmpRect.top = mChildTmpRect.bottom - child.getMeasuredHeight();
                singleHorizontalChildPositionLayout(child, mChildTmpRect, lp, i);
                break;
            case LayoutParams.ANCHOR_RIGHT://右
                mChildTmpRect.left = mTargetRectList.get(i).right;
                mChildTmpRect.right = mChildTmpRect.left + child.getMeasuredWidth();
                singleVerticalChildPositionLayout(child, mChildTmpRect, lp, i);
                break;
            case LayoutParams.ANCHOR_BOTTOM://下
                mChildTmpRect.top = mTargetRectList.get(i).bottom;
                mChildTmpRect.bottom = mChildTmpRect.top + child.getMeasuredHeight();
                singleHorizontalChildPositionLayout(child, mChildTmpRect, lp, i);
                break;
            case LayoutParams.ANCHOR_OVER://中心
                mChildTmpRect.left =
                        ((int) mTargetRectList.get(i).width() - child.getMeasuredWidth())
                                >> 1;
                mChildTmpRect.top =
                        ((int) mTargetRectList.get(i).height() - child.getMeasuredHeight())
                                >> 1;
                mChildTmpRect.right =
                        ((int) mTargetRectList.get(i).width() + child.getMeasuredWidth())
                                >> 1;
                mChildTmpRect.bottom =
                        ((int) mTargetRectList.get(i).height() + child.getMeasuredHeight())
                                >> 1;
                mChildTmpRect.offset(mTargetRectList.get(i).left, mTargetRectList.get(i).top);
                break;
        }
    }

    private void globalPosition(LayoutParams lp, View child) {
        for (RectF rectF : mTargetRectList) {
            mRectLeftList.add(rectF.left);
            mRectRightList.add(rectF.right);
            mRectTopList.add(rectF.top);
            mRectBottomList.add(rectF.bottom);
        }
        mMostLeft = Collections.max(mRectLeftList);
        mMostRight = Collections.max(mRectRightList);
        mMostTop = Collections.max(mRectTopList);
        mMostBottom = Collections.max(mRectBottomList);
        switch (lp.targetAnchor) {
            case LayoutParams.ANCHOR_LEFT://左
                mChildTmpRect.right = mMostLeft;
                mChildTmpRect.left = mChildTmpRect.right - child.getMeasuredWidth();
                globalVerticalChildPositionLayout(child, mChildTmpRect, lp);
                break;
            case LayoutParams.ANCHOR_TOP://上
                mChildTmpRect.bottom = mMostTop;
                mChildTmpRect.top = mChildTmpRect.bottom - child.getMeasuredHeight();
                globalHorizontalChildPositionLayout(child, mChildTmpRect, lp);
                break;
            case LayoutParams.ANCHOR_RIGHT://右
                mChildTmpRect.left = mMostRight;
                mChildTmpRect.right = mChildTmpRect.left + child.getMeasuredWidth();
                globalVerticalChildPositionLayout(child, mChildTmpRect, lp);
                break;
            case LayoutParams.ANCHOR_BOTTOM://下
                mChildTmpRect.top = mMostBottom;
                mChildTmpRect.bottom = mChildTmpRect.top + child.getMeasuredHeight();
                globalHorizontalChildPositionLayout(child, mChildTmpRect, lp);
                break;
            case LayoutParams.ANCHOR_OVER://中心
                mChildTmpRect.left = ((int) (mMostRight - mMostLeft) - child.getMeasuredWidth())
                        >> 1;
                mChildTmpRect.top = ((int) (mMostBottom - mMostTop) - child.getMeasuredHeight())
                        >> 1;
                mChildTmpRect.right = ((int) (mMostRight - mMostLeft) + child.getMeasuredWidth())
                        >> 1;
                mChildTmpRect.bottom = ((int) (mMostBottom - mMostTop) + child.getMeasuredHeight())
                        >> 1;
                mChildTmpRect.offset(mMostLeft, mMostTop);
                break;
        }
    }

    private void singleHorizontalChildPositionLayout(View child, RectF rect, LayoutParams lp,
            int i) {
        switch (lp.targetParentPosition) {
            case LayoutParams.PARENT_START:
                rect.left = mTargetRectList.get(i).left;
                rect.right = rect.left + child.getMeasuredWidth();
                break;
            case LayoutParams.PARENT_CENTER:
                rect.left = (mTargetRectList.get(i).width() - child.getMeasuredWidth()) / 2;
                rect.right = (mTargetRectList.get(i).width() + child.getMeasuredWidth()) / 2;
                rect.offset(mTargetRectList.get(i).left, 0);
                break;
            case LayoutParams.PARENT_END:
                rect.right = mTargetRectList.get(i).right;
                rect.left = rect.right - child.getMeasuredWidth();
                break;
        }
    }

    private void globalHorizontalChildPositionLayout(View child, RectF rect, LayoutParams lp) {
        switch (lp.targetParentPosition) {
            case LayoutParams.PARENT_START:
                rect.left = mMostLeft;
                rect.right = rect.left + child.getMeasuredWidth();
                break;
            case LayoutParams.PARENT_CENTER:
                rect.left = ((mMostRight - mMostLeft) - child.getMeasuredWidth()) / 2;
                rect.right = ((mMostRight - mMostLeft) + child.getMeasuredWidth()) / 2;
                rect.offset(mMostLeft, 0);
                break;
            case LayoutParams.PARENT_END:
                rect.right = mMostRight;
                rect.left = rect.right - child.getMeasuredWidth();
                break;
        }
    }

    private void globalVerticalChildPositionLayout(View child, RectF rect, LayoutParams lp) {
        switch (lp.targetParentPosition) {
            case LayoutParams.PARENT_START:
                rect.top = mMostTop;
                rect.bottom = rect.top + child.getMeasuredHeight();
                break;
            case LayoutParams.PARENT_CENTER:
                rect.top = ((mMostBottom - mMostTop) - child.getMeasuredHeight()) / 2;
                rect.bottom = ((mMostBottom - mMostTop) + child.getMeasuredHeight()) / 2;
                rect.offset(0, mMostTop);
                break;
            case LayoutParams.PARENT_END:
                rect.bottom = mMostBottom;
                rect.top = mMostBottom - child.getMeasuredHeight();
                break;
        }
    }

    private void singleVerticalChildPositionLayout(View child, RectF rect, LayoutParams lp, int i) {
        switch (lp.targetParentPosition) {
            case LayoutParams.PARENT_START:
                rect.top = mTargetRectList.get(i).top;
                rect.bottom = rect.top + child.getMeasuredHeight();
                break;
            case LayoutParams.PARENT_CENTER:
                rect.top = (mTargetRectList.get(i).height() - child.getMeasuredHeight()) / 2;
                rect.bottom = (mTargetRectList.get(i).height() + child.getMeasuredHeight()) / 2;
                rect.offset(0, mTargetRectList.get(i).top);
                break;
            case LayoutParams.PARENT_END:
                rect.bottom = mTargetRectList.get(i).bottom;
                rect.top = mTargetRectList.get(i).bottom - child.getMeasuredHeight();
                break;
        }
    }

    private void resetOutPath() {
        resetPadding();
    }

    /**
     * 设置padding
     */
    private void resetPadding() {
        if (!ignoreRepadding) {
            if (mPadding != 0 && mPaddingLeft == 0) {
                mMostLeft -= mPadding;
            }
            if (mPadding != 0 && mPaddingTop == 0) {
                mMostTop -= mPadding;
            }
            if (mPadding != 0 && mPaddingRight == 0) {
                mMostRight += mPadding;
            }
            if (mPadding != 0 && mPaddingBottom == 0) {
                mMostBottom += mPadding;
            }
            if (mPaddingLeft != 0) {
                mMostLeft -= mPaddingLeft;
            }
            if (mPaddingTop != 0) {
                mMostTop -= mPaddingTop;
            }
            if (mPaddingRight != 0) {
                mMostRight += mPaddingRight;
            }
            if (mPaddingBottom != 0) {
                mMostBottom += mPaddingBottom;
            }
            ignoreRepadding = true;
        }
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        final long drawingTime = getDrawingTime();
        try {
            View child;
            for (int i = 0; i < getChildCount(); i++) {
                child = getChildAt(i);
                drawChild(canvas, child, drawingTime);
            }
        } catch (NullPointerException e) {

        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mEraserBitmap.eraseColor(Color.TRANSPARENT);
        mEraserCanvas.drawColor(mFullingPaint.getColor());
        for (RectF targetRect : mTargetRectList) {
            if (mChangedHeight != 0) {
                targetRect.offset(0, mChangedHeight);
                mInitHeight = mInitHeight + mChangedHeight;
                mChangedHeight = 0;
            }
            if (!mOverlayTarget) {
                switch (mStyleList.get(mTargetRectList.indexOf(targetRect))) {
                    case Component.ROUNDRECT:
                        mEraserCanvas.drawRoundRect(targetRect, mCorner, mCorner, mEraser);
                        break;
                    case Component.CIRCLE:
                        mEraserCanvas.drawCircle(targetRect.centerX(), targetRect.centerY(),
                                targetRect.width() / 2, mEraser);
                        break;
                    default:
                        mEraserCanvas.drawRoundRect(targetRect, mCorner, mCorner, mEraser);
                        break;
                }
            }
        }
        canvas.drawBitmap(mEraserBitmap, mOverlayRect.left, mOverlayRect.top, null);
    }

    public void setTargetRectList(List<Rect> rectList) {
        for (Rect rect : rectList) {
            RectF rectF = new RectF();
            rectF.set(rect);
            mTargetRectList.add(rectF);
        }
    }

    public void setFullingAlpha(int alpha) {
        mFullingPaint.setAlpha(alpha);
    }

    public void setFullingColor(int color) {
        mFullingPaint.setColor(color);
    }

    public void setHighTargetCorner(int corner) {
        this.mCorner = corner;
    }

    public void setHighTargetGraphStyle(List<Integer> styleList) {
        this.mStyleList = styleList;
    }

    public void setOverlayTarget(boolean b) {
        mOverlayTarget = b;
    }

    public void setPadding(int padding) {
        this.mPadding = padding;
    }

    public void setPaddingLeft(int paddingLeft) {
        this.mPaddingLeft = paddingLeft;
    }

    public void setPaddingTop(int paddingTop) {
        this.mPaddingTop = paddingTop;
    }

    public void setPaddingRight(int paddingRight) {
        this.mPaddingRight = paddingRight;
    }

    public void setPaddingBottom(int paddingBottom) {
        this.mPaddingBottom = paddingBottom;
    }

    public void setComponentViewTargetViewMap(HashMap<View, Integer> viewTargetViewHashMap) {
        this.mComponentViewTargetViewMap = viewTargetViewHashMap;
    }

    static class LayoutParams extends ViewGroup.LayoutParams {

        static final int ANCHOR_LEFT = 0x01;

        static final int ANCHOR_TOP = 0x02;

        static final int ANCHOR_RIGHT = 0x03;

        static final int ANCHOR_BOTTOM = 0x04;

        static final int ANCHOR_OVER = 0x05;

        static final int PARENT_START = 0x10;

        static final int PARENT_CENTER = 0x20;

        static final int PARENT_END = 0x30;

        static final int GLOBAL_POSITION_PATTERN = 0;

        static final int SINGLE_POSITION_PATTERN = 1;

        int targetAnchor = ANCHOR_BOTTOM;

        int targetParentPosition = PARENT_CENTER;

        int offsetX = 0;

        int offsetY = 0;

        int positionPattern = SINGLE_POSITION_PATTERN;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }
}
