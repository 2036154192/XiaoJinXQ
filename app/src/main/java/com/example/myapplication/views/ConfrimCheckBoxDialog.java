package com.example.myapplication.views;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.R;

public class ConfrimCheckBoxDialog extends Dialog {
    private View mCancalSub;
    private View mQiveUp;
    private onDialogActionClickListener2 mClickListener = null;
    private CheckBox mCheckBox;

    public ConfrimCheckBoxDialog(@NonNull Context context) {
        this(context,0);
    }

    public ConfrimCheckBoxDialog(@NonNull Context context, int themeResId) {
        //true表示点击空白出可以取消
        this(context, true,null);
    }

    protected ConfrimCheckBoxDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        //
        setContentView(R.layout.checkbox);
        initView();
        initListener();
    }

    private void initListener() {
        mCancalSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    boolean checked = mCheckBox.isChecked();
                    mClickListener.onCancelSubClick2(checked);
                }
            }
        });
        mQiveUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    mClickListener.onGiveUpClick2();
                }
            }
        });
    }

    private void initView() {
        mCancalSub = this.findViewById(R.id.dialog_cancel_sub);
        mQiveUp = this.findViewById(R.id.dialog_give_up);
        mCheckBox = this.findViewById(R.id.dialog_check_box);
    }

    public void setOnDialogActionClickListener2(onDialogActionClickListener2 listener){
        this.mClickListener = listener;
    }

    public interface onDialogActionClickListener2{
        void onCancelSubClick2(boolean isCheck);
        void onGiveUpClick2();
    }
}
