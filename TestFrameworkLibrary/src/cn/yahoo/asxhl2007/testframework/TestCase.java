package cn.yahoo.asxhl2007.testframework;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import cn.yahoo.asxhl2007.testframework.LongMessageDialog.OnCloseListener;
import cn.yahoo.asxhl2007.testframework.Test.After;
import cn.yahoo.asxhl2007.testframework.Test.AfterClass;
import cn.yahoo.asxhl2007.testframework.Test.Before;
import cn.yahoo.asxhl2007.testframework.Test.BeforeClass;

/**
 * 自动布局测试UI的Activity
 * 
 * @author yangcheng
 * 
 */
public class TestCase extends ListActivity implements TestCallback {
    
    public static final String EXTRA_TEST_CLASS_NAME = "test_class_name";

    private String testCase;

    private Object testObject;

    public Handler handler;

    private final String key1 = "name";

    private final String key2 = "classname";

    private final String keyMethod = "method";

    private boolean beforeClassRan = false;
    private boolean afterClassRan = false;

    private Method beforeMethod;
    private Method afterMethod;
    private Method beforeClassMethod;
    private Method afterClassMethod;

    List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        testCase = this.getIntent().getStringExtra(
            EXTRA_TEST_CLASS_NAME);
        handler = new Handler();

        findAllTest();

        SimpleAdapter adapter = new SimpleAdapter(this, items,
                android.R.layout.simple_list_item_2,
                new String[] { key1, key2 }, new int[] { android.R.id.text1,
                        android.R.id.text2 });
        setListAdapter(adapter);
    }

    private void findAllTest() {

        try {
            Class<?> clazz = Class.forName(testCase);

            // 检查构造函数
            Constructor<?>[] constructors = clazz.getConstructors();
            boolean canNotCreate = true;
            for (Constructor<?> ct : constructors) {
                if (ct.getParameterTypes().length == 0) {
                    canNotCreate = false;
                    break;
                }
            }
            if (canNotCreate) {
                throw new IllegalAccessException("找不到无参数的构造方法！");
            }

            // 加载所有测试方法及预处理方法
            testObject = clazz.newInstance();
            Method[] methods = clazz.getMethods();
            for (Method m : methods) {
                Test testAnno = m.getAnnotation(Test.class);
                if (testAnno != null) {
                    HashMap<String, Object> item = new HashMap<String, Object>();
                    item.put(key1, testAnno.value());
                    item.put(key2, m.getName());
                    item.put(keyMethod, m);
                    items.add(item);
                } else if (m.getAnnotation(BeforeClass.class) != null) {
                    beforeClassMethod = m;
                } else if (m.getAnnotation(AfterClass.class) != null) {
                    afterClassMethod = m;
                } else if (m.getAnnotation(Before.class) != null) {
                    beforeMethod = m;
                } else if (m.getAnnotation(After.class) != null) {
                    afterMethod = m;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) getListView()
                .getItemAtPosition(position);
        final Method method = (Method) map.get(keyMethod);
        new Thread() {
            public void run() {
                runTest(method);
            }
        }.start();
        super.onListItemClick(l, v, position, id);
    }

    private void runTest(Method method) {
        if (!beforeClassRan && beforeClassMethod != null) {
            showBeforeDialog(method);
        } else {
            invoke(method, this, false);
        }
    }

    private void invoke(final Method method, final TestCallback callback, final boolean autoClose) {

        Class<?>[] parameters = method.getParameterTypes();
        if (parameters.length == 0) {
            callback.onTestOver("测试方法至少需要定义一个TestCallback参数", false);
            throw new IllegalArgumentException("测试方法至少需要定义一个TestCallback参数");
        } else if (parameters.length == 1) {
            if (TestCallback.class.isAssignableFrom(parameters[0])) {
                try {
                    method.invoke(testObject, new Object[] { callback });
                } catch (IllegalArgumentException e) {
                    callback.onTestOver("测试方法参数不符合要求。", false);
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    callback.onTestOver("测试方法不可访问，请申明为public。", false);
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    callback.onTestOver("测试方法执行中出现异常，请参照Log。", false);
                    e.printStackTrace();
                }
            } else {
                callback.onTestOver("测试方法至少需要定义一个TestCallback参数", false);
                throw new IllegalArgumentException("测试方法至少需要定义一个TestCallback参数");
            }
        } else {
            boolean hasTestCallback = false;
            for (Class<?> clazz : parameters) {
                if(clazz.equals(TestCallback.class)){
                    hasTestCallback = true;
                }
                if (clazz.equals(char.class) || clazz.equals(int.class)
                        || clazz.equals(long.class)
                        || clazz.equals(float.class)
                        || clazz.equals(double.class)
                        || clazz.equals(String.class)
                        || clazz.equals(TestCallback.class)) {
                    // nothing
                } else {
                    callback.onTestOver("测试方法中必须包含TestCallback参数，其余参数请使用字符串和基本数据类型作为参数。", false);
                    throw new IllegalArgumentException(
                            "测试方法中必须包含TestCallback参数，其余参数请使用字符串和基本数据类型作为参数。");
                }
            }
            if(!hasTestCallback){
                callback.onTestOver("测试方法至少需要定义一个TestCallback参数", false);
                throw new IllegalArgumentException("测试方法至少需要定义一个TestCallback参数");
            }
            handler.post(new Runnable(){

                @Override
                public void run() {
                    TestInputDialog dialog = new TestInputDialog(TestCase.this, testObject, method,
                        callback);
                    dialog.setAutoClose( autoClose );
                    dialog.show();
                }});
        }
    }

    private void showBeforeDialog(final Method method) {
        handler.post(new Runnable() {
            
            @Override
            public void run() {
                final AlertDialog ad = new AlertDialog.Builder(TestCase.this).create();
                ad.setTitle("提示");
                ad.setMessage("BeforeClass尚未运行，是否现在运行？"
                        + "\n跳过此步骤可能因缺少必要的初始化过程而导致测试失败。");
                ad.setButton(DialogInterface.BUTTON_POSITIVE, "现在运行BeforeClass", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        invoke(beforeClassMethod, new TestCallback() {

                            @Override
                            public void onTestOver(final String message, final boolean testPass) {
                                handler.post(new Runnable() {
                                    
                                    @Override
                                    public void run() {
                                        if (testPass) {
                                            beforeClassRan = true;onBeforeTestOver( message, testPass, new OnCloseListener() {
                                                
                                                @Override
                                                public void onClose() {
                                                    invoke(method, TestCase.this, false);
                                                }
                                            } );
                                        } else {
                                            onTestOver( message, testPass );
                                            
                                        }
                                        ad.dismiss();
                                    }
                                });
                            }
                        }, true);
                    }
                });
                ad.setButton(DialogInterface.BUTTON_NEGATIVE, "跳过BeforeClass", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        invoke(method, TestCase.this, false);
                    }
                });
                ad.setCancelable(false);
                ad.show();
            }
        });
    }

    private boolean fourceFinish;

    private void showAfterDialog() {
        handler.post(new Runnable() {
            
            @Override
            public void run() {
                final AlertDialog ad = new AlertDialog.Builder(TestCase.this).create();
                ad.setTitle("提示信息");
                ad.setMessage("测试完毕后可能需要做一些清除测试数据、释放资源的操作，现在要执行AfterClass吗？");
                ad.setButton(DialogInterface.BUTTON_POSITIVE, "执行并退出", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ad.dismiss();
                        final AlertDialog waitDialog = null;
                        showWaitDialog("正在执行，请稍候，若时间过场，您也可以强制退出。", "强制退出",
                                new DialogInterface.OnClickListener() {

                                    @SuppressWarnings("null")
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                            int which) {
                                        waitDialog.dismiss();
                                        fourceFinish = true;
                                        ;
                                        finish();
                                    }
                                });
                        invoke(afterClassMethod, new TestCallback() {

                            @Override
                            public void onTestOver(String message, final boolean testPass) {
                                if (!fourceFinish) {
                                    handler.post(new Runnable() {
                                        
                                        @SuppressWarnings("unused")
                                        @Override
                                        public void run() {
                                            if (testPass) {
                                                if (waitDialog != null) waitDialog
                                                        .setMessage("AfterClass执行失败，请检查测试代码。");
                                            } else {
                                                if (waitDialog != null) waitDialog
                                                        .setMessage("AfterClass执行完毕，点击结束按钮退出测试。");
                                            }
                                            if (waitDialog != null) waitDialog.setButton(DialogInterface.BUTTON_POSITIVE, "结束",
                                                    new DialogInterface.OnClickListener() {

                                                        @Override
                                                        public void onClick(
                                                                DialogInterface dialog,
                                                                int which) {
                                                            waitDialog.dismiss();
                                                            finish();
                                                        }
                                                    });
                                        }
                                    });
                                }
                            }
                        }, true);
                    }
                });
                ad.setButton(DialogInterface.BUTTON_NEGATIVE, "直接退出", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                ad.setButton(DialogInterface.BUTTON_NEUTRAL, "返回列表", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // nothing
                    }
                });
                ad.show();
            }
        });
    }

    private AlertDialog showWaitDialog(String message, String btnLabel,
            DialogInterface.OnClickListener ocl) {
        final AlertDialog ad = new AlertDialog.Builder(this).create();
        ad.setTitle("请稍候...");
        ad.setMessage(message);
        ad.setButton(DialogInterface.BUTTON_POSITIVE, btnLabel, ocl);
        ad.show();
        return ad;
    }

    private void showMessage(String message) {
        AlertDialog ad = new AlertDialog.Builder(this).create();
        ad.setTitle("提示信息");
        ad.setMessage(message);
        ad.setButton(DialogInterface.BUTTON_POSITIVE, "确认", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // nothing
            }
        });
        ad.show();
    }

    @Override
    public void onTestOver(final String message, final boolean testPass) {
        handler.post(new Runnable() {
            
            @Override
            public void run() {
                LongMessageDialog d = new LongMessageDialog(TestCase.this);
                d.setTitle(testPass ? "测试成功" : "测试失败");
                d.setMessage(message);
                d.show();
            }
        });
    }
    
    private void onBeforeTestOver(final String message, final boolean testPass, final OnCloseListener listener){
        handler.post(new Runnable() {
            
            @Override
            public void run() {
                LongMessageDialog d = new LongMessageDialog(TestCase.this);
                d.setOnCloseListener( listener );
                d.setTitle(testPass ? "TestBefore测试成功" : "TestBefore测试失败");
                d.setMessage(message);
                d.show();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && afterClassMethod != null) {
            showAfterDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
