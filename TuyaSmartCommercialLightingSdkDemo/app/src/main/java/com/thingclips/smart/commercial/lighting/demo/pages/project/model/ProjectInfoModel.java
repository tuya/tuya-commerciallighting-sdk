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

package com.thingclips.smart.commercial.lighting.demo.pages.project.model;

import android.content.Context;
import android.text.TextUtils;

import com.thingclips.sdk.core.PluginManager;
import com.thingclips.sdk.os.ThingOSActivator;
import com.thingclips.sdk.os.ThingOSBLE;
import com.thingclips.sdk.os.ThingOSDevice;
import com.thingclips.sdk.os.ThingOSMesh;
import com.thingclips.sdk.os.ThingOSUser;
import com.thingclips.sdk.os.lighting.ThingCommercialLightingArea;
import com.thingclips.sdk.os.lighting.ThingCommercialLightingMultiMesh;
import com.thingclips.sdk.ota.ThingOtaPlugin;
import com.thingclips.smart.activator.plug.mesosphere.ThingDeviceActivatorManager;
import com.thingclips.smart.android.mvp.model.BaseModel;
import com.thingclips.smart.home.sdk.ThingHomeSdk;
import com.thingclips.smart.home.sdk.bean.SimpleAreaBean;
import com.thingclips.smart.home.sdk.callback.IThingHomeResultCallback;
import com.thingclips.sdk.os.lighting.ThingCommercialLightingProject;
import com.thingclips.smart.home.sdk.callback.IThingResultCallback;
import com.thingclips.smart.interior.api.IThingDevicePlugin;
import com.thingclips.smart.lighting.bean.SigMeshExtBean;
import com.thingclips.smart.lighting.multimesh.api.ILightingMultiMeshPlugin;
import com.thingclips.smart.lighting.sdk.bean.AreaBean;
import com.thingclips.smart.sdk.api.IResultCallback;
import com.thingclips.smart.sdk.builder.MeshConnectBuilder;

import java.util.ArrayList;
import java.util.List;

public class ProjectInfoModel extends BaseModel implements IProjectInfoModel {

    public ProjectInfoModel(Context ctx) {
        super(ctx);
    }

    @Override
    public void getProjectDetail(long projectId, IThingHomeResultCallback callback) {
        ThingCommercialLightingProject.newProjectInstance(projectId).getProjectDetail(callback);
//        ThingHomeSdk.getActivatorInstance().getActivatorDeviceInfo();
//        ThingOSActivator.deviceActivator().getActivatorDeviceInfo()
//        ThingOSBLE.operator().stopLeScan();
//        ThingOSActivator.activator().newBleActivator().startActivator();
//        ThingOSBLE.manager().stopBleConfig();
//        ThingOSActivator.activator().newMultiModeActivator().startActivator();
//        ThingOSDevice.getDeviceBean().getIsOnline()
//        ThingOSBLE.manager().isBleLocalOnline()
//        ThingOSBLE.manager().disconnectBleDevice();
//        ThingOSDevice.newDeviceInstance().removeDevice();
//        ThingHomeSdk.getMeshInstance().requestUpgradeInfo();
//        ThingOSBLE.manager().startBleOta();
//        ThingOSMesh.activator().newSigActivator()
//        ThingHomeSdk.newSigMeshDeviceInstance()
//        ThingOSDevice.getDeviceBean()
//        ThingOSMesh.newOTA()
//        ThingHomeSdk.newMeshOtaManagerInstance()
//        ThingHomeSdk.newOTAInstance();
//        ThingOtaPlugin.newOtaInstance();
//        ThingHomeSdk.getRequestInstance().requestWithApiName();
//        ThingHomeSdk.getWifiBackupManager("")
//        ThingHomeSdk.getServerInstance()
//        ThingOSDevice.
//        IThingDevicePlugin devicePlugin = PluginManager.service(IThingDevicePlugin.class);
//        boolean isMqttConnected = devicePlugin.getServerInstance().isServerConnect();
//        ThingHomeSdk.getThingSigMeshClientInstance().getMeshDeviceList();
//        ThingOSMesh.getThingSigMeshClient().initMesh();
//        ThingOSMesh.getThingSigMeshClient().stop
//        SimpleAreaBean simpleAreaBean = ThingCommercialLightingArea.newAreaInstance(
//                projectId,
//                areaId
//        ).getCurrentAreaCache().getMeshId()
//        ThingCommercialLighting

//        ILightingMultiMeshPlugin service = PluginManager.service(ILightingMultiMeshPlugin.class);
//        SigMeshExtBean sigMeshBean = service.getInstance().getSigMeshBean(projectId, meshId);

        //1.根据areaId从缓存中获取对应的SimpleAreaBean对象信息
//        SimpleAreaBean areaBean = ThingCommercialLightingArea.newAreaInstance(
//                projectId,//项目ID
//                areaId //当前区域ID
//        ).getCurrentAreaCache();
//
////2.获取当前区域绑定的meshId
//        String meshId = areaBean.getMeshId();
//
////3.若meshId不为空，获取SigMeshBean对象
//        if (!TextUtils.isEmpty(meshId)) {
//            ILightingMultiMeshPlugin service = PluginManager.service(ILightingMultiMeshPlugin.class);
//            service.getMultiMeshManager().getSigMeshAreas(projectId, meshId, true,
//                    new IThingResultCallback<List<AreaBean>>() {
//                        @Override
//                        public void onSuccess(List<AreaBean> simpleAreaBeans) {
//
//                        }
//
//                        @Override
//                        public void onError(String errorCode, String errorMessage) {
//
//                        }
//                    });
//        }

//        List<String> connectedMeshIds = ThingOSMesh.getMeshManager().getConnectedMeshIds();
//        List<SigMeshExtBean> sigMeshExtBeans = new ArrayList<>();
//        ILightingMultiMeshPlugin service = PluginManager.service(ILightingMultiMeshPlugin.class);
//        for (String meshId : connectedMeshIds) {
//            SigMeshExtBean sigMeshBean = service.getInstance().getSigMeshBean(projectId, meshId);
//            if (sigMeshBean != null) {
//                sigMeshExtBeans.add(sigMeshBean);
//            }
//        }

//        List<String> areaIds = new ArrayList<>();
//        areaIds.add("areaId1");
//        areaIds.add("areaId2");
//        areaIds.add("areaId3");
//
//        ILightingMultiMeshPlugin service = PluginManager.service(ILightingMultiMeshPlugin.class);
//        service.getMultiMeshManager().unbindSigMeshAreas(
//                projectId,
//                meshId,
//                areaIds,
//                new IThingResultCallback<Boolean>() {
//                    @Override
//                    public void onSuccess(Boolean aBoolean) {
//
//                    }
//
//                    @Override
//                    public void onError(String errorCode, String errorMessage) {
//
//                    }
//                });
//        service.getMultiMeshManager().deleteSigMesh(
//                projectId,
//                meshId,
//                new IThingResultCallback<Boolean>() {
//                    @Override
//                    public void onSuccess(Boolean aBoolean) {
//
//                    }
//
//                    @Override
//                    public void onError(String errorCode, String errorMessage) {
//
//                    }
//                });
//        service.getMultiMeshManager().createSigMesh(
//                projectId,
//                meshName,
//                new IThingResultCallback<SigMeshExtBean>() {
//            @Override
//            public void onSuccess(SigMeshExtBean sigMeshExtBean) {
//
//            }
//
//            @Override
//            public void onError(String errorCode, String errorMessage) {
//
//            }
//        });
//        List<MeshConnectBuilder> builderList = new ArrayList<>();
//        builderList.add(new MeshConnectBuilder.Builder()
//                .setSigMeshBean(sigmeshBean1)
//                .setScanTimeout(30*1000)
//                .setConnectStatusListener(listener1)
//                .build());
////        ThingOSMesh.getMeshManager().connectMesh(builderList);
//        ThingOSMesh.getMeshManager().disconnectMesh(meshId);
//        SimpleAreaBean areaBean;
//       long relationId =  areaBean.getRelationId()

//        ThingOSUser.getUserInstance().loginWithPassword();
//        ThingOSUser.getUserInstance().loginWithVerifyCode();
//        ThingOSUser.getUserInstance().loginByTicket();
//        ThingDeviceActivatorManager.INSTANCE.addListener();
    }


    @Override
    public void updateProjectInfo(long projectId, String projectName, String leaderName, String leaderMobile, String regionId, String detailAddress, final IResultCallback callback) {
        ThingCommercialLightingProject.newProjectInstance(projectId).updateProjectInfo(projectName, regionId, detailAddress, leaderName, leaderMobile, callback);
    }

    @Override
    public void delete(long projectId, String key, IResultCallback callback) {
        ThingCommercialLightingProject.newProjectInstance(projectId).deleteProjectWithPassword(key, callback);
    }

    @Override
    public void onDestroy() {

    }
}
