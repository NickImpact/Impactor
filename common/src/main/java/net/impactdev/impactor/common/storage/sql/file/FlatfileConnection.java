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

package net.impactdev.impactor.common.storage.sql.file;

import net.impactdev.impactor.api.storage.connection.sql.SqlConnection;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class FlatfileConnection implements SqlConnection {

    protected static final DecimalFormat DF = new DecimalFormat("#.##");
    private final Path target;

    public FlatfileConnection(Path target) {
        this.target = target;
    }

    @Override
    public void init() throws Exception {}

    protected Path target() {
        return this.target;
    }

    @Override
    public Map<String, String> getMeta() {
        Map<String, String> ret = new LinkedHashMap<>();

        Path databaseFile = this.target();
        if (Files.exists(databaseFile)) {
            long length;
            try {
                length = Files.size(databaseFile);
            } catch (IOException e) {
                length = 0;
            }

            double size = length / 1048576D;
            ret.put("File Size", DF.format(size) + "MB");
        } else {
            ret.put("File Size", "0MB");
        }

        return ret;
    }

}
