package com.blog.www.guideview;

public final class Constants {

    public Constants() {
    }

    public interface GraphStyle {

        int ROUNDRECT = 0;

        int CIRCLE = 1;
    }

    public interface LocationWay {

        int GLOBAL_LIGHT_AREA_POSITION_PATTERN = 0;

        int SINGLE_POSITION_PATTERN = 1;

        int GLOBAL_POSITION_PATTERN = 2;
    }

    public interface AnchorTargetView {

        int ANCHOR_LEFT = 0x01;

        int ANCHOR_TOP = 0x02;

        int ANCHOR_RIGHT = 0x03;

        int ANCHOR_BOTTOM = 0x04;

        int ANCHOR_OVER = 0x05;

        int PARENT_START = 0x10;

        int PARENT_CENTER = 0x20;

        int PARENT_END = 0x30;
    }


    public interface AnchorScreen {

        int LEFT_TOP = 0;

        int RIGHT_TOP = 1;

        int LEFT_BOTTOM = 2;

        int RIGHT_BOTTOM = 3;

        int SCREEN_CENTER = 4;
    }

}
