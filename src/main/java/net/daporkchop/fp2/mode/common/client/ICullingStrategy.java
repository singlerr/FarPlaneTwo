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

package net.daporkchop.fp2.mode.common.client;

import lombok.NonNull;
import net.daporkchop.fp2.client.VanillaRenderabilityTracker;
import net.daporkchop.fp2.client.gl.shader.ComputeShaderBuilder;
import net.daporkchop.fp2.mode.api.IFarPos;

/**
 * General-purpose functions for tile culling and selection.
 *
 * @author DaPorkchop_
 */
public interface ICullingStrategy<POS extends IFarPos> {
    /**
     * @return a {@link ComputeShaderBuilder} for a compute shader which can do selection for this position type
     */
    ComputeShaderBuilder cullShaderBuilder();

    /**
     * Checks whether or not the tile at the given off-heap position shouldn't be rendered because it would intersect vanilla terrain.
     *
     * @param tracker a {@link VanillaRenderabilityTracker} to use for checking vanilla renderability
     * @param pos     the address of the off-heap tile position
     * @return whether or not the tile at the given off-heap position shouldn't be rendered because it would intersect vanilla terrain
     */
    boolean blockedByVanilla(@NonNull VanillaRenderabilityTracker tracker, long pos);
}
