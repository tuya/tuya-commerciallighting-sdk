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

package com.thingclips.smart.commercial.lighting.demo.pages.device.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.kyleduo.switchbutton.SwitchButton;
import com.thingclips.smart.commercial.lighting.demo.app.base.BaseActivity;
import com.thingclips.smart.android.demo.R;
import com.thingclips.smart.android.device.bean.BitmapSchemaBean;
import com.thingclips.smart.android.device.bean.BoolSchemaBean;
import com.thingclips.smart.android.device.bean.EnumSchemaBean;
import com.thingclips.smart.android.device.bean.SchemaBean;
import com.thingclips.smart.android.device.bean.StringSchemaBean;
import com.thingclips.smart.android.device.bean.ValueSchemaBean;
import com.thingclips.smart.android.device.enums.DataTypeEnum;
import com.thingclips.smart.android.device.enums.ModeEnum;
import com.thingclips.smart.sdk.ThingSdk;
import com.thingclips.smart.sdk.bean.DeviceBean;

import java.util.ArrayList;
import java.util.List;

public class CommonDebugDeviceAdapter extends BaseListArrayAdapter<SchemaBean> {

    private static final String TAG = "CommonDebugAdapter";
    private DeviceBean mBean;
    private CompoundButton.OnCheckedChangeListener mCheckChangeListener;
    private View.OnClickListener mOnClickListener;
    private List<View> mViewList;

    public CommonDebugDeviceAdapter(Context context, DeviceBean bean, int resource, List<SchemaBean> data) {
        super(context, resource, data);
        mBean = bean;
        mViewList = new ArrayList<>(20);
    }

    public void setOnClickListener(View.OnClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
    }

    public void setCheckChangeListener(CompoundButton.OnCheckedChangeListener mCheckChangeListener) {
        this.mCheckChangeListener = mCheckChangeListener;
    }

    @Override
    protected ViewHolder view2Holder(View view) {
        mViewList.add(view);
        return new InnerViewHolder(mBean, view, mCheckChangeListener, mOnClickListener);
    }

    @Override
    protected void bindView(ViewHolder viewHolder, SchemaBean data) {
        viewHolder.updateData(data);
    }

    public void updateViewData(String dpId, Object value) {
        for (View view : mViewList) {
            String schemaId = (String) view.getTag(R.id.schemaId);
            if (TextUtils.equals(schemaId, dpId)) {
                View valueView = (View) view.getTag(R.id.schemaView);
                SchemaBean schemaBean = getSchemaBeanById(schemaId);
                if (schemaBean.getType().equals(DataTypeEnum.OBJ.getType())) {
                    //obj 类型
                    String schemaType = schemaBean.getSchemaType();
                    if (TextUtils.equals(schemaType, StringSchemaBean.type)) {
                        TextView strTV = (TextView) valueView;
                        strTV.setText(String.valueOf(value));
                    } else if (TextUtils.equals(schemaType, ValueSchemaBean.type)) {
                        TextView strTV = (TextView) valueView;
                        strTV.setText(String.valueOf(value));
                    } else if (TextUtils.equals(schemaType, BoolSchemaBean.type)) {
                        SwitchButton switchButton = (SwitchButton) valueView;
                        switchButton.setCheckedImmediatelyNoEvent((Boolean) value);
                    } else if (TextUtils.equals(schemaType, EnumSchemaBean.type)) {
                        TextView strTV = (TextView) valueView;
                        strTV.setText(String.valueOf(value));
                    }
                } else if (schemaBean.getType().equals(DataTypeEnum.RAW.getType())) {
                    //raw | file 类型
                    TextView strTV = (TextView) valueView;
                    strTV.setText(String.valueOf(value));
                }
            }
        }
    }

    private SchemaBean getSchemaBeanById(String schemaId) {
        List<SchemaBean> data = getData();
        for (SchemaBean schemaBean : data) {
            if (schemaBean.getId().equals(schemaId))
                return schemaBean;
        }
        return null;
    }

    private static class InnerViewHolder extends ViewHolder<SchemaBean> {
        private final DeviceBean bean;
        private final SwitchButton sbBoolean;
        private final TextView dpName;
        private final TextView dpProperty;
        private final TextView dpNumber;
        private final TextView valueStr;
        private final TextView valueStrSend;
        private final TextView mEnumTV;
        private final ImageView mImageEnumMore;
        private final View numberLine1;
        private final View numberLine2;
        private final ImageView numberSub;
        private final ImageView numberAdd;
        private final TextView numberValue;
        private final TextView roNumberValue;
        private CompoundButton.OnCheckedChangeListener mCheckChangeListener;
        private View.OnClickListener mOnClickListener;

        InnerViewHolder(DeviceBean bean, View contentView, CompoundButton.
                OnCheckedChangeListener checkedChangeListener,
                        View.OnClickListener clickListener) {
            super(contentView);
            dpName = (TextView) contentView.findViewById(R.id.tv_dp_name);
            dpProperty = (TextView) contentView.findViewById(R.id.tv_dp_property);
            dpNumber = (TextView) contentView.findViewById(R.id.tv_dp_number);
            sbBoolean = (SwitchButton) contentView.findViewById(R.id.sb_boolean);
            valueStr = (TextView) contentView.findViewById(R.id.et_input);
            valueStrSend = (TextView) contentView.findViewById(R.id.tv_input_send);
            mEnumTV = (TextView) contentView.findViewById(R.id.tv_enum);
            numberLine1 = contentView.findViewById(R.id.line1);
            numberLine2 = contentView.findViewById(R.id.line2);
            mImageEnumMore = (ImageView) contentView.findViewById(R.id.iv_enum_more);
            numberSub = (ImageView) contentView.findViewById(R.id.iv_sub);
            numberAdd = (ImageView) contentView.findViewById(R.id.iv_add);
            numberValue = (TextView) contentView.findViewById(R.id.tv_number);
            roNumberValue = (TextView) contentView.findViewById(R.id.tv_ro_number);
            this.bean = bean;
            mCheckChangeListener = checkedChangeListener;
            mOnClickListener = clickListener;
        }

        @Override
        public void updateData(SchemaBean schemaBean) {
            dpName.setText(schemaBean.getName());
            dpNumber.setText(schemaBean.getId());
            dpProperty.setText(getProperty(schemaBean));
            initViewGone();
            initDpData(schemaBean, bean);
            initListener(schemaBean);
        }

        private void initListener(SchemaBean schemaBean) {
            boolean onlyReport = schemaBean.getMode().equals(ModeEnum.RO.getType());

            contentView.setTag(R.id.schemaId, schemaBean.getId());
            if (schemaBean.getType().equals(DataTypeEnum.OBJ.getType())) {
                String schemaType = schemaBean.getSchemaType();
                if (TextUtils.equals(schemaType, StringSchemaBean.type)) {
                    valueStrSend.setOnClickListener(mOnClickListener);
                    valueStrSend.setTag(R.id.schemaId, schemaBean.getId());
                    valueStrSend.setTag(R.id.schemaView, valueStr);
                    contentView.setTag(R.id.schemaView, valueStr);
                } else if (TextUtils.equals(schemaType, ValueSchemaBean.type)) {
                    numberAdd.setOnClickListener(mOnClickListener);
                    numberSub.setOnClickListener(mOnClickListener);
                    if (!onlyReport) {
                        contentView.setTag(R.id.schemaView, numberValue);
                        numberAdd.setTag(R.id.schemaId, schemaBean.getId());
                        numberAdd.setTag(R.id.schemaView, numberValue);
                        numberSub.setTag(R.id.schemaView, numberValue);
                        numberSub.setTag(R.id.schemaId, schemaBean.getId());
                    } else {
                        contentView.setTag(R.id.schemaView, roNumberValue);
                    }
                } else if (TextUtils.equals(schemaType, BoolSchemaBean.type)) {
                    sbBoolean.setOnCheckedChangeListener(mCheckChangeListener);
                    contentView.setTag(R.id.schemaView, sbBoolean);
                    sbBoolean.setTag(R.id.schemaId, schemaBean.getId());
                } else if (TextUtils.equals(schemaType, EnumSchemaBean.type)) {
                    mEnumTV.setOnClickListener(mOnClickListener);
                    contentView.setTag(R.id.schemaView, mEnumTV);
                    mEnumTV.setTag(R.id.schemaId, schemaBean.getId());
                }
            } else if (schemaBean.getType().equals(DataTypeEnum.RAW.getType())) {
                valueStrSend.setOnClickListener(mOnClickListener);
                valueStrSend.setTag(R.id.schemaId, schemaBean.getId());
                valueStrSend.setTag(R.id.schemaView, valueStr);
                contentView.setTag(R.id.schemaView, valueStr);
            }
        }

        private void initViewGone() {
            BaseActivity.setViewGone(valueStrSend);
            BaseActivity.setViewGone(valueStr);
            BaseActivity.setViewGone(numberLine1);
            BaseActivity.setViewGone(numberLine2);
            BaseActivity.setViewGone(numberSub);
            BaseActivity.setViewGone(numberAdd);
            BaseActivity.setViewGone(numberValue);
            BaseActivity.setViewGone(sbBoolean);
            BaseActivity.setViewGone(mEnumTV);
            BaseActivity.setViewGone(mImageEnumMore);
            BaseActivity.setViewGone(roNumberValue);
        }

        private void initDpData(SchemaBean schemaBean, DeviceBean bean) {
            boolean onlyReport = schemaBean.getMode().equals(ModeEnum.RO.getType());
            if (schemaBean.getType().equals(DataTypeEnum.OBJ.getType())) {
                String schemaType = schemaBean.getSchemaType();
                if (TextUtils.equals(schemaType, StringSchemaBean.type)) {
                    BaseActivity.setViewVisible(valueStr);
                    if (!onlyReport) {
                        BaseActivity.setViewVisible(valueStrSend);
                    }
                    try {
                        valueStr.setText((String) bean.getDps().get(schemaBean.getId()));
                    } catch (Exception ignored) {
                    }
                } else if (TextUtils.equals(schemaType, ValueSchemaBean.type)) {
                    if (!onlyReport) {
                        BaseActivity.setViewVisible(numberLine1);
                        BaseActivity.setViewVisible(numberLine2);
                        BaseActivity.setViewVisible(numberSub);
                        BaseActivity.setViewVisible(numberAdd);
                        BaseActivity.setViewVisible(numberValue);
                        try {
                            numberValue.setText((String) bean.getDps().get(schemaBean.getId()));
                        } catch (Exception ignored) {
                        }
                    } else {
                        BaseActivity.setViewVisible(roNumberValue);
                        try {
                            roNumberValue.setText((String) bean.getDps().get(schemaBean.getId()));
                        } catch (Exception ignored) {
                        }
                    }

                } else if (TextUtils.equals(schemaType, BoolSchemaBean.type)) {
                    BaseActivity.setViewVisible(sbBoolean);
                    try {
                        sbBoolean.setCheckedImmediatelyNoEvent((Boolean) bean.getDps().get(schemaBean.getId()));
                    } catch (Exception ignored) {
                    }
                } else if (TextUtils.equals(schemaType, EnumSchemaBean.type)) {
                    BaseActivity.setViewVisible(mEnumTV);
                    BaseActivity.setViewVisible(mImageEnumMore);
                    try {
                        mEnumTV.setText((String) bean.getDps().get(schemaBean.getId()));
                    } catch (Exception ignored) {
                    }
                }
            } else if (schemaBean.getType().equals(DataTypeEnum.RAW.getType())) {
                BaseActivity.setViewVisible(valueStr);
                if (!onlyReport) {
                    BaseActivity.setViewVisible(valueStrSend);
                }
                try {
                    valueStr.setText((String) bean.getDps().get(schemaBean.getId()));
                } catch (Exception ignored) {
                }
            }
        }

        private static String getProperty(SchemaBean schemaBean) {
            String mode = schemaBean.getMode();
            String modeType;
            if (TextUtils.equals(mode, ModeEnum.RO.getType())) {
                modeType = ThingSdk.getApplication().getString(R.string.mode_only_read);
            } else if (TextUtils.equals(mode, ModeEnum.RW.getType())) {
                modeType = ThingSdk.getApplication().getString(R.string.mode_read_write);
            } else {
                modeType = ThingSdk.getApplication().getString(R.string.mode_only_write);
            }

            String schemaTypeStr = "";
            if (schemaBean.getType().equals(DataTypeEnum.OBJ.getType())) {
                String schemaType = schemaBean.getSchemaType();
                if (TextUtils.equals(schemaType, StringSchemaBean.type)) {
                    schemaTypeStr = ThingSdk.getApplication().getString(R.string.stringType);
                } else if (TextUtils.equals(schemaType, ValueSchemaBean.type)) {
                    schemaTypeStr = ThingSdk.getApplication().getString(R.string.valueType);
                } else if (TextUtils.equals(schemaType, BitmapSchemaBean.type)) {
                    schemaTypeStr = ThingSdk.getApplication().getString(R.string.bitmapType);
                } else if (TextUtils.equals(schemaType, BoolSchemaBean.type)) {
                    schemaTypeStr = ThingSdk.getApplication().getString(R.string.booleanType);
                } else if (TextUtils.equals(schemaType, EnumSchemaBean.type)) {
                    schemaTypeStr = ThingSdk.getApplication().getString(R.string.enumType);
                }
            } else if (schemaBean.getType().equals(DataTypeEnum.RAW.getType())) {
                schemaTypeStr = ThingSdk.getApplication().getString(R.string.rawType);
            }
            return schemaTypeStr + " | " + modeType;
        }

    }
}