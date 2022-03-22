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

package net.impactdev.impactor.api.utilities.printing;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import java.util.Objects;

/**
 * Print a formatted representation of the specified {@link JsonElement} as a pretty
 * printed JSON output.
 *
 */
public final class JsonPrinter implements PrettyPrinter.IPrettyPrintable {
    private final JsonElement json;

    /**
     * @param json The element to print
     */
    public JsonPrinter(JsonElement json) {this.json = json;}

    @Override
    public void print(PrettyPrinter printer) {
        String raw = new GsonBuilder().setPrettyPrinting().create().toJson(this.json);
        for (String line : raw.split("\n")) {
            printer.add(line);
        }
    }

    public JsonElement json() {
        return json;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        JsonPrinter that = (JsonPrinter) obj;
        return Objects.equals(this.json, that.json);
    }

    @Override
    public int hashCode() {
        return Objects.hash(json);
    }

    @Override
    public String toString() {
        return "JsonPrinter[" +
                "json=" + json + ']';
    }


}
