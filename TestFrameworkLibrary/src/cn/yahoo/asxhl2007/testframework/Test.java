/**
 * 
 */
package cn.yahoo.asxhl2007.testframework;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author yangcheng
 * <p>
 * 用于标注测试类、测试方法
 * <p>
 * 当用于class时，表示此class适用于自动测试， 如果该class继承自Activity，将直接启动该Activity
 * <p>
 * 当用于method时，表示此method为测试方法
 * <p>
 * 当用于参数时，仅用于描述参数名，该标注将用于输入界面的label。
 */
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Test {
    /**
     * 用于对测试进行简要说明，将作为列表项显示
     * 
     * @return
     */
    String value();

    /**
     * @author yangcheng
     * <p>
     * 用于标注方法，被标注的方法将在每个测试方法之前执行
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Inherited
    public static @interface Before {
    }

    /**
     * @author yangcheng
     * <p>
     * 用于标注方法，被标注的方法将在每个测试方法之后执行
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Inherited
    public static @interface After {
    }

    /**
     * @author yangcheng
     * <p>
     * 用于标注方法，被标注的方法将在所有测试之前先被执行一遍
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Inherited
    public static @interface BeforeClass {
    }

    /**
     * @author yangcheng
     * <p>
     * 用于标注方法，被标注的方法将在所有测试完成之后被执行一遍
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Inherited
    public static @interface AfterClass {
    }
}
