package encrypt;
/**
 * @Author joway
 * @Email joway.w@gmail.com
 * @Date 16/3/5.
 */

import encrypt.core.Utils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 时间统计处理机，用于统计各方法耗时
 * @author Administrator
 *
 */
public class TimeCountProxyHandle implements InvocationHandler {

    private Object proxied;

    public TimeCountProxyHandle(Object obj) {
        proxied = obj;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        long begin = System.currentTimeMillis();
        Object result = method.invoke(proxied, args);
        long end = System.currentTimeMillis();
        Utils.log("方法: "+method.getName() + " 耗时:" + (end - begin) + "ms");
        return result;
    }
}