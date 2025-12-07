package cn.tesseract.union;

import net.fabricmc.tinyremapper.Main;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class JarTransformer {
    public static File dir = new File(System.getProperty("user.dir"));

    public static void main(String[] args) {
        System.out.println(dir);
        File jar = new File(dir, "game-lib/libs/game-lib.bak.jar"),
                mapped = new File(dir, "game-lib/libs/game-lib.jar"),
                transformed = new File(dir, "Server-Core/libs/game-lib.jar");


        mapped.delete();
        transformed.delete();

        Main.main(new String[]{
                jar.toPath().toString(),
                mapped.toPath().toString(),
                new File(dir, "game-lib/libs/pc.tiny").toPath().toString(),
                "intermediary", "named"});

        try (JarOutputStream newJar = new JarOutputStream(Files.newOutputStream(transformed.toPath()))) {
            ZipInputStream oldJar = new ZipInputStream(Files.newInputStream(mapped.toPath()));

            ZipEntry entry;
            while ((entry = oldJar.getNextEntry()) != null) {
                String name = entry.getName();
                newJar.putNextEntry(new JarEntry(name));
                byte[] data = readEntryBytes(oldJar);


                if (name.endsWith(".class") && name.startsWith("com/corrodinggames/")) {
                    String className = name.substring(0, name.length() - 6);

                    ClassNode node = new ClassNode();
                    ClassReader classReader = new ClassReader(data);

                    classReader.accept(node, ClassReader.EXPAND_FRAMES);
                    node.access = ~(~node.access | Modifier.FINAL | Modifier.PRIVATE | Modifier.PROTECTED) | Modifier.PUBLIC;
                    for (MethodNode method : node.methods)
                        method.access = ~(~method.access | Modifier.PRIVATE | Modifier.PROTECTED) | Modifier.PUBLIC;
                    for (FieldNode field : node.fields)
                        field.access = ~(~field.access | Modifier.PRIVATE | Modifier.PROTECTED) | Modifier.PUBLIC;


                    ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                    node.accept(classWriter);
                    data = classWriter.toByteArray();
                }
                newJar.write(data);

                newJar.closeEntry();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static byte[] readEntryBytes(ZipInputStream jar) throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        while ((bytesRead = jar.read(buffer)) != -1) {
            bytes.write(buffer, 0, bytesRead);
        }

        return bytes.toByteArray();
    }
}
