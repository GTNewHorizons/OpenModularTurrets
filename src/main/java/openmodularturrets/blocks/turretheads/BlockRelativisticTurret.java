package openmodularturrets.blocks.turretheads;

import java.util.Random;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import openmodularturrets.reference.ModInfo;
import openmodularturrets.reference.Names;
import openmodularturrets.tileentity.turrets.RelativisticTurretTileEntity;

public class BlockRelativisticTurret extends BlockAbstractTurretHead {

    public BlockRelativisticTurret() {
        super();
        this.setBlockName(Names.Blocks.unlocalisedRelativisticTurret);
        this.setBlockTextureName(ModInfo.ID + ":relativisticTurret");
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new RelativisticTurretTileEntity();
    }

    @Override
    public void randomDisplayTick(World worldIn, int x, int y, int z, Random random) {
        for (int i = 0; i <= 5; i++) {
            worldIn.spawnParticle(
                    "reddust",
                    x + (random.nextGaussian() / 10) + 0.5F,
                    y + 0.5F,
                    z + (random.nextGaussian() / 10) + 0.5F,
                    (200),
                    (200),
                    (200));
        }
    }
}
