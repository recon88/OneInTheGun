package com.entrocorp.linearlogic.oneinthegun.events;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import com.entrocorp.linearlogic.oneinthegun.OITG;
import com.entrocorp.linearlogic.oneinthegun.game.Arena;

public class GameListener implements Listener {

    private boolean listening = false;

    public boolean isRegistered() {
        return listening;
    }

    public void setRegistered(boolean registered) {
        if (listening == registered)
            return;
        if (registered)
            OITG.instance.getServer().getPluginManager().registerEvents(this, OITG.instance);
        else
            HandlerList.unregisterAll(this);
        listening = registered;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBuild(BlockPlaceEvent event) {
        Arena arena = OITG.instance.getArenaManager().getArena(event.getPlayer());
        if (arena != null && !arena.isBlockPlacingAllowed()) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(OITG.prefix + ChatColor.RED + "Block placing is disabled in this arena.");
            return;
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Player defender = (event.getEntity() instanceof Player ? (Player) event.getEntity() : null),
                attacker = (event.getDamager() instanceof Player ? (Player) event.getDamager() : null);
        if (defender == null && attacker == null)
            return;

        Arena defenderArena = OITG.instance.getArenaManager().getArena(defender);
        if (defenderArena != null) {
            if (attacker == null) {
                if (!defenderArena.isMobInteractAllowed()) {
                    event.setCancelled(true);
                    return;
                }
            }
        }

        Arena attackerArena = OITG.instance.getArenaManager().getArena(attacker);
        if (attackerArena != null) {
            if (defender == null && !attackerArena.isMobInteractAllowed()) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;
        Player player = (Player) event.getEntity();
        Arena arena = OITG.instance.getArenaManager().getArena(player);
        if (arena != null && !arena.isHealthRegenAllowed())
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onHunger(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;
        Player player = (Player) event.getEntity();
        Arena arena = OITG.instance.getArenaManager().getArena(player);
        if (arena != null && !arena.isHungerAllowed())
            event.setCancelled(true);
    }
}
