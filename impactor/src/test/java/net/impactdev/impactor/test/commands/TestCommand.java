package net.impactdev.impactor.test.commands;

import net.impactdev.impactor.api.commands.CommandSource;
import net.impactdev.impactor.api.commands.annotations.Command;
import net.impactdev.impactor.api.commands.annotations.Description;
import net.impactdev.impactor.api.commands.annotations.Permission;
import net.impactdev.impactor.api.commands.annotations.Proxy;

public class TestCommand {

    @Command("impactor test")
    @Description("This is the root test command")
    @Permission("impactor.commands.test")
    public void root(CommandSource source) {

    }

    @Command("impactor test abc")
    @Description("I am a command that is proxied")
    @Proxy("abc")
    @Permission("impactor.commands.test.abc")
    public void withProxy(CommandSource source) {

    }

}
