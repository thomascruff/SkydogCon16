/**
 * 
 */
package com.skydogcon.jbs;

import java.sql.*;

/**
 * @author TheCelticTyger
 *
 */
public final class ScoresTable extends DatabaseTableImpl{
	//Public Class Constants
	public static final String TABLE_NAME = "SCORES";
	public static final String COL_ID = "ID";
	public static final String COL_IP = "IP";
	public static final String COL_SCORE = "SCORE";
	public static final String COL_COUNTRY = "COUNTRY_CODE";
	public static final String COL_REGION = "REGION_CODE";
	public static final String COL_CITY = "CITY";
	public static final String COL_LAT = "LATITUDE";
	public static final String COL_LONG = "LONGITUDE";
	
	//Private Class Constants
	private static final String CREATE_SQL = "CREATE TABLE SCORES ("
			+ ScoresTable.COL_ID + " VARCHAR(10) NOT NULL, "
			+ ScoresTable.COL_IP + " VARCHAR(15) NOT NULL, "
			+ ScoresTable.COL_SCORE + " INT NOT NULL, "
			+ ScoresTable.COL_COUNTRY + " VARCHAR(2), "
			+ ScoresTable.COL_REGION + " VARCHAR(2), "
			+ ScoresTable.COL_CITY + " VARCHAR(40), "
			+ ScoresTable.COL_LAT + " FLOAT, "
			+ ScoresTable.COL_LONG + " FLOAT, "
			+ "PRIMARY KEY(ID,IP))";
	
	//Instance Variables
	private PreparedStatement psInsert = null;
	
	public ScoresTable(){
		super();
		
		//Prepare the insert statement for future use.
		try{			
			String pStatement = "insert into " + this.getTableName() + "("
					+ ScoresTable.COL_ID + ", "
					+ ScoresTable.COL_IP + ", "
					+ ScoresTable.COL_SCORE + ", "
					+ ScoresTable.COL_COUNTRY + ", "
					+ ScoresTable.COL_REGION + ", "
					+ ScoresTable.COL_CITY + ", "
					+ ScoresTable.COL_LAT + ", "
					+ ScoresTable.COL_LONG
					+ ") values (?,?,?,?,?,?,?,?)";
			this.psInsert = this.getConnection().prepareStatement(pStatement);
		}catch(SQLException sqle){
			ErrorHandler.errorPrint(sqle);
		}
	}
	
	public String getTableName(){
		return ScoresTable.TABLE_NAME;
	}
	
	public String getCreateSQL(){
		return ScoresTable.CREATE_SQL;
	}
	
	public boolean tableExists() throws SQLException{
		Statement s = null;
		boolean retVal;

		try{
	    	s = this.getConnection().createStatement();
	    	s.execute("SELECT * FROM " + this.getTableName());
	    	System.out.println(this.getTableName() + " table exists. Doing nothing.");
	    	s.close();
	    	retVal = true;
	    }catch (SQLException sqle) {
	    	String theError = (sqle).getSQLState();
	    	
	    	if (theError.equals("42X05")){   // Table does not exist
	    		System.out.println(this.getTableName() + " table does not exist.");
	    		retVal = false;
	    	}else if (theError.equals("42X14") || theError.equals("42821")) {
	    		System.err.println("Incorrect table definition.");
	    		throw sqle;
	    	}else{ 
	    		System.err.println("Unhandled SQLException" );
	    		throw sqle; 
	    	}
	    }
		
		return retVal;
	}
	
	public void insert (Object item){
		Score score = (Score) item;
		try{
			this.psInsert.setString(1, score.getId());
			this.psInsert.setString(2, score.getIp());
			this.psInsert.setInt(3, score.getScore());
			this.psInsert.setString(4, score.getCountry());
			this.psInsert.setString(5, score.getRegion());
			this.psInsert.setString(6, score.getCity());
			this.psInsert.setFloat(7, score.getLatitude());
			this.psInsert.setFloat(8, score.getLongitude());
			this.psInsert.executeUpdate();
		}catch (SQLException sqle){
			if(sqle.getSQLState().equals("23505")){
				System.out.println("The entry with ID=" + score.getId() + " and IP=" + score.getIp() + " could not be created because it already exists.");
			}else{
				ErrorHandler.errorPrint(sqle);
			}
		}
	}
	
	public void close(){
		try{
			this.psInsert.close();
			this.getConnection().close();
		}catch (SQLException sqle){
			ErrorHandler.errorPrint(sqle);
		}
	}
}