package cn.yahoo.asxhl2007.testframeworkexample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.yahoo.asxhl2007.testframework.Test;
import cn.yahoo.asxhl2007.testframework.Test.BeforeClass;
import cn.yahoo.asxhl2007.testframework.TestCallback;
import cn.yahoo.asxhl2007.testframework.TestUtils;

@Test("简单的单体测试")
public class SampleUnitTest {
    
    @BeforeClass
    public void onStart(
        @Test("口令：(I love you.)")
        String password,
        // callback
        TestCallback callback
        ){
        if(password.equals( "I love you." )){
            callback.onTestOver( "成功", true );
        }else{
            callback.onTestOver( "失败，看提示！", false );
        }
    }

    @Test( "测试1" )
    public void test1(TestCallback callback){
        callback.onTestOver( "测试成功", true );
    }
    
    @Test( "测试2" )
    public void test2(
        @Test( "User Name:" )
        String userName, 
        @Test( "password:" )
        String password,
        // callback
        TestCallback callback
        ){
        
        if(userName.equals( "admin" ) && password.equals( "admin" )){
            HashMap<Object, Object> testobj = new HashMap<Object, Object>();
            List<String> list = new ArrayList<String>();
            list.add( "aaaa" );
            list.add( "bbb" );
            list.add( "cc" );
            testobj.put( "list", list );
            testobj.put( "userName", "admin" );
            testobj.put( "password", "admin" );
            // 可以使用测试工具将一些集合对象转化为String
            String msg = TestUtils.formatObject( testobj );
            callback.onTestOver( msg, true );
        }else{
            callback.onTestOver( "失败，请尝试输入admin", false );
        }
        
    }
}
