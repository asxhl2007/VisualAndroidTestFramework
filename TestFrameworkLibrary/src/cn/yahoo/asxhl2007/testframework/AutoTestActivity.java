/**
 * 
 */
package cn.yahoo.asxhl2007.testframework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

/**
 * 
 * @author yangcheng
 * <p>
 * 用于执行自动测试
 */
public abstract class AutoTestActivity extends ListActivity {

    public static AutoTestActivity instance;

    public Handler handler;

    private final String key1 = "name";

    private final String key2 = "classname";

    List<Map<String, String>> items = new ArrayList<Map<String, String>>();

    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCreateTestActivity(savedInstanceState);
        handler = new Handler();
        instance = this;

        addAll();

        SimpleAdapter adapter = new SimpleAdapter(this, items,
                android.R.layout.simple_list_item_2,
                new String[] { key1, key2 }, new int[] { android.R.id.text1,
                        android.R.id.text2 });
        setListAdapter(adapter);
    }

    private void addAll() {
        Class<?>[] testClasses = getTestClasses();
        for (Class<?> clazz : testClasses) {
            addTestCase(clazz);
        }
    }

    private void addTestCase(Class<?> testClass) {
        Test testAnno = testClass.getAnnotation(Test.class);
        String classname = testClass.getName();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(key1, testAnno == null ? "未命名测试" : testAnno.value());
        map.put(key2, classname);
        items.add(map);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        @SuppressWarnings("unchecked")
        Map<String, String> map = (Map<String, String>) getListView()
                .getItemAtPosition(position);
        try {
            Class<?> clazz = Class.forName(map.get(key2));
            if (Activity.class.isAssignableFrom(clazz)) {
                Intent i = new Intent(this, clazz);
                startActivity(i);
            } else {
                Intent i = new Intent(this, TestCase.class);
                i.putExtra(TestCase.EXTRA_TEST_CLASS_NAME, clazz.getName());
                startActivity(i);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "无法启动测试", Toast.LENGTH_LONG).show();;
        }

        super.onListItemClick(l, v, position, id);
    }

    /**
     * 向基类提供需要测试的Class
     * 
     * @return
     */
    protected abstract Class<?>[] getTestClasses();

    /**
     * 创建Activity时被调用
     */
    public abstract void onCreateTestActivity(Bundle savedInstanceState);
}
