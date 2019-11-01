package com.blog.www.guideview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 遮罩系统的封装 <br>
 * * 外部需要调用{@link GuideBuilder}来创建该实例，实例创建后调用
 * * {@link #show(Activity)} 控制显示； 调用 {@link #dismiss()}让遮罩系统消失。 <br>
 * <p>
 * Created by binIoter
 */

public class Guide implements View.OnKeyListener, View.OnTouchListener {

    Guide() {
    }

    /**
     * 滑动临界值
     */
    private static final int SLIDE_THRESHOLD = 30;

    private Configuration mConfiguration;

    private MaskView mMaskView;

    private Component[] mComponents;

    //自定义component和其需要依赖定位的高亮区域view的map
    private HashMap<Component, Integer> mComponentTargetViewMap;

    //自定义component转化为view之后和其需要依赖定位的高亮区域view的map
    private HashMap<View, Integer> mViewTargetViewHashMap;

    // 根据locInwindow定位后，是否需要判断loc值非0
    private boolean mShouldCheckLocInWindow = true;

    private GuideBuilder.OnVisibilityChangedListener mOnVisibilityChangedListener;

    private GuideBuilder.OnSlideListener mOnSlideListener;

    void setConfiguration(Configuration configuration) {
        mConfiguration = configuration;
    }

    void setComponents(Component[] components) {
        mComponents = components;
    }

    void setComponentTargetViewMap(
            HashMap<Component, Integer> componentTargetViewMap) {
        mComponentTargetViewMap = componentTargetViewMap;
    }

    void setCallback(GuideBuilder.OnVisibilityChangedListener listener) {
        this.mOnVisibilityChangedListener = listener;
    }

    public void setOnSlideListener(GuideBuilder.OnSlideListener onSlideListener) {
        this.mOnSlideListener = onSlideListener;
    }

    /**
     * 显示遮罩
     *
     * @param activity 目标Activity
     */
    public void show(Activity activity) {
        show(activity, null);
    }

    /**
     * 显示遮罩
     *
     * @param activity 目标Activity
     * @param overlay  遮罩层view
     */
    public void show(Activity activity, ViewGroup overlay) {
        mMaskView = onCreateView(activity, overlay);
        if (overlay == null) {
            overlay = (ViewGroup) activity.getWindow().getDecorView();
        }
        if (mMaskView.getParent() == null && mConfiguration.mTargetViewList != null) {
            overlay.addView(mMaskView);
            if (mConfiguration.mEnterAnimationId != -1) {
                Animation anim = AnimationUtils
                        .loadAnimation(activity, mConfiguration.mEnterAnimationId);
                assert anim != null;
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if (mOnVisibilityChangedListener != null) {
                            mOnVisibilityChangedListener.onShown();
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                mMaskView.startAnimation(anim);
            } else {
                if (mOnVisibilityChangedListener != null) {
                    mOnVisibilityChangedListener.onShown();
                }
            }
        }
    }

    public void clear() {
        if (mMaskView == null) {
            return;
        }
        final ViewGroup vp = (ViewGroup) mMaskView.getParent();
        if (vp == null) {
            return;
        }
        vp.removeView(mMaskView);
        onDestroy();
    }

    /**
     * 隐藏该遮罩并回收资源相关
     */
    public void dismiss() {
        if (mMaskView == null) {
            return;
        }
        final ViewGroup vp = (ViewGroup) mMaskView.getParent();
        if (vp == null) {
            return;
        }
        if (mConfiguration.mExitAnimationId != -1) {
            // mMaskView may leak if context is null
            Context context = mMaskView.getContext();
            assert context != null;

            Animation anim = AnimationUtils.loadAnimation(context, mConfiguration.mExitAnimationId);
            assert anim != null;
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    vp.removeView(mMaskView);
                    if (mOnVisibilityChangedListener != null) {
                        mOnVisibilityChangedListener.onDismiss();
                    }
                    onDestroy();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            mMaskView.startAnimation(anim);
        } else {
            vp.removeView(mMaskView);
            if (mOnVisibilityChangedListener != null) {
                mOnVisibilityChangedListener.onDismiss();
            }
            onDestroy();
        }
    }

    /**
     * 根据locInwindow定位后，是否需要判断loc值非0
     */
    public void setShouldCheckLocInWindow(boolean set) {
        mShouldCheckLocInWindow = set;
    }

    private MaskView onCreateView(Activity activity, ViewGroup overlay) {
        if (overlay == null) {
            overlay = (ViewGroup) activity.getWindow().getDecorView();
        }
        MaskView maskView = new MaskView(activity);
        maskView.setFullingColor(activity.getResources().getColor(mConfiguration.mFullingColorId));
        maskView.setFullingAlpha(mConfiguration.mAlpha);
        maskView.setHighTargetConfigutation(mConfiguration.mHighLightConfigurationList);
        maskView.setOverlayTarget(mConfiguration.mOverlayTarget);
        maskView.setOnKeyListener(this);

        // For removing the height of status bar we need the root content view's
        // location on screen
        int parentX = 0;
        int parentY = 0;
        if (overlay != null) {
            int[] loc = new int[2];
            overlay.getLocationInWindow(loc);
            parentX = loc[0];
            parentY = loc[1];
        }

        if (mConfiguration.mTargetViewList != null) {
            List<Rect> rects = new ArrayList<>(mConfiguration.mTargetViewList.size());
            for (View targetView : mConfiguration.mTargetViewList) {
                rects.add(Common.getViewAbsRect(targetView, parentX, parentY));
            }
            maskView.setTargetRectList(rects);
        } else {
            // Gets the target view's abs rect
            List<Rect> rects = new ArrayList<>(mConfiguration.mTargetViewIdList.size());
            for (Integer id : mConfiguration.mTargetViewIdList) {
                View target = activity.findViewById(id);
                if (target != null) {
                    rects.add(Common.getViewAbsRect(target, parentX, parentY));
                }
            }
            maskView.setTargetRectList(rects);
        }

        if (mConfiguration.mOutsideTouchable) {
            maskView.setClickable(false);
        } else {
            maskView.setOnTouchListener(this);
        }

        // Adds the components to the mask view.
        mViewTargetViewHashMap = new HashMap<>();
        for (Component c : mComponents) {
            View componentView = Common.componentToView(activity.getLayoutInflater(), c);
            if (mComponentTargetViewMap != null && mComponentTargetViewMap.containsKey(c)) {
                mViewTargetViewHashMap.put(componentView, mComponentTargetViewMap.get(c));
            }
            maskView.addView(componentView);
        }
        maskView.setComponentViewTargetViewMap(mViewTargetViewHashMap);

        return maskView;
    }

    private void onDestroy() {
        mConfiguration = null;
        mComponents = null;
        mComponentTargetViewMap = null;
        mViewTargetViewHashMap = null;
        mOnVisibilityChangedListener = null;
        mOnSlideListener = null;
        mMaskView.removeAllViews();
        mMaskView = null;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if (mConfiguration != null && mConfiguration.mAutoDismiss) {
                dismiss();
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    float startY = -1f;

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            startY = motionEvent.getY();
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            if (startY - motionEvent.getY() > DimenUtil.dp2px(view.getContext(), SLIDE_THRESHOLD)) {
                if (mOnSlideListener != null) {
                    mOnSlideListener.onSlideListener(GuideBuilder.SlideState.UP);
                }
            } else if (motionEvent.getY() - startY > DimenUtil
                    .dp2px(view.getContext(), SLIDE_THRESHOLD)) {
                if (mOnSlideListener != null) {
                    mOnSlideListener.onSlideListener(GuideBuilder.SlideState.DOWN);
                }
            }
            if (mConfiguration != null && mConfiguration.mAutoDismiss) {
                dismiss();
            }
        }
        return true;
    }
}
