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

package net.daporkchop.fp2.net.packet.standard.client;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;
import net.daporkchop.fp2.config.FP2Config;
import net.daporkchop.fp2.util.Constants;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**
 * @author DaPorkchop_
 */
@Setter
@Getter
public class CPacketClientConfig implements IMessage {
    protected FP2Config config;

    @Override
    public void fromBytes(ByteBuf buf) {
        this.config = FP2Config.fromJson(Constants.readString(buf));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        Constants.writeString(buf, FP2Config.toJson(this.config));
    }
}
