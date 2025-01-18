package openmodularturrets.entity.projectiles;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import openmodularturrets.blocks.turretheads.BlockAbstractTurretHead;
import openmodularturrets.entity.projectiles.damagesources.NormalDamageSource;
import openmodularturrets.handler.ConfigHandler;
import openmodularturrets.tileentity.turretbase.TurretBase;

public class DisposableTurretProjectile extends TurretProjectile {

    private EntityItem itemBound;
    private boolean spawned = false;

    public DisposableTurretProjectile(World p_i1776_1_) {
        super(p_i1776_1_);
        this.gravity = 0.03F;
    }

    public DisposableTurretProjectile(World par1World, ItemStack ammo, TurretBase turretBase) {
        super(par1World, ammo, turretBase);
        this.gravity = 0.03F;
    }

    @Override
    public void onEntityUpdate() {
        if (!spawned && !this.worldObj.isRemote && ticksExisted >= 2) {
            itemBound = new EntityItem(this.worldObj, posX, posY, posZ, ammo);
            itemBound.motionX = this.motionX;
            itemBound.motionY = this.motionY + 0.1F;
            itemBound.motionZ = this.motionZ;
            itemBound.delayBeforeCanPickup = 100;
            this.worldObj.spawnEntityInWorld(itemBound);
            spawned = true;
        }

        if (ticksExisted > 100) {
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

            int damage = ConfigHandler.getDisposableTurretSettings().getDamage();

            if (isAmped && movingobjectposition.entityHit instanceof EntityLivingBase) {
                EntityLivingBase elb = (EntityLivingBase) movingobjectposition.entityHit;
                damage += ((int) elb.getHealth() * (0.05 * amp_level));
            }

            if (movingobjectposition.entityHit instanceof EntityLivingBase) {
                EntityLivingBase elb = (EntityLivingBase) movingobjectposition.entityHit;
                float healthBefore = elb.getHealth();

                elb.attackEntityFrom(new NormalDamageSource("disposable"), damage);
                elb.hurtResistantTime = 0;

                float healthAfter = elb.getHealth();
                if (healthBefore > 0 && healthAfter <= 0) {
                    turretBase.onKill(elb);
                }
            } else {
                movingobjectposition.entityHit.attackEntityFrom(new NormalDamageSource("disposable"), damage);
                movingobjectposition.entityHit.hurtResistantTime = 0;

                if (movingobjectposition.entityHit.isDead) {
                    turretBase.onKill(movingobjectposition.entityHit);
                }
            }
        }

        if (itemBound != null) {
            itemBound.setDead();
        }
        this.setDead();
    }

    @Override
    protected float getGravityVelocity() {
        return this.gravity;
    }
}
