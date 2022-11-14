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

package net.daporkchop.fp2.client.gui;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Optional;

/**
 * @author DaPorkchop_
 */
@SideOnly(Side.CLIENT)
public interface IConfigGuiComponent {
    /**
     * Initializes this component's contents.
     */
    void init();

    /**
     * Re-evaluates and updates this component's contents.
     */
    void pack();

    void render(int mouseX, int mouseY, float partialTicks);

    Optional<String[]> getTooltip(int mouseX, int mouseY);

    void mouseDown(int mouseX, int mouseY, int button);

    void mouseUp(int mouseX, int mouseY, int button);

    void mouseScroll(int mouseX, int mouseY, int dWheel);

    void mouseDragged(int oldMouseX, int oldMouseY, int newMouseX, int newMouseY, int button);

    void keyPressed(char typedChar, int keyCode);
}
