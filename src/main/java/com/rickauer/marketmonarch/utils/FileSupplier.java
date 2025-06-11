package com.rickauer.marketmonarch.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class FileSupplier {
	
	private static Logger _fileSupplierLogger = LogManager.getLogger(FileSupplier.class.getName());

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
	
	public static String getSessionFolder() {
		return getWorkingDirectory() + "/sessions";
	}
	
	public static void createFolder(String folder) {

		_fileSupplierLogger.info("Creating folder: '" + folder + "'.");

		File newFolder = new File(folder);
		
		if (!newFolder.exists()) {
			newFolder.mkdir();
			_fileSupplierLogger.info("Created '" + newFolder.getAbsolutePath() + "'.");
		} else {
			_fileSupplierLogger.info("Temporary folder: '" + newFolder.getAbsolutePath() + "' already exists.");
		}
	}

	public static void deleteFolder(String folder) {
		deleteFolderRecursively(new File(folder));
	}

	private static void deleteFolderRecursively(File file) {

		_fileSupplierLogger.info("Processing: '" + file + "'.");
		
		if (file.isDirectory()) {
			File[] directoryContent = file.listFiles();
			for (File actualFile : directoryContent) {
				deleteFolderRecursively(actualFile);
			}
		} 
		file.delete();
		_fileSupplierLogger.info("Deleted: '" + file + "'.");
	}
	
	public static String readFile(String path) {
		
		File file = new File(path);
		String content = "";
		
		if (!file.exists()) {
			_fileSupplierLogger.error("File '" + path + "' does not exist.");
			throw new IllegalArgumentException("File does not exist.");
		}
		
		try {
			content = Files.readString(Path.of(path));
		} catch (IOException e) {
			_fileSupplierLogger.error(e);
		}
		return content;
	}
	
	public static void writeFile(String fileName, String content) {
		
		try {
			Files.writeString(Path.of(fileName), content, StandardOpenOption.CREATE_NEW);
		} catch (IOException e) {
			_fileSupplierLogger.error(e);
			throw new RuntimeException("Could not write to file.");
		}
		
	}
}
