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
		deleteTemporaryFolderRecursively(new File(printTemporaryFolder()));
	}

	private static void deleteTemporaryFolderRecursively(File file) {

		fileSupplierLogger.info("Processing: '" + file + "'.");
		
		if (file.isDirectory()) {
			File[] directoryContent = file.listFiles();
			for (File actualFile : directoryContent) {
				deleteTemporaryFolderRecursively(actualFile);
			}
		} 
		file.delete();
		fileSupplierLogger.info("Deleted: '" + file + "'.");
	}
}
