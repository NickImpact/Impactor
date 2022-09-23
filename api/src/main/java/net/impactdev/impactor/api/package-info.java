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

/**
 * This set of packages provide a meaningful set of utilities that are meant to help aid in writing code
 * once, and only once, to target a multitude of platforms. Most of these packages will work across both
 * game and proxy based instances, however, there are a few packages which are bound purely to a game instance.
 * <p>
 * To aid in the process of game translation, the following items make use of Minecraft internal code, which
 * is not available on a proxy instance:
 * <ul>
 *     <li>Commands</li>
 *     <li>Items</li>
 *     <li>Scoreboards</li>
 *     <li>UI</li>
 * </ul>
 * Each of these packages make use of Mojang mappings, or are outright unsupported on a proxy instance.
 */
package net.impactdev.impactor.api;