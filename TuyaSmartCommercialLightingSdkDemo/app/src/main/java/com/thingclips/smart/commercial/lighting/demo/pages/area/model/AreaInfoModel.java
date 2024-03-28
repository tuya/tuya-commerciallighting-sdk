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
import com.thingclips.smart.lighting.sdk.bean.AreaBean;
import com.thingclips.sdk.os.lighting.ThingCommercialLightingArea;

import java.util.List;

public class AreaInfoModel extends BaseModel implements IAreaInfoModel {

    public static final String TAG = AreaInfoModel.class.getSimpleName();

    public AreaInfoModel(Context ctx) {
        super(ctx);
    }

    @Override
    public void getSubAreaList(long projectId, long areaId, IThingResultCallback<List<AreaBean>> callback) {
        ThingCommercialLightingArea.newAreaInstance(projectId, areaId).getSubAreaList(callback);
    }

    @Override
    public void getAreaInfo(long projectId, long areaId, IThingResultCallback<SimpleAreaBean> callback) {
        ThingCommercialLightingArea.newAreaInstance(projectId, areaId).getAreaInfo(callback);
    }

    @Override
    public AreaBean getAreaBeanInCache(long projectId, long areaId) {
        return ThingCommercialLightingArea.newAreaInstance(projectId, areaId).getCurrentAreaBeanCache();
    }

    @Override
    public void updateName(long projectId, long areaId, String name, IThingResultCallback<Boolean> callback) {
        ThingCommercialLightingArea.newAreaInstance(projectId, areaId).updateName(name, callback);
    }

    @Override
    public void delete(long projectId, long areaId, IThingResultCallback<Boolean> callback) {
        ThingCommercialLightingArea.newAreaInstance(projectId, areaId).delete(callback);
    }

    @Override
    public void onDestroy() {

    }
}
