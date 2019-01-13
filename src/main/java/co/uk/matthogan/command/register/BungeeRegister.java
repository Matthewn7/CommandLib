/*
 * MIT License
 *
 * Copyright (c) 2018 Matthew Hogan <matt@matthogan.co.uk>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package co.uk.matthogan.command.register;

import co.uk.matthogan.command.Command;
import co.uk.matthogan.command.CommandLib;
import co.uk.matthogan.command.DynamicCommand;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.lang.annotation.Annotation;

/**
 * @author Matthew Hogan
 */
public class BungeeRegister extends RegisterBase {

    public BungeeRegister(CommandLib commandLib) {
        super(commandLib);
    }

    @Override
    public void setup() {
        ;
    }

    @Override
    public CommandLib registerCommand(Command command, Plugin plugin) {
        for (Annotation annotation : command.getClass().getAnnotations()) {
            if (!DynamicCommand.class.isAssignableFrom(annotation.annotationType())) {
                continue;
            }

            WrappedCommand wrappedCommand = new WrappedCommand(command, (DynamicCommand) annotation);
            ProxyServer.getInstance().getPluginManager().registerCommand(plugin, wrappedCommand);
        }

        return super.getCommandLib();
    }

    private class WrappedCommand extends net.md_5.bungee.api.plugin.Command {

        private Command command;
        private DynamicCommand dynamicCommand;

        WrappedCommand(Command command, DynamicCommand dynamicCommand) {
            super(dynamicCommand.name(), dynamicCommand.description(), dynamicCommand.aliases());
            this.command = command;
            this.dynamicCommand = dynamicCommand;
        }

        @Override
        public void execute(CommandSender commandSender, String... strings) {
            if (commandSender instanceof ProxiedPlayer && this.dynamicCommand.console()) {
                return;
            }

            this.command.execute(commandSender, strings);
        }
    }
}
