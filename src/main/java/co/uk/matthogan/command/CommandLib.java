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
package co.uk.matthogan.command;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashSet;

/**
 * @author Matthew Hogan
 */
public class CommandLib {

    private Plugin plugin;
    private CommandMap commandMap;

    @Getter
    private HashSet<WrappedCommand> registeredCommands;

    {
        this.registeredCommands = new HashSet<>();
    }

    public CommandLib(Plugin plugin) {
        this.plugin = plugin;

        try {
            Field commandMap = SimplePluginManager.class.getDeclaredField("commandMap");
            commandMap.setAccessible(true);

            this.commandMap = (CommandMap) commandMap.get(Bukkit.getPluginManager());

        } catch (NoSuchFieldException | IllegalAccessException exception) {
            exception.printStackTrace();
        }
    }

    public CommandLib register(Command command) {
        for (Annotation annotation : command.getClass().getAnnotations()) {

            // Check if we have the correct annotation
            if (!DynamicCommand.class.isAssignableFrom(annotation.annotationType())) {
                continue;
            }

            WrappedCommand wrappedCommand = new WrappedCommand(
                    command, (DynamicCommand) annotation
            );

            this.commandMap.register(this.plugin.getName(), wrappedCommand);
            this.registeredCommands.add(wrappedCommand);
        }

        return this;
    }
}