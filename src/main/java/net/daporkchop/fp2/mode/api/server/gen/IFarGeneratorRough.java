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

package net.daporkchop.fp2.mode.api.server.gen;

import lombok.NonNull;
import net.daporkchop.fp2.mode.api.IFarPos;
import net.daporkchop.fp2.mode.api.IFarTile;
import net.minecraft.world.WorldServer;

/**
 * Extracts height and color information from a world for use by a rendering mode.
 * <p>
 * Once initialized, instances of this class are expected to be safely usable by multiple concurrent threads.
 *
 * @author DaPorkchop_
 */
public interface IFarGeneratorRough<POS extends IFarPos, T extends IFarTile> extends IFarGenerator {
    /**
     * @return whether or not this generator can generate tiles at low resolution
     */
    boolean supportsLowResolution();

    /**
     * Generates a rough estimate of the terrain in the given tile.
     *
     * @param pos  the position of the tile to generate
     * @param tile the tile to generate
     */
    void generate(@NonNull POS pos, @NonNull T tile);

    /**
     * Factory method for creating instances of {@link IFarGeneratorRough}.
     *
     * @author DaPorkchop_
     */
    @FunctionalInterface
    interface Factory<POS extends IFarPos, T extends IFarTile> {
        /**
         * Creates a new {@link IFarGeneratorRough} in the given world.
         *
         * @param world the world
         * @return the new {@link IFarGeneratorRough}, or {@code null} if no generator could be created for the given world
         */
        IFarGeneratorRough<POS, T> forWorld(@NonNull WorldServer world);
    }
}
