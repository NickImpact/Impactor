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

package net.impactdev.impactor.api.scoreboard.components;

/**
 * Represents a line on a scoreboard that can have its contents queried multiple times for potentially
 * different display information.
 *
 * It is up to the implementation to decide how exactly a line is capable of updating.
 */
public interface Updatable {

    /**
     * The purpose of this method is to initialize and activate the component that tracks and updates
     * the line per the type of line. For instance, this would be where a line that refreshes on an interval
     * would set up its scheduler.
     */
    void start();

    /**
     * Handles the actual updating of the line.
     */
    void update();

    /**
     * The purpose of this method is to deactivate any instance that is no longer needed for the line to
     * update, whether that be the scoreboard was hidden/removed or the player it belonged to logged off.
     * In the event of a scheduled updater, this is where the implementation should deactivate the updater.
     */
    void shutdown();
}
