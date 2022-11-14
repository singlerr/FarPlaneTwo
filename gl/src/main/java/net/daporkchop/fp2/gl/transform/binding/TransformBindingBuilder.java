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

package net.daporkchop.fp2.gl.transform.binding;

import lombok.NonNull;
import net.daporkchop.fp2.gl.attribute.AttributeBuffer;
import net.daporkchop.fp2.gl.attribute.AttributeUsage;
import net.daporkchop.fp2.gl.layout.binding.BaseBindingBuilder;

/**
 * Builder for {@link TransformBinding}s.
 *
 * @author DaPorkchop_
 */
public interface TransformBindingBuilder extends BaseBindingBuilder<TransformBindingBuilder, TransformBinding> {
    /**
     * Adds a {@link AttributeBuffer} which contains inputs.
     * <p>
     * Alias for {@code with(AttributeUsage.TRANSFORM_INPUT, buffer)}.
     *
     * @param buffer the {@link AttributeBuffer} containing the inputs
     * @see BaseBindingBuilder#with(AttributeUsage, net.daporkchop.fp2.gl.attribute.BaseAttributeBuffer)
     */
    default TransformBindingBuilder withInput(@NonNull AttributeBuffer<?> buffer) {
        return this.with(AttributeUsage.TRANSFORM_INPUT, buffer);
    }

    /**
     * Adds a {@link AttributeBuffer} which contains outputs.
     * <p>
     * Alias for {@code with(AttributeUsage.TRANSFORM_OUTPUT, buffer)}.
     *
     * @param buffer the {@link AttributeBuffer} containing the outputs
     * @see BaseBindingBuilder#with(AttributeUsage, net.daporkchop.fp2.gl.attribute.BaseAttributeBuffer)
     */
    default TransformBindingBuilder withOutput(@NonNull AttributeBuffer<?> buffer) {
        return this.with(AttributeUsage.TRANSFORM_OUTPUT, buffer);
    }
}
