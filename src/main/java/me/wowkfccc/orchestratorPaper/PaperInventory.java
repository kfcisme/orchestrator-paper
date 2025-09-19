package me.wowkfccc.orchestratorPaper;

import me.wowkfccc.Ports;

import java.util.List;

public class PaperInventory implements Ports.ServerInventory {
    private final PaperConfig C;
    public PaperInventory(PaperConfig C){ this.C = C; }

    @Override public List<String> listServers() { return List.of(C.serverId); }
    @Override public double capacityCpu(String serverId) { return C.effectiveCpu(); }
    @Override public double capacityRamMb(String serverId) { return C.effectiveRamMb(); }
}

