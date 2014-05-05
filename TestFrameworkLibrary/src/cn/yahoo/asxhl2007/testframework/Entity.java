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
 * 用于描述实体类，以方便阅读测试数据
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Entity {
    
    /**
     * 用于简单描述该实体类
     * @return
     */
    String value();
    
    /**
     * 用于标注该实体类采用何种方式来让自动测试框架读取数据
     * <p>默认采用AllGeter模式来读取数据
     * @return
     */
    ReadMode ReadMode() default ReadMode.AllGetter;

    /**
     * @author yangcheng
     * <p>
     * 用于标注实体类的属性(字段)或Getter,
     * 在使用Annotation模式来读取时，
     * 这些被标注的属性或get方法将被用来读取信息。
     */
    @Target({ElementType.FIELD, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Inherited
    public @interface Propertiy{
        /**
         * 用于标注某个属性（字段）或Getter的说明
         * @return
         */
        String value();
    }
    
    /**
     * @author yangcheng
     * <p>
     * 读取模式
     */
    public static enum ReadMode{
        
        /**
         * 通过所有属性（字段）读取
         */
        AllProperty,
        
        /**
         * 通过所有Getter来读取
         */
        AllGetter,
        
        /**
         * 通过所有添加了“Propertiy”注释的属性（字段）来读取
         */
        AnnotationProperty,
        
        /**
         * 通过所有添加了“Propertiy”注释的Getter来读取
         */
        AnnotationGetter;
    }
}
