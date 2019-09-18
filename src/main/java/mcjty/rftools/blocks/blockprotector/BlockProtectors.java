package mcjty.rftools.blocks.blockprotector;

import mcjty.lib.varia.GlobalCoordinate;
import mcjty.lib.worlddata.AbstractWorldData;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.util.Constants;

import java.util.*;

public class BlockProtectors extends AbstractWorldData<BlockProtectors> {

    public static final String PROTECTORS_NAME = "RFToolsBlockProtectors";

    private final Map<Integer,GlobalCoordinate> protectorById = new HashMap<>();
    private final Map<GlobalCoordinate,Integer> protectorIdByCoordinate = new HashMap<>();
    private int lastId = 0;

    public BlockProtectors() {
        super(PROTECTORS_NAME);
    }

    public static Collection<GlobalCoordinate> getProtectors(World world, int x, int y, int z) {
        if (world.isRemote) {
            return Collections.emptyList();
        }
        BlockProtectors blockProtectors = get();
        return blockProtectors.findProtectors(x, y, z, world.getDimension().getType(), 2);
    }

    public static boolean checkHarvestProtection(int x, int y, int z, IBlockReader world, Collection<GlobalCoordinate> protectors) {
        for (GlobalCoordinate protector : protectors) {
            TileEntity te = world.getTileEntity(protector.getCoordinate());
            if (te instanceof BlockProtectorTileEntity) {
                BlockProtectorTileEntity blockProtectorTileEntity = (BlockProtectorTileEntity) te;
                BlockPos relative = blockProtectorTileEntity.absoluteToRelative(x, y, z);
                boolean b = blockProtectorTileEntity.isProtected(relative);
                if (b) {
                    if (blockProtectorTileEntity.attemptHarvestProtection()) {
                        return true;
                    } else {
                        blockProtectorTileEntity.removeProtection(relative);
                    }
                }
            }
        }
        return false;
    }


    public static BlockProtectors get() {
        return getData(BlockProtectors::new, PROTECTORS_NAME);
    }

    // Set an old id to a new position (after moving a receiver).
    public void assignId(GlobalCoordinate key, int id) {
        protectorById.put(id, key);
        protectorIdByCoordinate.put(key, id);
    }

    public int getNewId(GlobalCoordinate key) {
        if (protectorIdByCoordinate.containsKey(key)) {
            return protectorIdByCoordinate.get(key);
        }
        lastId++;
        protectorById.put(lastId, key);
        protectorIdByCoordinate.put(key, lastId);
        return lastId;
    }

    // Get the id from a coordinate.
    public Integer getIdForCoordinate(GlobalCoordinate key) {
        return protectorIdByCoordinate.get(key);
    }

    public GlobalCoordinate getCoordinateForId(int id) {
        return protectorById.get(id);
    }

    public void removeDestination(BlockPos coordinate, DimensionType dimension) {
        GlobalCoordinate key = new GlobalCoordinate(coordinate, dimension);
        Integer id = protectorIdByCoordinate.get(key);
        if (id != null) {
            protectorById.remove(id);
            protectorIdByCoordinate.remove(key);
        }
    }

    public Collection<GlobalCoordinate> findProtectors(int x, int y, int z, DimensionType dimension, int radius) {
        List<GlobalCoordinate> protectors = new ArrayList<>();
        for (GlobalCoordinate coordinate : protectorIdByCoordinate.keySet()) {
            if (coordinate.getDimension().equals(dimension)) {
                BlockPos c = coordinate.getCoordinate();
                if (Math.abs(x-c.getX()) <= (16+radius+1) && Math.abs(y-c.getY()) <= (16+radius+1) && Math.abs(z-c.getZ()) <= (16+radius+1)) {
                    protectors.add(coordinate);
                }
            }
        }

        return protectors;
    }

    @Override
    public void read(CompoundNBT tagCompound) {
        protectorById.clear();
        protectorIdByCoordinate.clear();
        lastId = tagCompound.getInt("lastId");
        readDestinationsFromNBT(tagCompound);
    }

    private void readDestinationsFromNBT(CompoundNBT tagCompound) {
        ListNBT lst = tagCompound.getList("blocks", Constants.NBT.TAG_COMPOUND);
        for (int i = 0 ; i < lst.size() ; i++) {
            CompoundNBT tc = lst.getCompound(i);
            BlockPos c = new BlockPos(tc.getInt("x"), tc.getInt("y"), tc.getInt("z"));
            String dim = tc.getString("dim");

            GlobalCoordinate gc = new GlobalCoordinate(c, DimensionType.byName(new ResourceLocation(dim)));
            int id = tc.getInt("id");
            protectorById.put(id, gc);
            protectorIdByCoordinate.put(gc, id);
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT tagCompound) {
        ListNBT lst = new ListNBT();
        for (GlobalCoordinate destination : protectorIdByCoordinate.keySet()) {
            CompoundNBT tc = new CompoundNBT();
            BlockPos c = destination.getCoordinate();
            tc.putInt("x", c.getX());
            tc.putInt("y", c.getY());
            tc.putInt("z", c.getZ());
            tc.putString("dim", destination.getDimension().getRegistryName().toString());
            Integer id = protectorIdByCoordinate.get(new GlobalCoordinate(c, destination.getDimension()));
            tc.putInt("id", id);
            lst.add(tc);
        }
        tagCompound.put("blocks", lst);
        tagCompound.putInt("lastId", lastId);
        return tagCompound;
    }

}
