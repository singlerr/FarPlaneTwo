package net.daporkchop.fp2.util;


import lombok.experimental.UtilityClass;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.BlockPos;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@UtilityClass
public class BufferBuilderCache {
    private static final AtomicInteger count = new AtomicInteger();
    public static final BufferBuilder CONSTANT_BUFFER_BUILDER = new BufferBuilder(2097152);
    private static final Deque<BufferBuilder> cache = new LinkedList<>();

    public BufferBuilder pop(){
        return cache.pop();
    }

    public void push(BufferBuilder bufferBuilder){
        cache.push(bufferBuilder);
    }

    public static boolean isEmpty(){
        return count.get() == 0;
    }
    public static void pushBufferBuilder(){
        count.incrementAndGet();
    }

    public static void popBufferBuilder(){
        count.decrementAndGet();
    }
    public static void doUntilEmpty(Consumer<BufferBuilder> consumer){
        while (count.getAndDecrement() > 0){
            consumer.accept(CONSTANT_BUFFER_BUILDER);
        }
    }
    public void popUntilEmpty(Consumer<BufferBuilder> function){
        try {
            while (!cache.isEmpty()) {
                BufferBuilder elem = cache.pop();
                function.accept(elem);
            }
        }catch (Exception ex){
            System.out.println("Ignored error");
        }
    }
}
