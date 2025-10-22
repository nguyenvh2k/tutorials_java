package me.meocoder.method;

import me.meocoder.Cat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectMethodExample {
    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Lấy ra đối tượng Class mô tả class Cat
        Class<Cat> aClazz = Cat.class;

        // Lấy ra danh sách các method public của Cat
        // Bao gồm cả các method thừa kế từ class cha và các interface
        System.out.println("Declared Methos: ");
        Method[] methods = aClazz.getDeclaredMethods();
        for (Method method : methods) {
            System.out.println("+ " + method.getName());
        }

        // Lấy ra đối tượng 'Method' mô tả method getAge()
        Method getAgeMethod = aClazz.getMethod("getAge");

        // Kiểu trả về của method getAge
        Class<?> returnType = getAgeMethod.getReturnType();
        System.out.println("Return Type getAge method : " + returnType);

        Cat tom = new Cat("tom1",1);
        int age = (int) getAgeMethod.invoke(tom);

        System.out.println("Age: " + age);

        Method setAgeMethod = aClazz.getMethod("setAge", int.class);

        // Gọi method setAge(int) theo cách của Reflect.
        // Nó tương đương với gọi: tom.setAge(2);
        setAgeMethod.invoke(tom, 2);

        System.out.println("New Age: " + tom.getAge());

    }
}
