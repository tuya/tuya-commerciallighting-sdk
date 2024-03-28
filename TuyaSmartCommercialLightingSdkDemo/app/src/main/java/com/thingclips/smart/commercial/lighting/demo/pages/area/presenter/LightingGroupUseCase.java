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

package com.thingclips.smart.commercial.lighting.demo.pages.area.presenter;

import android.app.Activity;

import com.thingclips.sdk.core.PluginManager;
import com.thingclips.sdk.os.lighting.ThingCommercialLightingArea;
import com.thingclips.smart.api.service.MicroServiceManager;
import com.thingclips.smart.home.sdk.api.IThingGroupModel;
import com.thingclips.smart.home.sdk.bean.AreaGroupListRespBean;
import com.thingclips.smart.home.sdk.callback.IThingResultCallback;
import com.thingclips.smart.interior.api.IThingGroupPlugin;
import com.thingclips.smart.lighting.sdk.api.ILightingArea;
import com.thingclips.smart.lighting.sdk.bean.GroupPackListBean;
import com.thingclips.smart.panelcaller.api.AbsPanelCallerService;
import com.thingclips.smart.sdk.bean.GroupBean;

public class LightingGroupUseCase {
    IThingGroupPlugin mGroupPlugin = null;
    private final AbsPanelCallerService mAbsPanelCallerService;

    public LightingGroupUseCase() {
        mGroupPlugin = PluginManager.service(IThingGroupPlugin.class);
        mAbsPanelCallerService = MicroServiceManager.getInstance().findServiceByInterface(AbsPanelCallerService.class.getName());
    }

    public void getGroupDeviceByAreaId(long homeId, long areaId, String offsetKey, IThingResultCallback<AreaGroupListRespBean> callback) {
        ThingCommercialLightingArea.newAreaInstance(homeId, areaId).queryGroupListInArea(offsetKey, callback);
    }

    public void getGroupPackListByAreaId(long homeId, long areaId, String offsetKey, final IThingResultCallback<GroupPackListBean> callback) {
        ILightingArea lightingArea = ThingCommercialLightingArea.newAreaInstance(homeId, areaId);
        if (lightingArea == null) {
            callback.onError("", "lighting area cannot be null");
            return;
        }

        lightingArea.getPackedGroupList(20, offsetKey, callback);
    }

    public void openGroupPanel(Activity activity, GroupBean groupBean, boolean isAdmin) {
        mAbsPanelCallerService.goPanel(activity, groupBean, true);
    }
}
