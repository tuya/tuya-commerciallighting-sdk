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

package com.thingclips.smart.commercial.lighting.demo.pages.device.presenter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.thingclips.smart.commercial.lighting.demo.utils.SchemaMapper;
import com.thingclips.smart.commercial.lighting.demo.utils.SchemaUtil;
import com.thingclips.smart.commercial.lighting.demo.utils.ToastUtil;
import com.thingclips.smart.commercial.lighting.demo.pages.device.view.IDpSendView;
import com.thingclips.smart.android.device.bean.BitmapSchemaBean;
import com.thingclips.smart.android.device.bean.BoolSchemaBean;
import com.thingclips.smart.android.device.bean.EnumSchemaBean;
import com.thingclips.smart.android.device.bean.SchemaBean;
import com.thingclips.smart.android.device.bean.StringSchemaBean;
import com.thingclips.smart.android.device.bean.ValueSchemaBean;
import com.thingclips.smart.android.device.enums.DataTypeEnum;
import com.thingclips.smart.android.mvp.presenter.BasePresenter;
import com.thingclips.smart.lighting.sdk.api.ILightingDevice;
import com.thingclips.sdk.os.ThingOSDevice;
import com.thingclips.sdk.os.lighting.ThingCommercialLightingDevice;
import com.thingclips.smart.sdk.api.IDevListener;
import com.thingclips.smart.sdk.api.IResultCallback;
import com.thingclips.smart.sdk.api.IThingDevice;
import com.thingclips.smart.sdk.bean.DeviceBean;

import java.util.HashMap;
import java.util.Map;

public class DpSendPresenter extends BasePresenter implements IDevListener {

    public static final String INTENT_DEVID = "intent_devid";
    public static final String INTENT_DPID = "intent_dpid";
    private final Context mContext;
    private final IDpSendView mView;
    private String mDevId;
    private String mDpId;
    private IThingDevice mThingDevice;
    private SchemaBean mSchemaBean;
    private DeviceBean mDev;

    public DpSendPresenter(Context context, IDpSendView view) {
        mContext = context;
        mView = view;
        initData();
        initListener();
    }

    private void initListener() {
        mThingDevice.registerDevListener(this);
    }

    private void initData() {
        mDevId = ((Activity) mContext).getIntent().getStringExtra(INTENT_DEVID);
        mDpId = ((Activity) mContext).getIntent().getStringExtra(INTENT_DPID);
        mThingDevice = ThingOSDevice.newDeviceInstance(mDevId);
        Map<String, SchemaBean> schema = ThingCommercialLightingDevice.getDeviceManager().getSchema(mDevId);
        if (schema != null) {
            mSchemaBean = schema.get(mDpId);
        }
        mDev = ThingCommercialLightingDevice.getDeviceManager().getDeviceBean(mDevId);
        if (mDev == null) {
            ToastUtil.showToast(mContext, "Device is Removed");
            ((Activity) mContext).finish();
        }
    }

    public String getTitle() {
        return mSchemaBean == null ? "" : mSchemaBean.getName();
    }


    public void showView() {
        if (mSchemaBean == null) return;
        if (mSchemaBean.getType().equals(DataTypeEnum.OBJ.getType())) {
            String schemaType = mSchemaBean.getSchemaType();
            if (TextUtils.equals(schemaType, BoolSchemaBean.type)) {
                mView.showBooleanView((Boolean) mDev.getDps().get(mSchemaBean.getId()));
            } else if (TextUtils.equals(schemaType, EnumSchemaBean.type)) {
                EnumSchemaBean enumSchemaBean = SchemaMapper.toEnumSchema(mSchemaBean.getProperty());
                mView.showEnumView((String) mDev.getDps().get(mSchemaBean.getId()), enumSchemaBean.getRange());
            } else if (TextUtils.equals(schemaType, StringSchemaBean.type)) {
                mView.showStringView((String) mDev.getDps().get(mSchemaBean.getId()));
            } else if (TextUtils.equals(schemaType, ValueSchemaBean.type)) {
                mView.showValueView((Integer) mDev.getDps().get(mSchemaBean.getId()));
            } else if (TextUtils.equals(schemaType, BitmapSchemaBean.type)) {
                mView.showBitmapView((Integer) mDev.getDps().get(mSchemaBean.getId()));
            }
        } else if (mSchemaBean.getType().equals(DataTypeEnum.RAW.getType())) {
            mView.showRawView((String) mDev.getDps().get(mSchemaBean.getId()));
        }
    }

    public void sendDpValue(final Object dps) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(mDpId, dps);
        final String value = JSONObject.toJSONString(map);
        mView.showMessage("\n");
        mView.showMessage("send command: " + value);
        mThingDevice.publishDps(value, new IResultCallback() {
            @Override
            public void onError(String s, String s1) {
                mView.showMessage("send command failure");

            }

            @Override
            public void onSuccess() {
            }
        });
    }

    @Override
    public void onDpUpdate(String devId, String dps) {
        mView.showMessage("onDpUpdate: " + dps);
    }

    @Override
    public void onRemoved(String devId) {

    }

    @Override
    public void onStatusChanged(String devId, boolean online) {

    }

    @Override
    public void onNetworkStatusChanged(String devId, boolean status) {

    }

    @Override
    public void onDevInfoUpdate(String devId) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public String getName() {
        return mSchemaBean == null ? "" : mSchemaBean.getName() + ":";
    }

    public String getDpDes() {
        if (mSchemaBean == null) return "";
        return mSchemaBean.getProperty();
    }

    public void sendDpValue() {
        if (mSchemaBean == null) return;
        if (mSchemaBean.getType().equals(DataTypeEnum.OBJ.getType())) {
            String schemaType = mSchemaBean.getSchemaType();
            if (TextUtils.equals(schemaType, StringSchemaBean.type)) {
                String strValue = mView.getStrValue();
                if (SchemaUtil.checkStrValue(mSchemaBean, strValue)) {
                    sendDpValue(strValue);
                } else mView.showFormatErrorTip();
            } else if (TextUtils.equals(schemaType, ValueSchemaBean.type)) {
                String value = mView.getValue();
                if (SchemaUtil.checkValue(mSchemaBean, value)) {
                    sendDpValue(Integer.valueOf(value));
                } else mView.showFormatErrorTip();
            } else if (TextUtils.equals(schemaType, BitmapSchemaBean.type)) {
                String value = mView.getBitmapValue();
                if (SchemaUtil.checkBitmapValue(mSchemaBean, value)) {
                    sendDpValue(Integer.valueOf(value));
                } else {
                    mView.showFormatErrorTip();
                }
            }
        } else if (mSchemaBean.getType().equals(DataTypeEnum.RAW.getType())) {
            String rawValue = mView.getRAWValue();
            if (SchemaUtil.checkRawValue(rawValue)) {
                sendDpValue(rawValue);
            } else mView.showFormatErrorTip();
        }
    }

}
