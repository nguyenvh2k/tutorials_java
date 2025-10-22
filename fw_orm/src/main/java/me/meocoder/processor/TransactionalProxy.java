package me.meocoder.processor;

import me.meocoder.anotation.Transactional;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class TransactionalProxy implements InvocationHandler {

    private final Object target;
    private final TransactionManager txManager;

    public TransactionalProxy(final Object target, final TransactionManager transactionManager) {
        this.target = target;
        this.txManager = transactionManager;
    }

    @SuppressWarnings("unchecked")
    public static <T> T createProxy(T target, final TransactionManager transactionManager) {
        Class<?>[] interfaces = target.getClass().getInterfaces();
        if (interfaces.length == 0) {
            // todo fallback: if no interface, we can't use JDK proxy; return target as-is (or implement CGLIB)
            return target;
        }
        return (T) Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                interfaces,
                new TransactionalProxy(target, transactionManager)
        );
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Method implMethod = target.getClass().getMethod(method.getName(), method.getParameterTypes());
        boolean needTx = implMethod.isAnnotationPresent(Transactional.class)
                || target.getClass().isAnnotationPresent(Transactional.class)
                || method.isAnnotationPresent(Transactional.class);
        if (!needTx) {
            return implMethod.invoke(target, args);
        }
        txManager.begin();
        try {
            Object res = implMethod.invoke(target, args);
            txManager.commit();
            return res;
        }catch (InvocationTargetException e){
            txManager.rollback();
            throw e.getTargetException();
        }catch (Throwable t){
            txManager.rollback();
            throw t;
        }
    }
}
