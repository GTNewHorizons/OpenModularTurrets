package openmodularturrets.blocks.turretbases;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import openmodularturrets.ModularTurrets;
import openmodularturrets.blocks.util.BlockAbstractContainer;
import openmodularturrets.handler.ConfigHandler;
import openmodularturrets.tileentity.turretbase.TrustedPlayer;
import openmodularturrets.tileentity.turretbase.TurretBase;
import openmodularturrets.util.PlayerUtil;

public abstract class BlockAbstractTurretBase extends BlockAbstractContainer {

    public BlockAbstractTurretBase() {
        super(Material.rock);
        this.setCreativeTab(ModularTurrets.modularTurretsTab);
        if (!ConfigHandler.turretBreakable) {
            this.setBlockUnbreakable();
        }
        this.setStepSound(Block.soundTypeStone);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int metadata, float what,
            float these, float are) {
        if (!world.isRemote && player.isSneaking()
                && ConfigHandler.isAllowBaseCamo()
                && player.getCurrentEquippedItem() == null) {
            TurretBase base = (TurretBase) world.getTileEntity(x, y, z);
            if (base != null) {
                if (player.getUniqueID().toString().equals(base.getOwner())) {
                    base.camoStack = null;
                } else {
                    player.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("status.ownership")));
                }
            }
        }

        if (!world.isRemote && !player.isSneaking()
                && ConfigHandler.isAllowBaseCamo()
                && player.getCurrentEquippedItem() != null
                && player.getCurrentEquippedItem() != null
                && player.getCurrentEquippedItem().getItem() instanceof ItemBlock
                && Block.getBlockFromItem(player.getCurrentEquippedItem().getItem()).isNormalCube()
                && Block.getBlockFromItem(player.getCurrentEquippedItem().getItem()).isOpaqueCube()
                && !(Block.getBlockFromItem(
                        player.getCurrentEquippedItem().getItem()) instanceof BlockAbstractTurretBase)) {
            TurretBase base = (TurretBase) world.getTileEntity(x, y, z);
            if (base != null) {
                if (player.getUniqueID().toString().equals(base.getOwner())) {
                    base.camoStack = player.getCurrentEquippedItem();
                } else {
                    player.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("status.ownership")));
                }
            }

        } else if (!world.isRemote && !player.isSneaking()) {
            TurretBase base = (TurretBase) world.getTileEntity(x, y, z);
            TrustedPlayer trustedPlayer = PlayerUtil.getTrustedPlayer(player, base);

            if (trustedPlayer != null) {
                if (trustedPlayer.canOpenGUI) {
                    player.openGui(ModularTurrets.instance, base.getBaseTier(), world, x, y, z);
                    return true;
                }
            } else if (PlayerUtil.isPlayerOwner(player, base)) {
                player.openGui(ModularTurrets.instance, base.getBaseTier(), world, x, y, z);
            } else {
                player.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("status.ownership")));
            }
        }
        return true;
    }

    @Override
    public void randomDisplayTick(World worldIn, int x, int y, int z, Random random) {
        if (worldIn.isRemote) {
            Minecraft.getMinecraft().renderGlobal.markBlockForRenderUpdate(x, y, z);
        }
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block par5) {
        if (!world.isRemote) {
            if (world.isBlockIndirectlyGettingPowered(x, y, z)) {
                ((TurretBase) world.getTileEntity(x, y, z)).setRedstone(true);
            } else if (!world.isBlockIndirectlyGettingPowered(x, y, z)) {
                ((TurretBase) world.getTileEntity(x, y, z)).setRedstone(false);
            }
        }
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase elb, ItemStack stack) {
        if (!world.isRemote) {
            EntityPlayerMP player = (EntityPlayerMP) elb;
            TurretBase base = (TurretBase) world.getTileEntity(x, y, z);
            PlayerUtil.setBaseOwner(player, base);
            base.setOwnerName(player.getDisplayName());
            if (world.isBlockIndirectlyGettingPowered(x, y, z)) {
                base.setRedstone(true);
            } else if (!world.isBlockIndirectlyGettingPowered(x, y, z)) {
                base.setRedstone(false);
            }
            world.markBlockForUpdate(x, y, z);
        }
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block par5, int par6) {
        if (!world.isRemote) {
            dropItems(world, x, y, z);
            super.breakBlock(world, x, y, z, par5, par6);
        }
    }

    @Override
    public IIcon getIcon(IBlockAccess worldIn, int x, int y, int z, int side) {
        TurretBase base = (TurretBase) worldIn.getTileEntity(x, y, z);
        if (base != null && base.camoStack != null) {
            Block camoBlock = Block.getBlockFromItem(base.camoStack.getItem());
            if (camoBlock != null && camoBlock.renderAsNormalBlock())
                return camoBlock.getIcon(side, base.camoStack.getItemDamage());
        }

        return blockIcon;
    }
}
