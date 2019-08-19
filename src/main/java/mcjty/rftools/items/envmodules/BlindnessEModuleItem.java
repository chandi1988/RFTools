package mcjty.rftools.items.envmodules;

import mcjty.rftools.blocks.environmental.EnvModuleProvider;
import mcjty.rftools.blocks.environmental.EnvironmentalConfiguration;
import mcjty.rftools.blocks.environmental.modules.BlindnessEModule;
import mcjty.rftools.blocks.environmental.modules.EnvironmentModule;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class BlindnessEModuleItem extends GenericRFToolsItem implements EnvModuleProvider {

    public BlindnessEModuleItem() {
        super("blindness_module");
        setMaxStackSize(16);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, World player, List<String> list, ITooltipFlag whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);
        list.add("This module gives blindness when");
        list.add("used in the environmental controller.");
        list.add(TextFormatting.GREEN + "Uses " + EnvironmentalConfiguration.BLINDNESS_RFPERTICK.get() + " RF/tick (per cubic block)");
        if (!EnvironmentalConfiguration.blindnessAvailable.get()) {
            list.add(TextFormatting.RED + "This module only works on mobs (see config)");
        }
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 1;
    }

    @Override
    public Class<? extends EnvironmentModule> getServerEnvironmentModule() {
        return BlindnessEModule.class;
    }

    @Override
    public String getName() {
        return "Blindness";
    }
}