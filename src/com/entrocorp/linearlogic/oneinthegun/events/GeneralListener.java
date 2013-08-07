package com.entrocorp.linearlogic.oneinthegun.events;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.entrocorp.linearlogic.oneinthegun.OITG;
import com.entrocorp.linearlogic.oneinthegun.game.Arena;

public class GeneralListener {

    @EventHandler(ignoreCancelled = true)
    public void onClick(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            return;
        Player player = event.getPlayer();
        Arena arena = OITG.instance.getArenaManager().getArena(event.getClickedBlock().getLocation());
        if (arena == null)
            return;
        if (!player.hasPermission("oneinthegun.join")) {
            player.sendMessage(OITG.prefix + ChatColor.RED + "You don't have permission to play!");
            return;
        }
        if (arena.isIngame()) {
            player.sendMessage(OITG.prefix + ChatColor.RED + "A game is already in progress in that arena, choose another.");
            return;
        }
        if (arena.isClosed()) {
            player.sendMessage(OITG.prefix + ChatColor.RED + "That arena is closed, choose another.");
            return;
        }
        if (arena.getPlayerCount() >= arena.getPlayerLimit()) {
            player.sendMessage(OITG.prefix + ChatColor.RED + "That arena is full, choose another.");
            return;
        }
        if (arena.getLobby() == null) {
            player.sendMessage(OITG.prefix + ChatColor.RED + "That arena has not been set up: no lobby has been set.");
            return;
        }
        if (arena.getSpawns().length == 0) {
            player.sendMessage(OITG.prefix + ChatColor.RED + "That arena has not been set up: no spawn points have been set.");
            return;
        }
        if (OITG.instance.getArenaManager().getArena(player) != null) {
            player.sendMessage(OITG.prefix + ChatColor.RED + "You are already in an arena!");
            return;
        }
        arena.addPlayer(player);
        player.teleport(arena.getLobby());
        arena.broadcast(player.getName() + " has joined the arena!");
    }

    @EventHandler(ignoreCancelled = true)
    public void onSignChange(SignChangeEvent event) {
        if (OITG.instance.getArenaManager().getArena(event.getBlock().getLocation()) != null) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(OITG.prefix + ChatColor.RED + "Arena signs cannot be edited!");
            return;
        }
        if (!event.getLine(0).equalsIgnoreCase("oitg"))
            return;
        if (!event.getPlayer().hasPermission("oneinthegun.arena.signs")) {
            event.getPlayer().sendMessage(OITG.prefix + ChatColor.RED + "You don't have permission to create arena signs!");
            event.setCancelled(true);
            return;
        }
        Arena arena = OITG.instance.getArenaManager().getArena(event.getLine(1));
        if (arena == null) {
            event.getPlayer().sendMessage(OITG.prefix + ChatColor.RED + "The sign contains an invalid arena name!");
            event.setCancelled(true);
            return;
        }
        event.setLine(0, arena.toString());
        event.setLine(1, null);
        event.setLine(2, arena.getState());
        event.setLine(3, arena.getPlayerCount() + "/" + arena.getPlayerLimit());
        arena.addSignLocation(event.getBlock().getLocation());
        event.getPlayer().sendMessage(OITG.prefix + ChatColor.GRAY + "Created a sign for arena: " + arena.toString());
    }
}