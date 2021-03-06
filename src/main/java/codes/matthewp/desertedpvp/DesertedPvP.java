package codes.matthewp.desertedpvp;

import codes.matthewp.desertedcore.DesertedCore;
import codes.matthewp.desertedcore.database.Database;
import codes.matthewp.desertedpvp.cmd.admin.AddKSCmd;
import codes.matthewp.desertedpvp.cmd.admin.GiveCoinsCmd;
import codes.matthewp.desertedpvp.cmd.admin.ResetKSCmd;
import codes.matthewp.desertedpvp.cmd.player.BalanceCmd;
import codes.matthewp.desertedpvp.cmd.player.PayCmd;
import codes.matthewp.desertedpvp.cmd.player.RankupCmd;
import codes.matthewp.desertedpvp.cmd.spawn.SetSpawnCmd;
import codes.matthewp.desertedpvp.cmd.spawn.SpawnCmd;
import codes.matthewp.desertedpvp.cmd.team.TeamCmd;
import codes.matthewp.desertedpvp.data.BlockTracker;
import codes.matthewp.desertedpvp.data.CoinsDataAccess;
import codes.matthewp.desertedpvp.data.TeamsDataAccess;
import codes.matthewp.desertedpvp.event.entity.EntityDeath;
import codes.matthewp.desertedpvp.event.entity.EntitySpawn;
import codes.matthewp.desertedpvp.event.interact.InteractEvent;
import codes.matthewp.desertedpvp.event.interact.InventoryClickEvent;
import codes.matthewp.desertedpvp.event.player.*;
import codes.matthewp.desertedpvp.event.world.DropEvent;
import codes.matthewp.desertedpvp.event.world.TransEntityEvent;
import codes.matthewp.desertedpvp.file.FileUtil;
import codes.matthewp.desertedpvp.kit.KitManager;
import codes.matthewp.desertedpvp.placeholder.PvPPlaceholders;
import codes.matthewp.desertedpvp.rankup.RankManager;
import codes.matthewp.desertedpvp.teams.TeamManager;
import codes.matthewp.desertedpvp.user.UserManager;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main entry point
 * Hello World!
 */
public class DesertedPvP extends JavaPlugin {

    private FileUtil fileUtil;
    private UserManager user;
    private static DesertedPvP instance;
    private DesertedCore core;
    private RankManager rankManager;
    private TeamManager teamsManager;

    private CoinsDataAccess coinsDataAccess;
    private TeamsDataAccess teamsDataAcess;

    private Permission perms = null;

    @Override
    public void onEnable() {
        instance = this;
        fileUtil = new FileUtil(this);
        user = new UserManager();
        user.scanForUsers();

        if (!setupPermissions()) {
            System.out.println(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        rankManager = new RankManager(fileUtil.getRanks(), this);

        regCommands();


        regListeners(
                new JoinEvent(this),
                new LeaveEvent(this),
                new InteractEvent(this),
                new HungerEvent(),
                new RespawnEvent(this),
                new DeathEvent(this),
                new HurtEvent(this),
                new InventoryClickEvent(this),
                new DropEvent(this),
                new TransEntityEvent(),
                new EntityDeath(),
                new EntitySpawn(),
                new DamageEvent(),
                new InventoryClose(this),
                new KitEvent());
        core = DesertedCore.getCore();
        coinsDataAccess = new CoinsDataAccess(getDB(), this);
        teamsDataAcess = new TeamsDataAccess(getDB(), this);
        teamsManager = new TeamManager(this);

        //Start the Runnable that keep track of kits cooldowns
        KitManager.runCooldown();

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            System.out.println("Found placeholder API. Will attempt to hook.");
            new PvPPlaceholders().register();
        }
    }

    @Override
    public void onDisable() {
        user.saveUserCoins();

        //Save all Teams and TeamMembers information to the database
        teamsManager.saveTeams();
        teamsManager.saveTeamMembers();

        BlockTracker.removeBlocks();
        core.getDB().disconnect();
        instance = null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    public FileUtil getFileUtil() {
        return fileUtil;
    }

    private void regCommands() {
        getCommand("spawn").setExecutor(new SpawnCmd());
        getCommand("setspawn").setExecutor(new SetSpawnCmd(this));
        getCommand("addks").setExecutor(new AddKSCmd(this));
        getCommand("resetks").setExecutor(new ResetKSCmd(this));
        getCommand("balance").setExecutor(new BalanceCmd(this));
        getCommand("addcoins").setExecutor(new GiveCoinsCmd(this));
        getCommand("pay").setExecutor(new PayCmd(this));
        getCommand("rankup").setExecutor(new RankupCmd(this));
        getCommand("team").setExecutor(new TeamCmd(this));
    }

    private void regListeners(Listener... listeners) {
        PluginManager plMan = Bukkit.getPluginManager();

        for (Listener listener : listeners) {
            plMan.registerEvents(listener, this);
        }
    }

    public Database getDB() {
        return core.getDB();
    }

    public CoinsDataAccess getCoinDataAccessor() {
        return coinsDataAccess;
    }

    public static DesertedPvP getInstace() {
        return instance;
    }

    public UserManager getUserManager() {
        return user;
    }

    public Permission getPerms() {
        return perms;
    }

    public RankManager getRankManager() {
        return rankManager;
    }

    public TeamsDataAccess getTeamsDataAcess() { return teamsDataAcess; }

    public TeamManager getTeamManager() { return teamsManager; }
}