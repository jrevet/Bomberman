package net.samagames.bomberman.powerup;

import net.samagames.api.SamaGamesAPI;
import org.apache.commons.lang.math.RandomUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Enum of powerups types
 *
 * @author Azuxul
 * @version 1.0
 */
public enum PowerupTypes {

    SPEED("Speed", "speed"),
    SLOWNESS("Lenteur", "slowness"),
    AUTO_PLACE("Auto place", "auto-place", 5),
    RANDOM_FUSE("Random bomb", "random-fuse"),
    HYPER_BOMB("Hyper bomb", "hyper-bomb"),
    SUPER_BOMB("Super bomb", "super-bomb"),
    SELF_INVULNERABILITY("Self protection", "self-invulnerability"),
    EXPLOSION_KILL("Charge nucléaire", true, "explosion-kill"),
    BOMB_ACTIVATOR("Détonateur", "bomb-activator"),
    DESTRUCTOR("Destructeur", "destructor", 5, true, false),
    BOMB_PROTECTION("Seconde vie", "bomb-protection"),
    BLINDNESS("Jet d\'encre", "blindness", true),
    SWAP("Swap", "swap", true),
    NAUSEA("Nausée", "nausea", true),
    WALL_BUILDER("Constructeur", "wall-builder", 8, true, false),
    FIRE("Incendie", "fire", true),
    FIREWORKS("Festivité", "fireworks", true),
    INVULNERABILITY("Invincibilité", "invulnerability", 13),
    FREEZER("Freezer", "freezer", 4);

    public static final String JSON_POWERUP_CHANCE = "booster-chance";

    private final String name;
    private final int chance;
    private final int duration;
    private final boolean special;

    PowerupTypes(String name, String jsonName) {

        this(name, jsonName, -1, false, false);
    }

    PowerupTypes(String name, boolean persistent, String jsonName) {

        this(name, jsonName, -1, false, persistent);
    }

    PowerupTypes(String name, String jsonName, boolean special) {

        this(name, jsonName, -1, special, false);
    }

    PowerupTypes(String name, String jsonName, int duration) {

        this(name, jsonName, duration, false, false);
    }

    PowerupTypes(String name, String jsonName, int duration, boolean special, boolean persistent) {

        this.name = name;
        this.chance = SamaGamesAPI.get().getGameManager().getGameProperties().getConfigs().get(JSON_POWERUP_CHANCE).getAsJsonObject().get(jsonName).getAsInt();
        this.duration = duration;
        this.special = special;
    }

    @Nonnull
    public static PowerupTypes getRandomPowerupType(boolean special) {

        PowerupTypes[] values = values();
        int chanceTotal = 0;

        List<PowerupTypes> powerups = new ArrayList<>();

        for (PowerupTypes powerupTypes : values) {
            if (powerupTypes.isSpecial() == special) {
                chanceTotal += powerupTypes.chance;
                powerups.add(powerupTypes);
            }
        }

        int random = RandomUtils.nextInt(chanceTotal * 10);
        int index = 0;

        for (PowerupTypes powerupTypes : powerups) {

            random = random - powerupTypes.chance * 10;

            if (random < 0)
                break;
            else
                index++;
        }

        return powerups.get(index);
    }

    public boolean isSpecial() {
        return special;
    }

    public int getDuration() {
        return duration;
    }

    public String getName() {
        return name;
    }

    public int getChance() {
        return chance;
    }

}
