package com.kjrepo.infra.monitor.oshi;

import com.kjrepo.infra.monitor.IMonitor;

import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;

public class OshiMonitor implements IMonitor {

	private final SystemInfo systemInfo = new SystemInfo();

	@Override
	public void monitor() {
		OperatingSystem os = systemInfo.getOperatingSystem();
		os.getCurrentProcess();
		os.getCurrentThread();
		os.getFileSystem();

		HardwareAbstractionLayer hardware = systemInfo.getHardware();
		hardware.getComputerSystem();
		hardware.getProcessor();

	}

}
