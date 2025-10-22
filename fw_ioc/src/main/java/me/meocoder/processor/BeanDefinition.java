package me.meocoder.processor;

public class BeanDefinition {
    private final Class<?> beanClass;
    private final String beanName;
    private String scope = "singleton"; // mặc định singleton
    private boolean lazyInit = false;

    public BeanDefinition(Class<?> beanClass) {
        this.beanClass = beanClass;
        this.beanName = lowerFirst(beanClass.getSimpleName());
    }

    public BeanDefinition(Class<?> beanClass, String beanName) {
        this.beanClass = beanClass;
        this.beanName = beanName;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public String getBeanName() {
        return beanName;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public boolean isSingleton() {
        return "singleton".equalsIgnoreCase(scope);
    }

    public boolean isLazyInit() {
        return lazyInit;
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    private static String lowerFirst(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }

    @Override
    public String toString() {
        return "BeanDefinition{" +
                "beanName='" + beanName + '\'' +
                ", beanClass=" + beanClass.getName() +
                ", scope='" + scope + '\'' +
                ", lazyInit=" + lazyInit +
                '}';
    }
}
