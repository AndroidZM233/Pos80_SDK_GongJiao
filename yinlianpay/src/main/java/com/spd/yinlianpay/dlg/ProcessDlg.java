package com.spd.yinlianpay.dlg;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.spd.yinlianpay.R;


/**
 * Created by guoxiaomeng on 2017/6/23.
 */

public class ProcessDlg extends Dialog {

    private static ProcessDlg builder;
    private static ProgressBar processdlgbar;
    private static TextView textView,tvProgress,tvTimeOut;
    private static Button btnOK,btnCancel;

    public ProcessDlg(Context tmpcontext) {
        super(tmpcontext);
        LayoutInflater inflater = this.getLayoutInflater();
        View layout = inflater.inflate(R.layout.processlayout, null);
        textView = (TextView) layout.findViewById(R.id.title);
        tvProgress = (TextView) layout.findViewById(R.id.message);
        btnOK =  (Button) layout.findViewById(R.id.btnlogin);
        tvTimeOut = (TextView)layout.findViewById(R.id.tv_timeout);
        btnCancel = (Button) layout.findViewById(R.id.btncancel);
        processdlgbar = (ProgressBar)layout.findViewById(R.id.processdlgbar);
        //textView.setVisibility(View.GONE);
        FrameLayout.LayoutParams dlgparams = new FrameLayout.LayoutParams
                (FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setCanceledOnTouchOutside(false);
        this.setContentView(layout,dlgparams);
    }
    public static void showTvProgress(int timeOut) {
        if(builder!=null)
            if(builder.isShowing()) {
                tvTimeOut.setVisibility(View.VISIBLE);
                tvTimeOut.setText(""+timeOut);
                builder.show();
            }
            if(timeOut<=0)
                tvTimeOut.setVisibility(View.GONE);
    }
    public static void ShowProcess(Context context, String title, String tipsinfo) {

        Log.i("info", "ShowProcess: "+builder+tipsinfo);
        if(builder==null) {
            builder = new ProcessDlg(context);
        }
        btnOK.setVisibility(View.GONE);
        textView.setText(title);
        tvProgress.setText(tipsinfo);

        try {
            builder.show();
        }catch (Exception e)
        {
            builder = null;
            Log.e("", "CloseDlg: ", e);
        }
    }


    public static void ShowProcessRet(Context context, String title, String tipsinfo) {

        LayoutInflater inflater = builder.getLayoutInflater();
        View layout = inflater.inflate(R.layout.processlayout,null);
        textView = (TextView)layout.findViewById(R.id.title);
        textView.setText(title);
        //textView.setVisibility(View.GONE);
        btnOK =  (Button) layout.findViewById(R.id.btnlogin);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(builder!=null)
                {
                    builder.dismiss();
                }
            }
        });
        ((TextView)layout.findViewById(R.id.message)).setText(tipsinfo);
        FrameLayout.LayoutParams dlgparams = new FrameLayout.LayoutParams
                (FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT);
//        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.setCanceledOnTouchOutside(false);
        builder.setContentView(layout,dlgparams);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(builder!=null)
                {
                    builder.dismiss();
                }
            }
        });


    }
    public static void ShowBtnOK(Context context, String title, String tipsinfo) {
        if (builder == null){
            builder = new ProcessDlg(context);

    }
       ShowBtnOK(context,title,tipsinfo,null);

    }
    public static void ShowBtnOK(Context context, String title, String tipsinfo, final DLGOnClickListener clickListener) {
        if (builder == null){
            builder = new ProcessDlg(context);
        }
        tvTimeOut.setVisibility(View.GONE);
        tvProgress.setText(tipsinfo);
        btnOK.setVisibility(View.VISIBLE);
        if(clickListener!=null) {
            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onClickListener(builder, v);
                }
            });

        }
        else {
            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(builder!=null)
                    {
                        builder.dismiss();
                    }
                }
            });
        }
        textView.setText(title);
        builder.show();

    }

    public static void ShowBtnOKClose(Context context, String title, String tipsinfo, final DLGOnClickListener clickListener, final DLGOnClickListener cancelListener) {
        if (builder == null){
            builder = new ProcessDlg(context);
        }
        btnCancel.setVisibility(View.VISIBLE);
        tvTimeOut.setVisibility(View.GONE);
        tvProgress.setText(tipsinfo);
        processdlgbar.setVisibility(View.GONE);
        btnOK.setVisibility(View.VISIBLE);
        if(cancelListener!=null)
        {
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelListener.onClickListener(builder, v);
                }
            });
        }
        if(clickListener!=null) {
            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onClickListener(builder, v);
                }
            });

        }
        else {
            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(builder!=null)
                    {
                        builder.dismiss();
                    }
                }
            });
        }
        textView.setText(title);
        builder.show();

    }

    public static void CloseDlg() {
        if(builder!=null)
        {
            try {
                builder.dismiss();
                tvTimeOut.setVisibility(View.GONE);
            }catch (Exception e)
            {
                builder = null;
                Log.e("", "CloseDlg: ", e);
            }

        }
        builder = null;
    }

    public interface  DLGOnClickListener {
        void  onClickListener(ProcessDlg builder, View v);
    }
}
