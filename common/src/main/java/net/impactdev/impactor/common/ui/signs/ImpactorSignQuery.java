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

package net.impactdev.impactor.common.ui.signs;

import net.impactdev.impactor.api.ui.signs.SignQuery;
import net.impactdev.impactor.api.ui.signs.SignSubmission;
import net.kyori.adventure.text.Component;
import org.spongepowered.math.vector.Vector3i;

import java.util.List;

public abstract class ImpactorSignQuery implements SignQuery {

    private final List<Component> text;
    private final Vector3i position;
    private final boolean reopenOnFailure;
    private final SignSubmission handler;

    public ImpactorSignQuery(List<Component> text, Vector3i position, boolean reopenOnFailure, SignSubmission handler) {
        this.text = text;
        this.position = position;
        this.reopenOnFailure = reopenOnFailure;
        this.handler = handler;
    }

    @Override
    public List<Component> text() {
        return this.text;
    }

    @Override
    public Vector3i position() {
        return this.position;
    }

    @Override
    public boolean shouldReopenOnFailure() {
        return this.reopenOnFailure;
    }

    @Override
    public SignSubmission submissionHandler() {
        return this.handler;
    }

    public static abstract class ImpactorSignQueryBuilder implements SignQueryBuilder {

        public List<Component> lines;
        public Vector3i position;
        public boolean reopen;
        public SignSubmission callback;

        @Override
        public ImpactorSignQueryBuilder text(List<Component> text) {
            this.lines = text;
            return this;
        }

        @Override
        public ImpactorSignQueryBuilder position(Vector3i position) {
            this.position = position;
            return this;
        }

        @Override
        public ImpactorSignQueryBuilder reopenOnFailure(boolean state) {
            this.reopen = state;
            return this;
        }

        @Override
        public ImpactorSignQueryBuilder response(SignSubmission response) {
            this.callback = response;
            return this;
        }

    }
}
