package ja.lingo.engine.monitor;

import ja.lingo.application.util.progress.IMonitor;

public interface ISearchMonitor extends IMonitor {
    boolean isCanceled();
}
