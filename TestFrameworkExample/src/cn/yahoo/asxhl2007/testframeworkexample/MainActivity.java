package cn.yahoo.asxhl2007.testframeworkexample;

import android.os.Bundle;
import cn.yahoo.asxhl2007.testframework.AutoTestActivity;

public class MainActivity extends AutoTestActivity {

    @Override
    protected Class<?>[] getTestClasses() {
        return new Class<?>[]{
            LaunchActivityTest.class,
            SampleUnitTest.class
        };
    }

    @Override
    public void onCreateTestActivity(Bundle savedInstanceState) {
        
    }

}
