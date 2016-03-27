package fr.azuxul.bomberman.map;

import fr.azuxul.bomberman.GameManager;
import fr.azuxul.bomberman.entity.Bomb;
import fr.azuxul.bomberman.player.PlayerBomberman;
import fr.azuxul.bomberman.powerup.PowerupTypes;
import net.samagames.tools.Area;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;

/**
 * Map manager
 *
 * @author Azuxul
 * @version 1.0
 */
public class MapManager {

    private final CaseMap[][] map;
    private final int height;
    private final int wight;
    private final GameManager gameManager;
    private final Area area;

    public MapManager(GameManager gameManager, Location smallerLoc, Location higherLoc) {

        this.gameManager = gameManager;

        this.area = new Area(smallerLoc, higherLoc);

        this.wight = area.getSizeX() + 1;
        this.height = area.getSizeZ() + 1;

        this.map = new CaseMap[wight][height];

        for (int x = area.getMin().getBlockX(); x <= area.getMax().getBlockX(); x++) {
            for (int z = area.getMin().getBlockZ(); z <= area.getMax().getBlockZ(); z++) {

                int mapX = worldLocXToMapLocX(x);
                int mapY = worldLocZToMapLocY(z);

                map[mapX][mapY] = new CaseMap(gameManager, new Location(smallerLoc.getWorld(), x, smallerLoc.getY(), z), mapX, mapY);
            }
        }
    }

    public CaseMap[][] getMap() {
        return map;
    }

    public int worldLocXToMapLocX(int xWorld) {

        return xWorld - area.getMin().getBlockX();
    }

    public int worldLocZToMapLocY(int zWorld) {

        return zWorld - area.getMin().getBlockZ();
    }

    public int getHeight() {
        return height;
    }

    public int getWight() {
        return wight;
    }

    public CaseMap getCaseAtWorldLocation(Location location) {

        CaseMap result = null;

        int x = worldLocXToMapLocX(location.getBlockX());
        int y = worldLocZToMapLocY(location.getBlockZ());

        if (x < wight && x > -1 && y < height && y > -1)
            result = map[x][y];

        return result;
    }

    public void movePlayer(Player player, Location locTo) {

        PlayerBomberman playerBomberman = gameManager.getPlayer(player.getUniqueId());

        CaseMap caseMap = playerBomberman.getCaseMap();

        if (caseMap != null)
            caseMap.getPlayers().remove(playerBomberman);

        caseMap = getCaseAtWorldLocation(locTo);

        if (caseMap != null) {
            playerBomberman.setCaseMap(caseMap);
            caseMap.getPlayers().add(playerBomberman);

            if (playerBomberman.getPowerupTypes() != null && playerBomberman.getPowerupTypes().equals(PowerupTypes.AUTO_PLACE) && caseMap.getBomb() == null && playerBomberman.getBombNumber() > playerBomberman.getPlacedBombs())
                gameManager.getMapManager().spawnBomb(locTo.getBlock().getLocation(), playerBomberman);
        } else
            player.kickPlayer(ChatColor.RED + "Sortie de la map !");
    }

    @SuppressWarnings("deprecation")
    public boolean spawnBomb(Location location, PlayerBomberman player) {

        location.setY(gameManager.getBombY());
        Block block = location.getBlock();

        CaseMap caseMap = gameManager.getMapManager().getCaseAtWorldLocation(location);

        if (caseMap != null && (caseMap.getBomb() == null || (caseMap.getBomb() != null && !caseMap.getBomb().isAlive()))) {
            player.setPlacedBombs(player.getPlacedBombs() + 1);

            block.setType(Material.CARPET);
            block.setData((byte) 8);

            Bomb bomb = new Bomb(((CraftWorld) location.getWorld()).getHandle(), location.getX() + 0.5, location.getY(), location.getZ() + 0.5, player.getFuseTicks(), player.getRadius(), player.getPlayerIfOnline());

            caseMap.setBomb(bomb);

            gameManager.getServer().getScheduler().runTaskLater(gameManager.getPlugin(), () -> {

                if (caseMap.getBomb() != null && caseMap.getBomb().isAlive() && bomb.isAlive()) {
                    block.setType(Material.AIR);

                    ((CraftWorld) location.getWorld()).getHandle().addEntity(bomb, CreatureSpawnEvent.SpawnReason.CUSTOM);
                }

            }, 20L);

            return true;
        } else {
            return false;
        }
    }

}
