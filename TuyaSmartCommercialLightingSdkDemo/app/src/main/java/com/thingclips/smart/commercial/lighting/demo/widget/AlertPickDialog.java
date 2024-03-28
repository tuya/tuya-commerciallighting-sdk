package com.thingclips.smart.commercial.lighting.demo.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.thingclips.smart.commercial.lighting.demo.bean.AlertPickBean;
import com.thingclips.smart.android.demo.R;


public class AlertPickDialog {

    private static final String TAG = "ChooseDialog";

    public static void showAlertPickDialog(Activity activity, final AlertPickBean alertPickBean, final AlertPickCallBack alertPickCallBack) {
        final AlertDialog dialog = new AlertDialog.Builder(activity, R.style.dialog_alert).create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        Window window = dialog.getWindow();
        window.setContentView(R.layout.dialog_action_item);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.dialog_style);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        window.getDecorView().setPadding(0, 0, 0, 0);
        final NumberPicker numberPicker = (NumberPicker) dialog.findViewById(R.id.np_choose);
        TextView sureTV = (TextView) dialog.findViewById(R.id.tv_sure);
        TextView cancelTV = (TextView) dialog.findViewById(R.id.tv_cancel);
        TextView titleTV = (TextView) dialog.findViewById(R.id.tv_title);
        sureTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertPickCallBack.confirm(
                        alertPickBean.getRangeKeys().get(numberPicker.getValue()));
                dialog.cancel();
            }
        });
        cancelTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertPickCallBack.cancel();
                dialog.cancel();
            }
        });
        sureTV.setText(alertPickBean.getConfirmText());
        cancelTV.setText(alertPickBean.getCancelText());
        titleTV.setText(alertPickBean.getTitle());
        initData(numberPicker, alertPickBean);
    }

    private static void initData(NumberPicker numberPicker, AlertPickBean alertPickBean) {
        String[] value = new String[alertPickBean.getRangeValues().size()];
        value = alertPickBean.getRangeValues().toArray(value);
        numberPicker.setDisplayedValues(value);
        numberPicker.setMaxValue(value.length - 1);
        numberPicker.setValue(alertPickBean.getSelected());
        numberPicker.setWrapSelectorWheel(alertPickBean.isLoop());
    }

    public interface AlertPickCallBack {
        void confirm(String value);

        void cancel();
    }
}
