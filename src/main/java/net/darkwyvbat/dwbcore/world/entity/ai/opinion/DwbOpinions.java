package net.darkwyvbat.dwbcore.world.entity.ai.opinion;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.feline.Ocelot;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.warden.Warden;

import java.util.IdentityHashMap;
import java.util.Map;

public final class DwbOpinions {

    public static Opinion UNKNOWN = new Opinion(Reputation.NEUTRAL, DangerLevel.HARMLESS, InterestLevel.LOW);

    public static final OpinionResolver _HUMAN_TESTER_OPINIONS = createHumanTester();

    private static OpinionResolver createHumanTester() {
        Map<Class<? extends Entity>, Opinion> opinions = new IdentityHashMap<>();
        opinions.put(Monster.class, create(Reputation.DISLIKED, DangerLevel.DANGEROUS, InterestLevel.LOW));
        opinions.put(Sheep.class, create(Reputation.DISLIKED, DangerLevel.DANGEROUS, InterestLevel.LOW));
        opinions.put(Ocelot.class, create(Reputation.DISLIKED, DangerLevel.HARMLESS, InterestLevel.LOW));
        opinions.put(Warden.class, create(Reputation.DISLIKED, DangerLevel.DEADLY, InterestLevel.LOW));
        opinions.put(IronGolem.class, create(Reputation.TRUSTED, DangerLevel.ALLY, InterestLevel.LOW));
        return new OpinionResolver(opinions, UNKNOWN);
    }

    public static Opinion create(Reputation reputation, DangerLevel dangerLevel, InterestLevel interestLevel) {
        return new Opinion(reputation, dangerLevel, interestLevel);
    }
}
