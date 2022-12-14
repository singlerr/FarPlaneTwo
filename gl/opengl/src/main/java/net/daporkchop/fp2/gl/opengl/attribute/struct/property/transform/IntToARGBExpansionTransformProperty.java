/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2020-2022 DaPorkchop_
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
 * provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.fp2.gl.opengl.attribute.struct.property.transform;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.fp2.gl.opengl.attribute.struct.property.ComponentType;
import net.daporkchop.fp2.gl.opengl.attribute.struct.property.StructProperty;
import net.daporkchop.fp2.gl.opengl.attribute.struct.type.GLSLBasicType;
import net.daporkchop.fp2.gl.opengl.attribute.struct.type.GLSLTypeFactory;
import org.objectweb.asm.MethodVisitor;

import static net.daporkchop.lib.common.util.PValidation.*;
import static org.objectweb.asm.Opcodes.*;

/**
 * @author DaPorkchop_
 */
@Getter
public class IntToARGBExpansionTransformProperty implements StructProperty.Components {
    private final Components parent;
    private final boolean alpha;

    public IntToARGBExpansionTransformProperty(@NonNull Components parent, boolean alpha) {
        checkArg(parent.componentType() == ComponentType.INT, "parent component type must be %s (given: %s)", ComponentType.INT, parent.componentType());
        checkArg(parent.components() == 1, "parent must have exactly one component (given: %d)", parent.components());

        this.parent = parent;
        this.alpha = alpha;
    }

    @Override
    public ComponentType componentType() {
        return ComponentType.UNSIGNED_BYTE;
    }

    @Override
    public GLSLBasicType glslType() {
        return GLSLTypeFactory.vec(this.componentType().glslPrimitive(), this.components());
    }

    @Override
    public int cols() {
        return 1;
    }

    @Override
    public int rows() {
        return this.alpha ? 4 : 3;
    }

    @Override
    public void load(@NonNull MethodVisitor mv, int structLvtIndexIn, int lvtIndexAllocatorIn, @NonNull LoadCallback callback) {
        this.parent.load(mv, structLvtIndexIn, lvtIndexAllocatorIn, (structLvtIndexFromParent, lvtIndexAllocatorFromParent, loader) -> {
            int argbLvtIndex = lvtIndexAllocatorFromParent++;

            //load the 0th component (which is an int) and store it in the LVT
            loader.load(structLvtIndexFromParent, lvtIndexAllocatorFromParent, 0);
            mv.visitVarInsn(ISTORE, argbLvtIndex);

            callback.accept(argbLvtIndex, lvtIndexAllocatorFromParent, (structLvtIndex, lvtIndexAllocator, componentIndex) -> {
                checkIndex(this.components(), componentIndex);

                mv.visitVarInsn(ILOAD, argbLvtIndex);

                mv.visitLdcInsn((((2 - componentIndex) & 3) << 3));
                mv.visitInsn(ISHR);
                mv.visitLdcInsn(0xFF);
                mv.visitInsn(IAND);
            });
        });
    }
}
