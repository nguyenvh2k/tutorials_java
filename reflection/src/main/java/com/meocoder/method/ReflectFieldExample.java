package com.meocoder.method;

import com.meocoder.Cat;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class ReflectFieldExample {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        Class<Cat> aClazz = Cat.class;

        System.out.println("Field: ");

        // Lấy ra danh sách các field public
        // Kể các các public field thừa kế từ các class cha, và các interface
        Field[] fields = aClazz.getDeclaredFields();
        for (Field field : fields) {
            System.out.println("+ " + field.getName());
        }

        // Lấy ra field có tên 'NUMBER_OF_LEGS':
        Field field = aClazz.getField("NUMBER_OF_LEGS");

        // Ghi ra kiểu của Field
        Class<?> fieldType = field.getType();
        System.out.println("Field type: " + fieldType.getSimpleName());

        Cat tom = new Cat("tom",1);

        // Lấy ra giá trị của trường "age" theo cách của Reflect.
        Field ageField = aClazz.getField("age");
        Integer age = (Integer) ageField.get(tom);
        System.out.println("Age: " + age);

        //  Gán giá trị mới cho trường "age".
        ageField.set(tom, 2);
        System.out.println("New age: " + tom.getAge());

        // Lấy ra danh sách các Annotation của field.
        System.out.println("\nAnotation: ");
        Annotation[] annotations = field.getAnnotations();
        for (Annotation annotation : annotations) {
            System.out.println("+ "+ annotation.annotationType().getSimpleName());
        }

    }
}
