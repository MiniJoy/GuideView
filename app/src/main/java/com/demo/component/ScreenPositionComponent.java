package com.demo.component;

import com.blog.www.guideview.Component;
import com.blog.www.guideview.Constants;
import com.demo.guide.R;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by binIoter on 16/6/17.
 */
public class ScreenPositionComponent implements Component {

    @Override
    public View getView(LayoutInflater inflater) {

        LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.layer_lottie, null);
        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "引导层被点击了", Toast.LENGTH_SHORT).show();
            }
        });
        return ll;
    }

    @Override
    public int getAnchor() {
        return Constants.AnchorScreen.LEFT_BOTTOM;
    }

    @Override
    public int getFitPosition() {
        return Constants.AnchorTargetView.PARENT_CENTER;
    }

    @Override
    public int getXOffset() {
        return 0;
    }

    @Override
    public int getYOffset() {
        return 0;
    }

    @Override
    public int getPositionPattern() {
        return Constants.LocationWay.GLOBAL_POSITION_PATTERN;
    }
}
