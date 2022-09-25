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

package net.impactdev.impactor.api.commands;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.services.Service;
import net.minecraft.commands.CommandSourceStack;

import java.util.function.Predicate;

public interface PermissionsService extends Service {

    boolean hasPermission(CommandSourceStack stack, String permission);

    /**
     * Creates a {@link Predicate} responsible for verifying whether the source of an executed command
     * has a particular permission.
     * <p>
     * This type of predicate is meant to be chained in a manner such that further requirements for any particular
     * command can simply be appended to it via {@link Predicate#and(Predicate)}. As such, expected usage for
     * this predicate usage would be something along the lines of:
     * <ul><li>PermissionsService.validate(x).and(some other predicate)</li></ul>
     *
     * @param permission The permission to validate against the command source
     * @return A predicate which is responsible for the permissions validation
     */
    static Predicate<CommandSourceStack> validate(String permission) {
        return stack -> Impactor.instance().services().provide(PermissionsService.class).hasPermission(stack, permission);
    }

}
