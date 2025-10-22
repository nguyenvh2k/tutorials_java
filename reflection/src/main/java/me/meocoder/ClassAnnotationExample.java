package me.meocoder;

import me.meocoder.anotation.MyColumn;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

@MyColumn(name = "Table",value = "Nguyen Test value ")
public class ClassAnnotationExample {

    private String name;

    public static void main(String[] args) throws NoSuchFieldException {
        Class<?> aClazz = ClassAnnotationExample.class;

        //LAy danh sach anotation
        Annotation[] annotations = aClazz.getAnnotations();

        for (Annotation annotation : annotations) {
            System.out.println("Annotation Name: " + annotation.annotationType().getSimpleName());
            System.out.println("Annotation Simple Name: " + annotation.annotationType().getName());
        }

        Field field = aClazz.getDeclaredField("name");

        // Lay anotation cu the
        boolean isAnnotationPresent = field.isAnnotationPresent(MyColumn.class);
        System.out.println(isAnnotationPresent);

        Annotation ann = aClazz.getAnnotation(MyColumn.class);
        MyColumn myAnnotation = (MyColumn) ann;
        System.out.println("Name = " + myAnnotation.name());
        System.out.println("Value = " + myAnnotation.value());
    }
}
