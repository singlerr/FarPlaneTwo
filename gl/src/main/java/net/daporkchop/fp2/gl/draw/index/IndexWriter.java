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

package net.daporkchop.fp2.gl.draw.index;

import net.daporkchop.fp2.common.util.capability.CloseableResource;

/**
 * A buffer in client memory which is used for building sequences of index data.
 *
 * @author DaPorkchop_
 */
public interface IndexWriter extends CloseableResource {
    /**
     * @return the {@link IndexFormat} used by this writer
     */
    IndexFormat format();

    /**
     * @return the number of indices
     */
    int size();

    /**
     * Appends a single index to this writer.
     *
     * @param index the index
     */
    IndexWriter append(int index);

    /**
     * Appends 4 indices to this writer, forming a single quad.
     *
     * @param oppositeCorner the index of the vertex in the corner opposite the provoking vertex
     * @param c0             the index of one of the edge vertices
     * @param c1             the index of the other edge vertex
     * @param provoking      the index of the provoking vertex
     */
    default IndexWriter appendQuad(int oppositeCorner, int c0, int c1, int provoking) {
        return this.append(c1).append(oppositeCorner).append(c0).append(provoking);
    }

    /**
     * Appends 6 indices to this writer, forming a single quad consisting of two triangles.
     *
     * @param oppositeCorner the index of the vertex in the corner opposite the provoking vertex
     * @param c0             the index of one of the edge vertices
     * @param c1             the index of the other edge vertex
     * @param provoking      the index of the provoking vertex
     */
    default IndexWriter appendQuadAsTriangles(int oppositeCorner, int c0, int c1, int provoking) {
        return this.append(oppositeCorner).append(c0).append(provoking) //first triangle
                .append(c1).append(oppositeCorner).append(provoking); //second triangle
    }
}
