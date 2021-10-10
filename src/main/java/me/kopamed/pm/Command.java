package me.kopamed.pm;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

import java.util.Arrays;
import java.util.List;

public class Command extends CommandBase {
    private static final String command = "particlemod";
    private static final List<String> aliases = Arrays.asList(new String[]{"pm", "kpm"});

    public String getCommandName(){
        return command;
    }

    public String getCommandUsage(ICommandSender i){
        return "/" + command;
    }

    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        ParticleModGui.shown = true;
    }

    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    public List<String> getCommandAliases()
    {
        return aliases;
    }

    public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
        return true;
    }
}
