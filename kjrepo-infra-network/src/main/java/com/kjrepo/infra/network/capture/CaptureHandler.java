package com.kjrepo.infra.network.capture;

import org.openqa.selenium.chrome.ChromeDriver;

public interface CaptureHandler {

	void handle(ChromeDriver driver, Object arg);

	boolean isDone();

}
