/**
 * 
 */
package com.skydogcon.jbs;

import java.sql.SQLException;

/**
 * @author TheCelticTyger
 *
 */
public final class ErrorHandler {
	protected static void errorPrint(Throwable e) {
		if (e instanceof SQLException) {
			SQLExceptionPrint((SQLException)e);
		}else {
			System.err.println("A non SQL error occured.");
			e.printStackTrace();
		}
	}
	
	private static void SQLExceptionPrint(SQLException sqle) {
		while (sqle != null) {
			System.err.println("\n---SQLException Caught---\n");
			System.err.println("SQLState:   " + (sqle).getSQLState());
			System.err.println("Severity: " + (sqle).getErrorCode());
			System.err.println("Message:  " + (sqle).getMessage()); 
			sqle.printStackTrace();  
			sqle = sqle.getNextException();
		}
	}
}
