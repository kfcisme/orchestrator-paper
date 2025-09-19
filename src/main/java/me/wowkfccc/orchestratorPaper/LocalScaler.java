package me.wowkfccc.orchestratorPaper;

import me.wowkfccc.Ports;

public class LocalScaler implements Ports.ScalerOps {
    private final PaperConfig C;
    private int running = 1;

    public LocalScaler(PaperConfig C){ this.C = C; }

    @Override public int runningCount() { return running; }

    @Override
    public void requestScaleTo(int n) {
        running = n;
        if (C.logPlanOnly) {
            System.out.println("[MLP-Paper] scale request -> " + n + " (log only)");
        }
        // 之後可改成呼叫外部 REST / systemd / docker / k8s
    }
}

