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
	//Class constants
	private static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
	private static final String DATABASE_URI = "jdbc:derby:ScoringDB;create=true";
	private static final String SCORES_TABLE_SQL = "CREATE TABLE SCORES "
			+ "(ID VARCHAR(40) NOT NULL, "
			+ "IP VARCHAR(15) NOT NULL, "
			+ "SCORE INT NOT NULL, "
			+ "GEO_LOCATION VARCHAR(40), "
			+ "PRIMARY KEY(ID,IP))";

	// Instance variables
	private Connection conn = null;
	private PreparedStatement psInsert = null;
	
	public ScoringDatabase(){
		try{
			this.conn = DriverManager.getConnection(ScoringDatabase.DATABASE_URI);
			System.out.println("Connection to Scoring Database established.");
			this.psInsert = this.conn.prepareStatement("insert into SCORES(ID,IP,SCORE,GEO_LOCATION) values (?,?,?,?)");
		}catch(SQLException sqle){
			System.out.println("Connection could not be established.");
			ScoringDatabase.errorPrint(sqle);
		}
	}
	
	public void insert(String id, String ip, int score){
		try{
			this.psInsert.setString(1, id);
			this.psInsert.setString(2, ip);
			this.psInsert.setInt(3, score);
			this.psInsert.setString(4, null);
			this.psInsert.executeUpdate();
		}catch (SQLException sqle){
			if(sqle.getSQLState().equals("23505")){
				System.out.println("The entry with ID=" + id + " and IP=" + ip + " could not be created because it already exists.");
			}else{
				ScoringDatabase.errorPrint(sqle);
			}
		}
	}
	
	protected void finalize() throws Throwable {
		try {
			System.out.println("Closing instance database connection.");
			this.psInsert.close();
			this.conn.close();
			System.out.println("Instance database connection closed successfully.");
	     } catch (SQLException sqle){
	    	 System.err.println("Instance database connection could not be closed!");
	     }finally {
	         super.finalize();
	     }
	 }
	
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
			Connection conn = null;
			Statement s = null;
			
			conn = DriverManager.getConnection(ScoringDatabase.DATABASE_URI);		 
		    System.out.println("Connected to Scoring database.");
		    
		    try{
		    	s = conn.createStatement();
		    	s.execute("SELECT * FROM SCORES");
		    	System.out.println("SCORES table exists. Doing nothing.");
		    }catch (SQLException sqle) {
		    	String theError = (sqle).getSQLState();
		    	
		    	if (theError.equals("42X05")){   // Table does not exist
		    		System.out.println("SCORES table does not exist. Creating SCORES table.");
		    		s.execute(ScoringDatabase.SCORES_TABLE_SQL);
		    		System.out.println("SCORES table created.");
		    	}else if (theError.equals("42X14") || theError.equals("42821")) {
		    		System.err.println("Incorrect table definition.");
		    		throw sqle;
		    	}else{ 
		    		System.err.println("Unhandled SQLException" );
		    		throw sqle; 
		    	}
		    }
		    
		    System.out.println("Closing connection.");
		    s.close();
		    conn.close();
		    System.out.println("Connection closed.");
		    
		}catch(Throwable e){
			ScoringDatabase.errorPrint(e);
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
            	ScoringDatabase.errorPrint(sqle);            	
            }
         }
	}
	
	private static void errorPrint(Throwable e) {
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