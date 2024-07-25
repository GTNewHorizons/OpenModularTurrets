package openmodularturrets.blocks.turretheads;

import java.util.Random;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import openmodularturrets.reference.ModInfo;
import openmodularturrets.reference.Names;
import openmodularturrets.tileentity.turrets.TeleporterTurretTileEntity;

public class BlockTeleporterTurret extends BlockAbstractTurretHead {

    public boolean shouldAnimate = false;

    public BlockTeleporterTurret() {
        super();

        this.setBlockName(Names.Blocks.unlocalisedTeleporterTurret);
        this.setBlockTextureName(ModInfo.ID + ":teleporterTurret");
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TeleporterTurretTileEntity();
    }

    @Override
    public void randomDisplayTick(World worldIn, int x, int y, int z, Random random) {
        if (shouldAnimate) {
            for (int i = 0; i <= 25; i++) {
                float var21 = (random.nextFloat() - 0.5F) * 0.2F;
                float var22 = (random.nextFloat() - 0.5F) * 0.2F;
                float var23 = (random.nextFloat() - 0.5F) * 0.2F;
                worldIn.spawnParticle(
                        "portal",
                        x + 0.5f + random.nextGaussian(),
                        y + 0.5f + random.nextGaussian(),
                        z + 0.5f + random.nextGaussian(),
                        (double) var21,
                        (double) var22,
                        (double) var23);
            }
            shouldAnimate = false;
        }
    }
}
