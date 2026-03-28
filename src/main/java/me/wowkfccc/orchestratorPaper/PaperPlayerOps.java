package me.wowkfccc.orchestratorPaper;

import me.wowkfccc.Ports;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.stream.Collectors;

public class PaperPlayerOps implements Ports.PlayerOps {
    private final PaperConfig C;
    public PaperPlayerOps(PaperConfig C){ this.C=C; }

    @Override
    public List<Player> listOnlinePlayers() {
        return Bukkit.getOnlinePlayers().stream()
                .map(p -> new Player(p.getUniqueId().toString(), p.getName(), C.serverId, "AFK"))
                .collect(Collectors.toList());
    }

    @Override
    public void movePlayer(String playerId, String targetServerId) {
        if (C.logPlanOnly) {
            Bukkit.getLogger().info("[MLP-Paper] plan move "+playerId+" -> "+targetServerId+" (log only)");
        }
    }
}

