package org.star.starpc.proxy;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@Slf4j
public class MockServiceProxy implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> methodReturnType = method.getReturnType();
        log.info("mock invoke {}", method.getName());
        return getDefaultObject(methodReturnType);
    }

    /**
     * 获取基本类型的默认值；引用类型返回 null
     *
     * @param clazz
     * @return
     */
    private Object getDefaultObject(Class<?> clazz) {
        if (clazz == int.class || clazz == Integer.class) {
            return 0;
        } else if (clazz == double.class || clazz == Double.class) {
            return 0.0;
        } else if (clazz == float.class || clazz == Float.class) {
            return 0.0f;
        } else if (clazz == long.class || clazz == Long.class) {
            return 0L;
        } else if (clazz == boolean.class || clazz == Boolean.class) {
            return false;
        } else if (clazz == short.class || clazz == Short.class) {
            return (short) 0;
        } else if (clazz == byte.class || clazz == Byte.class) {
            return (byte) 0;
        } else if (clazz == char.class || clazz == Character.class) {
            return '\u0000';
        }
        return null;
    }
}
