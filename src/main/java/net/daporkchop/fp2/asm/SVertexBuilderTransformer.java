package net.daporkchop.fp2.asm;


import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public final class SVertexBuilderTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if(transformedName.equals("net.optifine.shaders.SVertexBuilder")){
            ClassReader reader = new ClassReader(basicClass);
            ClassNode classNode = new ClassNode();
            reader.accept(classNode,0);
            for (MethodNode method : classNode.methods) {
                if(method.name.equals("pushEntity") && method.desc.equals("(Lawt;Let;Lamy;Lbuk;)V")){
                    AbstractInsnNode node = method.instructions.getFirst();
                    while (node != null){
                        if(node.getOpcode() == Opcodes.INVOKEVIRTUAL){
                            MethodInsnNode methodInsnNode = (MethodInsnNode) node;
                            if(methodInsnNode.name.equals("pushEntity")){
                                //method.instructions.remove(methodInsnNode);
                                System.out.println("Removed pushEntity");
                            }

                        }
                        node = node.getNext();
                    }
                }
                if(method.name.equals("popEntity") && method.desc.equals("(Lbuk;)V")){
                    AbstractInsnNode node = method.instructions.getFirst();
                    while (node != null){
                        if(node.getOpcode() == Opcodes.INVOKEVIRTUAL){
                            MethodInsnNode methodInsnNode = (MethodInsnNode) node;
                            if(methodInsnNode.name.equals("popEntity")){
                                //method.instructions.remove(methodInsnNode);
                                System.out.println("Removed popEntity");
                            }

                        }
                        node = node.getNext();
                    }
                }
            }
        }
        /*
        if(transformedName.equals("net.minecraft.client.renderer.BlockModelRenderer")){
            ClassReader reader = new ClassReader(basicClass);
            ClassNode classNode = new ClassNode();
            reader.accept(classNode,0);

            String desc = "(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/client/renderer/block/model/IBakedModel;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/renderer/BufferBuilder;ZJ)Z";

            for (MethodNode methodNode : classNode.methods){
                if(methodNode.desc.equals(desc) && methodNode.name.equals("renderModel")){
                    AbstractInsnNode node = methodNode.instructions.getFirst();
                    while (node != null){
                        if(node instanceof MethodInsnNode){
                            MethodInsnNode methodInsnNode = (MethodInsnNode) node;
                            System.out.println(methodInsnNode.name+methodInsnNode.desc);
                        }
                        node = node.getNext();
                    }
                }
            }
        }

         */
        return basicClass;
    }
}
