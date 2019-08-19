package mcjty.rftools.items.creativeonly;

import mcjty.lib.varia.BlockTools;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.NBTTools;
import mcjty.rftools.RFTools;
import mcjty.rftools.setup.GuiProxy;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DevelopersDelightItem extends GenericRFToolsItem {

    public DevelopersDelightItem() {
        super("developers_delight");
        setMaxStackSize(1);
    }

    @Override
    public ActionResultType onItemUse(PlayerEntity player, World world, BlockPos pos, Hand hand, Direction facing, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            dumpInfo(world, pos);
            GuiDevelopersDelight.setSelected(pos);
            player.openGui(RFTools.instance, GuiProxy.GUI_DEVELOPERS_DELIGHT, player.getEntityWorld(), (int) player.posX, (int) player.posY, (int) player.posZ);
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.SUCCESS;
    }

    private void dumpInfo(World world, BlockPos pos) {
        if (world.isAirBlock(pos)) {
            return;
        }
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        int meta = block.getMetaFromState(state);
        String modid = BlockTools.getModidForBlock(block);
        Logging.log("Block: " + block.getUnlocalizedName() + ", Meta: " + meta + ", Mod: " + modid);
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity != null) {
            CompoundNBT tag = new CompoundNBT();
            try {
                tileEntity.writeToNBT(tag);
                StringBuffer buffer = new StringBuffer();
                NBTTools.convertNBTtoJson(buffer, tag, 0);
                Logging.log(buffer.toString());
            } catch (Exception e) {
                Logging.log("Catched a crash during dumping of NBT");
            }
        }
    }


}
