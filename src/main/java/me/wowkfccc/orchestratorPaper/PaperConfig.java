package me.wowkfccc.orchestratorPaper;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaperConfig {
    // server
    public final String serverId;
    public final double capacityCpu, headroom;
    public final double capacityRamMb, headroomRam;

    // lstm
    public final boolean lstmEnabled;
    public final String lstmBase;
    public final int lstmTimeout;

    // regression
    public final double intercept, yLag1, yLag2;
    public final Map<String, Double> beta;

    // unit cost
    public final Map<String, Double> unitCostCpu, unitCostRamMb;

    // autoscale
    public final List<Integer> cpuSteps;
    public final int cooldownMin, upH, downH;

    // placement
    public final double migratePenalty, allowOver, scoreCpuW, scoreRamW;

    // mysql
    public final String jdbcUrl, jdbcUser, jdbcPass;
    public final int poolSize, horizon;
    public final String tblComp, tblLoad, tblPred;

    // paper misc
    public final boolean moveViaProxy, logPlanOnly;

    public PaperConfig(JavaPlugin plugin) {
        plugin.saveDefaultConfig();
        FileConfiguration c = plugin.getConfig();

        serverId = c.getString("server.id", "self");
        capacityCpu = c.getDouble("server.capacity_cpu", 100.0);
        headroom    = c.getDouble("server.headroom", 0.80);
        capacityRamMb = c.getDouble("server.capacity_ram_mb", 8192.0);
        headroomRam   = c.getDouble("server.headroom_ram", 0.80);

        lstmEnabled = c.getBoolean("lstm.enabled", true);
        lstmBase    = c.getString("lstm.base_url", "http://127.0.0.1:8900");
        lstmTimeout = c.getInt("lstm.timeout_ms", 1500);

        intercept = c.getDouble("regression.intercept", 5.0);
        yLag1 = c.getDouble("regression.ar.y_lag1", 0.20);
        yLag2 = c.getDouble("regression.ar.y_lag2", 0.10);

        beta = new HashMap<>();
        for (String k : keys()) beta.put(k, c.getDouble("regression.beta."+k, 0.0));

        unitCostCpu = new HashMap<>();
        unitCostRamMb = new HashMap<>();
        for (String k : keys()) {
            unitCostCpu.put(k, c.getDouble("unit_cost_cpu."+k, 0.5));
            unitCostRamMb.put(k, c.getDouble("unit_cost_ram_mb."+k, 16.0));
        }

        cpuSteps = c.getIntegerList("autoscale.cpu_steps");
        cooldownMin = c.getInt("autoscale.cooldown_minutes", 20);
        upH = c.getInt("autoscale.hysteresis.up", 2);
        downH = c.getInt("autoscale.hysteresis.down", 3);

        migratePenalty = c.getDouble("placement.migrate_penalty", 0.5);
        allowOver = c.getDouble("placement.allow_over_ratio", 0.05);
        scoreCpuW = c.getDouble("placement.score.cpu_weight", 0.6);
        scoreRamW = c.getDouble("placement.score.ram_weight", 0.4);

        jdbcUrl  = c.getString("mysql.url");
        jdbcUser = c.getString("mysql.username");
        jdbcPass = c.getString("mysql.password");
        poolSize = c.getInt("mysql.pool_size", 6);

        tblComp = c.getString("tables.comp", "server_comp_30m");
        tblLoad = c.getString("tables.load", "server_load_30m");
        tblPred = c.getString("tables.pred", "player_type_pred");
        horizon = c.getInt("tables.horizon", 6);

        moveViaProxy = c.getBoolean("paper.move_via_proxy", false);
        logPlanOnly = c.getBoolean("paper.log_plan_only", true);
    }

    public double effectiveCpu(){ return capacityCpu * headroom; }
    public double effectiveRamMb(){ return capacityRamMb * headroomRam; }

    private static String[] keys(){ return new String[]{"AFK","Build","Explorer","Explosive","PvP","Redstone","Social","Survival"}; }
}

