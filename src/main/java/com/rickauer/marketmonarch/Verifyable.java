package com.rickauer.marketmonarch;

public interface Verifyable {
	
	; // Observable, weil Health Checker muss alle kennen und die anderen müssen sich dort registrieren
	String justSomeMethodToThrowWarnigAsAReminderToReadTheCommentAbove();
	boolean runHealthCheck();
	boolean isCoreType();
}
