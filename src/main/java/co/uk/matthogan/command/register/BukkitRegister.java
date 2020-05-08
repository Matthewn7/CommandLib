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

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

/**
 * @author Matthew Hogan
 */
public class BukkitRegister extends RegisterBase {

    private CommandMap commandMap;

    public BukkitRegister(CommandLib commandLib) {
        super(commandLib);
    }

    @Override
    public void setup() {
        try {
            Field commandMap = SimplePluginManager.class.getDeclaredField("commandMap");
            commandMap.setAccessible(true);

            this.commandMap = (CommandMap) commandMap.get(Bukkit.getPluginManager());

        } catch (NoSuchFieldException | IllegalAccessException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public CommandLib registerCommand(Command command, Plugin plugin) {
        CommandLib commandLib = super.getCommandLib();

        for (Annotation annotation : command.getClass().getAnnotations()) {
            if (!DynamicCommand.class.isAssignableFrom(annotation.annotationType())) {
                continue;
            }

            WrappedCommand wrappedCommand = new WrappedCommand(
                    command, (DynamicCommand) annotation
            );

            this.commandMap.register(plugin.getName(), wrappedCommand);
        }

        return commandLib;
    }

    private class WrappedCommand extends org.bukkit.command.Command {

        private Command command;
        private DynamicCommand dynamicCommand;

        WrappedCommand(Command command, DynamicCommand dynamicCommand) {
            super(dynamicCommand.name(), dynamicCommand.description(),
                    dynamicCommand.usage(), Arrays.asList(dynamicCommand.aliases())
            );

            this.command = command;
            this.dynamicCommand = dynamicCommand;
        }

        @Override
        public boolean execute(CommandSender sender, String label, String[] args) {
            if (!(sender instanceof Player) && !this.dynamicCommand.console()) {
                return true;
            }

            this.command.execute(sender, args);

            // run the async execute
            Plugin instance = BukkitRegister.super.commandLib.getBukkitPlugin();
            this.command.executeAsync(sender, args).runTaskAsynchronously(instance);

            return true;
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String alias, String... args)
                throws IllegalArgumentException
        {
            return this.command.tabComplete(sender, alias, args);
        }
    }
}
