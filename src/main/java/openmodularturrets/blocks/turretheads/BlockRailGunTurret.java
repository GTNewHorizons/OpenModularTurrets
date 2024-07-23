package openmodularturrets.blocks.turretheads;

import java.util.Random;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import openmodularturrets.reference.ModInfo;
import openmodularturrets.reference.Names;
import openmodularturrets.tileentity.turrets.RailGunTurretTileEntity;

public class BlockRailGunTurret extends BlockAbstractTurretHead {

    public BlockRailGunTurret() {
        super();

        this.setBlockName(Names.Blocks.unlocalisedRailGunTurret);
        this.setBlockTextureName(ModInfo.ID + ":railGunTurret");
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new RailGunTurretTileEntity();
    }

    @Override
    public void randomDisplayTick(World worldIn, int x, int y, int z, Random random) {
        for (int i = 0; i <= 5; i++) {
            worldIn.spawnParticle(
                    "reddust",
                    x + (random.nextGaussian() / 10) + 0.5F,
                    y,
                    z + (random.nextGaussian() / 10) + 0.5F,
                    (0),
                    (50),
                    (200));
        }
    }
}
