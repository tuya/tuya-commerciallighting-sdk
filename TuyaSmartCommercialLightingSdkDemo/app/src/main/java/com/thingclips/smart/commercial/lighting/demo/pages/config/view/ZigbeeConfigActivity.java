/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2021 Thing Inc.
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package com.thingclips.smart.commercial.lighting.demo.pages.config.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.thingclips.smart.commercial.lighting.demo.app.IntentExtra;
import com.thingclips.smart.commercial.lighting.demo.app.base.BaseActivity;
import com.thingclips.smart.commercial.lighting.demo.utils.ActivityUtils;
import com.thingclips.smart.commercial.lighting.demo.utils.ProgressUtil;
import com.thingclips.smart.commercial.lighting.demo.utils.ToastUtil;
import com.thingclips.smart.android.common.utils.L;
import com.thingclips.smart.android.demo.R;
import com.thingclips.smart.android.hardware.bean.HgwBean;
import com.thingclips.smart.home.sdk.api.IGwSearchListener;
import com.thingclips.smart.home.sdk.api.IThingGwSearcher;
import com.thingclips.smart.home.sdk.builder.ThingGwActivatorBuilder;
import com.thingclips.smart.lighting.sdk.bean.TransferResultSummary;
import com.thingclips.smart.lighting.sdk.impl.DefaultDeviceTransferListener;
import com.thingclips.sdk.os.ThingOSActivator;
import com.thingclips.sdk.os.lighting.ThingCommercialLightingArea;
import com.thingclips.smart.sdk.api.IThingActivator;
import com.thingclips.smart.sdk.api.IThingActivatorGetToken;
import com.thingclips.smart.sdk.api.IThingSmartActivatorListener;
import com.thingclips.smart.sdk.bean.DeviceBean;

import java.util.ArrayList;
import java.util.List;

public class ZigbeeConfigActivity extends BaseActivity implements View.OnClickListener, IGwSearchListener {
    private static final String TAG = "ZigbeeConfigActivity";
    protected static final long CONFIG_TIME_OUT = 120;
    private TextView mTextView;
    private HgwBean mHgwBean;
    private String mToken;
    private IThingActivator mIThingActivator;
    private List<String> devIds = new ArrayList<>();
    private long projectId;
    private long areaId;
    private IThingGwSearcher mIThingGwSearcher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zigbee_config);
        projectId = getIntent().getLongExtra(IntentExtra.KEY_PROJECT_ID, -1);
        areaId = getIntent().getLongExtra(IntentExtra.KEY_AREA_ID, -1);
        initView();
        initToolbar();
        setTitle("gateway config");
    }

    private void initView() {
        mTextView = findViewById(R.id.tv_message);
        findViewById(R.id.btn_wired_zigbee).setOnClickListener(this);
        findViewById(R.id.btn_zigbee).setOnClickListener(this);
        findViewById(R.id.btn_area).setOnClickListener(this);
    }

    public void requestGWToken(boolean isWire) {
        ProgressUtil.showLoading(this, "Request token... ");
        ThingOSActivator.deviceActivator().getActivatorToken(projectId, new IThingActivatorGetToken() {
            @Override
            public void onSuccess(String token) {
                mTextView.setText("Get token success");
                mToken = token;
                ProgressUtil.hideLoading();
                if (isWire) {
                    findWireZigbee();
                } else {
                    findZigbee();
                }
            }

            @Override
            public void onFailure(String s, String s1) {
                ProgressUtil.hideLoading();
                mTextView.setText("Get token failed");
                L.d("AddDeviceTypeActivity", "get token error:::" + s + " ++ " + s1);
                Toast.makeText(getApplicationContext(), "get token error: " + s + " ++ " + s1, Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void findZigbee() {
        ToastUtil.showToast(this, "Please confirm that the device is in the state of network configuration");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mIThingActivator != null) {
            mIThingActivator.onDestroy();
        }

        if (mIThingGwSearcher != null) {
            mIThingGwSearcher.unRegisterGwSearchListener();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_wired_zigbee:
                configWireZigbee();
                break;
            case R.id.btn_zigbee:
                configWire();
                break;
            case R.id.btn_area:
                gotoArea();
                break;


        }
    }


    private void configWire() {
        Intent intent = new Intent(this, ECActivity.class);
        intent.putExtra(ECActivity.CONFIG_MODE, ECActivity.EC_MODE);
        intent.putExtra(IntentExtra.KEY_AREA_ID, areaId);
        intent.putExtra(IntentExtra.KEY_PROJECT_ID, projectId);
        ActivityUtils.startActivity(this, intent, ActivityUtils.ANIMATE_FORWARD, true);
    }

    private void gotoArea() {
        if (devIds.isEmpty()) {
            ToastUtil.showToast(this, "Please configure the network first");
            return;
        }
        ThingCommercialLightingArea.newAreaInstance(projectId, areaId).transferDevices(devIds, new DefaultDeviceTransferListener() {
            @Override
            public void handleResult(TransferResultSummary transferResultSummary) {
                mTextView.setText("success：areaId = " + areaId);
            }

            @Override
            public void onError(String errorMsg, String errorCode) {
                mTextView.setText("failed");
            }
        });
    }

    private void configWireZigbee() {
        ToastUtil.showToast(this, "The phone needs to be connected to the same network as the device before acquiring the device");
        mTextView.setText("Obtaining the token...");
        requestGWToken(true);

    }

    private void findWireZigbee() {
        mTextView.setText("Start searching for wired gateway");
        mIThingGwSearcher = ThingOSActivator.deviceActivator().newThingGwActivator().newSearcher();
        mIThingGwSearcher.registerGwSearchListener(this);
    }

    private void config() {
        ProgressUtil.showLoading(this, "Start to configure");
        mIThingActivator = ThingOSActivator.deviceActivator().newGwActivator(
                new ThingGwActivatorBuilder()
                        .setToken(mToken)
                        .setTimeOut(CONFIG_TIME_OUT)
                        .setContext(this)
                        .setHgwBean(mHgwBean)
                        .setListener(new IThingSmartActivatorListener() {

                                         @Override
                                         public void onError(String errorCode, String errorMsg) {
                                             ProgressUtil.hideLoading();
                                             mIThingActivator.stop();
                                             mTextView.setText("failed");
                                         }

                                         @Override
                                         public void onActiveSuccess(DeviceBean devResp) {
                                             ProgressUtil.hideLoading();
                                             mIThingActivator.stop();
                                             mTextView.setText("succeed：devId =" + devResp.getDevId());
                                             devIds.clear();
                                             devIds.add(devResp.devId);
                                         }

                                         @Override
                                         public void onStep(String step, Object data) {
                                         }
                                     }
                        ));

        mIThingActivator.start();
    }

    @Override
    public void onDevFind(HgwBean gw) {
        mTextView.setText("Search successful");
        mHgwBean = gw;
        config();
    }


}
