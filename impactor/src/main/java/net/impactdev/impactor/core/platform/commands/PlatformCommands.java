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

package net.impactdev.impactor.core.platform.commands;

import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.processing.CommandContainer;
import cloud.commandframework.exceptions.CommandExecutionException;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.commands.CommandSource;
import net.impactdev.impactor.api.utility.Context;
import net.impactdev.impactor.api.utility.printing.PrettyPrinter;
import net.impactdev.impactor.core.translations.internal.ImpactorTranslations;

import java.io.BufferedOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@CommandContainer
@CommandPermission("impactor.commands.platform.base")
public class PlatformCommands {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    @CommandMethod("impactor platform info")
    @CommandPermission("impactor.commands.platform.info")
    public void info(final CommandSource source) {
        PrettyPrinter printer = new PrettyPrinter(80);
        printer.title("Platform Information");
        Impactor.instance().platform().info().print(printer);

        Path target = Paths.get("config")
                .resolve("impactor")
                .resolve("dumps")
                .resolve("impactor-platform-" + formatter.format(LocalDateTime.now()) + ".txt");

        target.getParent().toFile().mkdirs();
        try(BufferedOutputStream buffer = new BufferedOutputStream(Files.newOutputStream(target.toFile().toPath()))) {
            try(PrintStream output = new PrintStream(buffer, true)) {
                printer.print(output);
                ImpactorTranslations.PLATFORM_INFO_GENERATED.send(source, Context.empty());
            }
        } catch (Exception e) {
            throw new CommandExecutionException(e);
        }
    }

}
