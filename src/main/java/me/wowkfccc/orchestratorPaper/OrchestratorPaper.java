package me.wowkfccc.orchestratorPaper;

import me.wowkfccc.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.TimeUnit;

public class OrchestratorPaper extends JavaPlugin {
    private OrchestratorEngine engine;
    private PaperConfig C;

    @Override
    public void onEnable() {
        // load setting
        saveDefaultConfig();
        C = new PaperConfig(this);
        getLogger().info("[MLP-Paper] enabled. serverId=" + C.serverId);

        // adapters
        Ports.ServerInventory inv = new PaperInventory(C);
        Ports.PlayerOps pops = new PaperPlayerOps(C);
        Ports.MetricsRepo repo = new MySQLRepo(C);
        Ports.ScalerOps scaler = new LocalScaler(C);

        // Core
        LstmClient lstm = new LstmClient(C.lstmBase, C.lstmTimeout);

        Regressor.Coef coef = new Regressor.Coef();
        coef.intercept = C.intercept; coef.yLag1 = C.yLag1; coef.yLag2 = C.yLag2; coef.beta = C.beta;
        Regressor reg = new Regressor(coef);

        Planner planner = new Planner(C.unitCostCpu, C.unitCostRamMb);
        planner.migratePenalty = C.migratePenalty;
        planner.allowOver = C.allowOver;
        planner.wCpu = C.scoreCpuW;
        planner.wRam = C.scoreRamW;

        Autoscaler autoscaler = new Autoscaler(C.cpuSteps, C.cooldownMin);
        autoscaler.setHysteresis(C.upH, C.downH);

        engine = new OrchestratorEngine(inv, pops, repo, scaler, lstm, reg, planner, autoscaler);

        long firstDelayTicks = 20L * 60;
        long periodTicks = 20L * 60 * 5;
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            try { engine.runTick(); } catch (Throwable t) { t.printStackTrace(); }
        }, firstDelayTicks, periodTicks);
    }
}


