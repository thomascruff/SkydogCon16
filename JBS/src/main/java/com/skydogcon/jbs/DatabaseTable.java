/**
 * 
 */
package com.skydogcon.jbs;

import java.sql.*;

/**
 * @author TheCelticTyger
 *
 */
public interface DatabaseTable {
	public void createTable();
	public void insert(Object item);
	public boolean tableExists() throws SQLException;
	public String getCreateSQL();
	public String getTableName();
	public void close();
}
