package cn.yahoo.asxhl2007.testframework;

/**
 * 
 * @author yangcheng
 * <p>
 * 不是所有的测试方法都能同步返回，可以通过此接口异步返回测试结果
 */
public interface TestCallback {
    
    /**
     * 测试结束回调方法
     * @param message 测试消息
     * @param testPass 为true时表示测试通过
     */
    void onTestOver(String message, boolean testPass);
}
