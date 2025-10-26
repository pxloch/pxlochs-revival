package com.pxloch.pxlochsrevival;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public class PxlochsRevival extends JavaPlugin implements Listener, CommandExecutor {
    
    private HashMap<UUID, DeadPlayerData> deadPlayers = new HashMap<>();
    private HashMap<UUID, Integer> revivesRemaining = new HashMap<>();
    private static final int MAX_REVIVES = 3;
    
    private int reviveCost = 20; // Başlangıç: 20 demir
    private boolean diamondsAchievementGained = false;
    private int nightsPassed = 0;
    private long lastNightCheck = 0;
    private String language = "tr";
    
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        this.getCommand("revive").setExecutor(this);
        this.getCommand("lives").setExecutor(this);
        
        createDefaultConfig();
        loadConfig();
        startNightCounter();
        
        getLogger().info("Hardcore Revival Plugin aktif!");
        getLogger().info("Dil: " + language);
        getLogger().info("Canlandırma maliyeti: " + reviveCost + " " + (reviveCost == 20 ? "Demir" : "Elmas"));
    }
    
    @Override
    public void onDisable() {
        saveConfigData();
        getLogger().info("Hardcore Revival Plugin kapatıldı!");
    }
    
    private void createDefaultConfig() {
        // Config klasörünü oluştur
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        
        // Eğer config yoksa default oluştur
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveResource("config.yml", false);
            getLogger().info("Varsayılan config.yml oluşturuldu!");
        }
    }
    
    private void loadConfig() {
        reloadConfig();
        
        // Ayarları yükle
        language = getConfig().getString("settings.language", "tr");
        reviveCost = getConfig().getInt("game-data.revive-cost", 20);
        diamondsAchievementGained = getConfig().getBoolean("game-data.diamonds-achievement", false);
        nightsPassed = getConfig().getInt("game-data.nights-passed", 0);
        lastNightCheck = getConfig().getLong("game-data.last-night-check", 0);
        
        // Revive haklarını yükle
        if (getConfig().contains("game-data.revive-rights")) {
            for (String uuidStr : getConfig().getConfigurationSection("game-data.revive-rights").getKeys(false)) {
                UUID uuid = UUID.fromString(uuidStr);
                int rights = getConfig().getInt("game-data.revive-rights." + uuidStr);
                revivesRemaining.put(uuid, rights);
            }
        }
    }
    
    private void saveConfigData() {
        getConfig().set("game-data.revive-cost", reviveCost);
        getConfig().set("game-data.diamonds-achievement", diamondsAchievementGained);
        getConfig().set("game-data.nights-passed", nightsPassed);
        getConfig().set("game-data.last-night-check", lastNightCheck);
        
        // Revive haklarını kaydet
        for (UUID uuid : revivesRemaining.keySet()) {
            getConfig().set("game-data.revive-rights." + uuid.toString(), revivesRemaining.get(uuid));
        }
        
        saveConfig();
    }
    
    private String msg(String key) {
        String path = "messages." + language + "." + key;
        String message = getConfig().getString(path);
        
        if (message == null) {
            // Eğer dil bulunamazsa İngilizce'ye düş
            message = getConfig().getString("messages.en." + key, "Message not found: " + key);
        }
        
        return message.replace("&", "§");
    }
    
    private void startNightCounter() {
        new BukkitRunnable() {
            @Override
            public void run() {
                World world = Bukkit.getWorlds().get(0);
                long time = world.getTime();
                
                // Gece başlangıcı: 13000-23000 arası
                if (time >= 13000 && time <= 23000) {
                    // Eğer bu gece henüz sayılmadıysa
                    if (System.currentTimeMillis() - lastNightCheck > 600000) { // 10 dakika cooldown
                        lastNightCheck = System.currentTimeMillis();
                        nightsPassed++;
                        
                        // Her 2 gecede bir elmas maliyeti artır
                        if (diamondsAchievementGained && nightsPassed % 2 == 0) {
                            reviveCost++;
                            Bukkit.broadcastMessage("§6§l========================================");
                            Bukkit.broadcastMessage(msg("NIGHTS_PASSED"));
                            Bukkit.broadcastMessage(msg("COST_INCREASED").replace("{cost}", String.valueOf(reviveCost)));
                            Bukkit.broadcastMessage("§6§l========================================");
                            saveConfigData();
                        }
                    }
                }
            }
        }.runTaskTimer(this, 0L, 20L * 60); // Her dakika kontrol et
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // Yeni oyuncu için canlanma hakkı ver
        if (!revivesRemaining.containsKey(player.getUniqueId())) {
            revivesRemaining.put(player.getUniqueId(), MAX_REVIVES);
            saveConfigData();
        }
    }
    
    @EventHandler
    public void onAdvancement(PlayerAdvancementDoneEvent event) {
        String advancementKey = event.getAdvancement().getKey().getKey();
        
        // Elmas başarımı: story/mine_diamond
        if (advancementKey.equals("story/mine_diamond") && !diamondsAchievementGained) {
            diamondsAchievementGained = true;
            reviveCost = 5; // Artık 5 elmas
            nightsPassed = 0; // Gece sayacını sıfırla
            
            Bukkit.broadcastMessage("§6§l========================================");
            Bukkit.broadcastMessage(msg("DIAMOND_FOUND").replace("{player}", event.getPlayer().getName()));
            Bukkit.broadcastMessage(msg("COST_CHANGED"));
            Bukkit.broadcastMessage(msg("COST_OLD"));
            Bukkit.broadcastMessage(msg("COST_NEW"));
            Bukkit.broadcastMessage(msg("COST_INFO"));
            Bukkit.broadcastMessage("§6§l========================================");
            
            saveConfigData();
        }
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player deadPlayer = event.getEntity();
        
        // Eğer oyuncunun canlanma hakkı kalmamışsa
        if (!revivesRemaining.containsKey(deadPlayer.getUniqueId())) {
            revivesRemaining.put(deadPlayer.getUniqueId(), MAX_REVIVES);
        }
        
        int livesLeft = revivesRemaining.get(deadPlayer.getUniqueId());
        
        if (livesLeft <= 0) {
            // Canlanma hakkı yok - kalıcı ölüm
            deadPlayer.setGameMode(GameMode.SPECTATOR);
            Bukkit.broadcastMessage("§c§l========================================");
            Bukkit.broadcastMessage(msg("PERMANENT_DEATH").replace("{player}", deadPlayer.getName()));
            Bukkit.broadcastMessage(msg("NO_REVIVES_LEFT"));
            Bukkit.broadcastMessage(msg("SERVER_RESET"));
            Bukkit.broadcastMessage("§c§l========================================");
            
            // Sunucuyu sıfırla
            new BukkitRunnable() {
                int countdown = 10;
                
                @Override
                public void run() {
                    if (countdown > 0) {
                        Bukkit.broadcastMessage(msg("COUNTDOWN").replace("{seconds}", String.valueOf(countdown)));
                        countdown--;
                    } else {
                        resetServer();
                        cancel();
                    }
                }
            }.runTaskTimer(this, 0L, 20L);
            
            return;
        }
        
        // Ölü oyuncuyu kaydet
        deadPlayers.put(deadPlayer.getUniqueId(), new DeadPlayerData(
            deadPlayer.getLocation(),
            deadPlayer.getName(),
            livesLeft
        ));
        
        // Spectator moduna al
        deadPlayer.setGameMode(GameMode.SPECTATOR);
        
        // Kaç kişinin öldüğünü kontrol et
        int deadCount = 0;
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (deadPlayers.containsKey(p.getUniqueId())) {
                deadCount++;
            }
        }
        
        // Eğer 2 oyuncu da öldüyse
        if (deadCount >= 2) {
            Bukkit.broadcastMessage(msg("SEPARATOR"));
            Bukkit.broadcastMessage(msg("BOTH_DEAD"));
            Bukkit.broadcastMessage(msg("SERVER_RESET"));
            Bukkit.broadcastMessage(msg("SEPARATOR"));
            
            new BukkitRunnable() {
                int countdown = 10;
                
                @Override
                public void run() {
                    if (countdown > 0) {
                        Bukkit.broadcastMessage("§e" + countdown + " saniye...");
                        countdown--;
                    } else {
                        resetServer();
                        cancel();
                    }
                }
            }.runTaskTimer(this, 0L, 20L);
        } else {
            String materialName = diamondsAchievementGained ? msg("MATERIAL_DIAMOND") : msg("MATERIAL_IRON");
            
            // Canlı oyuncuya mesaj gönder
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!deadPlayers.containsKey(p.getUniqueId()) && revivesRemaining.getOrDefault(p.getUniqueId(), MAX_REVIVES) > 0) {
                    p.sendMessage(msg("SEPARATOR"));
                    p.sendMessage(msg("PLAYER_DIED").replace("{player}", deadPlayer.getName()));
                    p.sendMessage(msg("REVIVE_COMMAND").replace("{player}", deadPlayer.getName()));
                    p.sendMessage(msg("REVIVE_COST")
                        .replace("{cost}", String.valueOf(reviveCost))
                        .replace("{material}", materialName));
                    p.sendMessage(msg("LIVES_REMAINING").replace("{lives}", String.valueOf(livesLeft)));
                    p.sendMessage(msg("SEPARATOR"));
                }
            }
            
            deadPlayer.sendMessage(msg("SEPARATOR"));
            deadPlayer.sendMessage(msg("PLAYER_DIED").replace("{player}", ""));
            deadPlayer.sendMessage(msg("LIVES_REMAINING").replace("{lives}", String.valueOf(livesLeft)));
            deadPlayer.sendMessage(msg("REVIVED_SUBTITLE"));
            deadPlayer.sendMessage(msg("SEPARATOR"));
        }
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("lives")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cBu komutu sadece oyuncular kullanabilir!");
                return true;
            }
            
            Player player = (Player) sender;
            int lives = revivesRemaining.getOrDefault(player.getUniqueId(), MAX_REVIVES);
            String materialName = diamondsAchievementGained ? msg("MATERIAL_DIAMOND") : msg("MATERIAL_IRON");
            
            player.sendMessage(msg("SEPARATOR"));
            player.sendMessage(msg("CMD_LIVES_TITLE"));
            player.sendMessage(msg("YOUR_LIVES").replace("{lives}", String.valueOf(lives)));
            player.sendMessage(msg("CMD_LIVES_COST")
                .replace("{cost}", String.valueOf(reviveCost))
                .replace("{material}", materialName));
            player.sendMessage(msg("CMD_LIVES_NIGHTS").replace("{nights}", String.valueOf(nightsPassed)));
            
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!p.equals(player)) {
                    int otherLives = revivesRemaining.getOrDefault(p.getUniqueId(), MAX_REVIVES);
                    String status = deadPlayers.containsKey(p.getUniqueId()) ? 
                        msg("PLAYER_STATUS_DEAD") : msg("PLAYER_STATUS_ALIVE");
                    player.sendMessage("§7" + p.getName() + ": §e" + otherLives + "/" + MAX_REVIVES + " " + status);
                }
            }
            player.sendMessage(msg("SEPARATOR"));
            return true;
        }
        
        if (!(sender instanceof Player)) {
            sender.sendMessage(msg("CMD_ONLY_PLAYERS"));
            return true;
        }
        
        Player player = (Player) sender;
        
        // Kendisi ölü mü kontrol et
        if (deadPlayers.containsKey(player.getUniqueId())) {
            player.sendMessage(msg("REVIVE_FAILED_DEAD"));
            return true;
        }
        
        if (args.length == 0) {
            String materialName = diamondsAchievementGained ? msg("MATERIAL_DIAMOND") : msg("MATERIAL_IRON");
            player.sendMessage(msg("CMD_USAGE_REVIVE"));
            player.sendMessage(msg("CMD_LIVES_COST")
                .replace("{cost}", String.valueOf(reviveCost))
                .replace("{material}", materialName));
            
            // Ölü oyuncuları listele
            if (!deadPlayers.isEmpty()) {
                player.sendMessage(msg("CMD_DEAD_PLAYERS"));
                for (DeadPlayerData data : deadPlayers.values()) {
                    player.sendMessage("§e- " + data.playerName + " §7(" + 
                        msg("LIVES_REMAINING").replace("{lives}", String.valueOf(data.livesLeft)) + "§7)");
                }
            }
            return true;
        }
        
        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);
        
        if (target == null) {
            player.sendMessage(msg("CMD_PLAYER_NOT_FOUND").replace("{player}", targetName));
            return true;
        }
        
        // Hedef ölü mü kontrol et
        if (!deadPlayers.containsKey(target.getUniqueId())) {
            player.sendMessage(msg("REVIVE_FAILED_ALIVE").replace("{player}", target.getName()));
            return true;
        }
        
        // Hedefin canlanma hakkı var mı kontrol et
        int targetLives = revivesRemaining.getOrDefault(target.getUniqueId(), MAX_REVIVES);
        if (targetLives <= 0) {
            player.sendMessage(msg("REVIVE_FAILED_NO_RIGHTS").replace("{player}", target.getName()));
            return true;
        }
        
        // Gerekli materyali kontrol et
        Material requiredMaterial = diamondsAchievementGained ? Material.DIAMOND : Material.IRON_INGOT;
        String materialName = diamondsAchievementGained ? msg("MATERIAL_DIAMOND") : msg("MATERIAL_IRON");
        int materialCount = countItems(player, requiredMaterial);
        
        if (materialCount >= reviveCost) {
            // Materyali al
            removeItems(player, requiredMaterial, reviveCost);
            
            // Canlanma hakkını azalt
            revivesRemaining.put(target.getUniqueId(), targetLives - 1);
            saveConfigData();
            
            // Oyuncuyu canlandır
            revivePlayer(target);
            
            int newLives = targetLives - 1;
            Bukkit.broadcastMessage(msg("REVIVE_SUCCESS")
                .replace("{reviver}", player.getName())
                .replace("{target}", target.getName()));
            Bukkit.broadcastMessage(msg("LIVES_REMAINING")
                .replace("{lives}", String.valueOf(newLives))
                .replace("{player}", target.getName()));
            
            if (newLives == 0) {
                Bukkit.broadcastMessage(msg("LIVES_WARNING").replace("{player}", target.getName()));
            }
            
            player.sendMessage(msg("INV_USED")
                .replace("{cost}", String.valueOf(reviveCost))
                .replace("{material}", materialName));
        } else {
            player.sendMessage(msg("REVIVE_FAILED_ITEMS").replace("{material}", materialName));
            player.sendMessage(msg("INV_REQUIRED")
                .replace("{cost}", String.valueOf(reviveCost))
                .replace("{material}", materialName));
            player.sendMessage(msg("INV_CURRENT")
                .replace("{current}", String.valueOf(materialCount))
                .replace("{material}", materialName));
            player.sendMessage(msg("INV_MISSING")
                .replace("{missing}", String.valueOf(reviveCost - materialCount))
                .replace("{material}", materialName));
        }
        
        return true;
    }
    
    private void revivePlayer(Player player) {
        DeadPlayerData data = deadPlayers.get(player.getUniqueId());
        
        if (data != null) {
            // Oyuncuyu survival moduna al
            player.setGameMode(GameMode.SURVIVAL);
            
            // Son ölüm noktasına ışınla
            player.teleport(data.deathLocation);
            
            // Canı doldur
            player.setHealth(20.0);
            player.setFoodLevel(20);
            player.setSaturation(20.0f);
            
            // Ölüler listesinden çıkar
            deadPlayers.remove(player.getUniqueId());
            
            int livesLeft = revivesRemaining.getOrDefault(player.getUniqueId(), MAX_REVIVES);
            
            player.sendMessage(msg("SEPARATOR"));
            player.sendMessage(msg("REVIVED_TITLE"));
            player.sendMessage(msg("LIVES_REMAINING").replace("{lives}", String.valueOf(livesLeft)));
            if (livesLeft == 0) {
                player.sendMessage(msg("LAST_CHANCE"));
            } else {
                player.sendMessage(msg("REVIVED_SUBTITLE"));
            }
            player.sendMessage(msg("SEPARATOR"));
        }
    }
    
    private int countItems(Player player, Material material) {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == material) {
                count += item.getAmount();
            }
        }
        return count;
    }
    
    private void removeItems(Player player, Material material, int amount) {
        int remaining = amount;
        ItemStack[] contents = player.getInventory().getContents();
        
        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item != null && item.getType() == material) {
                int itemAmount = item.getAmount();
                if (itemAmount <= remaining) {
                    remaining -= itemAmount;
                    player.getInventory().setItem(i, null);
                } else {
                    item.setAmount(itemAmount - remaining);
                    remaining = 0;
                }
                
                if (remaining == 0) {
                    break;
                }
            }
        }
    }
    
    private void resetServer() {
        getLogger().info("==================================");
        getLogger().info(msg("SERVER_RESET"));
        getLogger().info("==================================");
        
        // Tüm oyuncuları at
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.kickPlayer(msg("SERVER_RESETTING"));
        }
        
        new BukkitRunnable() {
            @Override
            public void run() {
                // Config'i sıfırla
                reviveCost = 20;
                diamondsAchievementGained = false;
                nightsPassed = 0;
                lastNightCheck = 0;
                deadPlayers.clear();
                revivesRemaining.clear();
                
                // Config dosyasını sil
                File configFile = new File(getDataFolder(), "config.yml");
                if (configFile.exists()) {
                    configFile.delete();
                }
                
                getLogger().info(msg("CONFIG_RESET"));
                
                // Tüm dünyaları kaydet ve kaldır
                for (World world : Bukkit.getWorlds()) {
                    world.save();
                    getLogger().info("Dünya kaydedildi: " + world.getName());
                    Bukkit.unloadWorld(world, false);
                }
                
                // Dünya klasörlerini sil
                String worldName = "world";
                File worldFolder = new File(Bukkit.getWorldContainer(), worldName);
                File netherFolder = new File(Bukkit.getWorldContainer(), worldName + "_nether");
                File endFolder = new File(Bukkit.getWorldContainer(), worldName + "_the_end");
                
                getLogger().info("Dünyalar siliniyor...");
                deleteWorld(worldFolder);
                deleteWorld(netherFolder);
                deleteWorld(endFolder);
                getLogger().info(msg("WORLDS_DELETED"));
                
                getLogger().info("==================================");
                getLogger().info(msg("SERVER_SHUTDOWN"));
                getLogger().info(msg("SERVER_RESTART_INFO"));
                getLogger().info("==================================");
                
                // Sunucuyu kapat
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Bukkit.shutdown();
                    }
                }.runTaskLater(PxlochsRevival.this, 20L);
            }
        }.runTaskLater(this, 20L);
    }
    
    private boolean deleteWorld(File path) {
        if (!path.exists()) {
            return true;
        }
        
        boolean success = true;
        File[] files = path.listFiles();
        
        if (files != null) {
            for (File file : files) {
                // session.lock dosyasını atla
                if (file.getName().equals("session.lock")) {
                    continue;
                }
                
                if (file.isDirectory()) {
                    // Alt klasörleri recursive sil
                    if (!deleteWorld(file)) {
                        success = false;
                    }
                } else {
                    // Dosyayı sil
                    if (!file.delete()) {
                        getLogger().warning("Silinemedi: " + file.getAbsolutePath());
                        success = false;
                    }
                }
            }
        }
        
        // Klasörü sil
        if (!path.delete() && path.exists()) {
            getLogger().warning("Klasör silinemedi: " + path.getAbsolutePath());
            success = false;
        }
        
        return success;
    }
    
    // Inner class
    private class DeadPlayerData {
        org.bukkit.Location deathLocation;
        String playerName;
        int livesLeft;
        
        DeadPlayerData(org.bukkit.Location loc, String name, int lives) {
            this.deathLocation = loc;
            this.playerName = name;
            this.livesLeft = lives;
        }
    }
}