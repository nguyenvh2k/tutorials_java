package com.meocoder;

import com.meocoder.anotation.MyColumn;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class ParameterAnnotationExample {

    protected void doSomething(int jonType, @MyColumn(name = "test",value = "Nguyen Dinh")String info) {

    }

    public static void main(String[] args) throws NoSuchMethodException {
        Class<?> aClazz = ParameterAnnotationExample.class;

        Method method = aClazz.getDeclaredMethod("doSomething",int.class,String.class);

        // Lấy ra danh sách các Parameter của method.
        Class<?>[] parameterType = method.getParameterTypes();
        for (Class<?> type : parameterType) {
            System.out.println(" Parameter Type: " + type.getSimpleName());
        }

        System.out.println(" ---- ");
        // Lấy ra mảng 2 chiều các Annotation trong các Parameter
        Annotation[][] annotations = method.getParameterAnnotations();

        // Lấy ra danh sách các Annotation của Parameter tại vị trí Index =1.
        Annotation[] annotation = annotations[1];
        for (Annotation ann : annotation) {
            System.out.println(" Annotation: " + ann.annotationType().getSimpleName());
        }
    }
}
