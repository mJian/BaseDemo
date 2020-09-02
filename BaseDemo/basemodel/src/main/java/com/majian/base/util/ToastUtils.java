package com.majian.base.util;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by Administrator on 2015/12/21.
 */
public class ToastUtils {
    private static Toast toast;

    public static void showSnackbar(Activity context, String msg) {
//        Activity activity = (Activity) context;
//        final Snackbar snackbar = Snackbar.make(activity.findViewById(android.R.id.content), msg, Snackbar.LENGTH_SHORT);
//        snackbar.setAction("确定", new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                snackbar.dismiss();
//            }
//        });
//        Snackbar.SnackbarLayout ve = (Snackbar.SnackbarLayout)snackbar.getView();
//        ViewGroup.LayoutParams vl = ve.getLayoutParams();
//        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(vl.width,vl.height);
//        ll.gravity = Gravity.CENTER;
//        ve.setLayoutParams(ll);
//        snackbar.show();
        showToast(context, msg);
    }

    public static void showSnackbar(Activity context, int resId) {
        showSnackbar(context, context.getResources().getString(resId));
    }

    public static void showToast(Context context, String msg) {
        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
//        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void showToastLong(Context context, String msg) {
        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        } else {
            toast.setText(msg);
        }
//        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void showToast(Context context, int resId) {
        showToast(context, context.getResources().getString(resId));
    }
}
