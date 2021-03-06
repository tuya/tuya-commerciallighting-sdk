/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2021 Tuya Inc.
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

package com.tuya.smart.commercial.lighting.demo.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.core.view.ViewCompat;

import com.alibaba.fastjson.JSON;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.tuya.smart.android.base.bean.CountryBean;
import com.tuya.smart.android.base.database.StorageHelper;
import com.tuya.smart.android.demo.R;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class CommonUtil {
    public static final String URL_AY_STATIC = "http://static.airtakeapp.com/";
    public static final String URL_AZ_STATIC = "http://static.getairtake.com/";

    private static int transferSeq = 0;

    public static String getStaticUrl(boolean isEn) {
        return isEn ? URL_AZ_STATIC : URL_AY_STATIC;
    }

    public static void hideIMM(Activity activity) {
        if (activity == null) {
            return;
        }
        View fcs = activity.getCurrentFocus();
        if (fcs == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(fcs.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static String formatTimer(int h) {
        return String.format("%02d", h);
    }

    public static String formatDate(long mill, String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        try {
            return dateFormat.format(new Date(mill));
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }

    public static String getAppVersionName(Context context) {
        String versionName = "0";
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
            if (TextUtils.isEmpty(versionName)) {
                versionName = "0";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public static String getApplicationName(Context context, String packageName) {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = context.getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        String applicationName =
                (String) packageManager.getApplicationLabel(applicationInfo);
        return applicationName;
    }

    public static boolean isChineseLangWithSetting(String langKey) {
        int langIndex = StorageHelper.getIntValue(langKey, 0);
        boolean isChinese = false;
        switch (langIndex) {
            case 0:
                isChinese = isChineseLang();
                break;

            case 1:
                isChinese = true;
                break;

            default:
                isChinese = false;
                break;
        }

        return isChinese;
    }

    public static boolean isChineseLang() {
        String countryKey = Locale.getDefault().getCountry().toUpperCase();
        if (!TextUtils.isEmpty(countryKey)) {
            if (countryKey.equals("ZH")) return true;
        }
        Locale l = Locale.getDefault();
        String language = l.getLanguage();
        return language.equals("zh");
    }

    public static boolean isChina(Context ctx) {
        try {
            String countryCode = getCountryCode(ctx, null);
            if (TextUtils.isEmpty(countryCode)) {
                TimeZone tz = TimeZone.getDefault();
                String id = tz.getID();
                return TextUtils.equals(id, "Asia/Shanghai");
            }
            return TextUtils.equals(countryCode, "CN");
        } catch (Exception e) {
        }
        return false;
    }

    public static int isChinaByTimeZoneAndContryCode(Context ctx) {
        int ret = 0;
        try {
            String countryCode = getCountryCode(ctx, null);
            if (TextUtils.isEmpty(countryCode)) {
                TimeZone tz = TimeZone.getDefault();
                String id = tz.getID();
                return TextUtils.equals(id, "Asia/Shanghai") ? 1 : 0;
            }
            return TextUtils.equals(countryCode, "CN") ? 1 : 0;
        } catch (Exception e) {
            ret = -1;
        }
        return ret;
    }

    public static String getCountryCode(Context context, String def) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            int phoneType = tm.getPhoneType();
            if (phoneType != TelephonyManager.PHONE_TYPE_CDMA) {
                return tm.getNetworkCountryIso().toUpperCase();
            }
        } catch (Exception e) {
        }
        return def;
    }


    public static boolean checkUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        try {
            url = URLDecoder.decode(url, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
        if (url.indexOf("http://") == -1 || url.indexOf("https://") == -1 || url.indexOf("file:///") == -1) {
            return true;
        }

        return false;
    }

    public static int colorBurn(int RGBValues) {
        int alpha = RGBValues >> 24;
        int red = RGBValues >> 16 & 0xFF;
        int green = RGBValues >> 8 & 0xFF;
        int blue = RGBValues & 0xFF;
        red = (int) Math.floor(red * (1 - 0.1));
        green = (int) Math.floor(green * (1 - 0.1));
        blue = (int) Math.floor(blue * (1 - 0.1));
        return Color.rgb(red, green, blue);
    }

    public static void switchLanguage(Context context, int index) {
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        switch (index) {
            case 0:
                config.locale = Locale.getDefault();
                break;
            case 1:
                config.locale = Locale.SIMPLIFIED_CHINESE;
                break;
            case 2:
                config.locale = Locale.ENGLISH;
                break;
            case 3:
                config.locale = new Locale("es", "ES");
                break;
        }
        resources.updateConfiguration(config, null);
    }

    public static void restartApplication(Context ctx) {
        final Intent intent = ctx.getPackageManager().getLaunchIntentForPackage(ctx.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ctx.startActivity(intent);
    }

    public static String getTimeZone() {
        TimeZone tz = TimeZone.getDefault();
        String displayName = "+08:00";
        if (tz != null) {
            String str = tz.getDisplayName();
            if (!TextUtils.isEmpty(str)) {
                int indexOf = str.indexOf("+");
                if (indexOf == -1) indexOf = str.indexOf("-");
                if (indexOf != -1) {
                    displayName = str.substring(indexOf);
                }
                if (!displayName.contains(":")) {
                    displayName = displayName.substring(0, 3) + ":" + displayName.substring(3);
                }

            }
        }
        return displayName;
    }

    public static String getLanguage() {
        Locale l = Locale.getDefault();
        String language = l.getLanguage();
        String country = l.getCountry().toLowerCase();
        if ("zh".equals(language)) {
            if ("cn".equals(country)) {
                language = "zh";
            } else {
                language = "zh_tw";
            }
        }

        return language;
    }

    @SuppressWarnings("ResourceType")
    public static void initSystemBarColor(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(activity, true);
            SystemBarTintManager tintManager = new SystemBarTintManager(activity);
            tintManager.setStatusBarTintEnabled(true);
            TypedArray a = activity.obtainStyledAttributes(new int[]{
                    R.attr.status_system_bg_color, R.attr.status_bg_color});
            int setColor = a.getColor(0, -1);
            int statusBgColor = a.getColor(1, -1);
            //?????????????????????
            if (setColor != -1) {
                tintManager.setStatusBarTintColor(setColor);
            } else if (statusBgColor != -1) {
                tintManager.setStatusBarTintColor(statusBgColor);
            }

            a.recycle();
            ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0).setFitsSystemWindows(true);//???????????????
        }
    }

    public static void initSystemBarColor(Activity activity, String color, boolean isFits) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(activity, true);
            SystemBarTintManager tintManager = new SystemBarTintManager(activity);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintColor(Color.parseColor(color));//?????????????????????
            if (isFits)
                ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0).setFitsSystemWindows(true);//???????????????
        }
    }

    @TargetApi(19)
    protected static void setTranslucentStatus(Activity activity, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public static void initSystemBarColor(Activity activity, int color) {
        if (activity == null) return;
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
            ViewGroup mContentView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
            View mChildView = mContentView.getChildAt(0);
            if (mChildView != null) {
                mChildView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                ViewCompat.setFitsSystemWindows(mChildView, true);
            }
        } else if (Build.VERSION.SDK_INT >= 19) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            SystemBarTintManager tintManager = new SystemBarTintManager(activity);
            tintManager.setStatusBarTintColor(color);
            tintManager.setStatusBarTintEnabled(true);
        }
    }

    public static int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
            Log.v("@@@@@@", "the status bar height is : " + statusBarHeight);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }

    public static boolean isEmail(String data) {
        if (!TextUtils.isEmpty(data)) {
            Pattern p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
            return p.matcher(data).matches();
        }

        return false;
    }

    public static String getPhoneNumberFormMobile(String mobile) {
        if (TextUtils.isEmpty(mobile)) return "";
        int i = mobile.indexOf("-");
        if (i >= 0) {
            mobile = mobile.substring(i + 1);
        }
        return mobile;
    }

    public static String getRightPhoneCode(String phoneCode) {
        return phoneCode.replace("-", "").replace("+", "");
    }

    public static ArrayList<CountryBean> getDefaultCountryData() {
        String countryData = "[{\"abbr\":\"AF\",\"chinese\":\"?????????\",\"code\":\"93\",\"english\":\"Afghanistan\",\"spell\":\"afuhan\"},{\"abbr\":\"AL\",\"chinese\":\"???????????????\",\"code\":\"355\",\"english\":\"Albania\",\"spell\":\"aerbaniya\"},{\"abbr\":\"DZ\",\"chinese\":\"???????????????\",\"code\":\"213\",\"english\":\"Algeria\",\"spell\":\"aerjiliya\"},{\"abbr\":\"AO\",\"chinese\":\"?????????\",\"code\":\"244\",\"english\":\"Angola\",\"spell\":\"angela\"},{\"abbr\":\"AR\",\"chinese\":\"?????????\",\"code\":\"54\",\"english\":\"Argentina\",\"spell\":\"agenting\"},{\"abbr\":\"AM\",\"chinese\":\"????????????\",\"code\":\"374\",\"english\":\"Armenia\",\"spell\":\"yameiniya\"},{\"abbr\":\"AU\",\"chinese\":\"????????????\",\"code\":\"61\",\"english\":\"Australia\",\"spell\":\"aodaliya\"},{\"abbr\":\"AT\",\"chinese\":\"?????????\",\"code\":\"43\",\"english\":\"Austria\",\"spell\":\"aodili\"},{\"abbr\":\"AZ\",\"chinese\":\"????????????\",\"code\":\"994\",\"english\":\"Azerbaijan\",\"spell\":\"asaibaijiang\"},{\"abbr\":\"BH\",\"chinese\":\"??????\",\"code\":\"973\",\"english\":\"Bahrain\",\"spell\":\"balin\"},{\"abbr\":\"BD\",\"chinese\":\"????????????\",\"code\":\"880\",\"english\":\"Bangladesh\",\"spell\":\"mengjialaguo\"},{\"abbr\":\"BY\",\"chinese\":\"????????????\",\"code\":\"375\",\"english\":\"Belarus\",\"spell\":\"baieluosi\"},{\"abbr\":\"BE\",\"chinese\":\"?????????\",\"code\":\"32\",\"english\":\"Belgium\",\"spell\":\"bilishi\"},{\"abbr\":\"BZ\",\"chinese\":\"?????????\",\"code\":\"501\",\"english\":\"Belize\",\"spell\":\"bolizi\"},{\"abbr\":\"BJ\",\"chinese\":\"??????\",\"code\":\"229\",\"english\":\"Benin\",\"spell\":\"beining\"},{\"abbr\":\"BT\",\"chinese\":\"??????\",\"code\":\"975\",\"english\":\"Bhutan\",\"spell\":\"budan\"},{\"abbr\":\"BO\",\"chinese\":\"????????????\",\"code\":\"591\",\"english\":\"Bolivia\",\"spell\":\"boliweiya\"},{\"abbr\":\"BA\",\"chinese\":\"??????????????????????????????\",\"code\":\"387\",\"english\":\"Bosnia and Herzegovina\",\"spell\":\"bosiniyaheheisaigeweinei\"},{\"abbr\":\"BW\",\"chinese\":\"????????????\",\"code\":\"267\",\"english\":\"Botswana\",\"spell\":\"bociwana\"},{\"abbr\":\"BR\",\"chinese\":\"??????\",\"code\":\"55\",\"english\":\"Brazil\",\"spell\":\"baxi\"},{\"abbr\":\"VG\",\"chinese\":\"??????????????????\",\"code\":\"1284\",\"english\":\"British Virgin Islands\",\"spell\":\"yingshuweijingqundao\"},{\"abbr\":\"BN\",\"chinese\":\"??????\",\"code\":\"673\",\"english\":\"Brunei\",\"spell\":\"wenlai\"},{\"abbr\":\"BG\",\"chinese\":\"????????????\",\"code\":\"359\",\"english\":\"Bulgaria\",\"spell\":\"baojialiya\"},{\"abbr\":\"BF\",\"chinese\":\"???????????????\",\"code\":\"226\",\"english\":\"Burkina-faso\",\"spell\":\"bujinafasuo\"},{\"abbr\":\"BI\",\"chinese\":\"?????????\",\"code\":\"257\",\"english\":\"Burundi\",\"spell\":\"bulongdi\"},{\"abbr\":\"KH\",\"chinese\":\"?????????\",\"code\":\"855\",\"english\":\"Cambodia\",\"spell\":\"jianpuzhai\"},{\"abbr\":\"CM\",\"chinese\":\"?????????\",\"code\":\"237\",\"english\":\"Cameroon\",\"spell\":\"kamailong\"},{\"abbr\":\"CA\",\"chinese\":\"?????????\",\"code\":\"1\",\"english\":\"Canada\",\"spell\":\"jianada\"},{\"abbr\":\"CV\",\"chinese\":\"?????????\",\"code\":\"238\",\"english\":\"Cape Verde\",\"spell\":\"fodejiao\"},{\"abbr\":\"KY\",\"chinese\":\"????????????\",\"code\":\"1345\",\"english\":\"Cayman Islands\",\"spell\":\"kaimanqundao\"},{\"abbr\":\"CF\",\"chinese\":\"???????????????\",\"code\":\"236\",\"english\":\"Central African Republic\",\"spell\":\"zhongfeigongheguo\"},{\"abbr\":\"TD\",\"chinese\":\"??????\",\"code\":\"235\",\"english\":\"Chad\",\"spell\":\"zhade\"},{\"abbr\":\"CL\",\"chinese\":\"??????\",\"code\":\"56\",\"english\":\"Chile\",\"spell\":\"zhili\"},{\"abbr\":\"CN\",\"chinese\":\"??????\",\"code\":\"86\",\"english\":\"China\",\"spell\":\"zhongguo\"},{\"abbr\":\"CO\",\"chinese\":\"????????????\",\"code\":\"57\",\"english\":\"Colombia\",\"spell\":\"gelunbiya\"},{\"abbr\":\"KM\",\"chinese\":\"?????????\",\"code\":\"269\",\"english\":\"Comoros\",\"spell\":\"kemoluo\"},{\"abbr\":\"CG\",\"chinese\":\"??????(???)\",\"code\":\"242\",\"english\":\"Congo - Brazzaville\",\"spell\":\"gangguo(bu)\"},{\"abbr\":\"CD\",\"chinese\":\"??????(???)\",\"code\":\"243\",\"english\":\"Congo - Kinshasa\",\"spell\":\"gangguo(jin)\"},{\"abbr\":\"CR\",\"chinese\":\"???????????????\",\"code\":\"506\",\"english\":\"Costa Rica\",\"spell\":\"gesidalijia\"},{\"abbr\":\"HR\",\"chinese\":\"????????????\",\"code\":\"385\",\"english\":\"Croatia\",\"spell\":\"keluodiya\"},{\"abbr\":\"CY\",\"chinese\":\"????????????\",\"code\":\"357\",\"english\":\"Cyprus\",\"spell\":\"saipulusi\"},{\"abbr\":\"CZ\",\"chinese\":\"???????????????\",\"code\":\"420\",\"english\":\"Czech Republic\",\"spell\":\"jiekegongheguo\"},{\"abbr\":\"DK\",\"chinese\":\"??????\",\"code\":\"45\",\"english\":\"Denmark\",\"spell\":\"danmai\"},{\"abbr\":\"DJ\",\"chinese\":\"?????????\",\"code\":\"253\",\"english\":\"Djibouti\",\"spell\":\"jibuti\"},{\"abbr\":\"DO\",\"chinese\":\"?????????????????????\",\"code\":\"1809\",\"english\":\"Dominican Republic\",\"spell\":\"duominijiagongheguo\"},{\"abbr\":\"EC\",\"chinese\":\"????????????\",\"code\":\"593\",\"english\":\"Ecuador\",\"spell\":\"eguaduoer\"},{\"abbr\":\"EG\",\"chinese\":\"??????\",\"code\":\"20\",\"english\":\"Egypt\",\"spell\":\"aiji\"},{\"abbr\":\"SV\",\"chinese\":\"????????????\",\"code\":\"503\",\"english\":\"EI Salvador\",\"spell\":\"saerwaduo\"},{\"abbr\":\"GQ\",\"chinese\":\"???????????????\",\"code\":\"240\",\"english\":\"Equatorial Guinea\",\"spell\":\"chidaojineiya\"},{\"abbr\":\"ER\",\"chinese\":\"???????????????\",\"code\":\"291\",\"english\":\"Eritrea\",\"spell\":\"eliteliya\"},{\"abbr\":\"EE\",\"chinese\":\"????????????\",\"code\":\"372\",\"english\":\"Estonia\",\"spell\":\"aishaniya\"},{\"abbr\":\"ET\",\"chinese\":\"???????????????\",\"code\":\"251\",\"english\":\"Ethiopia\",\"spell\":\"aisaiebiya\"},{\"abbr\":\"FJ\",\"chinese\":\"??????\",\"code\":\"679\",\"english\":\"Fiji\",\"spell\":\"feiji\"},{\"abbr\":\"FI\",\"chinese\":\"??????\",\"code\":\"358\",\"english\":\"Finland\",\"spell\":\"fenlan\"},{\"abbr\":\"FR\",\"chinese\":\"??????\",\"code\":\"33\",\"english\":\"France\",\"spell\":\"faguo\"},{\"abbr\":\"GA\",\"chinese\":\"??????\",\"code\":\"241\",\"english\":\"Gabon\",\"spell\":\"jiapeng\"},{\"abbr\":\"GM\",\"chinese\":\"?????????\",\"code\":\"220\",\"english\":\"Gambia\",\"spell\":\"gangbiya\"},{\"abbr\":\"GE\",\"chinese\":\"????????????\",\"code\":\"995\",\"english\":\"Georgia\",\"spell\":\"gelujiya\"},{\"abbr\":\"DE\",\"chinese\":\"??????\",\"code\":\"49\",\"english\":\"Germany\",\"spell\":\"deguo\"},{\"abbr\":\"GH\",\"chinese\":\"??????\",\"code\":\"233\",\"english\":\"Ghana\",\"spell\":\"jiana\"},{\"abbr\":\"GR\",\"chinese\":\"??????\",\"code\":\"30\",\"english\":\"Greece\",\"spell\":\"xila\"},{\"abbr\":\"GL\",\"chinese\":\"?????????\",\"code\":\"299\",\"english\":\"Greenland\",\"spell\":\"gelinglan\"},{\"abbr\":\"GT\",\"chinese\":\"????????????\",\"code\":\"502\",\"english\":\"Guatemala\",\"spell\":\"weidimala\"},{\"abbr\":\"GN\",\"chinese\":\"?????????\",\"code\":\"224\",\"english\":\"Guinea\",\"spell\":\"jineiya\"},{\"abbr\":\"GY\",\"chinese\":\"?????????\",\"code\":\"592\",\"english\":\"Guyana\",\"spell\":\"guiyanei\"},{\"abbr\":\"HT\",\"chinese\":\"??????\",\"code\":\"509\",\"english\":\"Haiti\",\"spell\":\"haidi\"},{\"abbr\":\"HN\",\"chinese\":\"????????????\",\"code\":\"504\",\"english\":\"Honduras\",\"spell\":\"hongdoulasi\"},{\"abbr\":\"HK\",\"chinese\":\"???????????????????????????\",\"code\":\"852\",\"english\":\"Hongkong SAR China\",\"spell\":\"zhongguoxianggangtebiexingzhengqu\"},{\"abbr\":\"HU\",\"chinese\":\"?????????\",\"code\":\"36\",\"english\":\"Hungary\",\"spell\":\"xiongyali\"},{\"abbr\":\"IS\",\"chinese\":\"??????\",\"code\":\"354\",\"english\":\"Iceland\",\"spell\":\"bingdao\"},{\"abbr\":\"IN\",\"chinese\":\"??????\",\"code\":\"91\",\"english\":\"India\",\"spell\":\"yindu\"},{\"abbr\":\"ID\",\"chinese\":\"???????????????\",\"code\":\"62\",\"english\":\"Indonesia\",\"spell\":\"yindunixiya\"},{\"abbr\":\"IR\",\"chinese\":\"??????\",\"code\":\"98\",\"english\":\"Iran\",\"spell\":\"yilang\"},{\"abbr\":\"IQ\",\"chinese\":\"?????????\",\"code\":\"964\",\"english\":\"Iraq\",\"spell\":\"yilake\"},{\"abbr\":\"IE\",\"chinese\":\"?????????\",\"code\":\"353\",\"english\":\"Ireland\",\"spell\":\"aierlan\"},{\"abbr\":\"IM\",\"chinese\":\"?????????\",\"code\":\"44\",\"english\":\"Isle of Man\",\"spell\":\"maendao\"},{\"abbr\":\"IL\",\"chinese\":\"?????????\",\"code\":\"972\",\"english\":\"Israel\",\"spell\":\"yiselie\"},{\"abbr\":\"IT\",\"chinese\":\"?????????\",\"code\":\"39\",\"english\":\"Italy\",\"spell\":\"yidali\"},{\"abbr\":\"CI\",\"chinese\":\"????????????\",\"code\":\"225\",\"english\":\"Ivory Coast\",\"spell\":\"ketediwa\"},{\"abbr\":\"JM\",\"chinese\":\"?????????\",\"code\":\"1876\",\"english\":\"Jamaica\",\"spell\":\"yamaijia\"},{\"abbr\":\"JP\",\"chinese\":\"??????\",\"code\":\"81\",\"english\":\"Japan\",\"spell\":\"riben\"},{\"abbr\":\"JO\",\"chinese\":\"??????\",\"code\":\"962\",\"english\":\"Jordan\",\"spell\":\"yuedan\"},{\"abbr\":\"KZ\",\"chinese\":\"???????????????\",\"code\":\"7\",\"english\":\"Kazakstan\",\"spell\":\"hasakesitan\"},{\"abbr\":\"KE\",\"chinese\":\"?????????\",\"code\":\"254\",\"english\":\"Kenya\",\"spell\":\"kenniya\"},{\"abbr\":\"KR\",\"chinese\":\"??????\",\"code\":\"82\",\"english\":\"Korea\",\"spell\":\"hanguo\"},{\"abbr\":\"KW\",\"chinese\":\"?????????\",\"code\":\"965\",\"english\":\"Kuwait\",\"spell\":\"keweite\"},{\"abbr\":\"KG\",\"chinese\":\"??????????????????\",\"code\":\"996\",\"english\":\"Kyrgyzstan\",\"spell\":\"jierjisisitan\"},{\"abbr\":\"LA\",\"chinese\":\"??????\",\"code\":\"856\",\"english\":\"Laos\",\"spell\":\"laowo\"},{\"abbr\":\"LV\",\"chinese\":\"????????????\",\"code\":\"371\",\"english\":\"Latvia\",\"spell\":\"latuoweiya\"},{\"abbr\":\"LB\",\"chinese\":\"?????????\",\"code\":\"961\",\"english\":\"Lebanon\",\"spell\":\"libanen\"},{\"abbr\":\"LS\",\"chinese\":\"?????????\",\"code\":\"266\",\"english\":\"Lesotho\",\"spell\":\"laisuotuo\"},{\"abbr\":\"LR\",\"chinese\":\"????????????\",\"code\":\"231\",\"english\":\"Liberia\",\"spell\":\"libiliya\"},{\"abbr\":\"LY\",\"chinese\":\"?????????\",\"code\":\"218\",\"english\":\"Libya\",\"spell\":\"libiya\"},{\"abbr\":\"LT\",\"chinese\":\"?????????\",\"code\":\"370\",\"english\":\"Lithuania\",\"spell\":\"litaowan\"},{\"abbr\":\"LU\",\"chinese\":\"?????????\",\"code\":\"352\",\"english\":\"Luxembourg\",\"spell\":\"lusenbao\"},{\"abbr\":\"MO\",\"chinese\":\"???????????????????????????\",\"code\":\"853\",\"english\":\"Macao SAR China\",\"spell\":\"zhongguoaomentebiexingzhengqu\"},{\"abbr\":\"MK\",\"chinese\":\"?????????\",\"code\":\"389\",\"english\":\"Macedonia\",\"spell\":\"maqidun\"},{\"abbr\":\"MG\",\"chinese\":\"???????????????\",\"code\":\"261\",\"english\":\"Madagascar\",\"spell\":\"madajiasijia\"},{\"abbr\":\"MW\",\"chinese\":\"?????????\",\"code\":\"265\",\"english\":\"Malawi\",\"spell\":\"malawei\"},{\"abbr\":\"MY\",\"chinese\":\"????????????\",\"code\":\"60\",\"english\":\"Malaysia\",\"spell\":\"malaixiya\"},{\"abbr\":\"MV\",\"chinese\":\"????????????\",\"code\":\"960\",\"english\":\"Maldives\",\"spell\":\"maerdaifu\"},{\"abbr\":\"ML\",\"chinese\":\"??????\",\"code\":\"223\",\"english\":\"Mali\",\"spell\":\"mali\"},{\"abbr\":\"MT\",\"chinese\":\"?????????\",\"code\":\"356\",\"english\":\"Malta\",\"spell\":\"maerta\"},{\"abbr\":\"MR\",\"chinese\":\"???????????????\",\"code\":\"222\",\"english\":\"Mauritania\",\"spell\":\"maolitaniya\"},{\"abbr\":\"MU\",\"chinese\":\"????????????\",\"code\":\"230\",\"english\":\"Mauritius\",\"spell\":\"maoliqiusi\"},{\"abbr\":\"MX\",\"chinese\":\"?????????\",\"code\":\"52\",\"english\":\"Mexico\",\"spell\":\"moxige\"},{\"abbr\":\"MD\",\"chinese\":\"????????????\",\"code\":\"373\",\"english\":\"Moldova\",\"spell\":\"moerduowa\"},{\"abbr\":\"MC\",\"chinese\":\"?????????\",\"code\":\"377\",\"english\":\"Monaco\",\"spell\":\"monage\"},{\"abbr\":\"MN\",\"chinese\":\"??????\",\"code\":\"976\",\"english\":\"Mongolia\",\"spell\":\"menggu\"},{\"abbr\":\"ME\",\"chinese\":\"???????????????\",\"code\":\"382\",\"english\":\"Montenegro\",\"spell\":\"heishangongheguo\"},{\"abbr\":\"MA\",\"chinese\":\"?????????\",\"code\":\"212\",\"english\":\"Morocco\",\"spell\":\"moluoge\"},{\"abbr\":\"MZ\",\"chinese\":\"????????????\",\"code\":\"258\",\"english\":\"Mozambique\",\"spell\":\"mosangbike\"},{\"abbr\":\"MM\",\"chinese\":\"??????\",\"code\":\"95\",\"english\":\"Myanmar(Burma)\",\"spell\":\"miandian\"},{\"abbr\":\"NA\",\"chinese\":\"????????????\",\"code\":\"264\",\"english\":\"Namibia\",\"spell\":\"namibiya\"},{\"abbr\":\"NP\",\"chinese\":\"?????????\",\"code\":\"977\",\"english\":\"Nepal\",\"spell\":\"niboer\"},{\"abbr\":\"NL\",\"chinese\":\"??????\",\"code\":\"31\",\"english\":\"Netherlands\",\"spell\":\"helan\"},{\"abbr\":\"NZ\",\"chinese\":\"?????????\",\"code\":\"64\",\"english\":\"New Zealand\",\"spell\":\"xinxilan\"},{\"abbr\":\"NI\",\"chinese\":\"????????????\",\"code\":\"505\",\"english\":\"Nicaragua\",\"spell\":\"nijialagua\"},{\"abbr\":\"NE\",\"chinese\":\"?????????\",\"code\":\"227\",\"english\":\"Niger\",\"spell\":\"nirier\"},{\"abbr\":\"NG\",\"chinese\":\"????????????\",\"code\":\"234\",\"english\":\"Nigeria\",\"spell\":\"niriliya\"},{\"abbr\":\"KP\",\"chinese\":\"??????\",\"code\":\"850\",\"english\":\"North Korea\",\"spell\":\"chaoxian\"},{\"abbr\":\"NO\",\"chinese\":\"??????\",\"code\":\"47\",\"english\":\"Norway\",\"spell\":\"nuowei\"},{\"abbr\":\"OM\",\"chinese\":\"??????\",\"code\":\"968\",\"english\":\"Oman\",\"spell\":\"aman\"},{\"abbr\":\"PK\",\"chinese\":\"????????????\",\"code\":\"92\",\"english\":\"Pakistan\",\"spell\":\"bajisitan\"},{\"abbr\":\"PA\",\"chinese\":\"?????????\",\"code\":\"507\",\"english\":\"Panama\",\"spell\":\"banama\"},{\"abbr\":\"PY\",\"chinese\":\"?????????\",\"code\":\"595\",\"english\":\"Paraguay\",\"spell\":\"balagui\"},{\"abbr\":\"PE\",\"chinese\":\"??????\",\"code\":\"51\",\"english\":\"Peru\",\"spell\":\"milu\"},{\"abbr\":\"PH\",\"chinese\":\"?????????\",\"code\":\"63\",\"english\":\"Philippines\",\"spell\":\"feilvbin\"},{\"abbr\":\"PL\",\"chinese\":\"??????\",\"code\":\"48\",\"english\":\"Poland\",\"spell\":\"bolan\"},{\"abbr\":\"PT\",\"chinese\":\"?????????\",\"code\":\"351\",\"english\":\"Portugal\",\"spell\":\"putaoya\"},{\"abbr\":\"PR\",\"chinese\":\"????????????\",\"code\":\"1787\",\"english\":\"Puerto Rico\",\"spell\":\"boduolige\"},{\"abbr\":\"QA\",\"chinese\":\"?????????\",\"code\":\"974\",\"english\":\"Qatar\",\"spell\":\"kataer\"},{\"abbr\":\"RE\",\"chinese\":\"?????????\",\"code\":\"262\",\"english\":\"Reunion\",\"spell\":\"liuniwang\"},{\"abbr\":\"RO\",\"chinese\":\"????????????\",\"code\":\"40\",\"english\":\"Romania\",\"spell\":\"luomaniya\"},{\"abbr\":\"RU\",\"chinese\":\"?????????\",\"code\":\"7\",\"english\":\"Russia\",\"spell\":\"eluosi\"},{\"abbr\":\"RW\",\"chinese\":\"?????????\",\"code\":\"250\",\"english\":\"Rwanda\",\"spell\":\"luwangda\"},{\"abbr\":\"SM\",\"chinese\":\"????????????\",\"code\":\"378\",\"english\":\"San Marino\",\"spell\":\"shengmalinuo\"},{\"abbr\":\"SA\",\"chinese\":\"???????????????\",\"code\":\"966\",\"english\":\"Saudi Arabia\",\"spell\":\"shatealabo\"},{\"abbr\":\"SN\",\"chinese\":\"????????????\",\"code\":\"221\",\"english\":\"Senegal\",\"spell\":\"saineijiaer\"},{\"abbr\":\"RS\",\"chinese\":\"????????????\",\"code\":\"381\",\"english\":\"Serbia\",\"spell\":\"saierweiya\"},{\"abbr\":\"SL\",\"chinese\":\"????????????\",\"code\":\"232\",\"english\":\"Sierra Leone\",\"spell\":\"sailaliang\"},{\"abbr\":\"SG\",\"chinese\":\"?????????\",\"code\":\"65\",\"english\":\"Singapore\",\"spell\":\"xinjiapo\"},{\"abbr\":\"SK\",\"chinese\":\"????????????\",\"code\":\"421\",\"english\":\"Slovakia\",\"spell\":\"siluofake\"},{\"abbr\":\"SI\",\"chinese\":\"???????????????\",\"code\":\"386\",\"english\":\"Slovenia\",\"spell\":\"siluowenniya\"},{\"abbr\":\"SO\",\"chinese\":\"?????????\",\"code\":\"252\",\"english\":\"Somalia\",\"spell\":\"suomali\"},{\"abbr\":\"ZA\",\"chinese\":\"??????\",\"code\":\"27\",\"english\":\"South Africa\",\"spell\":\"nanfei\"},{\"abbr\":\"ES\",\"chinese\":\"?????????\",\"code\":\"34\",\"english\":\"Spain\",\"spell\":\"xibanya\"},{\"abbr\":\"LK\",\"chinese\":\"????????????\",\"code\":\"94\",\"english\":\"Sri Lanka\",\"spell\":\"sililanka\"},{\"abbr\":\"SD\",\"chinese\":\"??????\",\"code\":\"249\",\"english\":\"Sudan\",\"spell\":\"sudan\"},{\"abbr\":\"SR\",\"chinese\":\"?????????\",\"code\":\"597\",\"english\":\"Suriname\",\"spell\":\"sulinan\"},{\"abbr\":\"SZ\",\"chinese\":\"????????????\",\"code\":\"268\",\"english\":\"Swaziland\",\"spell\":\"siweishilan\"},{\"abbr\":\"SE\",\"chinese\":\"??????\",\"code\":\"46\",\"english\":\"Sweden\",\"spell\":\"ruidian\"},{\"abbr\":\"CH\",\"chinese\":\"??????\",\"code\":\"41\",\"english\":\"Switzerland\",\"spell\":\"ruishi\"},{\"abbr\":\"SY\",\"chinese\":\"?????????\",\"code\":\"963\",\"english\":\"Syria\",\"spell\":\"xuliya\"},{\"abbr\":\"TW\",\"chinese\":\"????????????\",\"code\":\"886\",\"english\":\"Taiwan\",\"spell\":\"zhongguotaiwan\"},{\"abbr\":\"TJ\",\"chinese\":\"???????????????\",\"code\":\"992\",\"english\":\"Tajikstan\",\"spell\":\"tajikesitan\"},{\"abbr\":\"TZ\",\"chinese\":\"????????????\",\"code\":\"255\",\"english\":\"Tanzania\",\"spell\":\"tansangniya\"},{\"abbr\":\"TH\",\"chinese\":\"??????\",\"code\":\"66\",\"english\":\"Thailand\",\"spell\":\"taiguo\"},{\"abbr\":\"TG\",\"chinese\":\"??????\",\"code\":\"228\",\"english\":\"Togo\",\"spell\":\"duoge\"},{\"abbr\":\"TO\",\"chinese\":\"??????\",\"code\":\"676\",\"english\":\"Tonga\",\"spell\":\"tangjia\"},{\"abbr\":\"TT\",\"chinese\":\"????????????????????????\",\"code\":\"1868\",\"english\":\"Trinidad and Tobago\",\"spell\":\"telinidaheduobage\"},{\"abbr\":\"TN\",\"chinese\":\"?????????\",\"code\":\"216\",\"english\":\"Tunisia\",\"spell\":\"tunisi\"},{\"abbr\":\"TR\",\"chinese\":\"?????????\",\"code\":\"90\",\"english\":\"Turkey\",\"spell\":\"tuerqi\"},{\"abbr\":\"TM\",\"chinese\":\"???????????????\",\"code\":\"993\",\"english\":\"Turkmenistan\",\"spell\":\"tukumansitan\"},{\"abbr\":\"VI\",\"chinese\":\"?????????????????????\",\"code\":\"1340\",\"english\":\"U.S. Virgin Islands\",\"spell\":\"meishuweierjingqundao\"},{\"abbr\":\"UG\",\"chinese\":\"?????????\",\"code\":\"256\",\"english\":\"Uganda\",\"spell\":\"wuganda\"},{\"abbr\":\"UA\",\"chinese\":\"?????????\",\"code\":\"380\",\"english\":\"Ukraine\",\"spell\":\"wukelan\"},{\"abbr\":\"AE\",\"chinese\":\"????????????????????????\",\"code\":\"971\",\"english\":\"United Arab Emirates\",\"spell\":\"alabolianheqiuzhangguo\"},{\"abbr\":\"GB\",\"chinese\":\"??????\",\"code\":\"44\",\"english\":\"United Kiongdom\",\"spell\":\"yingguo\"},{\"abbr\":\"US\",\"chinese\":\"??????\",\"code\":\"1\",\"english\":\"USA\",\"spell\":\"meiguo\"},{\"abbr\":\"UY\",\"chinese\":\"?????????\",\"code\":\"598\",\"english\":\"Uruguay\",\"spell\":\"wulagui\"},{\"abbr\":\"UZ\",\"chinese\":\"??????????????????\",\"code\":\"998\",\"english\":\"Uzbekistan\",\"spell\":\"wuzibiekesitan\"},{\"abbr\":\"VA\",\"chinese\":\"????????????\",\"code\":\"379\",\"english\":\"Vatican City\",\"spell\":\"fandigangcheng\"},{\"abbr\":\"VE\",\"chinese\":\"????????????\",\"code\":\"58\",\"english\":\"Venezuela\",\"spell\":\"weineiruila\"},{\"abbr\":\"VN\",\"chinese\":\"??????\",\"code\":\"84\",\"english\":\"Vietnam\",\"spell\":\"yuenan\"},{\"abbr\":\"YE\",\"chinese\":\"??????\",\"code\":\"967\",\"english\":\"Yemen\",\"spell\":\"yemen\"},{\"abbr\":\"YU\",\"chinese\":\"????????????\",\"code\":\"381\",\"english\":\"Yugoslavia\",\"spell\":\"nansilafu\"},{\"abbr\":\"ZR\",\"chinese\":\"?????????\",\"code\":\"243\",\"english\":\"Zaire\",\"spell\":\"zhayier\"},{\"abbr\":\"ZM\",\"chinese\":\"?????????\",\"code\":\"260\",\"english\":\"Zambia\",\"spell\":\"zanbiya\"},{\"abbr\":\"ZW\",\"chinese\":\"????????????\",\"code\":\"263\",\"english\":\"Zimbabwe\",\"spell\":\"jinbabuwei\"}]";
        return (ArrayList<CountryBean>) JSON.parseArray(countryData, CountryBean.class);
    }
}
