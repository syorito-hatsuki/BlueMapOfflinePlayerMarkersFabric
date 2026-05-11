package com.technicjelle.bluemapofflineplayermarkers.common;

/// You can implement this logger for your platform.
///
/// But if your platform offers a [java.util.logging.Logger], consider using [JavaUtilLogger].
///
/// You can also use [BMNLogger], which will "hijack" BlueMap's own logging system, through [com.technicjelle.BMUtils.BMNative.BMNLogger].<br>
/// This has the added benefit of logging the fine details into BlueMap's `debug.log` file, so they don't disappear into the ether.
public interface Logger {
	void error(String message);
	void error(String message, Throwable throwable);
	void warning(String message);
	void info(String message);
	void debug(String message);
	void trace(String message);
}