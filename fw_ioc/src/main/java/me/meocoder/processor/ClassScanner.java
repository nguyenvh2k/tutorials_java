package me.meocoder.processor;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class ClassScanner {
    public static List<Class<?>> scan(String basePackage) {
        List<Class<?>> classes = new ArrayList<>();
        String path = basePackage.replace('.', '/');
        try {
            // Lấy tất cả URL tương ứng với package (có thể nhiều trong classpath)
            Enumeration<URL> resources = Thread.currentThread()
                    .getContextClassLoader()
                    .getResources(path);

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                File directory = new File(resource.getFile());
                if (directory.exists() && directory.isDirectory()) {
                    findClasses(directory, basePackage, classes);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to scan package " + basePackage, e);
        }
        return classes;
    }

    private static void findClasses(File directory, String packageName, List<Class<?>> classes) {
        File[] files = directory.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                // quét đệ quy sub-package
                findClasses(file, packageName + "." + file.getName(), classes);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                try {
                    Class<?> cls = Class.forName(className);
                    classes.add(cls);
                } catch (ClassNotFoundException e) {
                    // bỏ qua nếu không load được class
                }
            }
        }
    }

}
