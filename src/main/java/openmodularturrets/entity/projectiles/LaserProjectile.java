package openmodularturrets.entity.projectiles;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import openmodularturrets.blocks.turretheads.BlockAbstractTurretHead;
import openmodularturrets.entity.projectiles.damagesources.NormalDamageSource;
import openmodularturrets.handler.ConfigHandler;
import openmodularturrets.tileentity.turretbase.TurretBase;

public class LaserProjectile extends TurretProjectile {

    public int arrowShake;

    public LaserProjectile(World par1World) {
        super(par1World);
        this.gravity = 0.00F;
    }

    public LaserProjectile(World par1World, TurretBase turretBase) {
        super(par1World, turretBase);
        this.gravity = 0.00F;
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer par1EntityPlayer) {
        this.setDead();
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
            Random random = new Random();
            worldObj.playSoundEffect(
                    posX,
                    posY,
                    posZ,
                    "openmodularturrets:laserHit",
                    ConfigHandler.getTurretSoundVolume(),
                    random.nextFloat() + 0.5F);

            int damage = ConfigHandler.getLaserTurretSettings().getDamage();
            boolean wasAlive = !movingobjectposition.entityHit.isDead;

            if (isAmped && movingobjectposition.entityHit instanceof EntityLivingBase) {
                EntityLivingBase elb = (EntityLivingBase) movingobjectposition.entityHit;
                damage += ((int) elb.getHealth() * (0.1 * amp_level));
            }

            if (movingobjectposition.entityHit instanceof EntityLivingBase) {
                EntityLivingBase elb = (EntityLivingBase) movingobjectposition.entityHit;
                float healthBefore = elb.getHealth();

                elb.setFire(2);
                elb.attackEntityFrom(new NormalDamageSource("laser"), damage);
                elb.hurtResistantTime = 0;

                float healthAfter = elb.getHealth();
                if (wasAlive && healthBefore > 0 && healthAfter <= 0) {
                    turretBase.onKill(elb);
                }
            } else {
                movingobjectposition.entityHit.setFire(2);
                movingobjectposition.entityHit.attackEntityFrom(new NormalDamageSource("laser"), damage);
                movingobjectposition.entityHit.hurtResistantTime = 0;

                if (wasAlive && movingobjectposition.entityHit.isDead) {
                    turretBase.onKill(movingobjectposition.entityHit);
                }
            }
        }
        this.setDead();
    }

    @Override
    protected float getGravityVelocity() {
        return this.gravity;
    }
}
