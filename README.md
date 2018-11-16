# CommandLib
Elegant Spigot command API, which replaces the God awful default API.

## Usage

* Create a command class

```java
@DynamicCommand(
        name = "ping",
        aliases = {"hello"},
        console = true
)
public class PingCmd extends Command {

    @Override
    public void execute(CommandSender sender, String... args) {
        sender.sendMessage("Pong!");
    }
}
```

* Initialize the your command class

```java
@Override
public void onEnable() {
    // ...

    CommandLib commandLib = new CommandLib(this)
            .register(new PingCmd());
}
```
