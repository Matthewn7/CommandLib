package co.uk.matthogan.command.register;

import co.uk.matthogan.command.Command;
import co.uk.matthogan.command.CommandLib;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Matthew Hogan
 */
@AllArgsConstructor
public abstract class RegisterBase {

    @Getter
    protected CommandLib commandLib;

    public abstract void setup();

    public CommandLib registerCommand(Command command, net.md_5.bungee.api.plugin.Plugin plugin) {
        return this.commandLib;
    }

    public CommandLib registerCommand(Command command, org.bukkit.plugin.Plugin plugin) {
        return this.commandLib;
    }
}