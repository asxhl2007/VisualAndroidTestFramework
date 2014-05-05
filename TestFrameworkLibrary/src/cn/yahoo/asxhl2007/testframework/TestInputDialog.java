package cn.yahoo.asxhl2007.testframework;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 当测试方法需要参数时将显示此Dialog（参数仅限字符串和基础数据类型）
 * 
 * @author yangcheng
 * 
 */
public class TestInputDialog extends Dialog implements android.view.View.OnClickListener {

    private Context context;

    private Object testObject;
    private Method testMethod;
    private TestCallback testCallback;
    private List<EditText> editList;

    private boolean autoClose;

    /**
     * 构造函数
     * 
     * @param context 应用环境
     * @param testObject 测试对象
     * @param testMethod 测试方法
     * @param testCallback 回调对象
     */
    public TestInputDialog(Context context, Object testObject,
            Method testMethod, TestCallback testCallback) {
        super(context, android.R.style.Theme_NoTitleBar);
        this.context = context;
        this.testObject = testObject;
        this.testMethod = testMethod;
        this.testCallback = testCallback;
        this.context = context;
        init();
    }
    
    public void setAutoClose(boolean autoClose){
        this.autoClose = autoClose;
    }

    private void init() {
        editList = new ArrayList<EditText>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        final LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT);

        LayoutInflater inflater = LayoutInflater.from(context);

        View dialog = inflater.inflate(R.layout.test_dialog_input, null);
        this.setContentView(dialog, params);
        // this.setContentView(R.layout.test_dialog_input);

        ViewGroup inputRoot = (ViewGroup) findViewById(R.id.inputRoot);
        findViewById(R.id.submit).setOnClickListener(this);

        Class<?>[] paramsClasse = testMethod.getParameterTypes();
        Annotation[][] annotations = testMethod.getParameterAnnotations();
        View firstInput = null;
        for (int i = 0; i < paramsClasse.length; i++) {

            if (TestCallback.class.isAssignableFrom(paramsClasse[i])) {
                editList.add(null);
                continue;
            }

            View item = inflater.inflate(R.layout.test_dialog_input_item,
                    null, false);

            if (annotations != null && annotations.length > 0
                    && annotations[i] != null && annotations[i].length > 0
                    && annotations[i][0] != null
                    && Test.class.isInstance(annotations[i][0])) {
                TextView label = (TextView) item.findViewById(R.id.label);
                label.setText(((Test) annotations[i][0]).value());
            }

            EditText et = (EditText) item.findViewById(R.id.input);
            if (paramsClasse[i].equals(int.class)
                    || paramsClasse.equals(long.class)
                    || paramsClasse.equals(float.class)
                    || paramsClasse.equals(double.class)) {
                et.setInputType(InputType.TYPE_CLASS_NUMBER);
            } else if (paramsClasse[i].equals(char.class)) {
                et.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
                        1) });
            }
            if(firstInput == null) firstInput = et;

            inputRoot.addView(item);
            editList.add(et);
        }
        if(firstInput != null) firstInput.requestFocus();
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.submit) {

            Class<?>[] paramsClass = testMethod.getParameterTypes();
            Object[] params = new Object[paramsClass.length];
            int offset = 0;
            for(int i = 0; i < paramsClass.length; i++){
                if(TestCallback.class.isAssignableFrom(paramsClass[i])){
                    offset++;
                    params[i] = testCallback; 
                }else{
                    String text = editList.get(i - offset).getText().toString();
                    if(paramsClass[i].equals(char.class)){
                        params[i] = text.length() > 0 ? text.charAt(0) : " ";
                    }else if(paramsClass[i].equals(int.class)){
                        params[i] = Integer.valueOf(text); 
                    }else if(paramsClass[i].equals(long.class)){
                        params[i] = Long.valueOf(text); 
                    }else if(paramsClass[i].equals(float.class)){
                        params[i] = Float.valueOf(text); 
                    }else if(paramsClass[i].equals(double.class)){
                        params[i] = Double.valueOf(text);
                    }else if(paramsClass[i].equals(String.class)){
                        params[i] = text; 
                    }else{
                        new IllegalArgumentException("测试方法中包含了无法解析的参数类型！");
                    }
                }
            }
            
            try {
                testMethod.invoke(testObject, params);
                return;
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }finally{
                if(autoClose){
                    dismiss();
                }
            }
            
            Toast.makeText(context, "测试启动失败！", Toast.LENGTH_LONG).show();
        }
    }
}
