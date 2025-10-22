# 🌱 Tutorials Java

![Java](https://img.shields.io/badge/Java-17%2B-orange?logo=java&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.8%2B-blue?logo=apachemaven)
![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)
![Status](https://img.shields.io/badge/Status-Learning%20Project-yellow)

> A collection of self-built **Java Core tutorials** and **mini frameworks** exploring Reflection, Annotation, IoC (Dependency Injection), and ORM — all written from scratch with pure Java.

---

## 📚 Overview

This repository demonstrates how core ideas behind frameworks like **Spring** and **Hibernate** can be implemented manually using:
- Java Reflection & Annotation
- Custom IoC Container
- Lightweight ORM framework
- Modular, extensible architecture

---

## 🏗️ Project Structure

```
tutorials_java/
├── anotation/           # Custom annotations and metadata examples
├── reflection/          # Reflection API, dynamic class loading, method invocation
├── fw_ioc/              # Lightweight IoC/DI framework (Spring Core-like)
├── fw_orm/              # Mini ORM framework using JDBC + reflection
├── .gitignore
├── pom.xml              # Maven project descriptor
└── README.md
```

---

## ⚙️ Requirements

| Tool | Version | Description |
|------|----------|-------------|
| Java | 17+ | Tested with OpenJDK 17 |
| Maven | 3.8+ | Build and dependency management |
| IDE | IntelliJ IDEA / VSCode / Eclipse | Recommended development environments |

---

## 🚀 Getting Started

### 1️⃣ Clone the repository
```bash
git clone https://github.com/<your-username>/tutorials_java.git
cd tutorials_java
```

### 2️⃣ Build the project
```bash
mvn clean install
```

### 3️⃣ Run examples

#### Run the IoC Framework demo:
```bash
cd fw_ioc
mvn exec:java -Dexec.mainClass="com.meocoder.ioc.Main"
```

#### Run the ORM Framework demo:
```bash
cd fw_orm
mvn exec:java -Dexec.mainClass="com.meocoder.orm.Main"
```

---

## 🧩 Modules Breakdown

### 🔹 **reflection/**
> Explore Java’s runtime type system and reflection mechanisms.

- Work with `Class`, `Field`, `Method`, and `Constructor`
- Dynamically instantiate and invoke classes
- Retrieve annotations and metadata at runtime

Example:
```java
Class<?> clazz = Class.forName("com.meocoder.demo.MyService");
Object instance = clazz.getDeclaredConstructor().newInstance();
Method method = clazz.getMethod("sayHello");
method.invoke(instance);
```

---

### 🔹 **anotation/**
> Custom annotations used across the IoC and ORM frameworks.

Example:
```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Service {
    String value() default "";
}
```

Used in combination with reflection to detect and register components automatically.

---

### 🔹 **fw_ioc/**
> A lightweight **Inversion of Control (IoC)** / **Dependency Injection** container.

- Scans packages for annotated components (`@Service`, `@Component`)
- Automatically injects dependencies (`@Autowired`)
- Supports singleton bean management
- Mimics **Spring Core** architecture

Example:
```java
@Service
public class UserService {
    @Autowired
    private UserRepository repository;

    public void printUsers() {
        repository.findAll().forEach(System.out::println);
    }
}
```

---

### 🔹 **fw_orm/**
> A simple **Object Relational Mapping (ORM)** framework built with JDBC and reflection.

- Maps Java objects to database tables using annotations (`@Entity`, `@Column`)
- Handles CRUD operations (insert, update, select)
- Uses reflection to map `ResultSet` → entity objects
- Can be extended with transaction and connection management

Example:
```java
@Entity(table = "users")
public class User {
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;
}
```

Basic CRUD:
```java
EntityManager em = new EntityManager();
em.save(new User(1L, "Alice"));
List<User> users = em.findAll(User.class);
```

---

## 🧠 Learning Goals

- Deep dive into **Java Reflection** and **Annotation Processing**
- Understand **IoC / Dependency Injection** design
- Build an ORM layer from scratch
- Learn how modern frameworks (Spring, Hibernate) work under the hood
- Practice clean architecture & modular design principles

---

## 🛠️ Future Enhancements

- Add `fw_aop` module → implement **Aspect-Oriented Programming (AOP)**
- Introduce `BeanPostProcessor` and `ApplicationContext` for IoC
- Add database abstraction for multiple DBs (MySQL, PostgreSQL, etc.)
- Add configuration management & logging
- CLI generator for bootstrapping demo projects

---

## 📸 Example Architecture

```
+---------------------+
|  @Service classes   |
+----------+----------+
           |
           v
+----------+----------+
|  IoC Container       |
|  (BeanFactory)       |
+----------+-----------+
           |
           v
+----------+----------+
|  ORM Layer (Entity)  |
+----------------------+
```

---

## 👨‍💻 Author

**Nguyên Đinh (@nguyenvh2k)**
> Educational and research project — exploring how frameworks work internally.  
> Contributions, issues, and pull requests are always welcome!

---

## 📄 License

```
MIT License © 2025 Nguyên Đinh
Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the “Software”),
to deal in the Software without restriction, including without limitation
the rights to use, copy, modify, merge, publish, distribute, sublicense,
and/or sell copies of the Software.
```

---

⭐ **If you find this project helpful, please give it a star to support learning-based Java projects!**
