package com.meocoder.method;

import com.meocoder.Cat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AccessPrivateMethodExample {
    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<Cat> aClazz = Cat.class;

        // Class.getMethod(String) chỉ lấy được các method public.
        // Sử dụng Class.getDeclaredMethod(String):
        // Lấy ra đối tượng Method mô tả method setName(String) của class Cat.
        // (Phương thức khai báo trong class).
        Method private_setNameMethod = aClazz.getDeclaredMethod("setName",String.class);

        // Cho phép để truy cập vào các method private.
        // Nếu không sẽ bị ngoại lệ IllegalAccessException
        private_setNameMethod.setAccessible(true);

        Cat tom = new Cat("Tom");

        // Gọi private method.
        private_setNameMethod.invoke(tom,"Tom 2");
        System.out.println("New name value " + tom.getName());
    }
}
