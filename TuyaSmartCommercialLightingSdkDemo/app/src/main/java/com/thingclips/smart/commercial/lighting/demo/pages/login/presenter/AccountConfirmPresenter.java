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

package com.thingclips.smart.commercial.lighting.demo.pages.login.presenter;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;

import com.thingclips.smart.commercial.lighting.demo.app.Constant;
import com.thingclips.smart.commercial.lighting.demo.utils.ActivityUtils;
import com.thingclips.smart.commercial.lighting.demo.utils.DialogUtil;
import com.thingclips.smart.commercial.lighting.demo.utils.LoginHelper;
import com.thingclips.smart.commercial.lighting.demo.utils.MessageUtil;
import com.thingclips.smart.android.demo.R;
import com.thingclips.smart.android.mvp.bean.Result;
import com.thingclips.smart.android.mvp.presenter.BasePresenter;
import com.thingclips.smart.android.user.bean.ThingMerchantBean;
import com.thingclips.smart.android.user.bean.ThingUserFindPwdReqBean;
import com.thingclips.smart.android.user.bean.ThingUserLoginWithCodeReqBean;
import com.thingclips.smart.android.user.bean.ThingUserLoginWithPwdReqBean;
import com.thingclips.smart.android.user.bean.ThingUserQueryMerchantWithCodeReqBean;
import com.thingclips.smart.android.user.bean.ThingUserRegisterReqBean;
import com.thingclips.smart.android.user.bean.ThingUserSendCodeReqBean;
import com.thingclips.smart.android.user.bean.ThingUserSimpleBean;
import com.thingclips.smart.android.user.bean.User;
import com.thingclips.smart.android.user.callback.IThingUserLoginCallback;
import com.thingclips.smart.android.user.callback.IThingUserResultCallback;
import com.thingclips.smart.commercial.lighting.demo.pages.login.view.AccountConfirmActivity;
import com.thingclips.smart.commercial.lighting.demo.pages.login.view.IAccountConfirmView;
import com.thingclips.sdk.os.ThingOSUser;
import com.thingclips.smart.sdk.api.IResultCallback;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.thingclips.smart.commercial.lighting.demo.pages.login.view.AccountConfirmActivity.MODE_REGISTER;

public class AccountConfirmPresenter extends BasePresenter {
    private static final String TAG = "AccountConfirmPresenter";

    public static final int MSG_SEND_VALIDATE_CODE_SUCCESS = 12;
    public static final int MSG_SEND_VALIDATE_CODE_ERROR = 13;
    public static final int MSG_RESET_PASSWORD_SUCC = 14;
    public static final int MSG_RESET_PASSWORD_FAIL = 15;
    public static final int MSG_REGISTER_SUCC = 16;
    public static final int MSG_REGISTER_FAIL = 17;
    public static final int MSG_LOGIN_FAIL = 18;

    private static final int GET_VALIDATE_CODE_PERIOD = 60 * 1000;

    private IAccountConfirmView mView;


    private String mCountryCode;
    private String mPhoneNum;
    private String mEmail;

    protected boolean mSend;
    private CountDownTimer mCountDownTimer;
    private Activity mContext;

    private IResultCallback mIValidateCallback = new IResultCallback() {
        @Override
        public void onSuccess() {
            mHandler.sendEmptyMessage(MSG_SEND_VALIDATE_CODE_SUCCESS);
        }

        @Override
        public void onError(String s, String s1) {
            getValidateCodeFail(s, s1);
        }
    };

    private IResultCallback mIResetPasswordCallback = new IResultCallback() {
        @Override
        public void onSuccess() {
            mHandler.sendEmptyMessage(MSG_RESET_PASSWORD_SUCC);
        }

        @Override
        public void onError(String errorCode, String errorMsg) {
            Message msg = MessageUtil.getCallFailMessage(MSG_RESET_PASSWORD_FAIL, errorCode, errorMsg);
            mHandler.sendMessage(msg);
        }
    };

    private final IThingUserResultCallback<ThingUserSimpleBean> mIRegisterCallback = new IThingUserResultCallback<ThingUserSimpleBean>() {
        @Override
        public void onSuccess(ThingUserSimpleBean user) {
            mHandler.sendEmptyMessage(MSG_REGISTER_SUCC);
        }

        @Override
        public void onError(String errorCode, String errorMsg) {
            Message msg = MessageUtil.getCallFailMessage(MSG_REGISTER_FAIL, errorCode, errorMsg);
            mHandler.sendMessage(msg);
        }
    };

    private final IThingUserLoginCallback<User> mILoginCallback = new IThingUserLoginCallback<User>() {
        @Override
        public void onPreLogin(List<ThingMerchantBean> list) {
            //Do nothing because of unique merchant code
        }

        @Override
        public void onSuccess(User user) {
            loginSuccess();
        }

        public void onError(String errorCode, String errorMsg) {
            Message msg = MessageUtil.getCallFailMessage(MSG_LOGIN_FAIL, errorCode, errorMsg);
            mHandler.sendMessage(msg);
        }
    };


    public AccountConfirmPresenter(Activity activity, IAccountConfirmView validateCodeView) {
        super(activity);
        mView = validateCodeView;
        mContext = activity;
        initData(activity);
        if (mView.getMode() == AccountConfirmActivity.MODE_FORGET_PASSWORD
                || (mView.getMode() == MODE_REGISTER && mView.getPlatform() == AccountConfirmActivity.PLATFORM_PHONE)) {
            getValidateCode();
        }
    }


    private void initData(Activity activity) {
        mCountryCode = activity.getIntent().getStringExtra(AccountConfirmActivity.EXTRA_COUNTRY_CODE);
        if (TextUtils.isEmpty(mCountryCode)) {
            mCountryCode = "86";
        }
        String account = activity.getIntent().getStringExtra(AccountConfirmActivity.EXTRA_ACCOUNT);
        String showAccount;
        if (mView.getPlatform() == AccountConfirmActivity.PLATFORM_PHONE) {
            mPhoneNum = account;
            showAccount = mCountryCode + "-" + mPhoneNum;
        } else {
            mEmail = account;
            showAccount = mEmail;
        }

        String tip;

        if (mView.getMode() == AccountConfirmActivity.MODE_CHANGE_PASSWORD) {
            tip = getTip(R.string.thing_current_bind_phone_tip, showAccount);
        } else {
            if (mView.getPlatform() == AccountConfirmActivity.PLATFORM_PHONE) {
                tip = getTip(R.string.code_has_send_to_phone, showAccount);
            } else {
                tip = getTip(R.string.code_has_send_to_email, showAccount);
            }
        }

        mView.setValidateTip(Html.fromHtml(tip));
    }

    private String getTip(int tipResId, String showAccount) {
        return "<font color=\"#626262\">" + mContext.getString(tipResId) + "</font>"
                + "<br><font color=\"#ff0000\">" + showAccount + "</font>";
    }

    public void getValidateCode() {
        mSend = true;
        buildCountDown();
        if (mView == null) {
            return;
        }
        switch (mView.getPlatform()) {
            case AccountConfirmActivity.PLATFORM_EMAIL:
                if (mView.getMode() == MODE_REGISTER) {
                    ThingUserSendCodeReqBean sendCodeReqBean = new ThingUserSendCodeReqBean();
                    sendCodeReqBean.setCodeType(1);
                    sendCodeReqBean.setUsername(mEmail);
                    sendCodeReqBean.setCountryCode(mCountryCode);
                    ThingOSUser.getUserInstance().getValidateCode(sendCodeReqBean, mIValidateCallback);

                } else {
                    ThingUserSendCodeReqBean sendCodeReqBean = new ThingUserSendCodeReqBean();
                    sendCodeReqBean.setCodeType(2);
                    sendCodeReqBean.setUsername(mEmail);
                    sendCodeReqBean.setCountryCode(mCountryCode);
                    ThingOSUser.getUserInstance().getValidateCode(sendCodeReqBean, mIValidateCallback);

                }
                break;

            case AccountConfirmActivity.PLATFORM_PHONE:
                if (mView.getMode() == MODE_REGISTER) {
                    ThingUserSendCodeReqBean sendCodeReqBean = new ThingUserSendCodeReqBean();
                    sendCodeReqBean.setCodeType(1);
                    sendCodeReqBean.setUsername(mPhoneNum);
                    sendCodeReqBean.setCountryCode(mCountryCode);
                    ThingOSUser.getUserInstance().getValidateCode(sendCodeReqBean, mIValidateCallback);

                } else {
                    ThingUserSendCodeReqBean sendCodeReqBean = new ThingUserSendCodeReqBean();
                    sendCodeReqBean.setCodeType(2);
                    sendCodeReqBean.setUsername(mPhoneNum);
                    sendCodeReqBean.setCountryCode(mCountryCode);
                    ThingOSUser.getUserInstance().getValidateCode(sendCodeReqBean, mIValidateCallback);

                }

                break;
        }

    }


    public void confirm() {
        if (mView.getPassword().length() < 6 || mView.getPassword().length() > 20) {
            DialogUtil.simpleSmartDialog(mContext, mContext.getString(R.string.thing_enter_keyword_tip), null);
            return;
        } else {
            final Pattern PASS_PATTERN = Pattern.compile("^[A-Za-z\\d!@#$%*&_\\-.,:;+=\\[\\]{}~()^]{6,20}$");
            Matcher matcher = PASS_PATTERN.matcher(mView.getPassword());
            if (!matcher.matches()) {
                DialogUtil.simpleSmartDialog(mContext, mContext.getString(R.string.thing_enter_keyword_tip), null);
                return;
            }
        }

        switch (mView.getMode()) {
            case AccountConfirmActivity.MODE_CHANGE_PASSWORD:
                queryMerchantCode();
                break;
            case AccountConfirmActivity.MODE_FORGET_PASSWORD:
                queryMerchantCode();
                break;

            case MODE_REGISTER:
                register();
                break;
        }
    }

    private void queryMerchantCode() {
        String username = "";
        switch (mView.getPlatform()) {
            case AccountConfirmActivity.PLATFORM_EMAIL:
                username = mEmail;
                break;

            case AccountConfirmActivity.PLATFORM_PHONE:
                username = mPhoneNum;
                break;
        }
        ThingUserQueryMerchantWithCodeReqBean merchantWithCodeReqBean = new ThingUserQueryMerchantWithCodeReqBean();
        merchantWithCodeReqBean.setUsername(username);
        merchantWithCodeReqBean.setCode(mView.getValidateCode());
        merchantWithCodeReqBean.setCountryCode(mCountryCode);
        String finalUsername = username;
        ThingOSUser.getUserInstance().queryMerchantList(merchantWithCodeReqBean, new IThingUserResultCallback<List<ThingMerchantBean>>() {
            @Override
            public void onSuccess(List<ThingMerchantBean> thingMerchantBeans) {
                if (thingMerchantBeans != null && thingMerchantBeans.size() > 0) {
                    resetPassword(finalUsername, thingMerchantBeans.get(0));
                } else {
                    Message msg = MessageUtil.getCallFailMessage(MSG_RESET_PASSWORD_FAIL, "error", "query merchant code error");
                    mHandler.sendMessage(msg);
                }
            }

            @Override
            public void onError(String s, String s1) {
                Message msg = MessageUtil.getCallFailMessage(MSG_RESET_PASSWORD_FAIL, s, s1);
                mHandler.sendMessage(msg);
            }
        });
    }

    private void resetPassword(String username, ThingMerchantBean thingMerchantBean) {
        ThingUserFindPwdReqBean findPwdReqBean = new ThingUserFindPwdReqBean();
        findPwdReqBean.setUsername(username);
        findPwdReqBean.setCode(mView.getValidateCode());
        findPwdReqBean.setNewPassword(mView.getPassword());
        findPwdReqBean.setCountryCode(mCountryCode);
        findPwdReqBean.setMerchantCode(thingMerchantBean.getMerchantCode());
        ThingOSUser.getUserInstance().findPassword(findPwdReqBean, mIResetPasswordCallback);
    }

    private void register() {
        String username = "";
        switch (mView.getPlatform()) {
            case AccountConfirmActivity.PLATFORM_EMAIL:
                username = mEmail;
                break;

            case AccountConfirmActivity.PLATFORM_PHONE:
                username = mPhoneNum;
                break;
        }
        ThingUserRegisterReqBean registerReqBean = new ThingUserRegisterReqBean();
        registerReqBean.setCountryCode(mCountryCode);
        registerReqBean.setUsername(username);
        registerReqBean.setCompanyName(mView.getCompanyName());
        registerReqBean.setPassword(mView.getPassword());
        registerReqBean.setCode(mView.getValidateCode());
        registerReqBean.setIndustryType("illumination");
        ThingOSUser.getUserInstance().register(registerReqBean, mIRegisterCallback);
    }

    private void loginWithPhoneCode() {
        ThingUserLoginWithCodeReqBean loginWithCodeReqBean = new ThingUserLoginWithCodeReqBean();
        loginWithCodeReqBean.setCode(mView.getValidateCode());
        loginWithCodeReqBean.setCountryCode(mCountryCode);
        loginWithCodeReqBean.setUsername(mPhoneNum);
        ThingOSUser.getUserInstance().loginWithVerifyCode(loginWithCodeReqBean, mILoginCallback);
    }

    private void loginWithPassword() {
        if (mView.getPlatform() == AccountConfirmActivity.PLATFORM_PHONE) {
            if (mView.getMode() == AccountConfirmActivity.MODE_FORGET_PASSWORD) {
                loginWithUserName(mCountryCode, mPhoneNum, mView.getPassword());
            } else {
                loginWithUserName(mCountryCode, mPhoneNum, mView.getPassword());
            }
        } else {
            loginWithUserName(mCountryCode, mEmail, mView.getPassword());
        }
    }

    private void loginWithUserName(String mCountryCode, String mPhoneNum, String password) {
        ThingUserLoginWithPwdReqBean loginWithCodeReqBean = new ThingUserLoginWithPwdReqBean();
        loginWithCodeReqBean.setCountryCode(mCountryCode);
        loginWithCodeReqBean.setUsername(mPhoneNum);
        loginWithCodeReqBean.setPassword(password);
        ThingOSUser.getUserInstance().loginWithPassword(loginWithCodeReqBean, mILoginCallback);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_SEND_VALIDATE_CODE_SUCCESS:
                mView.modelResult(msg.what, null);
                break;

            case MSG_SEND_VALIDATE_CODE_ERROR:
            case MSG_RESET_PASSWORD_FAIL:
            case MSG_LOGIN_FAIL:
                mView.modelResult(msg.what, (Result) msg.obj);
                break;

            case MSG_REGISTER_FAIL:
                Result result = (Result) msg.obj;
                if ("IS_EXISTS".equals(result.getErrorCode())) {
                    if (AccountConfirmActivity.PLATFORM_PHONE == mView.getPlatform()) {
                        DialogUtil.simpleConfirmDialog(mContext, mContext.getString(R.string.user_exists), mContext.getString(R.string.direct_login),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == DialogInterface.BUTTON_POSITIVE) {
                                            loginWithPhoneCode();
                                        } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                                            //todo
//                                            resetPassword();
                                        }
                                    }
                                });
                    } else {
                        mView.modelResult(msg.what, (Result) msg.obj);
                    }
                } else {
                    mView.modelResult(msg.what, (Result) msg.obj);
                }
                break;

            case MSG_RESET_PASSWORD_SUCC:
                if (mView.getMode() == AccountConfirmActivity.MODE_CHANGE_PASSWORD) {
                    DialogUtil.simpleSmartDialog(mContext, R.string.modify_password_success, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            LoginHelper.reLogin(mContext, false);
                        }
                    });

                } else {
                    loginWithPassword();
                }

                break;

            case MSG_REGISTER_SUCC:
                autoLogin();
                break;
        }
        return super.handleMessage(msg);
    }

    private void autoLogin() {
        loginWithPassword();
    }

    private void loginSuccess() {
        Constant.finishActivity();
        LoginHelper.afterLogin();
        ActivityUtils.gotoHomeActivity(mContext);
    }

    protected void getValidateCodeFail(String errorCode, String errorMsg) {
        Message msg = MessageUtil.getCallFailMessage(MSG_SEND_VALIDATE_CODE_ERROR, errorCode, errorMsg);
        mHandler.sendMessage(msg);
        mSend = false;
    }

    public boolean isSended() {
        return mSend;
    }

    private void buildCountDown() {
        mCountDownTimer = new Countdown(GET_VALIDATE_CODE_PERIOD, 1000);
        mCountDownTimer.start();
        mView.disableGetValidateCode();
    }

    private class Countdown extends CountDownTimer {

        public Countdown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            mView.setCountdown((int) (millisUntilFinished / 1000));
        }

        @Override
        public void onFinish() {
            mView.enableGetValidateCode();
            mSend = false;
            mView.checkValidateCode();
        }
    }
}
