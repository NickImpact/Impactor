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

package net.impactdev.impactor.api.config.adapter.system;

import net.impactdev.impactor.api.core.annotations.DesignBasedOn;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@DesignBasedOn(
        github = "https://github.com/LuckPerms/LuckPerms/tree/master",
        license = "https://github.com/LuckPerms/LuckPerms/blob/master/LICENSE.txt"
)
public final class SystemPropertyConfigAdapter extends StringBasedConfigAdapter {

    private static final Pattern PREFIX = Pattern.compile("[.]$");

    private final String prefix;
    private final Predicate<String> censorship;

    /**
     * Creates a new system property configuration adapter. Keys are expected to be prefixed with some
     * sort of identifier, suffixed with a '.' at minimum.
     *
     * @param prefix
     */
    public SystemPropertyConfigAdapter(final String prefix) {
        this(prefix, in -> false);
    }

    public SystemPropertyConfigAdapter(final String prefix, Predicate<String> censor) {
        Matcher matcher = PREFIX.matcher(prefix);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid prefix: " + prefix);
        }

        this.prefix = prefix;
        this.censorship = censor;
    }

    @Override
    protected @Nullable String resolve(String path) {
        // e.g.
        // 'server'            -> impactor.server
        // 'data.table_prefix' -> impactor.data.table-prefix
        String key = this.prefix + path;

        String value = System.getProperty(key);
        if (value != null) {
            String printableValue = this.censorship.test(value) ? "*****" : value;
//            this.plugin.getLogger().info(String.format("Resolved configuration value from system property: %s = %s", key, printableValue));
        }

        return value;
    }

    @Override
    public void reload() {
    }

}
