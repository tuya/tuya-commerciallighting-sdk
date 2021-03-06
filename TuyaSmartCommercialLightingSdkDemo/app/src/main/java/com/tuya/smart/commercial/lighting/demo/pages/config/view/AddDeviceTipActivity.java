package com.tuya.smart.commercial.lighting.demo.pages.config.view;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.tuya.smart.android.demo.R;
import com.tuya.smart.commercial.lighting.demo.app.base.BaseActivity;
import com.tuya.smart.commercial.lighting.demo.utils.ActivityUtils;

import static com.tuya.smart.commercial.lighting.demo.app.IntentExtra.KEY_AREA_ID;
import static com.tuya.smart.commercial.lighting.demo.app.IntentExtra.KEY_PROJECT_ID;


public class AddDeviceTipActivity extends BaseActivity implements View.OnClickListener {
    private static String TAG = "AddDeviceTipActivity";

    private TextView mStatusLightTip;
    private Button mStatusLightOption;
    private Button mStatusLightHelp;
    private ImageView mStatusLightImageView;
    private AnimationDrawable mStatusLightAnimation;
    private long areaId;
    private long projectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device_tip);
        areaId = getIntent().getLongExtra(KEY_AREA_ID, -1);
        projectId = getIntent().getLongExtra(KEY_PROJECT_ID, -1);
        initToolbar();
        initView();
        initMenu();
        setTitle(R.string.choose_wifi);
        setDisplayHomeAsUpEnabled();
    }

    private void initView() {

        mStatusLightTip = (TextView) findViewById(R.id.status_light_tip);
        mStatusLightTip.setText(R.string.ty_add_device_ez_info);
        initTipImageView();
        mStatusLightOption = (Button) findViewById(R.id.status_light_option);
        mStatusLightOption.setText(R.string.ty_add_device_ez_btn_info);
        mStatusLightOption.setOnClickListener(this);
        mStatusLightHelp = (Button) findViewById(R.id.status_light_help);
        mStatusLightHelp.setOnClickListener(this);
    }

    private void initTipImageView() {
        mStatusLightImageView = (ImageView) findViewById(R.id.status_light_imageview);
        mStatusLightAnimation = new AnimationDrawable();
        mStatusLightAnimation.addFrame(ContextCompat.getDrawable(this, R.drawable.ty_adddevice_lighting), 250);
        mStatusLightAnimation.addFrame(ContextCompat.getDrawable(this, R.drawable.ty_adddevice_light), 250);
        mStatusLightAnimation.setOneShot(false);
        mStatusLightImageView.setImageDrawable(mStatusLightAnimation);
        mStatusLightAnimation.start();
    }

    private void initMenu() {
        setMenu(R.menu.toolbar_ap_menu, new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_ap_mode_onclick) {
                    Intent intent = new Intent(AddDeviceTipActivity.this, AddDeviceAPTipActivity.class);
                    intent.putExtra(KEY_AREA_ID, areaId);
                    intent.putExtra(KEY_PROJECT_ID, projectId);
                    ActivityUtils.startActivity(AddDeviceTipActivity.this, intent, ActivityUtils.ANIMATE_FORWARD, true);
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.status_light_option) {
            Intent intent = new Intent(AddDeviceTipActivity.this, ECActivity.class);
            intent.putExtra(ECActivity.CONFIG_MODE, ECActivity.EC_MODE);
            intent.putExtra(KEY_AREA_ID, areaId);
            intent.putExtra(KEY_PROJECT_ID, projectId);
            ActivityUtils.startActivity(AddDeviceTipActivity.this, intent, ActivityUtils.ANIMATE_FORWARD, true);

        } else if (v.getId() == R.id.status_light_help) {
            ActivityUtils.gotoAddDeviceHelpActivity(this, getString(R.string.ty_ez_help));
        }
    }

    @Override
    public void onBackPressed() {
        ActivityUtils.back(this, ActivityUtils.ANIMATE_SLIDE_BOTTOM_FROM_TOP);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mStatusLightAnimation != null && mStatusLightAnimation.isRunning()) {
            mStatusLightAnimation.stop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mStatusLightAnimation != null && !mStatusLightAnimation.isRunning()) {
            mStatusLightAnimation.start();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mStatusLightAnimation != null && mStatusLightAnimation.isRunning()) {
            mStatusLightAnimation.stop();
        }
    }
}
