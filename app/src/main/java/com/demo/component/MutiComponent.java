package com.demo.component;

import com.blog.www.guideview.Component;
import com.blog.www.guideview.Constants;
import com.demo.guide.R;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by binIoter on 16/6/17.
 */
public class MutiComponent implements Component {

    @Override
    public View getView(LayoutInflater inflater) {
        LinearLayout ll = new LinearLayout(inflater.getContext());
        LinearLayout.LayoutParams param =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setLayoutParams(param);
        TextView textView = new TextView(inflater.getContext());
        textView.setText(R.string.nearby);
        textView.setTextColor(inflater.getContext().getResources().getColor(R.color.color_white));
        textView.setTextSize(20);
        ImageView imageView = new ImageView(inflater.getContext());
        imageView.setImageResource(R.mipmap.arrow);
        ll.removeAllViews();
        ll.addView(textView);
        ll.addView(imageView);
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
        return Constants.AnchorTargetView.ANCHOR_BOTTOM;
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
        return 20;
    }

    @Override
    public int getPositionPattern() {
        return Constants.LocationWay.SINGLE_POSITION_PATTERN;
    }
}
