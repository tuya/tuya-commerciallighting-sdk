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

package com.thingclips.smart.commercial.lighting.demo.pages.area.model;


import android.content.Context;

import com.thingclips.smart.android.mvp.model.BaseModel;
import com.thingclips.smart.home.sdk.bean.SimpleAreaBean;
import com.thingclips.smart.home.sdk.callback.IThingResultCallback;
import com.thingclips.smart.lighting.sdk.bean.AreaConfig;
import com.thingclips.sdk.os.lighting.ThingCommercialLightingArea;
import com.thingclips.sdk.os.lighting.ThingCommercialLightingProject;

import java.util.List;

public class AreaAddModel extends BaseModel implements IAreaAddModel {

    public static final String TAG = AreaAddModel.class.getSimpleName();

    public AreaAddModel(Context ctx) {
        super(ctx);
    }

    @Override
    public void getProjectConfig(long projectId, IThingResultCallback<List<AreaConfig>> callback) {
        ThingCommercialLightingProject.getLightingProjectManager().getProjectConfig(projectId, callback);
    }

    @Override
    public void createArea(long projectId, long currentAreaId, String name, int roomLevel, IThingResultCallback<SimpleAreaBean> callback) {
        ThingCommercialLightingArea.getLightingAreaManager().createArea(projectId, currentAreaId, name, roomLevel, callback);
    }

    @Override
    public void createArea(long projectId, long currentAreaId, String name, int roomLevel, double longitude, double latitude, String address, IThingResultCallback<SimpleAreaBean> callback) {
        ThingCommercialLightingArea.getLightingAreaManager().createArea(projectId, currentAreaId, name, roomLevel, longitude, latitude, address, callback);
    }

    @Override
    public void createSubArea(long projectId, long areaId, String subAreaName, IThingResultCallback<SimpleAreaBean> callback) {
        ThingCommercialLightingArea.newAreaInstance(projectId, areaId).createSubArea(subAreaName, callback);
    }

    @Override
    public void createSubArea(long projectId, long areaId, String subAreaName, double longitude, double latitude, String address, IThingResultCallback<SimpleAreaBean> callback) {
        ThingCommercialLightingArea.newAreaInstance(projectId, areaId).createSubArea(subAreaName, longitude, latitude, address, callback);
    }

    @Override
    public void createParentArea(long projectId, long areaId, String parentAreaName, IThingResultCallback<SimpleAreaBean> callback) {
        ThingCommercialLightingArea.newAreaInstance(projectId, areaId).createParentArea(parentAreaName, callback);
    }

    @Override
    public void createParentArea(long projectId, long areaId, String parentAreaName, double longitude, double latitude, String address, IThingResultCallback<SimpleAreaBean> callback) {
        ThingCommercialLightingArea.newAreaInstance(projectId, areaId).createParentArea(parentAreaName, longitude, latitude, address, callback);
    }

    @Override
    public void onDestroy() {

    }
}
