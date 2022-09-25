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

package net.impactdev.impactor.api.commands.executors;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.leangen.geantyref.TypeToken;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.commands.exceptions.CommandResultException;
import net.impactdev.impactor.api.utilities.context.Context;
import net.impactdev.impactor.api.utilities.printing.PrettyPrinter;
import net.minecraft.commands.CommandSourceStack;
import org.apache.commons.lang3.StringUtils;

public interface CommandExecutors extends Command<CommandSourceStack> {

    TypeToken<CommandContext<CommandSourceStack>> COMMAND_CONTEXT = new TypeToken<CommandContext<CommandSourceStack>>() {};

    static CommandExecutors allowAll(CommandExecutor executor) {
        return Impactor.instance().factories().provide(Factory.class).allowAll(executor);
    }

    static CommandExecutors playersOnly(CommandExecutor executor) {
        return Impactor.instance().factories().provide(Factory.class).playersOnly(executor);
    }

    CommandExecutor executor();

    @Override
    default int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        try {
            Context ctx = Context.empty().append(COMMAND_CONTEXT, context);
            CommandResult result = this.executor().execute(ctx);
            if(result.reason().isPresent()) {
                throw new CommandResultException(result, result.reason().get());
            }

            return result.result();
        } catch (CommandResultException tracked) {
            PrettyPrinter printer = new PrettyPrinter(80);
            printer.title("Command Parsing Exception");
            printer.add("A tracked exception occurred while attempting to process")
                    .add("a command! This typically indicates the exception was expected")
                    .add("and was logged by the result parser. Information about the error")
                    .add("is listed below...");

            printer.hr('-');
            printer.add("Raw Input: " + context.getInput());
            printer.add("Result:");
            printer.add("Successful: " + tracked.result().isSuccessful(), 2);
            printer.add("Result Value: " + tracked.result().result(), 2);
            printer.newline();
            printer.add("Parse Results:");
            printer.add(context.getInput()).add(StringUtils.leftPad("^", context.getRange().getEnd()));

            printer.newline().add("Tracked Exception Stacktrace:");
            printer.add(tracked.getCause());

            printer.print(System.err);
            throw new RuntimeException("Tracked Exception", tracked.getCause());
        } catch (RuntimeException unexpected) {
            PrettyPrinter printer = new PrettyPrinter(80);
            printer.title("Command Parsing Exception");
            printer.add("An unexpected exception occurred while attempting to process");
            printer.add("a command!");
            printer.newline().add("Tracked Exception Stacktrace:");
            printer.add(unexpected);

            printer.print(System.err);
            throw unexpected;
        }
    }

    interface Factory {

        CommandExecutors allowAll(CommandExecutor executor);

        CommandExecutors playersOnly(CommandExecutor executor);

    }

}
