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

package net.daporkchop.fp2.gl.opengl.attribute.common;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.fp2.gl.attribute.AttributeBuffer;
import net.daporkchop.fp2.gl.opengl.attribute.BaseAttributeBufferImpl;
import net.daporkchop.fp2.gl.opengl.command.AbstractCommandBufferBuilder;
import net.daporkchop.fp2.gl.opengl.command.CodegenArgs;
import net.daporkchop.fp2.gl.opengl.command.methodwriter.FieldHandle;
import net.daporkchop.fp2.gl.opengl.command.methodwriter.MethodWriter;
import net.daporkchop.fp2.gl.opengl.command.state.CowState;
import net.daporkchop.fp2.gl.opengl.command.state.State;
import net.daporkchop.fp2.gl.opengl.command.state.StateProperty;
import net.daporkchop.fp2.gl.opengl.command.uop.BaseUop;
import net.daporkchop.fp2.gl.opengl.command.uop.Uop;

import java.util.List;
import java.util.stream.Stream;

import static net.daporkchop.lib.common.util.PValidation.*;
import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Type.*;

/**
 * @author DaPorkchop_
 */
@Getter
public abstract class AttributeBufferImpl<F extends AttributeFormatImpl<F, S, ?>, S> extends BaseAttributeBufferImpl<F> implements AttributeBuffer<S> {
    public AttributeBufferImpl(@NonNull F format) {
        super(format);
    }

    public List<Uop> copyTo(@NonNull AttributeBufferImpl<F, S> dst) {
        //TODO: this could be FAR more optimized
        checkArg(this.getClass() == dst.getClass(), "cannot copy from %s to %s", this.getClass(), dst.getClass());

        return ImmutableList.of(new BaseUop(new CowState()) {
            @Override
            protected Stream<StateProperty> dependsFirst() {
                return Stream.empty();
            }

            @Override
            public void emitCode(@NonNull State effectiveState, @NonNull AbstractCommandBufferBuilder builder, @NonNull MethodWriter<CodegenArgs> writer) {
                FieldHandle<AttributeBufferImpl<?, ?>> srcHandle = builder.makeFieldHandle(getType(dst.getClass()), AttributeBufferImpl.this);
                FieldHandle<AttributeBufferImpl<?, ?>> dstHandle = builder.makeFieldHandle(getType(dst.getClass()), dst);

                writer.write((mv, args) -> {
                    dstHandle.get(mv);
                    srcHandle.get(mv);
                    mv.visitMethodInsn(INVOKEVIRTUAL, getInternalName(dst.getClass()), "setContentsFrom", getMethodDescriptor(VOID_TYPE, getType(AttributeBuffer.class)), false);
                });
            }
        });
    }
}
