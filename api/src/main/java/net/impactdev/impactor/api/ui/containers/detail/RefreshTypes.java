/*
 * This file is part of Impactor, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2018-2022 NickImpact
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package net.impactdev.impactor.api.ui.containers.detail;

public enum RefreshTypes implements RefreshType {

    /** Updates every single icon within the view */
    ALL,

    /** Updates only icons belonging to the layout of the view */
    LAYOUT,

    /** Updates only icons belonging to the content zone of a pagination */
    CONTENT,

    /** Updates an icon at a slot index (0, 1, 2, 3, etc) */
    SLOT_INDEX,

    /**
     * Updates an icon at a slot position, via a Vector2i
     *
     * <p>Examples are such:
     * <ul>
     *     <li>(0, 0)</li>
     *     <li>(1, 0)</li>
     * </ul>
     */
    SLOT_POS,

    /**
     * Updates icons within the specified grid. To satisfy the context for
     * this operation whilst only specifying one vector, this typing bases
     * on a {@link org.spongepowered.math.vector.Vector4i Vector4i} to specify
     * both the size of the grid, and the top left corner of the grid.
     */
    GRID,

    /**
     * Updates a particular section within a sectioned pagination. If you want to
     * update all sections in a pagination, use {@link #CONTENT}.
     *
     * @see #CONTENT
     */
    SECTION,

}
