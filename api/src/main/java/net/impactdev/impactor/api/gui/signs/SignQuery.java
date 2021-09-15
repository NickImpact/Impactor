/*
 * This file is part of Impactor, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2018-2021 NickImpact
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

package net.impactdev.impactor.api.gui.signs;

import com.flowpowered.math.vector.Vector3d;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.utilities.Builder;

import java.util.List;

public interface SignQuery<T, P, M> {

    String TEXT_FORMAT = "{\"text\":\"%s\"}";
    int action = 9;

    List<T> getText();

    M getSignPosition();

    boolean shouldReopenOnFailure();

    SignSubmission getSubmissionHandler();

    void sendTo(P player);

    @SuppressWarnings("unchecked")
    static <T, P, M> SignQueryBuilder<T, P, M> builder() {
        return Impactor.getInstance().getRegistry().createBuilder(SignQueryBuilder.class);
    }

    interface SignQueryBuilder<T, P, M> extends Builder<SignQuery<T, P, M>, SignQueryBuilder<T, P, M>> {

        SignQueryBuilder<T, P, M> text(List<T> text);

        SignQueryBuilder<T, P, M> position(M position);

        SignQueryBuilder<T, P, M> reopenOnFailure(boolean state);

        SignQueryBuilder<T, P, M> response(SignSubmission response);

    }

}
