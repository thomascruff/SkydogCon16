/**
 * 
 */
package com.skydogcon.jbs;

/**
 * @author TheCelticTyger
 *
 */
public final class Score {
	//Instance Variable
	private String id = null;
	private String ip = null;
	private int score = 0;
	private String country = null;
	private String region = null;
	private String city = null;
	private float latitude = 0;
	private float longitude = 0;
	
	public Score (String id, String ip, int score){
		this.id = id;
		this.ip = ip;
		this.score = score;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public float getLatitude() {
		return latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	public float getLongitude() {
		return longitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}
	
	private void geoLocationLookup(){
		
	}
}
