/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2020-2021 DaPorkchop_
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

package net.daporkchop.fp2.common.util.alloc;

import lombok.NonNull;

import java.util.BitSet;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * A simple, efficient memory allocator for fixed-size units of memory backed by a sequential heap.
 *
 * @author DaPorkchop_
 */
public final class SequentialFixedSizeAllocator extends BitSet implements Allocator { //extend BitSet to eliminate an indirection
    /*
     * Performance characteristics (N=capacity):
     *
     * alloc():
     *   - O(1) (best-case)
     *   - O(N/2) (average)
     *   - O(N) (worst-case)
     * free():
     *   - O(1)
     */

    protected final long blockSize;
    protected final GrowFunction growFunction;
    protected final SequentialHeapManager manager;
    protected long capacity = 0L;
    protected int fromIndex = 0;

    public SequentialFixedSizeAllocator(long blockSize, @NonNull SequentialHeapManager manager) {
        this(blockSize, manager, GrowFunction.DEFAULT);
    }

    public SequentialFixedSizeAllocator(long blockSize, @NonNull SequentialHeapManager manager, @NonNull GrowFunction growFunction) {
        this.blockSize = positive(blockSize, "blockSize");
        this.manager = manager;
        this.growFunction = growFunction;
    }

    @Override
    public long alloc(long size) {
        checkArg(size == this.blockSize, "size must be exactly block size (%d)", this.blockSize);
        int slot = this.fromIndex;
        this.set(slot);
        this.fromIndex = this.nextClearBit(slot + 1);

        long addr = slot * this.blockSize;
        if (addr >= this.capacity) {
            long oldCapacity = this.capacity;
            this.capacity = this.growFunction.grow(this.capacity, this.blockSize);

            if (oldCapacity == 0L) {
                this.manager.brk(this.capacity);
            } else {
                this.manager.sbrk(this.capacity);
            }
        }
        return addr;
    }

    @Override
    public void free(long address) {
        int slot = toInt(address / this.blockSize);
        this.clear(slot);
        if (slot < this.fromIndex) {
            this.fromIndex = slot;
        }
    }

    @Override
    public Stats stats() {
        long allocations = this.cardinality();

        return Stats.builder()
                .heapRegions(1L)
                .allocations(allocations)
                .allocatedSpace(allocations * this.blockSize)
                .totalSpace(this.capacity)
                .build();
    }
}
