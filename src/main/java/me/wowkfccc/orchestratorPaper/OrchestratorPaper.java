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
        // 讀取設定
        saveDefaultConfig();
        C = new PaperConfig(this);
        getLogger().info("[MLP-Paper] enabled. serverId=" + C.serverId);

        // 準備 adapters
        Ports.ServerInventory inv = new PaperInventory(C);
        Ports.PlayerOps pops = new PaperPlayerOps(C);
        Ports.MetricsRepo repo = new MySQLRepo(C);     // 先用 stub；之後換 JDBC 版
        Ports.ScalerOps scaler = new LocalScaler(C);   // Paper 不真的開關實例

        // Core 組件
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

        // 每 5 分鐘跑一次（首次延遲 1 分鐘）
        long firstDelayTicks = 20L * 60;
        long periodTicks = 20L * 60 * 5;
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            try { engine.runTick(); } catch (Throwable t) { t.printStackTrace(); }
        }, firstDelayTicks, periodTicks);
    }
}


