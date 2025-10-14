package com.kjrepo.infra.perf;

import java.util.Map;

public interface PerfHandler {

	void handle(Map<PerfLogTag, PerfLogMetrics> perfs);
}
