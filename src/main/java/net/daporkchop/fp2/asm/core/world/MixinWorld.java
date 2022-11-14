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

package net.daporkchop.fp2.asm.core.world;

import net.daporkchop.fp2.mode.api.ctx.IFarWorld;
import net.daporkchop.fp2.util.Constants;
import net.daporkchop.fp2.util.math.IntAxisAlignedBB;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.stream.IntStream;

import static net.daporkchop.fp2.util.Constants.*;
import static net.daporkchop.fp2.util.math.MathUtil.*;
import static net.daporkchop.lib.common.util.PValidation.*;
import static net.daporkchop.lib.common.util.PorkUtil.*;

/**
 * @author DaPorkchop_
 */
@Mixin(World.class)
public abstract class MixinWorld implements IFarWorld {
    @Unique
    protected IntAxisAlignedBB[] fp2_coordLimits;

    @Unique
    protected boolean isInitialized() {
        return this.fp2_coordLimits != null;
    }

    @Override
    public IntAxisAlignedBB[] fp2_IFarWorld_coordLimits() {
        IntAxisAlignedBB[] coordLimits = this.fp2_coordLimits;
        checkState(this.isInitialized(), "not initialized!");
        return coordLimits;
    }

    @Override
    public void fp2_IFarWorld_init() {
        checkState(!this.isInitialized(), "already initialized!");

        IntAxisAlignedBB bounds = Constants.getBounds(uncheckedCast(this));
        this.fp2_coordLimits = IntStream.range(0, Integer.SIZE)
                .mapToObj(lvl -> new IntAxisAlignedBB(
                        asrFloor(bounds.minX(), T_SHIFT + lvl),
                        asrFloor(bounds.minY(), T_SHIFT + lvl),
                        asrFloor(bounds.minZ(), T_SHIFT + lvl),
                        asrCeil(bounds.maxX(), T_SHIFT + lvl),
                        asrCeil(bounds.maxY(), T_SHIFT + lvl),
                        asrCeil(bounds.maxZ(), T_SHIFT + lvl)))
                .toArray(IntAxisAlignedBB[]::new);
    }
}
