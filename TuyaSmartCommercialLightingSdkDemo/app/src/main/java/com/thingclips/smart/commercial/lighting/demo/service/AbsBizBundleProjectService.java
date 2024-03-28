package com.thingclips.smart.commercial.lighting.demo.service;

import androidx.annotation.CallSuper;

import com.thingclips.smart.api.MicroContext;
import com.thingclips.smart.api.service.MicroService;
import com.thingclips.smart.api.service.MicroServiceManager;
import com.thingclips.smart.commonbiz.area.api.AbsCommonbizSpaceService;
import com.thingclips.smart.commonbiz.relation.api.AbsRelationService;

public abstract class AbsBizBundleProjectService extends MicroService {
    public abstract long getCurrentHomeId();

    private AbsCommonbizSpaceService mSpaceService = MicroServiceManager.getInstance().findServiceByInterface(AbsCommonbizSpaceService.class.getName());

    @CallSuper
    public void shiftCurrentProject(long familyId, String curName) {
        AbsRelationService relationService = MicroContext.findServiceByInterface(AbsRelationService.class.getName());
        if (relationService != null) {
            relationService.shiftCurrentRelation(familyId, curName);
        }
    }

    public void onEnterArea(long areaId) {
        mSpaceService.onSpaceEnter(areaId);
    }

    public void onLeaveArea(long areaId) {
        mSpaceService.onSpaceExit(areaId);
    }

}
