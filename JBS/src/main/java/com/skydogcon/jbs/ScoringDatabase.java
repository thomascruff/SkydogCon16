/**
 * 
 */
package com.skydogcon.jbs;

import java.sql.*;

/**
 * @author TheCelticTyger
 *
 */
public final class ScoringDatabase {	
	//Private Class Constants
	protected static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
	protected static final String DATABASE_URI = "jdbc:derby:ScoringDB;create=true";

	public static final void initialize(){
		/*
		 * Attempt to load database driver.
		 * With the embedded Derby driver a successful load starts the DB engine.
		 */
		try{
			Class.forName(ScoringDatabase.DRIVER);
			System.out.println("Derby database embedded driver loaded.");
		}catch(ClassNotFoundException e){
		     System.err.print("ClassNotFoundException: ");
		     System.err.println(e.getMessage());
		}
		
		/*
		 * Check for the existence of the SCORES table.
		 * Create SCORES table if this is first boot.
		 */
		try{
			ScoresTable st = new ScoresTable();
			st.createTable();
		}catch(Throwable e){
			ErrorHandler.errorPrint(e);
		}
	}
	
	public static final void shutdown(){
		try {
            DriverManager.getConnection("jdbc:derby:;shutdown=true");
         } catch (SQLException sqle)  {	
            if (sqle.getSQLState().equals("XJ015")) {
            	System.out.println("Database shut down normally");
            }else{
            	System.err.println("Database did not shut down normally");
            	ErrorHandler.errorPrint(sqle);            	
            }
         }
	}
}