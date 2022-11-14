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

package net.daporkchop.fp2.mode.voxel.server.gen.exact;

import lombok.NonNull;
import mod.chiselsandbits.chiseledblock.TileEntityBlockChiseled;
import mod.chiselsandbits.helpers.ModUtil;
import net.daporkchop.fp2.compat.vanilla.FastRegistry;
import net.daporkchop.fp2.compat.vanilla.IBlockHeightAccess;
import net.daporkchop.fp2.mode.api.server.gen.IFarGeneratorExact;
import net.daporkchop.fp2.mode.voxel.VoxelData;
import net.daporkchop.fp2.mode.voxel.VoxelPos;
import net.daporkchop.fp2.mode.voxel.VoxelTile;
import net.daporkchop.fp2.mode.voxel.server.gen.AbstractVoxelGenerator;
import net.daporkchop.fp2.util.Constants;
import net.daporkchop.lib.common.reference.ReferenceStrength;
import net.daporkchop.lib.common.reference.cache.Cached;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;

import static java.lang.Math.*;
import static net.daporkchop.fp2.mode.voxel.VoxelConstants.*;
import static net.daporkchop.fp2.util.BlockType.*;
import static net.daporkchop.fp2.util.Constants.*;
import static net.daporkchop.fp2.util.math.MathUtil.*;
import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * @author DaPorkchop_
 */
public abstract class AbstractExactVoxelGenerator extends AbstractVoxelGenerator implements IFarGeneratorExact<VoxelPos, VoxelTile> {
    protected final Cached<int[]> stateMapCache = Cached.threadLocal(() -> new int[cb(CACHE_SIZE)], ReferenceStrength.WEAK);

    public AbstractExactVoxelGenerator(@NonNull WorldServer world) {
        super(world);
    }

    protected int[] populateStateMapFromWorld(@NonNull IBlockHeightAccess world, int baseX, int baseY, int baseZ) {
        int[] stateMap = this.stateMapCache.get();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        //range check here to allow JIT to avoid range checking inside the loop
        checkArg(stateMap.length >= cb(CACHE_SIZE));

        for (int i = 0, dx = CACHE_MIN; dx < CACHE_MAX; dx++) { //set each type flag depending on the block state at the corresponding position
            for (int dy = CACHE_MIN; dy < CACHE_MAX; dy++) {
                for (int dz = CACHE_MIN; dz < CACHE_MAX; dz++, i++) {
                    stateMap[i] = FastRegistry.getId(world.getBlockState(pos.setPos(baseX + dx, baseY + dy, baseZ + dz)));
                }
            }
        }

        return stateMap;
    }

    protected byte[] populateTypeMapFromStateMap(@NonNull int[] stateMap) {
        byte[] typeMap = this.typeMapCache.get();

        //range check here to allow JIT to avoid range checking inside the loop
        checkArg(typeMap.length >= cb(CACHE_SIZE) && stateMap.length >= cb(CACHE_SIZE));

        for (int i = 0; i < cb(CACHE_SIZE); i++) { //set each type flag depending on the block state at the corresponding position
            typeMap[i] = (byte) blockType(stateMap[i]);
        }

        return typeMap;
    }

    @Override
    public void generate(@NonNull IBlockHeightAccess world, @NonNull VoxelPos posIn, @NonNull VoxelTile tile) {
        final int baseX = posIn.blockX();
        final int baseY = posIn.blockY();
        final int baseZ = posIn.blockZ();

        int[] stateMap = this.populateStateMapFromWorld(world, baseX, baseY, baseZ);
        //use bit flags to identify voxel types rather than reading from the world each time to keep innermost loop head tight and cache-friendly
        byte[] CACHE = this.populateTypeMapFromStateMap(stateMap);

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        VoxelData data = new VoxelData();

        data.x = data.y = data.z = POS_ONE;

        for (int dx = 0; dx < T_VOXELS; dx++) {
            for (int dy = 0; dy < T_VOXELS; dy++) {
                for (int dz = 0; dz < T_VOXELS; dz++) {
                    int corners = 0;
                    for (int ciCache = cacheIndex(dx, dy, dz), i = 0; i < 8; i++) {
                        corners |= (CACHE[ciCache + CACHE_INDEX_ADD[i]] & 0xFF) << (i << 1);
                    }

                    if (corners == 0 || corners == 0x5555 || corners == 0xAAAA) { //if all corners are the same type, this voxel can be safely skipped
                        continue;
                    }

                    int edges = 0;
                    for (int edge = 0; edge < EDGE_COUNT; edge++) {
                        int c0 = EDGE_VERTEX_MAP[edge << 1] << 1;
                        int c1 = EDGE_VERTEX_MAP[(edge << 1) | 1] << 1;

                        if (((corners >> c0) & 3) == ((corners >> c1) & 3)) { //both corners along the current edge are identical, this edge can be skipped
                            continue;
                        }

                        if (((corners >> c0) & 3) < ((corners >> c1) & 3)) { //the face is facing towards negative coordinates
                            edges |= EDGE_DIR_NEGATIVE << (edge << 1);
                        } else {
                            edges |= EDGE_DIR_POSITIVE << (edge << 1);
                        }
                    }

                    data.edges = edges;

                    for (int edge = 0; edge < EDGE_COUNT; edge++) {
                        if ((edges & (EDGE_DIR_MASK << (edge << 1))) != EDGE_DIR_NONE) {
                            //((edges >> (edge << 1) >> 1) & 1) is 1 if the face is negative, 0 otherwise
                            int i = EDGE_VERTEX_MAP[(edge << 1) | ((edges >> (edge << 1) >> 1) & 1)];
                            pos.setPos(baseX + dx + ((i >> 2) & 1), baseY + dy + ((i >> 1) & 1), baseZ + dz + (i & 1));
                            TileEntityBlockChiseled tileEntity = ModUtil.getChiseledTileEntity(world,pos);
                            if(tileEntity != null){
                                data.states[edge] = tileEntity.getBitAccess().getVoxelStats().mostCommonState;
                            }else{
                                data.states[edge] = FastRegistry.getId(world.getBlockState(pos));
                            }


                        }
                    }

                    data.biome = FastRegistry.getId(world.getBiome(pos));

                    int skyLight = 0;
                    int blockLight = 0;
                    int samples = 0;
                    if (edges == 0) { //this voxel is only present as a dummy placeholder for other voxels to connect to
                        //compute average light levels for the least opaque block type intersecting this voxel

                        int type = BLOCK_TYPE_OPAQUE;
                        for (int i = 0; i < 8; i++) {
                            type = min(type, (corners >> (i << 1)) & 3);
                        }

                        for (int i = 0; i < 8; i++) {
                            if (((corners >> (i << 1)) & 3) == type) {
                                pos.setPos(baseX + dx + ((i >> 2) & 1), baseY + dy + ((i >> 1) & 1), baseZ + dz + (i & 1));
                                int light = world.getCombinedLight(pos, 0);
                                skyLight += light >> 20;
                                blockLight += (light >> 4) & 0xF;
                                samples++;
                            }
                        }
                    } else {
                        //compute average light levels for the "less opaque" side of all non-transparent faces
                        for (int edge = 0; edge < EDGE_COUNT; edge++) {
                            if ((edges & (EDGE_DIR_MASK << (edge << 1))) != EDGE_DIR_NONE) {
                                int i = EDGE_VERTEX_MAP[(edge << 1) | (~(edges >> (edge << 1) >> 1) & 1)];
                                pos.setPos(baseX + dx + ((i >> 2) & 1), baseY + dy + ((i >> 1) & 1), baseZ + dz + (i & 1));
                                int light = world.getCombinedLight(pos, 0);
                                skyLight += light >> 20;
                                blockLight += (light >> 4) & 0xF;
                                samples++;
                            }
                        }
                    }
                    if (samples > 1) {
                        skyLight /= samples;
                        blockLight /= samples;
                    }
                    data.light = Constants.packCombinedLight(skyLight << 20 | blockLight << 4);

                    tile.set(dx, dy, dz, data);
                }
            }
        }

        //TODO: compute neighbor connections
        tile.extra(0L);
    }
}
