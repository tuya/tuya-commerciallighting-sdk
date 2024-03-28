package com.thingclips.smart.commercial.lighting.demo.pages.group.presenter;

import com.thingclips.smart.commercial.lighting.demo.bean.DeviceBeanWrapper;
import com.thingclips.sdk.os.ThingOSDevice;
import com.thingclips.sdk.os.lighting.ThingCommercialLightingDevice;
import com.thingclips.sdk.os.lighting.ThingCommercialLightingGroupPack;
import com.thingclips.smart.android.common.utils.L;
import com.thingclips.smart.commercial.lighting.demo.pages.group.view.IGroupDeviceListView;
import com.thingclips.smart.home.sdk.callback.IThingResultCallback;
import com.thingclips.smart.interior.device.bean.DeviceRespBean;
import com.thingclips.smart.lighting.sdk.bean.ComplexDeviceBean;
import com.thingclips.smart.lighting.sdk.bean.GroupDeviceListRespBean;
import com.thingclips.smart.lighting.sdk.bean.LightingGroupDeviceBean;
import com.thingclips.smart.lighting.sdk.bean.TransferResultSummary;
import com.thingclips.smart.lighting.sdk.impl.DefaultDeviceTransferListener;
import com.thingclips.smart.sdk.api.IDevListener;
import com.thingclips.smart.sdk.api.IThingDevice;
import com.thingclips.smart.sdk.bean.DeviceBean;
import com.thingclips.smart.sdk.bean.GroupDeviceBean;

import java.util.ArrayList;
import java.util.List;

public class PackedGroupListPresenter implements IPackedGroupDeviceListPresenter {
    private long mProjectId;
    private long mAreaId;
    private String mPackedGroupId;
    private IGroupDeviceListView mView;

    public PackedGroupListPresenter(IGroupDeviceListView view) {
        this.mView = view;
    }

    public void setData(long projectId, long areaId, String groupPackId) {
        this.mProjectId = projectId;
        this.mAreaId = areaId;
        this.mPackedGroupId = groupPackId;
    }

    @Override
    public void getData(boolean createGroup) {
        ThingCommercialLightingGroupPack.newGroupPackInstance(mProjectId, mPackedGroupId)
                .getAvailableDevices2JoinGroupPack(mAreaId, "zm", 20, "1", new IThingResultCallback<GroupDeviceListRespBean>() {
                    @Override
                    public void onSuccess(GroupDeviceListRespBean result) {
                        if (result != null && result.devices != null) {
                            transformData(result.devices);
                        } else {
                            mView.toast("Error response");
                        }
                    }

                    @Override
                    public void onError(String errorCode, String errorMessage) {
                        mView.toast(errorMessage);
                    }
                });
    }

    @Override
    public void createPackedGroup(String packedGroupName, List<ComplexDeviceBean> deviceBeans) {
        ThingCommercialLightingGroupPack.getGroupPackManager().createPackedGroup(mProjectId, mAreaId, packedGroupName, deviceBeans, "zm","", new DefaultDeviceTransferListener() {
            @Override
            public void handleResult(TransferResultSummary transferResultSummary) {
                mView.toast("Create Packed group finished");
            }

            @Override
            public void onError(String errorMsg, String errorCode) {
                mView.toast("Create Packed group failed:" + errorMsg);
            }
        });
    }


    private void transformData(List<LightingGroupDeviceBean> devices) {
        if (devices == null) {
            return;
        }

        ArrayList<DeviceBeanWrapper> deviceBeans = new ArrayList<>();

        for (LightingGroupDeviceBean deviceRespBean : devices) {
            if (deviceRespBean == null) {
                continue;
            }

            DeviceBean deviceBean = ThingCommercialLightingDevice.getDeviceManager().getDeviceBean(deviceRespBean.getDevId());
            if (deviceBean == null) {
                continue;
            }

            DeviceBeanWrapper deviceBeanWrapper = new DeviceBeanWrapper();
            deviceBeanWrapper.setDeviceBean(deviceBean);
            deviceBeanWrapper.setChecked(deviceRespBean.isChecked());
            deviceBeanWrapper.setDeviceRespBean(deviceRespBean);
            deviceBeans.add(deviceBeanWrapper);

            IThingDevice lightingDevice = ThingOSDevice.newDeviceInstance(deviceRespBean.getDevId());
            lightingDevice.registerDevListener(new IDevListener() {
                @Override
                public void onDpUpdate(String devId, String dpStr) {

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
            });
        }

        mView.refresh(deviceBeans);
    }
}
