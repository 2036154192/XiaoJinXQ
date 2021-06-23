package com.example.myapplication.views;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.R;

public class ConfrimDialog extends Dialog {

    private View mCancalSub;
    private View mQiveUp;
    private onDialogActionClickListener mClickListener = null;

    public ConfrimDialog(@NonNull Context context) {
        this(context,0);
    }

    public ConfrimDialog(@NonNull Context context, int themeResId) {
        //true表示点击空白出可以取消
        this(context, true,null);
    }

    protected ConfrimDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        //
        setContentView(R.layout.dialog_confrim);
        initView();
        initListener();
    }

    private void initListener() {
        mCancalSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    mClickListener.onCancelSubClick();
                }
            }
        });
        mQiveUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    mClickListener.onGiveUpClick();
                }
            }
        });
    }

    private void initView() {
        mCancalSub = this.findViewById(R.id.dialog_cancel_sub_tv);
        mQiveUp = this.findViewById(R.id.dialog_give_up_tv);
    }

    public void setOnDialogActionClickListener(onDialogActionClickListener listener){
        this.mClickListener = listener;
    }

    public interface onDialogActionClickListener{
        void onCancelSubClick();
        void onGiveUpClick();
    }
}
