package com.rickauer.marketmonarch.utils;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class FileSupplier {
	
	private static Logger fileSupplierLogger = LogManager.getLogger(FileSupplier.class.getName());

	private FileSupplier() {
		throw new UnsupportedOperationException();
	}
	
	public static String getWorkingDirectory() {
		return System.getProperty("user.dir");
	}

	public static String getTemporaryFolder() {
		return getWorkingDirectory() + "/temp";
	}

	public static String getBackupFolder() {
		return getWorkingDirectory() + "/backup";
	}
	
	public static void createFolder(String folder) {

		fileSupplierLogger.info("Creating folder: '" + folder + "'.");

		File newFolder = new File(folder);
		
		if (!newFolder.exists()) {
			newFolder.mkdir();
			fileSupplierLogger.info("Created '" + newFolder.getAbsolutePath() + "'.");
		} else {
			fileSupplierLogger.info("Temporary folder: '" + newFolder.getAbsolutePath() + "' already exists.");
		}
	}

	public static void deleteFolder(String folder) {
		deleteFolderRecursively(new File(folder));
	}

	private static void deleteFolderRecursively(File file) {

		fileSupplierLogger.info("Processing: '" + file + "'.");
		
		if (file.isDirectory()) {
			File[] directoryContent = file.listFiles();
			for (File actualFile : directoryContent) {
				deleteFolderRecursively(actualFile);
			}
		} 
		file.delete();
		fileSupplierLogger.info("Deleted: '" + file + "'.");
	}
}
