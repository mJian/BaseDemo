package com.majian.base.mvp;

import android.content.Context;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public interface BaseView {
     Context getMContext();
     void showLoadingView();
     void hideLoadingView();
}
