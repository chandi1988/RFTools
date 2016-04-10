package mcjty.rftools.blocks.spawner;

import mcjty.lib.api.Infusable;
import mcjty.lib.container.GenericGuiContainer;
import mcjty.rftools.RFTools;
import mcjty.rftools.blocks.GenericRFToolsBlock;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.List;

//@Optional.InterfaceList({
//        @Optional.Interface(iface = "crazypants.enderio.api.redstone.IRedstoneConnectable", modid = "EnderIO")})
public class MatterBeamerBlock extends GenericRFToolsBlock implements Infusable /*, IRedstoneConnectable*/ {

    public MatterBeamerBlock() {
        super(Material.iron, MatterBeamerTileEntity.class, MatterBeamerContainer.class, "matter_beamer", true);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void initModel() {
        super.initModel();
        ClientRegistry.bindTileEntitySpecialRenderer(MatterBeamerTileEntity.class, new MatterBeamerRenderer());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> list, boolean whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);
//        NBTTagCompound tagCompound = itemStack.getTagCompound();
//        if (tagCompound != null) {
//            String name = tagCompound.getString("tpName");
//            int id = tagCompound.getInteger("destinationId");
//            list.add(EnumChatFormatting.GREEN + "Name: " + name + (id == -1 ? "" : (", Id: " + id)));
//        }
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            list.add(TextFormatting.WHITE + "This block converts matter into a beam");
            list.add(TextFormatting.WHITE + "of energy. It can then send that beam to");
            list.add(TextFormatting.WHITE + "a connected spawner. Connect by using a wrench.");
            list.add(TextFormatting.YELLOW + "Infusing bonus: reduced power usage");
            list.add(TextFormatting.YELLOW + "and increased speed.");
        } else {
            list.add(TextFormatting.WHITE + RFTools.SHIFT_MESSAGE);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currenttip, accessor, config);
        TileEntity te = accessor.getTileEntity();
        if (te instanceof MatterBeamerTileEntity) {
            MatterBeamerTileEntity matterBeamerTileEntity = (MatterBeamerTileEntity) te;
            BlockPos coordinate = matterBeamerTileEntity.getDestination();
            if (coordinate == null) {
                currenttip.add(TextFormatting.RED + "Not connected to a spawner!");
            } else {
                currenttip.add(TextFormatting.GREEN + "Connected!");
            }
        }
        return currenttip;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Class<? extends GenericGuiContainer> getGuiClass() {
        return GuiMatterBeamer.class;
    }

    @Override
    public int getGuiID() {
        return RFTools.GUI_MATTER_BEAMER;
    }

    @Override
    protected boolean wrenchUse(World world, BlockPos pos, EnumFacing side, EntityPlayer player) {
        if (world.isRemote) {
            MatterBeamerTileEntity matterBeamerTileEntity = (MatterBeamerTileEntity) world.getTileEntity(pos);
            world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvent.soundEventRegistry.getObject(new ResourceLocation("block.note.pling")), SoundCategory.BLOCKS, 1.0f, 1.0f, false);
            matterBeamerTileEntity.useWrench(player);
        }
        return true;
    }

    @Override
    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
        checkRedstoneWithTE(world, pos);
    }
}
