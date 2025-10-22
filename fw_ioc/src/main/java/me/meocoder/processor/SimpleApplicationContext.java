package me.meocoder.processor;

import me.meocoder.anotation.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleApplicationContext {
    private Map<String, Object> singletonObjects = new ConcurrentHashMap<>();
    private Map<Class<?>, BeanDefinition> beanDefinitions = new HashMap<>();

    public SimpleApplicationContext(String basePackage) {
        // 1. scan classes (implement or reuse library)
        List<Class<?>> candidates = ClassScanner.scan(basePackage); // implement simple scanner
        // 2. register beanDefinitions
        for (Class<?> cl : candidates) {
            if (cl.isAnnotationPresent(Component.class) ||
                    cl.isAnnotationPresent(Service.class) ||
                    cl.isAnnotationPresent(Repository.class)) {
                beanDefinitions.put(cl, new BeanDefinition(cl));
            }
        }
        // 3. instantiate singletons
        for (BeanDefinition bd : beanDefinitions.values()) {
            if (bd.isSingleton()) {
                Object bean = createBean(bd.getBeanClass());
                String name = bd.getBeanName();
                singletonObjects.put(name, bean);
            }
        }
    }

    private Object createBean(Class<?> clazz) {
        // constructor injection: find constructor with @Autowired or default constructor
        Constructor<?> injectableCtor = findAutowiredOrDefaultConstructor(clazz);
        Object instance;
        try {
            if (injectableCtor.getParameterCount() == 0) {
                instance = injectableCtor.newInstance();
            } else {
                Object[] args = Arrays.stream(injectableCtor.getParameterTypes())
                        .map(this::getBean) // recursive getBean
                        .toArray();
                instance = injectableCtor.newInstance(args);
            }
            // field injection
            for (Field f : clazz.getDeclaredFields()) {
                if (f.isAnnotationPresent(Autowired.class)) {
                    Object dep = getBean(f.getType());
                    f.setAccessible(true);
                    f.set(instance, dep);
                }
            }
            // post construct
            for (Method m : clazz.getDeclaredMethods()) {
                if (m.isAnnotationPresent(PostConstruct.class)) {
                    m.setAccessible(true);
                    m.invoke(instance);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return instance;
    }

    public <T> T getBean(Class<T> type) {
        // find singleton or create prototype
        for (Object o : singletonObjects.values()) {
            if (type.isAssignableFrom(o.getClass())) {
                return type.cast(o);
            }
        }
        BeanDefinition bd = beanDefinitions.get(type);
        if (bd == null) throw new RuntimeException("No bean for " + type);
        if (bd.isSingleton()) {
            // maybe created earlier
            String name = bd.getBeanName();
            return type.cast(singletonObjects.computeIfAbsent(name, k -> createBean(type)));
        } else {
            return type.cast(createBean(type));
        }
    }

    private Constructor<?> findAutowiredOrDefaultConstructor(Class<?> clazz) {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();

        for (Constructor<?> ctor : constructors) {
            if (ctor.isAnnotationPresent(Autowired.class)) {
                ctor.setAccessible(true);
                return ctor;
            }
        }

        if (constructors.length == 1) {
            constructors[0].setAccessible(true);
            return constructors[0];
        }

        try {
            Constructor<?> defaultCtor = clazz.getDeclaredConstructor();
            defaultCtor.setAccessible(true);
            return defaultCtor;
        } catch (NoSuchMethodException e) {
            // ignore
        }

        throw new RuntimeException("No suitable constructor found for class: " + clazz.getName());
    }

}
