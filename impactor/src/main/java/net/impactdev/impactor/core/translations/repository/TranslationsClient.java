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

package net.impactdev.impactor.core.translations.repository;

import net.impactdev.impactor.core.plugin.BaseImpactorPlugin;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static net.impactdev.impactor.core.utility.future.Futures.reportRunningTasks;

public class TranslationsClient {

    public static final String USER_AGENT = "Impactor/Translations";

    private final OkHttpClient client = new OkHttpClient.Builder()
            .callTimeout(15, TimeUnit.SECONDS)
            .build();

    void shutdown() {
        ExecutorService service = this.client.dispatcher().executorService();
        service.shutdown();

        try {
            this.client.connectionPool().evictAll();
            Optional.ofNullable(this.client.cache()).ifPresent(cache -> {
                try {
                    cache.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            if (!service.awaitTermination(10, TimeUnit.SECONDS)) {
                BaseImpactorPlugin.instance().logger().severe("Timed out waiting for the Impactor worker thread pool to terminate");
                reportRunningTasks(thread -> thread.getName().startsWith("OkHttp"));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    Response makeRequest(Request request) throws Exception {
        Response response = this.client.newCall(request).execute();
        if(!response.isSuccessful()) {
            response.close();
            throw new RuntimeException("Request was unsuccessful: " + response.code() + " - " + response.message());
        }

        return response;
    }

}
