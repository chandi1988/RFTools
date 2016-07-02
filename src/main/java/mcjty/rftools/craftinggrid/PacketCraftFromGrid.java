package mcjty.rftools.craftinggrid;

import io.netty.buffer.ByteBuf;
import mcjty.lib.network.NetworkTools;
import mcjty.rftools.blocks.storage.ModularStorageItemContainer;
import mcjty.rftools.blocks.storage.ModularStorageSetup;
import mcjty.rftools.blocks.storage.RemoteStorageItemContainer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketCraftFromGrid implements IMessage {

    private BlockPos pos;
    private int count;

    @Override
    public void fromBytes(ByteBuf buf) {
        if (buf.readBoolean()) {
            pos = NetworkTools.readPos(buf);
        } else {
            pos = null;
        }
        count = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        if (pos != null) {
            buf.writeBoolean(true);
            NetworkTools.writePos(buf, pos);
        } else {
            buf.writeBoolean(false);
        }
        buf.writeInt(count);
    }

    public PacketCraftFromGrid() {
    }

    public PacketCraftFromGrid(BlockPos pos, int count) {
        this.pos = pos;
        this.count = count;
    }

    public static class Handler implements IMessageHandler<PacketCraftFromGrid, IMessage> {
        @Override
        public IMessage onMessage(PacketCraftFromGrid message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketCraftFromGrid message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            if (message.pos == null) {
                // Handle tablet version
                ItemStack mainhand = player.getHeldItemMainhand();
                if (mainhand != null && mainhand.getItem() == ModularStorageSetup.storageModuleTabletItem) {
                    if (player.openContainer instanceof ModularStorageItemContainer) {
                        ModularStorageItemContainer storageItemContainer = (ModularStorageItemContainer) player.openContainer;
                        storageItemContainer.getCraftingGridProvider().craft(player, message.count);
                    } else if (player.openContainer instanceof RemoteStorageItemContainer) {
                        RemoteStorageItemContainer storageItemContainer = (RemoteStorageItemContainer) player.openContainer;
                        storageItemContainer.getCraftingGridProvider().craft(player, message.count);
                    }
                }
            } else {
                TileEntity te = player.worldObj.getTileEntity(message.pos);
                if (te instanceof CraftingGridProvider) {
                    ((CraftingGridProvider) te).craft(player, message.count);
                }
            }
        }
    }
}