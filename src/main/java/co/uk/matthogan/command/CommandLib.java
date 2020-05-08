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

import co.uk.matthogan.command.register.BukkitRegister;
import co.uk.matthogan.command.register.BungeeRegister;
import co.uk.matthogan.command.register.RegisterBase;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author Matthew Hogan
 */
@NoArgsConstructor
public class CommandLib {

    @Getter
    private org.bukkit.plugin.Plugin bukkitPlugin;
    private net.md_5.bungee.api.plugin.Plugin bungeePlugin;
    private RegisterBase platform;

    public CommandLib setupBukkit(org.bukkit.plugin.Plugin bukkitPlugin) {
        this.bukkitPlugin = bukkitPlugin;
        this.platform = new BukkitRegister(this);
        this.platform.setup();

        return this;
    }

    public CommandLib setupBungeeCord(net.md_5.bungee.api.plugin.Plugin bungeePlugin) {
        this.bungeePlugin = bungeePlugin;
        this.platform = new BungeeRegister(this);
        this.platform.setup();

        return this;
    }

    public CommandLib register(Command command) {
        if (this.bukkitPlugin != null) {
            this.platform.registerCommand(command, this.bukkitPlugin);
        } else {
            this.platform.registerCommand(command, this.bungeePlugin);
        }

        return this;
    }
}