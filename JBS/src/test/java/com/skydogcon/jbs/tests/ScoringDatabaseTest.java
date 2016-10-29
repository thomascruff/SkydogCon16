/**
 * 
 */
package com.skydogcon.jbs.tests;

import com.skydogcon.jbs.ScoringDatabase;

/**
 * @author bsmith
 *
 */
public class ScoringDatabaseTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ScoringDatabase.initialize();
		
		try {
			System.out.println("Creating database object instance.");
			ScoringDatabase dbLink = new ScoringDatabase();
			System.out.println("Database object instance created.");
			
			System.out.println("Performing first insert.");
			dbLink.insert("ABCDEF123","8.8.8.8",2);
			System.out.println("First insert complete.");
			
			System.out.println("Performing second insert");
			dbLink.insert("ABCDEF123","8.8.8.8",2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ScoringDatabase.shutdown();
	}
}
