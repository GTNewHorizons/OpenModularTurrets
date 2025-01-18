package openmodularturrets.entity.projectiles;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import openmodularturrets.blocks.turretheads.BlockAbstractTurretHead;
import openmodularturrets.entity.projectiles.damagesources.NormalDamageSource;
import openmodularturrets.handler.ConfigHandler;
import openmodularturrets.tileentity.turretbase.TurretBase;

public class BlazingClayProjectile extends TurretProjectile {

    public BlazingClayProjectile(World p_i1776_1_) {
        super(p_i1776_1_);
        this.gravity = 0.00F;
    }

    public BlazingClayProjectile(World par1World, ItemStack ammo, TurretBase turretBase) {
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

        if (movingobjectposition.typeOfHit.equals(0)) {
            if (worldObj.isAirBlock(
                    movingobjectposition.blockX,
                    movingobjectposition.blockY,
                    movingobjectposition.blockZ)) {
                return;
            }
        }

        if (!worldObj.isRemote) {
            AxisAlignedBB axis = AxisAlignedBB.getBoundingBox(
                    this.posX - 5,
                    this.posY - 5,
                    this.posZ - 5,
                    this.posX + 5,
                    this.posY + 5,
                    this.posZ + 5);
            List<Entity> targets = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, axis);

            int damage = ConfigHandler.getIncendiary_turret().getDamage();

            if (isAmped && movingobjectposition.entityHit instanceof EntityLivingBase) {
                EntityLivingBase elb = (EntityLivingBase) movingobjectposition.entityHit;
                damage += ((int) elb.getHealth() * (0.05 * amp_level));
            }

            for (Entity mob : targets) {
                boolean wasAlive = !mob.isDead;
                float healthBefore = 0;

                if (mob instanceof EntityLivingBase) {
                    EntityLivingBase elb = (EntityLivingBase) mob;
                    healthBefore = elb.getHealth();

                    if (mob instanceof EntityPlayer) {
                        if (canDamagePlayer((EntityPlayer) mob)) {
                            elb.attackEntityFrom(new NormalDamageSource("bullet"), damage);
                            elb.hurtResistantTime = 0;
                            elb.setFire(5);
                        }
                    } else {
                        elb.attackEntityFrom(new NormalDamageSource("bullet"), damage);
                        elb.hurtResistantTime = 0;
                        elb.setFire(5);
                    }

                    float healthAfter = elb.getHealth();
                    if (wasAlive && healthBefore > 0 && healthAfter <= 0) {
                        turretBase.onKill(mob);
                    }
                } else {
                    // Handle non-living base entities
                    mob.attackEntityFrom(new NormalDamageSource("bullet"), damage);
                    mob.hurtResistantTime = 0;
                    mob.setFire(5);

                    if (wasAlive && mob.isDead) {
                        turretBase.onKill(mob);
                    }
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
