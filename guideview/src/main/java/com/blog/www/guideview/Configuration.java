package com.blog.www.guideview;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import java.util.List;

/**
 * 遮罩系统创建时配置参数的封装 <br/>
 * Created by binIoter
 */

public class Configuration implements Parcelable {

    /**
     * 需要被找的View list
     */
    List<View> mTargetViewList = null;

    /**
     * 目标控件Id list
     */
    List<Integer> mTargetViewIdList = null;

    /**
     * target view的属性配置
     */
    List<HighLightAreaConfiguration> mHighLightConfigurationList = null;

    /**
     * target view 的点击事件响应
     */
    List<Action0> mTargetViewActions;

    /**
     * 是否可以透过蒙层点击，默认不可以
     */
    boolean mOutsideTouchable = false;

    /**
     * 遮罩透明度
     */
    int mAlpha = 255;

    /**
     * 遮罩覆盖区域控件Id
     * <p/>
     * 该控件的大小既该导航页面的大小
     */
    int mFullingViewId = -1;

    /**
     * 遮罩背景颜色id
     */
    int mFullingColorId = android.R.color.black;

    /**
     * 是否在点击的时候自动退出导航
     */
    boolean mAutoDismiss = true;

    /**
     * 是否覆盖目标控件
     */
    boolean mOverlayTarget = false;

    boolean mShowCloseButton = false;

    int mEnterAnimationId = -1;

    int mExitAnimationId = -1;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mAlpha);
        dest.writeInt(mFullingViewId);
        dest.writeList(mTargetViewIdList);
        dest.writeInt(mFullingColorId);
        dest.writeList(mHighLightConfigurationList);
        dest.writeByte((byte) (mAutoDismiss ? 1 : 0));
        dest.writeByte((byte) (mOverlayTarget ? 1 : 0));
    }

    public static final Creator<Configuration> CREATOR = new Creator<Configuration>() {
        @Override
        public Configuration createFromParcel(Parcel source) {
            Configuration conf = new Configuration();
            conf.mAlpha = source.readInt();
            conf.mFullingViewId = source.readInt();
            source.readList(conf.mTargetViewIdList, ClassLoader.getSystemClassLoader());
            conf.mFullingColorId = source.readInt();
            source.readList(conf.mHighLightConfigurationList, ClassLoader.getSystemClassLoader());
            conf.mAutoDismiss = source.readByte() == 1;
            conf.mOverlayTarget = source.readByte() == 1;
            return conf;
        }

        @Override
        public Configuration[] newArray(int size) {
            return new Configuration[size];
        }
    };

    public static class HighLightAreaConfiguration implements Parcelable {

        /**
         * 高亮区域的padding
         */
        private int mPadding;

        /**
         * 高亮区域的左侧padding
         */
        private int mPaddingLeft;

        /**
         * 高亮区域的顶部padding
         */
        private int mPaddingTop;

        /**
         * 高亮区域的右侧padding
         */
        private int mPaddingRight;

        /**
         * 高亮区域的底部padding
         */
        private int mPaddingBottom;

        /**
         * 高亮区域的圆角大小
         */
        private int mCorner;

        /**
         * 高亮区域的图形样式，默认为矩形
         */
        private int mGraphStyle;

        HighLightAreaConfiguration(int padding, int paddingLeft, int paddingTop,
                int paddingRight,
                int paddingBottom, int corner, int graphStyle) {
            mPadding = padding;
            mPaddingLeft = paddingLeft;
            mPaddingTop = paddingTop;
            mPaddingRight = paddingRight;
            mPaddingBottom = paddingBottom;
            mCorner = corner;
            mGraphStyle = graphStyle;
        }

        public static Builder builder() {
            return new Builder();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(mCorner);
            dest.writeInt(mPadding);
            dest.writeInt(mPaddingLeft);
            dest.writeInt(mPaddingTop);
            dest.writeInt(mPaddingRight);
            dest.writeInt(mPaddingBottom);
            dest.writeInt(mGraphStyle);
        }

        public final Creator<HighLightAreaConfiguration> CREATOR
                = new Creator<HighLightAreaConfiguration>() {
            @Override
            public HighLightAreaConfiguration createFromParcel(Parcel source) {
                HighLightAreaConfiguration conf = new HighLightAreaConfiguration(
                        source.readInt(),
                        source.readInt(),
                        source.readInt(),
                        source.readInt(),
                        source.readInt(),
                        source.readInt(),
                        source.readInt()
                );
                return conf;
            }

            @Override
            public HighLightAreaConfiguration[] newArray(int size) {
                return new HighLightAreaConfiguration[size];
            }
        };

        public int getPadding() {
            return mPadding;
        }

        public int getPaddingLeft() {
            return mPaddingLeft;
        }

        public int getPaddingTop() {
            return mPaddingTop;
        }

        public int getPaddingRight() {
            return mPaddingRight;
        }

        public int getPaddingBottom() {
            return mPaddingBottom;
        }

        public int getCorner() {
            return mCorner;
        }

        public int getGraphStyle() {
            return mGraphStyle;
        }

        public Creator<HighLightAreaConfiguration> getCREATOR() {
            return CREATOR;
        }

        public static class Builder {

            private int mPadding = 0;

            private int mPaddingLeft;

            private int mPaddingTop;

            private int mPaddingRight;

            private int mPaddingBottom;

            private int mCorner;

            private int mGraphStyle;

            Builder() {
            }

            public Builder setPadding(int padding) {
                this.mPadding = padding;
                return this;
            }

            public Builder setLeftPadding(int leftPadding) {
                this.mPaddingLeft = leftPadding;
                return this;
            }

            public Builder setRightPadding(int rightPadding) {
                this.mPaddingRight = rightPadding;
                return this;
            }

            public Builder setTopPadding(int topPadding) {
                this.mPaddingTop = topPadding;
                return this;
            }

            public Builder setBottomPadding(int bottomPadding) {
                this.mPaddingBottom = bottomPadding;
                return this;
            }

            public Builder setCorner(int corner) {
                this.mCorner = corner;
                return this;
            }

            public Builder setGraphStyle(int graphStyle) {
                this.mGraphStyle = graphStyle;
                return this;
            }

            public HighLightAreaConfiguration build() {
                return new HighLightAreaConfiguration(mPadding, mPaddingLeft, mPaddingTop,
                        mPaddingRight, mPaddingBottom, mCorner, mGraphStyle);
            }

        }

    }
}
