package com.blog.www.guideview;

import android.view.LayoutInflater;
import android.view.View;

/**
 * * 遮罩系统中相对于目标区域而绘制一些图片或者文字等view需要实现的接口. <br>
 * * <br>
 * * {@link #getView(LayoutInflater)} <br>
 * * {@link #getAnchor()} <br>
 * * {@link #getFitPosition()} <br>
 * * {@link #getXOffset()} <br>
 * * {@link #getYOffset()}
 * * <br>
 * * 具体创建遮罩的说明请参加{@link GuideBuilder}
 * *
 *
 * Created by binIoter
 */

public interface Component {

    /**
     * 需要显示的view
     *
     * @param inflater use to inflate xml resource file
     * @return the component view
     */
    View getView(LayoutInflater inflater);

    /**
     * 相对目标View的锚点
     *
     * @return could be
     * {@link Constants.AnchorTargetView#ANCHOR_LEFT},
     * {@link Constants.AnchorTargetView#ANCHOR_RIGHT},
     * {@link Constants.AnchorTargetView#ANCHOR_TOP},
     * {@link Constants.AnchorTargetView#ANCHOR_BOTTOM},
     * {@link Constants.AnchorTargetView#ANCHOR_OVER}
     * {@link Constants.AnchorScreen#LEFT_BOTTOM}
     * {@link Constants.AnchorScreen#LEFT_TOP}
     * {@link Constants.AnchorScreen#RIGHT_BOTTOM}
     * {@link Constants.AnchorScreen#RIGHT_TOP}
     * {@link Constants.AnchorScreen#SCREEN_CENTER}
     */
    int getAnchor();

    /**
     * 相对目标View的对齐
     *
     * @return could be
     * {@link Constants.AnchorTargetView#PARENT_START},
     * {@link Constants.AnchorTargetView#PARENT_END},
     * {@link Constants.AnchorTargetView#PARENT_CENTER}
     */
    int getFitPosition();

    /**
     * 相对目标View的X轴位移，在计算锚点和对齐之后。
     *
     * @return X轴偏移量, 单位 dp
     */
    int getXOffset();

    /**
     * 相对目标View的Y轴位移，在计算锚点和对齐之后。
     *
     * @return Y轴偏移量，单位 dp
     */
    int getYOffset();

    /**
     * 设定定位方式，多个高亮区，Component可选择基于整体高亮区定位（多用于单个component）
     * 或者基于每个高亮区域单独定位（多用于多个component）
     */
    int getPositionPattern();
}
