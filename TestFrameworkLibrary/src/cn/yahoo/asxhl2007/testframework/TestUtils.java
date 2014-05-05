package cn.yahoo.asxhl2007.testframework;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class TestUtils {

    
    
    
    /**
     * 由于实体类没有按通用标准编写，只能打印所有public字段
     * @param object
     * @param tag
     */
    public static String object2StringX(Object object, boolean forceAccess){
        StringBuilder sb = new StringBuilder();
        Field[] fields = object.getClass().getFields();
        for(Field f : fields){
            try {
                Object value = null;
                if(f.isAccessible()){
                    value = f.get(object);
                }
                else if(forceAccess) {
                    f.setAccessible(true);
                    value = f.get(object);
                }
                sb.append(f.getName() + ": " + (value == null ? "[null]" : value.toString()) + "\n\n");
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public static String object2StringX(Object object){
        return object2String(object2StringX(object, false));
    }
    
    /**
     * 输出标准的实体类的所有通过get方法暴露的属性
     * @param object
     * @param tag
     */
    public static String object2String(Object object){
        StringBuilder sb = new StringBuilder();
        Method[] methods = object.getClass().getMethods();
        for(Method m : methods){
            m.setAccessible(true);
            try {
                if(m.getName().contains("get") && m.isAccessible()){
                    sb.append(m.getName().substring(3) + ": " + m.invoke(object, new Object[]{}) + "\n\n");
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
    
    @SuppressWarnings("unchecked")
    public static <T>T runPrivateMethod(Object object, String methodName, Class<?>[] parameterTypes, Object[] args){
        T t = null;
        try {
            Method m = object.getClass().getMethod(methodName, parameterTypes);
            m.setAccessible(true);
            t = (T) m.invoke(object, args);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return t;
    }


    public static String formatObject(Object object){
        String s = "";
        StringBuilder sb = new StringBuilder();
        new Formatter().formatObject(object.getClass().getSimpleName(), object, sb);
        s = sb.toString();
        return s;
    }
    
    private static class Formatter{

        
        public final String[] SPACE = {
            "", "　", "　　", "　　　", "　　　　", "　　　　　"
        };
        
        public int level = -1;
        
        public void formatObject(String name, Object object, StringBuilder sb) {

//            level++;
//            sb.append(SAPCE[level] + "#" + o.getClass().getName() + "# : ");
//            level--;

            if (object == null) {
                level++;
                sb.append(SPACE[level] + name + "=[null]\n");
                level--;
                return;
            }

            if (object instanceof String || object instanceof Byte || object instanceof Character || object instanceof Short || object instanceof Integer || object instanceof Long || object instanceof Float || object instanceof Double) {
                level++;
                sb.append(SPACE[level] + name + ":" + object + "\n");
                level--;
                return;
            }
            
            if(object.getClass().isArray()){
                level++;
                sb.append(SPACE[level] + name + "(length=" + ((Object[])object).length + "):{\n");
                int i = 0;
                for(Object a : (Object[])object){
                    formatObject("[" + i + "]", a, sb);
                    i++;
                }
                sb.append(SPACE[level] + "}\n");
                level--;
                return;
            }

            if (object instanceof Collection) {
                level++;
                sb.append(SPACE[level] + name + "(size=" + ((Collection<?>)object).size() + "):{\n");
                int i = 0;
                for (Object c : (Collection<?>) object) {
                    formatObject("[" + i + "]", c, sb);
                    i++;
                }
                sb.append(SPACE[level] + "}\n");
                level--;
                return;
            }
            
            if(object instanceof Map){
                level++;
                sb.append(SPACE[level] + name + "(size=" + ((Map<?, ?>)object).size() + "):{\n");
                for(Map.Entry<?, ?> entry : ((Map<?, ?>)object).entrySet()){
                    formatObject(entry.getKey().toString(), entry.getValue(), sb);
                }
                sb.append(SPACE[level] + "}\n");
                level--;
                return;
            }

            Class<?> c = object.getClass();
            Method[] m = c.getDeclaredMethods();
            level++;
            sb.append(SPACE[level] + name + ":{\n");
            for (int i = 0; i < m.length; i++) {
                String mName = m[i].getName();
                if (mName.length() > 3 && mName.substring(0, 3).equals("get")) {
                    Object o1 = null;
                    try {
                        if(m[i].getParameterTypes() == null || m[i].getParameterTypes().length == 0)
                            o1 = m[i].invoke(object, new Object[]{});
                    } catch (IllegalAccessException ex) {
                    } catch (IllegalArgumentException ex) {
                    } catch (InvocationTargetException ex) {
                    }
                    if(o1 == null){
                        sb.append(SPACE[level] + m[i].getName().substring(3) + ": [null]");
                    }else{
                        formatObject(mName.substring(3), o1, sb);
                    }
                }
            }
            sb.append(SPACE[level] + "}\n");
            level--;
        }
    }
}
