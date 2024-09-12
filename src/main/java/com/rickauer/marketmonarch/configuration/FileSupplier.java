package com.rickauer.marketmonarch.configuration;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileSupplier {

	private static Logger fileSupplierLogger = LogManager.getLogger(FileSupplier.class.getName());

	public static String printWorkingDirectory() {
		return System.getProperty("user.dir");
	}
	
	public static String printTemporaryFolder() {
		return printWorkingDirectory() + "/temp";
	}

	public static void createTemporaryFolder() {

		fileSupplierLogger.info("Creating temporary folder.");

		File temp = new File(printTemporaryFolder());

		if (!temp.exists()) {
			temp.mkdir();
			fileSupplierLogger.info("Created '" + temp.getAbsolutePath() + "'.");
		} else {
			fileSupplierLogger.info("Temporary folder: '" + temp.getAbsolutePath() + "' already exists.");
		}
	}

	public static void deleteTemporaryFolder() {
		; // auch Inhalt des Ordners löschen, falls vorhanden.
		fileSupplierLogger.info("Deleting temporary folder.");

		File temp = new File(printTemporaryFolder());

		if (!temp.exists()) {
			fileSupplierLogger.info("Temporary folder: '" + temp.getAbsolutePath() + "' does not exist.");
		} else {
			temp.delete();
			fileSupplierLogger.info("Deleted '" + temp.getAbsolutePath() + "'.");
		}
	}
}
