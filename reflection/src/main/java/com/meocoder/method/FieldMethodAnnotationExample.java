package com.meocoder.method;

import com.meocoder.anotation.MyColumn;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class FieldMethodAnnotationExample {

    @MyColumn(name = "order_code")
    private int orderCode;

    @MyColumn(name = "staff_code",value = "0")
    private String staffCode;

    @MyColumn(name = "My Method", value = "My Method Value")
    protected void myMethod(String str) {

    }

    public static void main(String[] args) throws NoSuchFieldException, NoSuchMethodException {
        Class<?> aClazz = FieldMethodAnnotationExample.class;

        System.out.println("====== FIELD ANNOTATION ======");
        Field field = aClazz.getDeclaredField("orderCode");

        //Lay danh sach antation cua field
        Annotation[] fieldAnnotations = field.getAnnotations();
        for (Annotation fieldAnnotation : fieldAnnotations) {
            System.out.println("Annotation :: " + fieldAnnotation.annotationType().getSimpleName());
        }

        Annotation ann = field.getAnnotation(MyColumn.class);
        MyColumn myAnnotation = (MyColumn) ann;
        System.out.println("Name = " + myAnnotation.name());
        System.out.println("Value = " + myAnnotation.value());

        System.out.println("==== METHOD ANNOTATION ======");
        Method method = aClazz.getDeclaredMethod("myMethod", String.class);
        Annotation[] methodAnnotations = method.getAnnotations();
        for (Annotation methodAnn : methodAnnotations) {
            System.out.println("Annotation :: " + methodAnn.annotationType().getSimpleName());
        }
        Annotation anotationMethod = method.getAnnotation(MyColumn.class);
        MyColumn myColumn2 = (MyColumn) anotationMethod;
        System.out.println("Name = " + myColumn2.name());
        System.out.println("Value = " + myColumn2.value());

    }
}
