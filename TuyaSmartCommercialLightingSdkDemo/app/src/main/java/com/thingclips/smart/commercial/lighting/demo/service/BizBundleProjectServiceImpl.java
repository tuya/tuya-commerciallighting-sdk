package com.thingclips.smart.commercial.lighting.demo.service;


public class BizBundleProjectServiceImpl extends AbsBizBundleProjectService {
    private long mHomeId;

    @Override
    public long getCurrentHomeId() {
        return mHomeId;
    }

    @Override
    public void shiftCurrentProject(long familyId, String curName) {
        super.shiftCurrentProject(familyId, curName);
        mHomeId = familyId;
    }
}
