/**
 * 
 */
package com.skydogcon.jbs.tests;

import com.skydogcon.jbs.Score;
import com.skydogcon.jbs.ScoresTable;
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
			System.out.println("Creating database table instance.");
			ScoresTable dbLink = new ScoresTable();
			System.out.println("Database object instance created.");
			
			System.out.println("Creating Score object");
			Score score = new Score("ABCDEF123","8.8.8.8",2);
			System.out.println("Score object created.");
			
			System.out.println("Performing first insert.");
			dbLink.insert(score);
			System.out.println("First insert complete.");
			
			System.out.println("Performing second insert");
			dbLink.insert(score);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ScoringDatabase.shutdown();
	}
}
