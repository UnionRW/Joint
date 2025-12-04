package cn.tesseract.vml;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.ProtectionDomain;

public class VacuumModLoader {
    public static ClassLoader transformerLoader;
    public static Class transformerClass;
    public static Method transformMethod;

    static {
        try {
            var urls = new URL[]{new File("D:\\0WorkSpace\\RW-HPS\\VML\\build\\libs\\VML-all.jar").toURI().toURL()};
            transformerLoader = new URLClassLoader(urls, null) {
                @Override
                protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
                    try {
                        return super.loadClass(name, resolve);
                    } catch (ClassNotFoundException e) {
                        return findClass(name);
                    }
                }
            };
            transformerClass = transformerLoader.loadClass("cn.tesseract.vml.VacuumTransformer");
            transformMethod = transformerClass.getDeclaredMethod("transform", byte[].class);
        } catch (MalformedURLException | ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static void premain(String agentArgs, Instrumentation inst) {

        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                try {
                    return (byte[]) transformMethod.invoke(null, classfileBuffer);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}