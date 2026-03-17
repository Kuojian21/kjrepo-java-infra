package com.kjrepo.infra.perf.handler;

import java.util.List;

import com.kjrepo.infra.common.spi.PkgSpi;
import com.kjrepo.infra.perf.model.PerfLogHolder;

public interface IPerfHandler extends PkgSpi {

	void handle(List<PerfLogHolder> perfs);

}
