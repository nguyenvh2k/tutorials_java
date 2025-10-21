package com.meocoder.method;

import com.meocoder.Cat;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ChangeStaticFinalFieldExample {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        Cat cat = new Cat();

        Field field = cat.getClass().getDeclaredField("NUMBER_OF_LEGS");
        field.setAccessible(true);

        //Todo java 12 tro len khong con nua
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);

        modifiersField.setInt(field,field.getModifiers() & ~Modifier.FINAL);

        //get value
        Integer fieldValue = (Integer) field.get(null);
        System.out.println(cat.getNumberOfLegs());

        field.set(null, 2);
        System.out.println(cat.getNumberOfLegs());

    }

}
