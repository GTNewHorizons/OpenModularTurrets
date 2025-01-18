package openmodularturrets.entity.projectiles;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import openmodularturrets.blocks.turretheads.BlockAbstractTurretHead;
import openmodularturrets.entity.projectiles.damagesources.ArmorBypassDamageSource;
import openmodularturrets.handler.ConfigHandler;
import openmodularturrets.tileentity.turretbase.TurretBase;

public class FerroSlugProjectile extends TurretProjectile {

    public FerroSlugProjectile(World p_i1776_1_) {
        super(p_i1776_1_);
        this.gravity = 0.00F;
    }

    public FerroSlugProjectile(World par1World, ItemStack ammo, TurretBase turretBase) {
        super(par1World, ammo, turretBase);
        this.gravity = 0.00F;
    }

    @Override
    public void onEntityUpdate() {
        if (ticksExisted >= 50) {
            this.setDead();
        }
    }

    @Override
    protected void onImpact(MovingObjectPosition movingobjectposition) {
        if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            Block hitBlock = worldObj
                    .getBlock(movingobjectposition.blockX, movingobjectposition.blockY, movingobjectposition.blockZ);
            if (hitBlock != null
                    && (!hitBlock.getMaterial().isSolid() || hitBlock instanceof BlockAbstractTurretHead)) {
                // Go through non-solid block or turrets
                return;
            }
        }

        if (movingobjectposition.entityHit != null && !worldObj.isRemote) {
            if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                if (worldObj.isAirBlock(
                        movingobjectposition.blockX,
                        movingobjectposition.blockY,
                        movingobjectposition.blockZ)) {
                    return;
                }
            }

            int damage = ConfigHandler.getRailgun_turret().getDamage();
            boolean wasAlive = !movingobjectposition.entityHit.isDead;

            if (isAmped && movingobjectposition.entityHit instanceof EntityLivingBase) {
                EntityLivingBase elb = (EntityLivingBase) movingobjectposition.entityHit;
                damage += ((int) elb.getHealth() * (0.25 * amp_level));
            }

            Random random = new Random();
            worldObj.playSoundEffect(
                    posX,
                    posY,
                    posZ,
                    "openmodularturrets:railGunHit",
                    ConfigHandler.getTurretSoundVolume(),
                    random.nextFloat() + 0.5F);

            if (movingobjectposition.entityHit instanceof EntityLivingBase) {
                EntityLivingBase elb = (EntityLivingBase) movingobjectposition.entityHit;
                float healthBefore = elb.getHealth();

                elb.attackEntityFrom(new ArmorBypassDamageSource("ferroslug"), damage);
                elb.hurtResistantTime = 0;

                float healthAfter = elb.getHealth();
                if (wasAlive && healthBefore > 0 && healthAfter <= 0) {
                    turretBase.onKill(elb);
                }
            } else {
                movingobjectposition.entityHit.attackEntityFrom(new ArmorBypassDamageSource("ferroslug"), damage);
                movingobjectposition.entityHit.hurtResistantTime = 0;

                if (wasAlive && movingobjectposition.entityHit.isDead) {
                    turretBase.onKill(movingobjectposition.entityHit);
                }
            }
        }

        this.setDead();
    }

    @Override
    protected void updateFallState(double par1, boolean par3) {
        this.posY = posY + 12F;
    }

    @Override
    protected float getGravityVelocity() {
        return this.gravity;
    }
}
