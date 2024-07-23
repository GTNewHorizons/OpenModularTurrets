package openmodularturrets.blocks.turretheads;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import openmodularturrets.reference.ModInfo;
import openmodularturrets.reference.Names;
import openmodularturrets.tileentity.turrets.DisposableItemTurretTileEntity;

public class BlockDisposableTurret extends BlockAbstractTurretHead {

    public BlockDisposableTurret() {
        super();

        this.setBlockName(Names.Blocks.unlocalisedDisposableItemTurret);
        this.setBlockTextureName(ModInfo.ID + ":disposeItemTurret");
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new DisposableItemTurretTileEntity();
    }
}
