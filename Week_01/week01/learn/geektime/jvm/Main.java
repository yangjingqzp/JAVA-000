package learn.geektime.jvm;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 主程序入口
 *
 * @author yangjing
 * @date 2020/10/20 7:54 AM
 * @version v1.0
 */
public class Main {
	public static void main(String[] args)
		throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
		Class cls = (new HelloClassLoader()).findClass("Hello");
		Object obj = cls.newInstance();
		Method method = cls.getMethod("hello");
		method.invoke(obj);
	}
}
