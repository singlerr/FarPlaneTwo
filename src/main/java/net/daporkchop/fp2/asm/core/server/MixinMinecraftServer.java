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

package net.daporkchop.fp2.asm.core.server;

import net.daporkchop.fp2.util.threading.futureexecutor.ServerThreadMarkedFutureExecutor;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.daporkchop.lib.common.util.PorkUtil.*;

/**
 * Makes {@link MinecraftServer} implement {@link ServerThreadMarkedFutureExecutor.Holder}.
 *
 * @author DaPorkchop_
 */
@Mixin(MinecraftServer.class)
@SuppressWarnings("deprecation")
public abstract class MixinMinecraftServer implements ServerThreadMarkedFutureExecutor.Holder {
    @Unique
    private ServerThreadMarkedFutureExecutor fp2_executor;

    @Inject(method = "Lnet/minecraft/server/MinecraftServer;startServerThread()V",
            at = @At(value = "INVOKE",
                    target = "Ljava/lang/Thread;start()V",
                    shift = At.Shift.BEFORE))
    private void fp2_startServerThread_constructMarkedExecutor(CallbackInfo ci) {
        this.fp2_executor = new ServerThreadMarkedFutureExecutor(uncheckedCast(this));
    }

    @Override
    public ServerThreadMarkedFutureExecutor fp2_ServerThreadMarkedFutureExecutor$Holder_get() {
        return this.fp2_executor;
    }
}
