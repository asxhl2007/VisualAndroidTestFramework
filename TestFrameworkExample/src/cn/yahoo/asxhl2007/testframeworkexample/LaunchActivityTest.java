package cn.yahoo.asxhl2007.testframeworkexample;


import android.app.Activity;
import android.os.Bundle;
import cn.yahoo.asxhl2007.testframework.Test;

/**
 * 启动一个Activity
 * 
 * @author yangcheng
 *
 */
@Test( "启动一个Activity" )
public class LaunchActivityTest extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        
        setContentView( R.layout.activity_main );
    }

}
