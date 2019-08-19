package mcjty.rftools.commands;

import mcjty.lib.McJtyLib;
import mcjty.lib.preferences.PreferencesProperties;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class CmdSetBuffBar extends AbstractRfToolsCommand {
    @Override
    public String getHelp() {
        return "[<x> <y>]";
    }

    @Override
    public String getCommand() {
        return "buffs";
    }

    @Override
    public int getPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(ICommandSender sender, String[] args) {
        if (args.length > 3) {
            ITextComponent component = new TextComponentString(TextFormatting.RED + "Too many parameters!");
            if (sender instanceof PlayerEntity) {
                ((PlayerEntity) sender).sendStatusMessage(component, false);
            } else {
                sender.sendMessage(component);
            }
            return;
        }

        if (!(sender instanceof PlayerEntity)) {
            ITextComponent component = new TextComponentString(TextFormatting.RED + "This command only works as a player!");
            if (sender instanceof PlayerEntity) {
                ((PlayerEntity) sender).sendStatusMessage(component, false);
            } else {
                sender.sendMessage(component);
            }
            return;
        }

        PlayerEntity player = (PlayerEntity) sender;
        PreferencesProperties properties = McJtyLib.getPreferencesProperties(player);

        if (args.length < 3) {
            int buffX = properties.getBuffX();
            int buffY = properties.getBuffY();
            ITextComponent component = new TextComponentString(TextFormatting.YELLOW + "Current buffbar location: " + buffX + "," + buffY);
            if (sender instanceof PlayerEntity) {
                ((PlayerEntity) sender).sendStatusMessage(component, false);
            } else {
                sender.sendMessage(component);
            }
            return;
        }

        int x = fetchInt(sender, args, 1, 0);
        int y = fetchInt(sender, args, 2, 0);
        properties.setBuffXY(x, y);
    }
}
