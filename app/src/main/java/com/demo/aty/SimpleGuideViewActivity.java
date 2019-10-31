package com.demo.aty;

import com.blog.www.guideview.Component;
import com.blog.www.guideview.Guide;
import com.blog.www.guideview.GuideBuilder;
import com.demo.component.LottieComponent;
import com.demo.component.MutiComponent;
import com.demo.component.SimpleComponent;
import com.demo.guide.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SimpleGuideViewActivity extends Activity {

    private Button header_imgbtn;

    private LinearLayout ll_nearby, ll_video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_guide_view);
        header_imgbtn = (Button) findViewById(R.id.header_imgbtn);
        header_imgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SimpleGuideViewActivity.this, "show", Toast.LENGTH_SHORT).show();
            }
        });
        ll_nearby = (LinearLayout) findViewById(R.id.ll_nearby);
        ll_video = (LinearLayout) findViewById(R.id.ll_video);
        header_imgbtn.post(new Runnable() {
            @Override
            public void run() {
                showGuideView();
            }
        });
    }

    public void showGuideView() {
        GuideBuilder builder = new GuideBuilder();
        builder.addTargetView(header_imgbtn, Component.ROUNDRECT, new SimpleComponent())
                .setAlpha(150)
                .setHighTargetCorner(20)
                .setHighTargetPadding(10);
        builder.setOnVisibilityChangedListener(new GuideBuilder.OnVisibilityChangedListener() {
            @Override
            public void onShown() {
            }

            @Override
            public void onDismiss() {
                showGuideView2();
            }
        });

        Guide guide = builder.createGuide();
        guide.show(SimpleGuideViewActivity.this);
    }

    public void showGuideView2() {
        final GuideBuilder builder1 = new GuideBuilder();
        builder1.addTargetView(ll_nearby, Component.CIRCLE, new SimpleComponent())
                .addTargetView(ll_video, Component.ROUNDRECT, new LottieComponent())
                .setFullingColorId(R.color.color_green)
                .setAlpha(50);
        builder1.setOnVisibilityChangedListener(new GuideBuilder.OnVisibilityChangedListener() {
            @Override
            public void onShown() {
            }

            @Override
            public void onDismiss() {
                showGuideView3();
            }
        });

        Guide guide = builder1.createGuide();
        guide.show(SimpleGuideViewActivity.this);
    }

    public void showGuideView3() {
        final GuideBuilder builder1 = new GuideBuilder();
        builder1.addTargetView(ll_video, Component.ROUNDRECT, new MutiComponent())
                .addTargetView(ll_nearby, Component.CIRCLE, new LottieComponent())
                .setAlpha(150)
                .setHighTargetCorner(20)
                .setHighTargetPadding(10)
                .setExitAnimationId(android.R.anim.fade_out);
        builder1.setOnVisibilityChangedListener(new GuideBuilder.OnVisibilityChangedListener() {
            @Override
            public void onShown() {
            }

            @Override
            public void onDismiss() {
                showGuideView4();
            }
        });

        Guide guide = builder1.createGuide();
        guide.setShouldCheckLocInWindow(false);
        guide.show(SimpleGuideViewActivity.this);
    }

    public void showGuideView4() {
        final GuideBuilder builder1 = new GuideBuilder();
        builder1.addTargetView(ll_video, Component.ROUNDRECT, null)
                .addTargetView(ll_nearby, Component.CIRCLE, null)
                .setAlpha(150)
                .setHighTargetCorner(20)
                .setHighTargetPadding(10)
                .setExitAnimationId(android.R.anim.fade_out)
                .addComponent(new SimpleComponent(), 0)
                .addComponent(new SimpleComponent(), 1)
                .addComponent(new LottieComponent(), 0)
                .addComponent(new MutiComponent(), 1);
        builder1.setOnVisibilityChangedListener(new GuideBuilder.OnVisibilityChangedListener() {
            @Override
            public void onShown() {
            }

            @Override
            public void onDismiss() {
            }
        });

        Guide guide = builder1.createGuide();
        guide.setShouldCheckLocInWindow(false);
        guide.show(SimpleGuideViewActivity.this);
    }
}
