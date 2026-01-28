package net.darkwyvbat.dwbcore.world.entity.ai;

import net.darkwyvbat.dwbcore.util.PoorRandom;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.entity.projectile.hurtingprojectile.windcharge.WindCharge;
import net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownLingeringPotion;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownSplashPotion;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;

public final class WeaponActions {

    private static final Map<Item, WeaponUsageAction> WEAPON_ACTIONS = new IdentityHashMap<>();

    public static final WeaponUsageAction BOW = (user, weapon, target, charge) -> {
        if (!(user.level() instanceof ServerLevel serverLevel)) return;

        ItemStack itemStack = user.getItemInHand(ProjectileUtil.getWeaponHoldingHand(user, Items.BOW));
        ItemStack itemStack2 = new ItemStack(Items.ARROW);
        AbstractArrow arrow = ProjectileUtil.getMobArrow(user, itemStack, charge, itemStack2);
        double d = target.getX() - user.getX();
        double e = target.getY(0.34) - arrow.getY();
        double g = target.getZ() - user.getZ();
        double h = Math.sqrt(d * d + g * g);
        Projectile.spawnProjectileUsingShoot(arrow, serverLevel, itemStack2, d, e + h * 0.2F, g, 1.6F, 14 - serverLevel.getDifficulty().getId() * 4);

        user.playSound(SoundEvents.ARROW_SHOOT, 1.0F, 1.0F / (user.getRandom().nextFloat() * 0.4F + 0.8F));
    };

    public static final WeaponUsageAction TRIDENT = (user, weapon, target, charge) -> {
        if (!(user.level() instanceof ServerLevel serverLevel)) return;

        ItemStack itemStack2 = weapon.is(Items.TRIDENT) ? weapon : new ItemStack(Items.TRIDENT);
        ThrownTrident thrownTrident = new ThrownTrident(user.level(), user, itemStack2);
        double d = target.getX() - user.getX();
        double e = target.getY(0.3) - thrownTrident.getY();
        double g = target.getZ() - user.getZ();
        double h = Math.sqrt(d * d + g * g);
        Projectile.spawnProjectileUsingShoot(thrownTrident, serverLevel, itemStack2, d, e + h * 0.2F, g, 1.6F, (14 - user.level().getDifficulty().getId() * 4) / 2.0F);
        user.playSound(SoundEvents.TRIDENT_THROW.value(), 1.0F, (1.0F / (user.getRandom().nextFloat() * 0.4F + 0.8F) / 2.0F));
    };

    public static final WeaponUsageAction WIND_CHARGE = (user, weapon, target, charge) -> {
        if (!(user.level() instanceof ServerLevel serverLevel)) return;

        WindCharge windCharge = new WindCharge(EntityType.WIND_CHARGE, serverLevel);
        windCharge.setOwner(user);
        windCharge.setPos(user.getX(), user.getEyeY() - 0.1, user.getZ());
        double dX = target.getX() - user.getX();
        double dY = target.getEyeY() - windCharge.getY() - Math.random();
        double dZ = target.getZ() - user.getZ();
        windCharge.shoot(dX, dY, dZ, 1.5F, 0.5F);
        serverLevel.addFreshEntity(windCharge);

        serverLevel.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.SNOWBALL_THROW, user.getSoundSource(), 1.0F, 1.0F);
    };

    public static final WeaponUsageAction THROWN_POTION = (user, weapon, target, charge) -> {
        if (!(user.level() instanceof ServerLevel serverLevel)) return;

        Vec3 targetD = target.getDeltaMovement();
        double dX = target.getX() + targetD.x - user.getX();
        double dY = target.getEyeY() - 1.1 - user.getY();
        double dZ = target.getZ() + targetD.z - user.getZ();
        double dist = Math.sqrt(dX * dX + dZ * dZ);
        Projectile.ProjectileFactory<? extends AbstractThrownPotion> projectileFactory;
        SoundEvent throwSound;
        if (weapon.is(Items.LINGERING_POTION)) {
            projectileFactory = ThrownLingeringPotion::new;
            throwSound = SoundEvents.LINGERING_POTION_THROW;
        } else {
            projectileFactory = ThrownSplashPotion::new;
            throwSound = SoundEvents.SPLASH_POTION_THROW;
        }
        AbstractThrownPotion thrownPotion = projectileFactory.create(serverLevel, user, weapon);
        thrownPotion.shoot(dX, dY + dist * 0.2, dZ, 0.75F, 8.0F);
        serverLevel.addFreshEntity(thrownPotion);
        serverLevel.playSound(null, user.getX(), user.getY(), user.getZ(), throwSound, user.getSoundSource(), 1.0F, 0.8F + PoorRandom.quickFloat() * 0.4F);
    };

    static {
        register(Items.BOW, BOW);
        register(Items.TRIDENT, TRIDENT);
        register(Items.SPLASH_POTION, THROWN_POTION);
        register(Items.LINGERING_POTION, THROWN_POTION);
        register(Items.WIND_CHARGE, WIND_CHARGE);
    }

    public static void register(Item item, WeaponUsageAction strategy) {
        WEAPON_ACTIONS.put(item, strategy);
    }

    public static Optional<WeaponUsageAction> get(ItemStack weapon) {
        return Optional.ofNullable(WEAPON_ACTIONS.get(weapon.getItem()));
    }
}
