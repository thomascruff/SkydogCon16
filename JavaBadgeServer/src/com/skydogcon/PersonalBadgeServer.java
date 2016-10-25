/**
 * 
 */
package com.skydogcon;

/**
 * @author thomascruff
 * A single threaded badge server for demonstration purposes at SkydogCon 6
 */
public class PersonalBadgeServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
        new BadgeServerThread().start();
    }

}
