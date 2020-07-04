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

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Matthew Hogan
 */
public abstract class Command {

    public void setup() {

    }

    public void execute(org.bukkit.command.CommandSender sender, String... args) {
        ;
    }

    public void execute(net.md_5.bungee.api.CommandSender sender, String... args) {
        ;
    }

    public BukkitRunnable executeAsync(org.bukkit.command.CommandSender sender, String... args) {
        return new BukkitRunnable() {
            @Override public void run() {}
        };
    }

    public ArrayList<String> tabComplete(org.bukkit.command.CommandSender sender, String alias, String... args) {
        if (args.length <= 1 && !this.sub.isEmpty()) {
            if (args[0].isEmpty()) {
                return this.tabSubCommands();
            } else {
                return (this.tabSubCommands()
                        .stream()
                        .filter(item -> item.toLowerCase().startsWith(args[0]))
                        .collect(Collectors.toCollection(ArrayList::new))
                );
            }
        }

        return new ArrayList<>();
    }

    private HashMap<String, Command> sub = new HashMap<>();

    public void sub(String name, Command command) {
        this.sub.put(name.toLowerCase(), command);
    }

    public boolean containsSub(String name) {
        return this.sub.containsKey(name);
    }

    // Spigot only
    public void processSubCommands(Player player, String[] args, Command help, Plugin instance) {
        if (args.length < 1) {
            return;
        }

        String arg = args[0];
        Command command;

        if (this.sub.containsKey(arg.toLowerCase())) {
            command = this.sub.get(arg);
        } else {
            command = help;
        }

        // remove the sub command from the original arguments
        args = Arrays.copyOfRange(args, 1, args.length);

        command.execute(player, args);
        command.executeAsync(player, args).runTaskAsynchronously(instance);
    }

    public ArrayList<String> tabSubCommands() {
        return new ArrayList<>(this.sub.keySet());
    }
}