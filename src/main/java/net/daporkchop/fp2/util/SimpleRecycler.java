/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2020-2020 DaPorkchop_
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

package net.daporkchop.fp2.util;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * A super trivial, unsafe recycler for re-usable objects.
 * <p>
 * Not thread-safe.
 *
 * @author DaPorkchop_
 */
public abstract class SimpleRecycler<V> {
    protected final Deque<V> stack = new ArrayDeque<>();

    public V allocate() {
        return this.stack.isEmpty()
                ? Objects.requireNonNull(this.allocate0(), "allocate0 returned null!")
                : this.stack.pop();
    }

    protected abstract V allocate0();

    public void release(@NonNull V value) {
        this.reset0(value);
        this.stack.push(value);
    }

    protected abstract void reset0(@NonNull V value);

    /**
     * Implementation of {@link SimpleRecycler} which is able to handle {@link IReusablePersistent} values.
     * <p>
     * Values are created using a {@link Supplier} provided at construction time.
     *
     * @param <V> the value type
     * @author DaPorkchop_
     */
    @RequiredArgsConstructor
    public static class OfReusablePersistent<V extends IReusablePersistent> extends SimpleRecycler<V> {
        @NonNull
        protected final Supplier<V> factory;

        @Override
        protected V allocate0() {
            return this.factory.get();
        }

        @Override
        protected void reset0(@NonNull V value) {
            value.reset();
        }
    }
}
