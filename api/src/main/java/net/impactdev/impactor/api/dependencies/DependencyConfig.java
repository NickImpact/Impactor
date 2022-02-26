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

package net.impactdev.impactor.api.dependencies;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.impactdev.impactor.api.dependencies.relocation.Relocation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.List;

public class DependencyConfig  {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Dependency.class, new DependencyDeserializer())
            .setPrettyPrinting()
            .create();
    private final List<Dependency> dependencies = Lists.newArrayList();

    public List<Dependency> getDependencies() {
        return this.dependencies;
    }

    public static DependencyConfig read(Path path) throws Exception {
        try(BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            return GSON.fromJson(reader, DependencyConfig.class);
        }
    }

    public static class DependencyDeserializer implements JsonDeserializer<Dependency> {

        @Override
        public Dependency deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            JsonArray relocations = object.getAsJsonArray("relocations");

            int index = 0;
            Relocation[] translated = new Relocation[relocations.size()];
            for(JsonElement element : relocations) {
                JsonObject obj = element.getAsJsonObject();
                translated[index++] = Relocation.of(obj.get("pattern").getAsString(), obj.get("replacement").getAsString());
            }

            return Dependency.builder()
                    .group(object.get("group").getAsString())
                    .artifact(object.get("artifact").getAsString())
                    .version(object.get("version").getAsString())
                    .checksum(object.has("checksum") ? object.get("checksum").getAsString() : null)
                    .relocations(translated)
                    .build();
        }

    }

}
