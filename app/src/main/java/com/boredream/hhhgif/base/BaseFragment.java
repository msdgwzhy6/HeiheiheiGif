package com.boredream.hhhgif.base;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.boredream.bdcodehelper.utils.ToastUtils;
import com.boredream.hhhgif.activity.MainActivity;

public abstract class BaseFragment extends Fragment {

    protected String TAG;
    protected MainActivity activity;
    // progressDialog/sp/application 等使用activity.xx 调用

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TAG = getClass().getSimpleName();

        activity = (MainActivity) getActivity();
    }

    protected void intent2Activity(Class<? extends Activity> tarActivity) {
        Intent intent = new Intent(activity, tarActivity);
        startActivity(intent);
    }

    protected void showToast(String msg) {
        ToastUtils.showToast(activity, msg, Toast.LENGTH_SHORT);
    }

    protected void showLog(String msg) {
        Log.i(TAG, msg);
    }

    protected void showProgressDialog() {
        activity.showProgressDialog();
    }

    protected void dismissProgressDialog() {
        activity.dismissProgressDialog();
    }
}
