package openmodularturrets.blocks.turretheads;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import openmodularturrets.ModularTurrets;
import openmodularturrets.tileentity.turretbase.TurretBase;

public abstract class BlockAbstractTurretHead extends Block implements ITileEntityProvider {

    private static final AxisAlignedBB boundingBox = AxisAlignedBB.getBoundingBox(0.2F, 0.2F, 0.2F, 0.8F, 0.8F, 0.8F);

    BlockAbstractTurretHead() {
        super(Material.glass);

        this.setCreativeTab(ModularTurrets.modularTurretsTab);
        this.setBlockUnbreakable();
        this.setResistance(6000000.0F);
        this.setStepSound(Block.soundTypeStone);
        this.setBlockBounds(0.2F, 0.2F, 0.2F, 0.8F, 0.8F, 0.8F);
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return world.getTileEntity(x + 1, y, z) instanceof TurretBase
                || world.getTileEntity(x - 1, y, z) instanceof TurretBase
                || world.getTileEntity(x, y + 1, z) instanceof TurretBase
                || world.getTileEntity(x, y - 1, z) instanceof TurretBase
                || world.getTileEntity(x, y, z + 1) instanceof TurretBase
                || world.getTileEntity(x, y, z - 1) instanceof TurretBase;
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World worldIn, int x, int y, int z) {
        return boundingBox;
    }

    @Override
    public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z) {
        return false;
    }
}
