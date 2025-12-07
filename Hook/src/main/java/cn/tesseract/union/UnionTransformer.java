package cn.tesseract.union;

import cn.tesseract.union.asm.Accessor;
import cn.tesseract.union.asm.HookClassTransformer;
import cn.tesseract.union.asm.NodeTransformer;
import cn.tesseract.union.asm.SafeClassWriter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class UnionTransformer extends HookClassTransformer {
    public final HashMap<String, List<NodeTransformer>> nodeTransformers = new HashMap<>();

    public UnionTransformer() {
        try (var reader = new BufferedReader(new InputStreamReader(UnionTransformer.class.getResourceAsStream("/hook.txt")))) {
            String line;
            while ((line = reader.readLine()) != null) registerHookContainer(line);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try (var reader = new BufferedReader(new InputStreamReader(UnionTransformer.class.getResourceAsStream("/accessor.txt")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Accessor accessor = new Accessor(classMetadataReader.getClassData(line));
                registerNodeTransformer(accessor.target, accessor);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        registerNodeTransformer("com.corrodinggames.rts.union.gameFramework.j.class_1001", node -> {
            for (MethodNode method : node.methods) {
                for (int i = 0; i < method.instructions.size(); i++) {
                    if (method.name.equals("method_2737")) {
                        var j = method.instructions.get(i);
                        if (j instanceof MethodInsnNode) {
                            var insn = ((MethodInsnNode) j);
                            if (insn.name.equals("method_2765")) {
                                insn.name = "onPlayerJoin";
                            }
                        }
                    }
                }
            }
        });
    }

    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] bytecode) {
        bytecode = super.transform(loader, className, classBeingRedefined, protectionDomain, bytecode);
        List<NodeTransformer> transformers = nodeTransformers.get(className);

        if (transformers != null) {
            logger.debug("Applying node transformers for: " + className);

            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader(bytecode);

            classReader.accept(classNode, ClassReader.EXPAND_FRAMES);

            Iterator<NodeTransformer> it = transformers.iterator();
            while (it.hasNext()) {
                it.next().transform(classNode);
                it.remove();
            }

            ClassWriter classWriter = new SafeClassWriter(classReader, loader, ClassWriter.COMPUTE_MAXS);
            classNode.accept(classWriter);

            bytecode = classWriter.toByteArray();
        }
        return bytecode;
    }

    public void registerNodeTransformer(String className, NodeTransformer transformer) {
        List<NodeTransformer> list = nodeTransformers.computeIfAbsent(className.replace('.', '/'), k -> new ArrayList<>());
        list.add(transformer);
    }
}
