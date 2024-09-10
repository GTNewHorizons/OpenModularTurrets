package openmodularturrets.blocks.misc;

import static net.minecraftforge.common.util.ForgeDirection.*;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import openmodularturrets.ModularTurrets;
import openmodularturrets.blocks.util.BlockAbstract;
import openmodularturrets.reference.ModInfo;
import openmodularturrets.reference.Names;
import openmodularturrets.tileentity.LeverTileEntity;
import openmodularturrets.tileentity.turretbase.TurretBaseTierOneTileEntity;

public class LeverBlock extends BlockAbstract implements ITileEntityProvider {

    private static final AxisAlignedBB BOUNDING_BOX = AxisAlignedBB.getBoundingBox(0.2F, 0.2F, 0.2F, 0.8F, 0.8F, 0.8F);

    public LeverBlock() {
        super(Material.rock);
        this.setBlockName(Names.Blocks.unlocalisedLever);
        this.setCreativeTab(ModularTurrets.modularTurretsTab);
        this.setHardness(2F);
        this.setBlockBounds(0.2F, 0.2F, 0.2F, 0.8F, 0.8F, 0.8F);
        this.setResistance(15F);
        this.setStepSound(Block.soundTypeStone);
    }

    @Override
    public void registerBlockIcons(IIconRegister icon) {
        blockIcon = icon.registerIcon(ModInfo.ID.toLowerCase() + ":turretBaseTierTwo");
    }

    @Override
    public TileEntity createNewTileEntity(World world, int par2) {
        return new LeverTileEntity();
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return (world.getTileEntity(x + 1, y, z) instanceof TurretBaseTierOneTileEntity
                || world.getTileEntity(x - 1, y, z) instanceof TurretBaseTierOneTileEntity
                || world.getTileEntity(x, y, z + 1) instanceof TurretBaseTierOneTileEntity
                || world.getTileEntity(x, y, z - 1) instanceof TurretBaseTierOneTileEntity);
    }

    @Override
    public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLivingBase,
            ItemStack par6ItemStack) {
        float l = 0;
        if (par1World.getTileEntity(par2 + 1, par3, par4) instanceof TurretBaseTierOneTileEntity) {
            l = 270F;
        }
        if (par1World.getTileEntity(par2 - 1, par3, par4) instanceof TurretBaseTierOneTileEntity) {
            l = 90F;
        }
        if (par1World.getTileEntity(par2, par3, par4 + 1) instanceof TurretBaseTierOneTileEntity) {
            l = 0F;
        }
        if (par1World.getTileEntity(par2, par3, par4 - 1) instanceof TurretBaseTierOneTileEntity) {
            l = 180;
        }
        int shu = MathHelper.floor_double((l * 4.0F / 360.0F) + 0.5D) & 3;
        par1World.setBlockMetadataWithNotify(par2, par3, par4, shu, 2);
    }

    public TurretBaseTierOneTileEntity getTurretBase(World world, int x, int y, int z) {
        int metadata = world.getBlockMetadata(x, y, z);
        ForgeDirection turretDirection = decipherMetadata(metadata);
        int xReal = x;
        int zReal = z;
        x += turretDirection.offsetX;
        z += turretDirection.offsetZ;

        if (!(world.getTileEntity(x, y, z) instanceof TurretBaseTierOneTileEntity)) {
            world.setBlockToAir(x, y, z);
            return null;
        }

        return (TurretBaseTierOneTileEntity) world.getTileEntity(x, y, z);
    }

    private static ForgeDirection decipherMetadata(int metadata) {
        // Your metadata deciphering logic goes here
        // For now, let's just pretend it's a simple mapping
        switch (metadata) {
            case 0:
                return SOUTH;
            case 1:
                return WEST;
            case 2:
                return NORTH;
            case 3:
                return EAST;
            default:
                throw new IllegalArgumentException(
                        "Bad metadata, kid. It's like reading a book with half the pages torn out.");
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX,
            float subY, float subZ) {
        TurretBaseTierOneTileEntity base = getTurretBase(worldIn, x, y, z);
        TileEntity te = worldIn.getTileEntity(x, y, z);
        if (base != null && te instanceof LeverTileEntity lever) {
            lever.isTurning = true;
            if (lever.rotation == 0F) {
                worldIn.playSoundEffect(x, y, z, "openmodularturrets:windup", 1.0F, 1.0F);
                base.receiveEnergy(ForgeDirection.UNKNOWN, 50, false);
            }
        }

        return true;
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World worldIn, int x, int y, int z) {
        return BOUNDING_BOX;
    }

    @Override
    public boolean isBlockNormalCube() {
        return false;
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }
}
