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

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.thingclips.smart.commercial.lighting.demo.app.IntentExtra;
import com.thingclips.smart.commercial.lighting.demo.app.base.BaseActivity;
import com.thingclips.smart.commercial.lighting.demo.utils.ProgressUtil;
import com.thingclips.smart.commercial.lighting.demo.utils.ToastUtil;
import com.thingclips.smart.android.blemesh.api.IThingBlueMeshActivatorListener;
import com.thingclips.smart.android.blemesh.api.IThingBlueMeshSearch;
import com.thingclips.smart.android.blemesh.api.IThingBlueMeshSearchListener;
import com.thingclips.smart.android.blemesh.bean.SearchDeviceBean;
import com.thingclips.smart.android.blemesh.builder.SearchBuilder;
import com.thingclips.smart.android.blemesh.builder.ThingSigMeshActivatorBuilder;
import com.thingclips.smart.android.demo.R;
import com.thingclips.smart.home.sdk.bean.HomeBean;
import com.thingclips.smart.home.sdk.callback.IThingHomeResultCallback;
import com.thingclips.smart.lighting.sdk.bean.TransferResultSummary;
import com.thingclips.smart.lighting.sdk.impl.DefaultDeviceTransferListener;
import com.thingclips.sdk.os.ThingOSMesh;
import com.thingclips.sdk.os.lighting.ThingCommercialLightingArea;
import com.thingclips.sdk.os.lighting.ThingCommercialLightingBLE;
import com.thingclips.sdk.os.lighting.ThingCommercialLightingProject;
import com.thingclips.smart.sdk.api.bluemesh.ISigMeshCreateCallback;
import com.thingclips.smart.sdk.api.bluemesh.IThingBlueMeshActivator;
import com.thingclips.smart.sdk.bean.DeviceBean;
import com.thingclips.smart.sdk.bean.SigMeshBean;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SigMeshConfigActivity extends BaseActivity implements View.OnClickListener, IThingBlueMeshSearchListener {
    public static final String TAG = SigMeshConfigActivity.class.getSimpleName();
    private IThingBlueMeshSearch mMeshSearch;
    private TextView tv_result;
    private SearchDeviceBean deviceBean;
    private SigMeshBean mSigMeshBean;
    private DeviceBean mDeviceBean;
    private long areaId;
    private long projectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        areaId = getIntent().getLongExtra(IntentExtra.KEY_AREA_ID, -1);
        projectId = getIntent().getLongExtra(IntentExtra.KEY_PROJECT_ID, -1);
        setContentView(R.layout.cl_activity_sig_mesh_config);
        findViewById(R.id.btn_sigmesh_search).setOnClickListener(this);
        findViewById(R.id.btn_sigmesh_config).setOnClickListener(this);
        findViewById(R.id.btn_sigmesh_area).setOnClickListener(this);
        findViewById(R.id.btn_sigmesh_create).setOnClickListener(this);
        tv_result = findViewById(R.id.tv_result);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sigmesh_create:
                createSigMesh();
                break;
            case R.id.btn_sigmesh_search:
                searchSigMesh();
                break;
            case R.id.btn_sigmesh_config:
                configDevice();
                break;
            case R.id.btn_sigmesh_area:
                setArea();
                break;
        }
    }

    private void createSigMesh() {
        if (projectId == -1) {
            ToastUtil.showToast(this, "Invalid projectId");
        }
        tv_result.setText("Begin..");
        ThingCommercialLightingBLE.getSigMeshInstance().createSigMesh(projectId, new ISigMeshCreateCallback() {
            @Override
            public void onError(String errorCode, String errorMsg) {

            }

            @Override
            public void onSuccess(SigMeshBean sigMeshBean) {
                tv_result.setText("Create a mesh successful：meshId = " + sigMeshBean.getMeshId());
                mSigMeshBean = sigMeshBean;
            }
        });
    }

    private void setArea() {
        if (mDeviceBean == null) {
            ToastUtil.showToast(this, "Invalid mDeviceBean");
            return;
        }

        if (areaId == -1) {
            ToastUtil.showToast(this, "Invalid areaId");
            return;
        }


        List<String> devIds = new ArrayList<>();
        devIds.add(mDeviceBean.devId);
        tv_result.setText("Putting devices in area.");
        ProgressUtil.showLoading(this, "Putting devices in area...");
        ThingCommercialLightingArea.newAreaInstance(projectId, areaId).transferDevices(devIds, new DefaultDeviceTransferListener() {
            @Override
            public void handleResult(TransferResultSummary transferResultSummary) {
                tv_result.setText("Success");
                ProgressUtil.hideLoading();
            }

            @Override
            public void onError(String errorMsg, String errorCode) {
                ProgressUtil.hideLoading();
                tv_result.setText("Failed");
            }
        });
    }

    private void configDevice() {
        if (mMeshSearch != null) {
            mMeshSearch.stopSearch();
        }
        if (mSigMeshBean == null) {
            ToastUtil.shortToast(this, "Create a mesh at first.");
            return;
        }
        if (deviceBean == null) {
            ToastUtil.shortToast(this, "No devices found.");
            return;
        }


        tv_result.setText("Start activation..");
        List<SearchDeviceBean> beans = new ArrayList<>();
        beans.add(deviceBean);
        ProgressUtil.showLoading(this, "Start activation..");
        ThingSigMeshActivatorBuilder thingSigMeshActivatorBuilder = new ThingSigMeshActivatorBuilder()
                .setSearchDeviceBeans(beans)
                .setSigMeshBean(mSigMeshBean)
                .setTimeOut(60000)
                .setThingBlueMeshActivatorListener(new IThingBlueMeshActivatorListener() {
                    @Override
                    public void onSuccess(String mac, DeviceBean deviceBean) {
                        ToastUtil.shortToast(SigMeshConfigActivity.this, "Success");
                        tv_result.setText("Activation succeed！Mac address:" + mac + "---" + "name=" + deviceBean.getName());
                        mDeviceBean = deviceBean;
                        getProjectDetail();

                    }

                    @Override
                    public void onError(String mac, String errorCode, String errorMsg) {
                        ToastUtil.shortToast(SigMeshConfigActivity.this, "Failed");
                        tv_result.setText("Failed");
                        ProgressUtil.hideLoading();
                    }

                    @Override
                    public void onFinish() {
                        tv_result.setText("Config finish");
                        ProgressUtil.hideLoading();
                    }
                });

        IThingBlueMeshActivator iThingBlueMeshActivator = ThingOSMesh.activator().newSigActivator(thingSigMeshActivatorBuilder);
        iThingBlueMeshActivator.startActivator();

    }

    private void getProjectDetail() {
        ThingCommercialLightingProject.newProjectInstance(projectId).getProjectDetail(new IThingHomeResultCallback() {
            @Override
            public void onSuccess(HomeBean bean) {
                ProgressUtil.hideLoading();
                ThingCommercialLightingBLE.getThingSigMeshClient().startClient(mSigMeshBean);
            }

            @Override
            public void onError(String errorCode, String errorMsg) {
                ProgressUtil.hideLoading();
            }
        });

    }

    private void searchSigMesh() {
        tv_result.setText("Start searching for devices");
        UUID[] MESH_PROVISIONING_UUID = {UUID.fromString("00001827-0000-1000-8000-00805f9b34fb")};
        SearchBuilder searchBuilder = new SearchBuilder()
                .setServiceUUIDs(MESH_PROVISIONING_UUID)    //SigMesh的UUID是固定值
                .setTimeOut(100)        //扫描时长 单位秒
                .setThingBlueMeshSearchListener(this).build();
        mMeshSearch = ThingOSMesh.activator().newThingBlueMeshSearch(searchBuilder);
        mMeshSearch.startSearch();
    }

    @Override
    public void onSearched(SearchDeviceBean deviceBean) {
        this.deviceBean = deviceBean;
        ToastUtil.shortToast(this, "Search a device");
        tv_result.setText("Mac address of devices found is ：" + deviceBean.getMacAdress());
        if (mMeshSearch != null) {
            mMeshSearch.stopSearch();
        }
    }

    @Override
    public void onSearchFinish() {
        ToastUtil.shortToast(this, "Search failed");
    }
}