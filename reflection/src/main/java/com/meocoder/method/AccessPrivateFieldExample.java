package com.meocoder.method;

import com.meocoder.Cat;

import java.lang.reflect.Field;

public class AccessPrivateFieldExample {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        // Tạo một đối tượng Class mô tả class Cat.
        Class<Cat> aClazz = Cat.class;

        // Class.getField(String) chỉ lấy được các trường public.
        // Sử dụng Class.getDeclaredField(String):
        // Lấy ra đối tượng Field mô tả trường name của class Cat.
        // (Trường khi báo trong class này).
        Field privateField = aClazz.getDeclaredField("name");

        // Cho phép để truy cập vào các trường private.
        // Nếu không sẽ bị ngoại lệ IllegalAccessException
        privateField.setAccessible(true);

        Cat tom = new Cat("Tom");
        String fieldValue = (String) privateField.get(tom);
        System.out.println("Field name value: " + fieldValue);

        // Sét đặt trường name giá trị mới.
        privateField.set(tom, "Tom Cat");
        System.out.println("New field name value: " + fieldValue);

    }
}
