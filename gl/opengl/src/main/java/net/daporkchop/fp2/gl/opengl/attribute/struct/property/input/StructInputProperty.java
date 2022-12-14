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

package net.daporkchop.fp2.gl.opengl.attribute.struct.property.input;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.fp2.gl.opengl.attribute.struct.property.StructProperty;
import org.objectweb.asm.MethodVisitor;

import java.util.Map;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class StructInputProperty implements StructProperty.Fields {
    @NonNull
    private final String structName;
    @NonNull
    private final Map.Entry<String, StructProperty>[] properties;

    @Override
    public int fields() {
        return this.properties.length;
    }

    @Override
    public Map.Entry<String, StructProperty> field(int elementIndex) {
        return this.properties[elementIndex];
    }

    @Override
    public void load(@NonNull MethodVisitor mv, int structLvtIndex, int lvtIndexAllocator, @NonNull LoadCallback callback) {
        callback.accept(structLvtIndex, lvtIndexAllocator);
    }
}
