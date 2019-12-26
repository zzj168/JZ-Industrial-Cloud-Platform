package cn.jbolt.common.gen;

import java.util.Date;

import cn.jbolt.common.util.DateUtil;

public class ConsoleUtil {
	public static void printJboltcn(){
		System.out.println("   _ _           _ _               \r\n" + 
			"  (_) |__   ___ | | |_   ___ _ __  \r\n" + 
			"  | | '_ \\ / _ \\| | __| / __| '_ \\ \r\n" + 
			"  | | |_) | (_) | | |_ _ (__| | | |\r\n" + 
			" _/ |_.__/ \\___/|_|\\__(_)___|_| |_|\r\n" + 
			"|__/                               ");
	}
	public static void printMessageWithDate(String message) {
		System.out.println("[JBolt Gen]:["+DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss SSS")+"]"+message);
	}
	public static void printErrorMessageWithDate(String message) {
		System.err.println("[JBolt Gen]:["+DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss SSS")+"]"+message);
	}
	public static void printMessage(String message) {
		System.out.println("[JBolt Gen]:"+message);
	}
}
