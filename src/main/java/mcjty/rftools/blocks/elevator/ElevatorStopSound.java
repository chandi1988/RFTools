package mcjty.rftools.blocks.elevator;

import net.minecraft.block.Block;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ElevatorStopSound extends TickableSound {
    private final World world;
    private final BlockPos pos;

    public ElevatorStopSound(World world, int x, int y, int z) {
        super(ElevatorSounds.stopSound, SoundCategory.BLOCKS);
        this.world = world;
        this.pos = new BlockPos(x, y, z);

        this.x = x;
        this.y = y;
        this.z = z;

        this.attenuationType = ISound.AttenuationType.LINEAR;
        this.repeat = true;
        this.repeatDelay = 0;
    }

    public void move(float x, float y, float z) {
        x = x;
        y = y;
        z = z;
    }


    @Override
    public boolean isDonePlaying() {
        return false;
    }

    @Override
    public void tick() {
        Block block = world.getBlockState(pos).getBlock();
        if (block != ElevatorSetup.elevatorBlock) {
            donePlaying = true;
            return;
        }
        volume = (float) ElevatorConfiguration.baseElevatorVolume.get();
    }
}