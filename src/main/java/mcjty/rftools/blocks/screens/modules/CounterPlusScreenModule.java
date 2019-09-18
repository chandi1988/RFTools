package mcjty.rftools.blocks.screens.modules;

import mcjty.lib.varia.BlockPosTools;
import mcjty.rftools.blocks.screens.ScreenConfiguration;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class CounterPlusScreenModule extends CounterScreenModule {

    @Override
    public void setupFromNBT(CompoundNBT tagCompound, DimensionType dim, BlockPos pos) {
        if (tagCompound != null) {
            coordinate = BlockPosTools.INVALID;
            if (tagCompound.contains("monitorx")) {
                if (tagCompound.contains("monitordim")) {
                    this.dim = DimensionType.byName(new ResourceLocation(tagCompound.getString("monitordim")));
                } else {
                    // Compatibility reasons
                    this.dim = DimensionType.byName(new ResourceLocation(tagCompound.getString("dim")));
                }
                coordinate = new BlockPos(tagCompound.getInt("monitorx"), tagCompound.getInt("monitory"), tagCompound.getInt("monitorz"));
            }
        }
    }

    @Override
    public int getRfPerTick() {
        return ScreenConfiguration.COUNTERPLUS_RFPERTICK.get();
    }

    @Override
    public void mouseClick(World world, int x, int y, boolean clicked, PlayerEntity player) {

    }
}
