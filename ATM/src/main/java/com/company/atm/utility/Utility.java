package com.company.atm.utility;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Utility {

	final private static String CONFIG_FILE_PATH = "C:\\Ganesan\\installations\\sts-4.11.0.RELEASE\\workspace\\ATM\\src\\main\\resources\\config.properties";

	public static Properties getResources() {
		Properties prop = new Properties();
		try (InputStream input = new FileInputStream(CONFIG_FILE_PATH)) {
			prop.load(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return prop;
	}

}
