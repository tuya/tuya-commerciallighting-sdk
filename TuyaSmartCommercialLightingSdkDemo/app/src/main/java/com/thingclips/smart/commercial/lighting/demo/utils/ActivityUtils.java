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

package com.thingclips.smart.commercial.lighting.demo.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Handler;

import com.thingclips.sdk.os.lighting.ThingCommercialLightingArea;
import com.thingclips.smart.activator.plug.mesosphere.ThingDeviceActivatorManager;
import com.thingclips.smart.activator.plug.mesosphere.api.IThingDeviceActiveListener;
import com.thingclips.smart.android.common.utils.L;
import com.thingclips.smart.android.common.utils.ThingUtil;
import com.thingclips.smart.android.demo.R;
import com.thingclips.smart.api.router.UrlRouter;
import com.thingclips.smart.commercial.lighting.demo.app.IntentExtra;
import com.thingclips.smart.commercial.lighting.demo.pages.BrowserActivity;
import com.thingclips.smart.commercial.lighting.demo.pages.LauncherActivity;
import com.thingclips.smart.commercial.lighting.demo.pages.area.view.AreaIndexActivity;
import com.thingclips.smart.commercial.lighting.demo.pages.config.CommonConfig;
import com.thingclips.smart.commercial.lighting.demo.pages.config.view.AddDeviceTypeActivity;
import com.thingclips.smart.commercial.lighting.demo.pages.project.view.ProjectIndexActivity;
import com.thingclips.smart.commercial.lighting.demo.pages.project.view.RegionListActivity;
import com.thingclips.smart.lighting.sdk.bean.TransferResultSummary;
import com.thingclips.smart.lighting.sdk.impl.DefaultDeviceTransferListener;
import com.thingclips.smart.sdk.ThingSdk;

import java.util.List;


public class ActivityUtils {
    public static final int ANIMATE_NONE = -1;
    public static final int ANIMATE_FORWARD = 0;
    public static final int ANIMATE_BACK = 1;
    public static final int ANIMATE_EASE_IN_OUT = 2;
    public static final int ANIMATE_SLIDE_TOP_FROM_BOTTOM = 3;
    public static final int ANIMATE_SLIDE_BOTTOM_FROM_TOP = 4;
    public static final int ANIMATE_SCALE_IN = 5;
    public static final int ANIMATE_SCALE_OUT = 6;
    private static final String TAG = "ActivityUtils";

    public static void gotoActivity(Activity from, Class<? extends Activity> clazz, int direction, boolean finished) {
        if (clazz == null) return;
        Intent intent = new Intent();
        intent.setClass(from, clazz);
        startActivity(from, intent, direction, finished);
    }

    public static void gotoLauncherActivity(Activity activity, int direction, boolean finished) {
        Intent intent = new Intent(activity, LauncherActivity.class);
        startActivity(activity, intent, direction, finished);
    }

    public static void startActivity(Activity activity, Intent intent, int direction, boolean finishLastActivity) {
        if (activity == null) return;
        activity.startActivity(intent);
        if (finishLastActivity) activity.finish();
        overridePendingTransition(activity, direction);
    }

    public static void startActivityForResult(Activity activity, Intent intent, int backCode, int direction, boolean finishLastActivity) {
        if (activity == null) return;
        activity.startActivityForResult(intent, backCode);
        if (finishLastActivity) activity.finish();
        overridePendingTransition(activity, direction);
    }

    public static void back(Activity activity) {
        activity.finish();
        overridePendingTransition(activity, ANIMATE_BACK);
    }

    public static void back(Activity activity, int direction) {
        activity.finish();
        overridePendingTransition(activity, direction);
    }

    public static void overridePendingTransition(Activity activity, int direction) {
        if (direction == ANIMATE_FORWARD) {
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else if (direction == ANIMATE_BACK) {
            activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        } else if (direction == ANIMATE_EASE_IN_OUT) {
            activity.overridePendingTransition(R.anim.easein, R.anim.easeout);
        } else if (direction == ANIMATE_SLIDE_TOP_FROM_BOTTOM) {
            activity.overridePendingTransition(R.anim.slide_bottom_to_top, R.anim.slide_none_medium_time);
        } else if (direction == ANIMATE_SLIDE_BOTTOM_FROM_TOP) {
            activity.overridePendingTransition(R.anim.slide_none_medium_time, R.anim.slide_top_to_bottom);
        } else if (direction == ANIMATE_SCALE_IN) {
            activity.overridePendingTransition(R.anim.popup_scale_in, R.anim.slide_none);
        } else if (direction == ANIMATE_SCALE_OUT) {
            activity.overridePendingTransition(R.anim.slide_none, R.anim.popup_scale_out);
        } else if (direction == ANIMATE_NONE) {
            //do nothing
        } else {
//            activity.overridePendingTransition(R.anim.magnify_fade_in, R.anim.slide_none);
            activity.overridePendingTransition(R.anim.magnify_fade_in, R.anim.fade_out);
        }
    }

    public static void gotoHomeActivity(Activity context) {
//        Intent intent = new Intent(context, HomeActivity.class);
        Intent intent = new Intent(context, LauncherActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(context, intent, ANIMATE_NONE, true);
    }

    public static void gotoAddDeviceHelpActivity(Activity activity, String title) {
        Intent intent = new Intent(activity, BrowserActivity.class);
        intent.putExtra(BrowserActivity.EXTRA_LOGIN, false);
        intent.putExtra(BrowserActivity.EXTRA_REFRESH, true);
        intent.putExtra(BrowserActivity.EXTRA_TOOLBAR, true);
        intent.putExtra(BrowserActivity.EXTRA_TITLE, title);

        TypedArray a = activity.obtainStyledAttributes(new int[]{
                R.attr.is_add_device_help_get_from_native});
        boolean isAddDeviceHelpAsset = a.getBoolean(0, false);
        if (isAddDeviceHelpAsset) {
            boolean isChinese = ThingUtil.isZh(ThingSdk.getApplication());
            if (isChinese) {
                intent.putExtra(BrowserActivity.EXTRA_URI, "file:///android_asset/add_device_help_cn.html");
            } else {
                intent.putExtra(BrowserActivity.EXTRA_URI, "file:///android_asset/add_device_help_en.html");
            }
        } else {
            intent.putExtra(BrowserActivity.EXTRA_URI, CommonConfig.RESET_URL);
        }
        a.recycle();

        activity.startActivity(intent);
    }


    public static void gotoProjectManagementActivity(Activity context) {
        Intent intent = new Intent(context, ProjectIndexActivity.class);
        startActivity(context, intent, ANIMATE_SCALE_IN, false);
    }

    public static void gotoAreaManagementActivity(Activity context, long projectId) {
        Intent intent = new Intent(context, AreaIndexActivity.class);
        intent.putExtra(IntentExtra.KEY_PROJECT_ID, projectId);
        startActivity(context, intent, ANIMATE_SCALE_IN, false);
    }

    public static void gotoActivatorActivity(Activity context) {
        ActivityUtils.gotoActivity(context, AddDeviceTypeActivity.class, ActivityUtils.ANIMATE_SLIDE_TOP_FROM_BOTTOM, false);
    }

    public static void gotoActivatorActivity(Activity context, long projectId, long areaId) {
        UrlRouter.execute(UrlRouter.makeBuilder(context, "config_device", null, 0x1000));

        ThingDeviceActivatorManager.INSTANCE.addListener(new IThingDeviceActiveListener() {
            @Override
            public void onDevicesAdd(List<String> devIds) {
                L.i(TAG, "onDeviceAdd");
                UrlRouter.execute(
                        UrlRouter.makeBuilder(context, "meshAction")
                                .putString("action", "meshScan")
                );

                StringBuilder str = new StringBuilder();
                for (String id : devIds) {
                    str.append("add device success, id: " + id).append("\n");
                }

                new Handler().postAtTime(new Runnable() {
                    @Override
                    public void run() {
                        putDevicesInArea(context, projectId, areaId, devIds);
                    }
                }, 5000);
            }

            @Override
            public void onRoomDataUpdate() {

            }

            @Override
            public void onOpenDevicePanel(String deviceId) {

            }
        });
    }

    private static void putDevicesInArea(Activity context, long projectId, long areaId, List<String> devIds) {
        context.runOnUiThread(() -> {
            ProgressUtil.showLoading(context, "");
            ThingCommercialLightingArea.newAreaInstance(projectId, areaId).transferDevices(devIds, new DefaultDeviceTransferListener() {
                @Override
                public void handleResult(TransferResultSummary transferResultSummary) {
                    ToastUtil.showToast(context, "Success");
                    ProgressUtil.hideLoading();
                }

                @Override
                public void onError(String errorMsg, String errorCode) {
                    ProgressUtil.hideLoading();
                    ToastUtil.showToast(context, "Failed");
                }
            });
        });
    }

    public static void gotoRegionListActivity(Activity context, int requestCode) {
        Intent intent = new Intent(context, RegionListActivity.class);
        startActivityForResult(context, intent, requestCode, ActivityUtils.ANIMATE_SLIDE_TOP_FROM_BOTTOM, false);
    }
}
