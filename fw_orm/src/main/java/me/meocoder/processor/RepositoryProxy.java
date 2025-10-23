package me.meocoder.processor;

import java.lang.reflect.*;

public class RepositoryProxy implements InvocationHandler {

    private final  Class<?> repoInterface;
    private final EntityManager em;

    public RepositoryProxy(Class<?> repoInterface, EntityManager em) {
        this.repoInterface = repoInterface;
        this.em = em;
    }

    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> repoInterface, EntityManager em) {
        return (T) Proxy.newProxyInstance(
                repoInterface.getClassLoader(),
                new Class[]{repoInterface},
                new RepositoryProxy(repoInterface, em)
        );
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String name = method.getName();
        if (name.equals("findById") && args != null && args.length == 1) {
            Class<?> entityType = method.getReturnType();
            return em.find(entityType, args[0]);
        }
        if (name.equals("findAll")) {
            Type rt = method.getGenericReturnType();
            if (rt instanceof ParameterizedType) {
                Class<?> entityType = (Class<?>) ((ParameterizedType) rt).getActualTypeArguments()[0];
                return em.queryList("SELECT * FROM " + entityType.getSimpleName().toLowerCase(), entityType);
            }
        }
        if (name.equals("save") && args != null && args.length == 1) {
            em.persist(args[0]);
            return null;
        }
        throw new UnsupportedOperationException("Method not supported: " + method);
    }
}
