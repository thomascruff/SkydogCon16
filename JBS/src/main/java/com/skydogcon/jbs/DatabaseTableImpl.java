/**
 * 
 */
package com.skydogcon.jbs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author TheCelticTyger
 *
 */
public abstract class DatabaseTableImpl implements DatabaseTable{
	// Instance Variables
	private Connection conn = null;

	public DatabaseTableImpl(){
		try{
			this.conn = DriverManager.getConnection(ScoringDatabase.DATABASE_URI);
			System.out.println("Connection to database established.");
		}catch(SQLException sqle){
			System.out.println("Connection to database could not be established.");
			ErrorHandler.errorPrint(sqle);
		}
	}
	
	public void createTable(){
		try{
			if(!this.tableExists()){
				Statement s = this.getConnection().createStatement();
	    		System.out.println(this.getTableName() + " table does not exist. Creating table.");
	    		s.execute(this.getCreateSQL());
	    		System.out.println(this.getTableName() + " table created.");
	    		s.close();
			}else{
		    	System.out.println(this.getTableName() + " table exists. Doing nothing.");
			}
		}catch(SQLException sqle){
			ErrorHandler.errorPrint(sqle);
		}
	}
	
	protected Connection getConnection(){
		return this.conn;
	}
}
