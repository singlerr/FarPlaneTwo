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

package net.daporkchop.fp2.compat.vanilla.asyncblockaccess;

import io.github.opencubicchunks.cubicchunks.api.world.ICube;
import lombok.NonNull;
import lombok.SneakyThrows;
import net.daporkchop.fp2.compat.vanilla.IBlockHeightAccess;
import net.daporkchop.fp2.compat.vanilla.region.ThreadSafeRegionFileCache;
import net.daporkchop.fp2.server.worldlistener.IWorldChangeListener;
import net.daporkchop.fp2.server.worldlistener.WorldChangeListenerManager;
import net.daporkchop.fp2.util.datastructure.Datastructures;
import net.daporkchop.fp2.util.datastructure.NDimensionalIntSegtreeSet;
import net.daporkchop.fp2.util.threading.ThreadingHelper;
import net.daporkchop.fp2.util.threading.asyncblockaccess.AsyncCacheNBTBase;
import net.daporkchop.fp2.util.threading.asyncblockaccess.IAsyncBlockAccess;
import net.daporkchop.fp2.util.threading.futurecache.GenerationNotAllowedException;
import net.daporkchop.fp2.util.threading.futurecache.IAsyncCache;
import net.daporkchop.fp2.util.threading.lazy.LazyFutureTask;
import net.daporkchop.lib.common.function.throwing.ERunnable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;

import java.io.IOException;
import java.util.function.Function;
import java.util.stream.Stream;

import static net.daporkchop.lib.common.util.PorkUtil.*;

/**
 * Default implementation of {@link IAsyncBlockAccess} for vanilla worlds.
 *
 * @author DaPorkchop_
 */
public class VanillaAsyncBlockAccessImpl implements IAsyncBlockAccess, IWorldChangeListener {
    protected final WorldServer world;
    protected final AnvilChunkLoader io;

    protected final ChunkCache chunks = new ChunkCache();

    protected final NDimensionalIntSegtreeSet chunksExistCache;

    public VanillaAsyncBlockAccessImpl(@NonNull WorldServer world) {
        this.world = world;
        this.io = (AnvilChunkLoader) this.world.getChunkProvider().chunkLoader;

        this.chunksExistCache = Datastructures.INSTANCE.nDimensionalIntSegtreeSet()
                .dimensions(2)
                .threadSafe(true)
                .initialPoints(() -> ThreadSafeRegionFileCache.INSTANCE.allChunks(this.io.chunkSaveLocation.toPath().resolve("region"))
                        .map(pos -> new int[]{ pos.x, pos.z })
                        .parallel())
                .build();

        WorldChangeListenerManager.add(this.world, this);
    }

    @Override
    public IBlockHeightAccess prefetch(@NonNull Stream<ChunkPos> columns) {
        //collect all futures into a list first in order to issue all tasks at once before blocking, thus ensuring maximum parallelism
        LazyFutureTask<Chunk>[] chunkFutures = uncheckedCast(columns.map(pos -> this.chunks.get(pos, true)).toArray(LazyFutureTask[]::new));

        return new PrefetchedColumnsVanillaAsyncBlockAccess(this, this.world, true, LazyFutureTask.scatterGather(chunkFutures).stream());
    }

    @Override
    public IBlockHeightAccess prefetchWithoutGenerating(@NonNull Stream<ChunkPos> columns) throws GenerationNotAllowedException {
        //collect all futures into a list first in order to issue all tasks at once before blocking, thus ensuring maximum parallelism
        LazyFutureTask<Chunk>[] chunkFutures = uncheckedCast(columns.map(pos -> this.chunks.get(pos, false)).toArray(LazyFutureTask[]::new));

        return new PrefetchedColumnsVanillaAsyncBlockAccess(this, this.world, false, LazyFutureTask.scatterGather(chunkFutures).stream()
                .peek(GenerationNotAllowedException.throwIfNull()));
    }

    @Override
    public IBlockHeightAccess prefetch(@NonNull Stream<ChunkPos> columns, @NonNull Function<IBlockHeightAccess, Stream<Vec3i>> cubesMappingFunction) {
        return this.prefetch(columns); //silently ignore cubes
    }

    @Override
    public IBlockHeightAccess prefetchWithoutGenerating(@NonNull Stream<ChunkPos> columns, @NonNull Function<IBlockHeightAccess, Stream<Vec3i>> cubesMappingFunction) throws GenerationNotAllowedException {
        return this.prefetchWithoutGenerating(columns); //silently ignore cubes
    }

    @Override
    public void onColumnSaved(@NonNull World world, int columnX, int columnZ, @NonNull NBTTagCompound nbt, @NonNull Chunk column) {
        this.chunksExistCache.add(columnX, columnZ);
        this.chunks.notifyUpdate(new ChunkPos(columnX, columnZ), nbt);
    }

    @Override
    public boolean anyColumnIntersects(int tileX, int tileZ, int level) {
        return this.chunksExistCache.containsAny(level, tileX, tileZ);
    }

    @Override
    public boolean anyCubeIntersects(int tileX, int tileY, int tileZ, int level) {
        return tileY < 16 && level >= 0 && this.anyColumnIntersects(tileX, tileZ, level);
    }

    @Override
    public void onCubeSaved(@NonNull World world, int cubeX, int cubeY, int cubeZ, @NonNull NBTTagCompound nbt, @NonNull ICube cube) {
        throw new UnsupportedOperationException("vanilla world shouldn't have cubes!");
    }

    protected Chunk getChunk(int chunkX, int chunkZ, boolean allowGeneration) {
        return GenerationNotAllowedException.throwIfNull(this.chunks.get(new ChunkPos(chunkX, chunkZ), allowGeneration).join());
    }

    @Override
    public int getTopBlockY(int blockX, int blockZ, boolean allowGeneration) {
        return this.getChunk(blockX >> 4, blockZ >> 4, allowGeneration).getHeightValue(blockX & 0xF, blockZ & 0xF) - 1;
    }

    @Override
    public int getTopBlockYBelow(int blockX, int blockY, int blockZ, boolean allowGeneration) {
        throw new UnsupportedOperationException("Not implemented"); //TODO: i could actually write an implementation for this
    }

    @Override
    public int getBlockLight(BlockPos pos, boolean allowGeneration) {
        if (!this.world.isValid(pos)) {
            return 0;
        } else {
            return this.getChunk(pos.getX() >> 4, pos.getZ() >> 4, allowGeneration).getLightFor(EnumSkyBlock.BLOCK, pos);
        }
    }

    @Override
    public int getSkyLight(BlockPos pos, boolean allowGeneration) {
        if (!this.world.provider.hasSkyLight()) {
            return 0;
        } else if (!this.world.isValid(pos)) {
            return 15;
        } else {
            return this.getChunk(pos.getX() >> 4, pos.getZ() >> 4, allowGeneration).getLightFor(EnumSkyBlock.SKY, pos);
        }
    }

    @Override
    public IBlockState getBlockState(BlockPos pos, boolean allowGeneration) {
        return this.getChunk(pos.getX() >> 4, pos.getZ() >> 4, allowGeneration).getBlockState(pos);
    }

    @Override
    public Biome getBiome(BlockPos pos, boolean allowGeneration) {
        return this.getChunk(pos.getX() >> 4, pos.getZ() >> 4, allowGeneration).getBiome(pos, null); //provider is not used on client
    }

    @Override
    public WorldType getWorldType() {
        return this.world.getWorldType();
    }

    /**
     * {@link IAsyncCache} for chunks.
     *
     * @author DaPorkchop_
     */
    protected class ChunkCache extends AsyncCacheNBTBase<ChunkPos, Object, Chunk> {
        //TODO: this doesn't handle the difference between "chunk is populated" and "chunk and its neighbors are populated", which is important because vanilla is very dumb

        @Override
        protected Chunk parseNBT(@NonNull ChunkPos key, @NonNull Object param, @NonNull NBTTagCompound nbt) {
            Chunk chunk = VanillaAsyncBlockAccessImpl.this.io.checkedReadChunkFromNBT(VanillaAsyncBlockAccessImpl.this.world, key.x, key.z, nbt);
            return chunk.isTerrainPopulated() ? chunk : null;
        }

        @Override
        @SneakyThrows(IOException.class)
        protected Chunk loadFromDisk(@NonNull ChunkPos key, @NonNull Object param) {
            Object[] data = VanillaAsyncBlockAccessImpl.this.io.loadChunk__Async(VanillaAsyncBlockAccessImpl.this.world, key.x, key.z);
            Chunk chunk = data != null ? (Chunk) data[0] : null;
            return chunk != null && chunk.isTerrainPopulated() ? chunk : null;
        }

        @Override
        protected void triggerGeneration(@NonNull ChunkPos key, @NonNull Object param) {
            ThreadingHelper.scheduleTaskInWorldThread(VanillaAsyncBlockAccessImpl.this.world, (ERunnable) () -> {
                int x = key.x;
                int z = key.z;

                //see net.minecraftforge.server.command.ChunkGenWorker#doWork() for more info

                Chunk chunk = VanillaAsyncBlockAccessImpl.this.world.getChunk(x, z);
                VanillaAsyncBlockAccessImpl.this.world.getChunk(x + 1, z);
                VanillaAsyncBlockAccessImpl.this.world.getChunk(x + 1, z + 1);
                VanillaAsyncBlockAccessImpl.this.world.getChunk(x, z + 1);

                VanillaAsyncBlockAccessImpl.this.io.saveChunk(VanillaAsyncBlockAccessImpl.this.world, chunk);
            }).join();
        }
    }
}
