package com.razahamid.medopd.ExtraFiles;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;

import com.razahamid.medopd.R;
import com.wang.avi.AVLoadingIndicatorView;

/**
 * Created by dev on 2019/8/1.
 */

public class LoadingDialog extends Dialog{

    private AVLoadingIndicatorView loadingview;
    private TextView UploadedData;
    public LoadingDialog(@NonNull Context context) {
        super(context);
    }

    public LoadingDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    protected LoadingDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
        loadingview = findViewById(R.id.dialog_loading);
        UploadedData=findViewById(R.id.UploadedData);
    }
    public void SetProgress(String progress){
        UploadedData.setText(progress);
    }

//    @Override
//    public void show() {
//        super.show();
//        loadingview.show();
//    }
//
//    @Override
//    public void dismiss() {
//        super.dismiss();
//        loadingview.hide();
//    }
}
