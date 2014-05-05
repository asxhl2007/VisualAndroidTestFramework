package cn.yahoo.asxhl2007.testframework;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 * @author yangcheng 用于显示很长的消息
 */
public class LongMessageDialog extends Dialog implements
        android.view.View.OnClickListener, OnCancelListener {

    private Context context;
    private LinearLayout rootLayout;
    private TextView titleView;
    private TextView messageView;
    private Button btnOk;
    private OnCloseListener onCloseListener;

    public LongMessageDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
        init();
    }

    protected LongMessageDialog(Context context, boolean cancelable,
            OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context = context;
        init();
    }

    public LongMessageDialog(Context context) {
        super(context, android.R.style.Theme_NoTitleBar);
        this.context = context;
        init();
    }
    
    public void setOnCloseListener(OnCloseListener listener){
        onCloseListener = listener;
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        rootLayout = (LinearLayout) inflater.inflate(
                R.layout.test_dialog_message, null);
        titleView = (TextView) rootLayout.findViewById(R.id.title);
        messageView = (TextView) rootLayout.findViewById(R.id.message);
        messageView.setBackgroundColor(Color.BLACK);
        messageView.setTextColor(Color.WHITE);
        btnOk = (Button) rootLayout.findViewById(R.id.okbtn);
        btnOk.setOnClickListener(this);
        setOnCancelListener( this );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT);
        this.setContentView(rootLayout, params);
    }

    /**
     * 设置消息（换行请插入"\n"）
     * 
     * @param message
     */
    public void setMessage(CharSequence message) {
        messageView.setText(message);
    }

    /**
     * 设置标题
     */
    @Override
    public void setTitle(CharSequence title) {
        titleView.setText(title);
    }

    @Override
    public void onClick(View view) {
        this.dismiss();
        if(onCloseListener != null){
            onCloseListener.onClose();
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        this.dismiss();
        if(onCloseListener != null){
            onCloseListener.onClose();
        }
    }
    
    public static interface OnCloseListener {
        void onClose();
    }
}
