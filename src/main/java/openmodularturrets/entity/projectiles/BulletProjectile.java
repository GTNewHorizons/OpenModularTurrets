package openmodularturrets.entity.projectiles;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import openmodularturrets.blocks.turretheads.BlockAbstractTurretHead;
import openmodularturrets.entity.projectiles.damagesources.NormalDamageSource;
import openmodularturrets.handler.ConfigHandler;
import openmodularturrets.tileentity.turretbase.TurretBase;

public class BulletProjectile extends TurretProjectile {

    public BulletProjectile(World p_i1776_1_) {
        super(p_i1776_1_);
        this.gravity = 0.00F;
    }

    public BulletProjectile(World par1World, ItemStack ammo, TurretBase turretBase) {
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
        EntityPlayer player = worldObj.getClosestPlayerToEntity(this, 50); // Closest player (if needed)

        if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            Block hitBlock = worldObj
                    .getBlock(movingobjectposition.blockX, movingobjectposition.blockY, movingobjectposition.blockZ);
            if (hitBlock != null
                    && (!hitBlock.getMaterial().isSolid() || hitBlock instanceof BlockAbstractTurretHead)) {
                if (player != null) {
                    player.addChatMessage(new ChatComponentText("Hit non-solid block or turret head"));
                }
                return; // Stop if hitting a non-solid block or turret head
            }
        }

        // Check if we hit an entity and we're on the server side
        if (movingobjectposition.entityHit != null && !worldObj.isRemote) {
            // Determine final damage
            int damage = ConfigHandler.getGunTurretSettings().getDamage();
            if (isAmped && movingobjectposition.entityHit instanceof EntityLivingBase) {
                EntityLivingBase elb = (EntityLivingBase) movingobjectposition.entityHit;
                damage += ((int) elb.getHealth() * (0.1 * amp_level));
            }

            // Check if it's a player and if we can damage them
            if (movingobjectposition.entityHit instanceof EntityPlayer) {
                if (!canDamagePlayer((EntityPlayer) movingobjectposition.entityHit)) {
                    // If we can't damage the player, exit early
                    return;
                }
            }

            // Single damage call
            if (movingobjectposition.entityHit instanceof EntityLivingBase) {
                EntityLivingBase elb = (EntityLivingBase) movingobjectposition.entityHit;
                // Track health before dealing damage
                float healthBefore = elb.getHealth();

                // Deal damage once
                elb.attackEntityFrom(new NormalDamageSource("bullet"), damage);
                elb.hurtResistantTime = 0;

                // Check if the entity was killed
                float healthAfter = elb.getHealth();
                if (healthBefore > 0 && healthAfter <= 0) {
                    // If final blow, increment kill count or handle kill logic
                    turretBase.onKill(elb);
                }
            } else {
                // If the hit entity is not a LivingEntity, just deal damage once
                movingobjectposition.entityHit.attackEntityFrom(new NormalDamageSource("bullet"), damage);
            }
        }

        // If we didn't hit an entity, play a sound (optional)
        if (movingobjectposition.entityHit == null && !worldObj.isRemote) {
            Random random = new Random();
            worldObj.playSoundEffect(
                    posX,
                    posY,
                    posZ,
                    "openmodularturrets:bulletHit",
                    ConfigHandler.getTurretSoundVolume(),
                    random.nextFloat() + 0.5F);
        }

        // Destroy the projectile after impact
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
