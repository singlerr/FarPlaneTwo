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

package net.daporkchop.fp2.gl.opengl.transform;

import com.google.common.collect.ImmutableSet;
import lombok.NonNull;
import net.daporkchop.fp2.gl.opengl.OpenGL;
import net.daporkchop.fp2.gl.attribute.AttributeUsage;
import net.daporkchop.fp2.gl.opengl.layout.BaseLayoutBuilderImpl;
import net.daporkchop.fp2.gl.transform.TransformLayout;
import net.daporkchop.fp2.gl.transform.TransformLayoutBuilder;

import java.util.EnumSet;
import java.util.Set;

/**
 * @author DaPorkchop_
 */
public class TransformLayoutBuilderImpl extends BaseLayoutBuilderImpl<TransformLayoutBuilder, TransformLayout> implements TransformLayoutBuilder {
    private static final Set<AttributeUsage> VALID_USAGES = ImmutableSet.copyOf(EnumSet.of(
            AttributeUsage.UNIFORM,
            AttributeUsage.UNIFORM_ARRAY,
            AttributeUsage.TRANSFORM_INPUT,
            AttributeUsage.TRANSFORM_OUTPUT,
            AttributeUsage.TEXTURE
    ));

    public TransformLayoutBuilderImpl(@NonNull OpenGL gl) {
        super(gl);
    }

    @Override
    protected Set<AttributeUsage> validUsages() {
        return VALID_USAGES;
    }

    @Override
    public TransformLayout build() {
        return new TransformLayoutImpl(this);
    }
}
