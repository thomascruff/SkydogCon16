/**
 * 
 */
package com.skydogcon.jbs;

import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * @author TheCelticTyger
 *
 */
public final class Score {
	//Class Constants
	private static final String baseGeoURL = "http://freegeoip.net/xml/";
	
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
		this.geoLocationLookup();
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
	
	public String toString(){
		String retVal = super.toString() + "\n";
		retVal = retVal + this.printField(this.getId());
		retVal = retVal + this.printField(this.getIp());
		retVal = retVal + this.printField(this.getCountry());
		retVal = retVal + this.printField(this.getRegion());
		retVal = retVal + this.printField(this.getCity());
		retVal = retVal + this.printField(new Float(this.getLatitude()).toString());
		retVal = retVal + this.printField(new Float(this.getLongitude()).toString());
		return retVal;
	}
	
	private String printField(String field){
		return "\t" + field + "\n";
	}
	
	private void geoLocationLookup(){
        try {
			URL url = new URL(Score.baseGeoURL + this.ip);
			URLConnection connection = url.openConnection();
			DocumentBuilderFactory objDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder objDocumentBuilder = objDocumentBuilderFactory.newDocumentBuilder();
			Document doc = objDocumentBuilder.parse(connection.getInputStream());

			NodeList descNodes = null;
			
			//Retrieve and set Country Code
			descNodes =	doc.getElementsByTagName("CountryCode");
			this.setCountry(descNodes.item(0).getTextContent());
			
			//Retrieve and set Region Code
			descNodes = doc.getElementsByTagName("RegionCode");
			this.setRegion(descNodes.item(0).getTextContent());
			
			//Retrieve and set City
			descNodes = doc.getElementsByTagName("City");
			this.setCity(descNodes.item(0).getTextContent());
			
			//Retrieve and set Latitude
			descNodes = doc.getElementsByTagName("Latitude");
			this.setLatitude(new Float(descNodes.item(0).getTextContent()));
			
			//Retrieve and set Longitude
			descNodes = doc.getElementsByTagName("Longitude");
			this.setLongitude(new Float(descNodes.item(0).getTextContent()));
		} catch (Exception e) {
			ErrorHandler.errorPrint(e);
		}
    }
}
