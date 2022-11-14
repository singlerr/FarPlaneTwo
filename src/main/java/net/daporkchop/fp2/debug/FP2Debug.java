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

package net.daporkchop.fp2.debug;

import lombok.experimental.UtilityClass;
import net.daporkchop.fp2.client.FP2Client;
import net.daporkchop.fp2.client.gl.shader.ShaderManager;
import net.daporkchop.fp2.config.FP2Config;
import net.daporkchop.fp2.config.listener.ConfigListenerManager;
import net.daporkchop.fp2.debug.client.DebugClientEvents;
import net.daporkchop.fp2.debug.client.DebugKeyBindings;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

import static net.daporkchop.fp2.util.Constants.*;

/**
 * Container class for FP2 debug mode.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class FP2Debug {
    /**
     * Whether or not we are currently running in debug mode.
     */
    public static final boolean FP2_DEBUG = /*Boolean.parseBoolean(System.getProperty("fp2.debug", "false"));*/true;

    /**
     * Called during {@link FMLPreInitializationEvent}.
     */
    public void preInit() {
        if (!FP2_DEBUG) {
            return;
        }

        bigWarning("FarPlaneTwo debug mode enabled!");

        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            ConfigListenerManager.add(() -> FP2Client.GLOBAL_SHADER_MACROS
                    .define("FP2_DEBUG_COLORS_ENABLED", FP2Config.global().debug().debugColors().enable())
                    .define("FP2_DEBUG_COLORS_MODE", FP2Config.global().debug().debugColors().ordinal()));

            MinecraftForge.EVENT_BUS.register(new DebugClientEvents());
        }
    }

    /**
     * Called during {@link FMLInitializationEvent}.
     */
    public void init() {
        if (!FP2_DEBUG) {
            return;
        }

        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            DebugKeyBindings.register();
        }
    }
}
