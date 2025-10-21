package me.meocoder.anotation.test;

import me.meocoder.anotation.process.ObjectToJsonConverter;
import me.meocoder.anotation.process.Person;

public class JsonSeriableTest {
    public static void main(String[] args) {
        Person person = new Person("soufiane", "cheouati", "34");
        ObjectToJsonConverter serializer = new ObjectToJsonConverter();
        String jsonString = serializer.convertToJson(person);
        System.out.println(jsonString);
    }
}
