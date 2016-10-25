package com.skydogcon;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/**
 * @author Tom Ruff
 * This is the worker thread that talks to the badge
 * In the spirit of Election 2016, and to celebrate #Dave4President
 * this simple state machine allows users to be polled by the badge.
 * Voting for anyone other than Dave Kennedy for President causes a nasty clown to be displayed
 * on the badge
 * 
 * To vote, move the joystick to the right
 * To see the next candidate, move the joystick down or up
 * 
 * Next iteration should have something to terminate the connection (and thread) if the badge is not heard from in 60 seconds
 * 
 */

class BadgeServerThread extends Thread {
    private ServerSocket socket = null;
    BufferedReader qfs = null;
    private boolean electionYear = true;
    private int bufLength = 1024;
    private static Logger log = Logger.getLogger(BadgeServerThread.class.getName());
    private static final String BASEIMGFOLDER = "./";
    private static final String START = "a";
    private static final String DEMOCRAT = "b";
    private static final String REPUBLICAN = "c";
    private static final String LOSER = "d";
    private static final String DAVE = "e";
    
    private String state = BadgeServerThread.START;
    int counter = 0;
    
    BadgeServerThread() {
        super("BadgeServer");
        try {
            socket = new ServerSocket(12345);
            log.info("BadgeServer listening on port: " + socket.getLocalPort());
        } catch (java.io.IOException e) {
            log.log(Level.SEVERE, "Could not create server socket.");
        }
    }

    public void run() {
    	if (socket == null)
            return;
    	Socket connectedSocket = null;
        try {
			connectedSocket = socket.accept();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        while (electionYear) {
        	log.log(Level.FINE,"No Mercy");
        	byte[] buf = new byte[bufLength]; // Define buf here so it doesn't go out of scope
            try {
                
                byte[] dString = null;
                byte[] incomingData = new byte[1024];
                // receive request
                
                log.log(Level.FINEST,"WTF");
                OutputStream os = connectedSocket.getOutputStream();
                InputStream is = connectedSocket.getInputStream();
                int bytesRead = is.read(incomingData);    
                String incomingDataString = new String(incomingData);
                
                System.out.println("Got a packet " + bytesRead + new String(incomingData));
                // send response
                if (incomingDataString.contains("LU"))
                {
                	dString = getJoystickImage("left", this.counter++);
                } else if (incomingDataString.contains("RU"))
                {
                	dString = getJoystickImage("right", this.counter++);
                } else if (incomingDataString.contains("DU"))
                {
                	dString = getJoystickImage("down", this.counter++);
                } else if (incomingDataString.contains("UU"))
                {
                	dString = getJoystickImage("up", this.counter++);
                } else if (counter > 0)// Write out the last image
                	dString = buf;
                else //prevent null pointer on startup
                	dString = getJoystickImage("up", this.counter++);                
                buf = dString;
                os.write(buf);
            } catch (IOException e) {
                System.err.println("IOException:  " + e);
                electionYear = false;
                e.printStackTrace();
            }
        }
        try {
        	socket.close();
        } catch(Exception e)
        {
        	e.printStackTrace();
        }
    }
    /*
     * This method generates a random snow image if the required file does not exist on the filesystem
     * 
     */
    private byte[] getNextImage() throws IOException {
        int width = 128;
        int height = 64;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        //file object
 
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
              int a = (int)(Math.random()*256); //alpha
              int r = (int)(Math.random()*256); //red
              int g = (int)(Math.random()*256); //green
              int b = (int)(Math.random()*256); //blue
      
              int p = (a<<24) | (r<<16) | (g<<8) | b; //pixel
      
              img.setRGB(x, y, p);
            }
          }
        ImageIO.write(img, "png", bos);
        
		return bos.toByteArray();
    }
        
    /*
     * This method returns which image to send to the badge as a 1024 byte array
     */
    private byte[] getJoystickImage(String direction, int count) throws IOException {
    	   	
           File imgFile = new File(BASEIMGFOLDER + playTheGame(this.state, counter, direction));
           byte [] rc = null;
           BufferedImage img = null;
           System.out.println(imgFile.getCanonicalPath() + " " + imgFile.exists());
           if (!imgFile.exists())
           {
        	   rc = this.getNextImage();
           }  else 
           {
        	   img = ImageIO.read(imgFile).getSubimage(0, 0, 128, 64); 
   			}
           rc = this.thresholdConverterAndCompress(img);
           System.out.println("your image is " + rc.length);
   		return rc;
    }
    
    /* 
     * State machine for the election "game"
     */
    private String playTheGame(String state2, int counter2, String direction) {
		String rc = null;
		if (counter == 0)
		{
			this.state = BadgeServerThread.START;
			rc = "start.jpg";
			return rc;
		}
		if (BadgeServerThread.START.equals(state2))
		{
			if ("up".equals(direction) || "left".equals(direction) ||  "right".equals(direction))
			{
				this.state = BadgeServerThread.START;
				rc = "start.jpg";	
			}
			else {
				this.state = BadgeServerThread.DEMOCRAT;
				rc = "hillary.jpg";
			} 
				
		} else if (BadgeServerThread.DEMOCRAT.equals(state2))
		{
			if ("up".equals(direction) || "left".equals(direction))
			{
				this.state = BadgeServerThread.START;
				rc = "start.jpg";
			} else if ("right".equals(direction))
			{
				this.state = BadgeServerThread.LOSER;
				rc = "loser.jpg";
			} else if("down".equals(direction))
			{
				this.state = BadgeServerThread.REPUBLICAN;
				rc = "donald.jpg";
			}
		} else if (BadgeServerThread.REPUBLICAN.equals(state2))
		{
			if ("up".equals(direction) || "left".equals(direction))
			{
				this.state = BadgeServerThread.DEMOCRAT;
				rc = "hillary.jpg";
			} else if ("right".equals(direction))
			{
				this.state = BadgeServerThread.LOSER;
				rc = "loser.jpg";
			} else if("down".equals(direction))
			{
				this.state = BadgeServerThread.DAVE;
				rc = "dave.jpg";
			}
			
		} else if (BadgeServerThread.DAVE.equals(state2))
		{
			if (direction.equals("right"))
			{
				rc = "sponsor.jpg";
				this.state = BadgeServerThread.START;
			} else if ("up".equals(direction) || "left".equals(direction))
			{
				this.state = BadgeServerThread.REPUBLICAN;
				rc = "donald.jpg";
			} else
			{
				this.state = BadgeServerThread.DAVE;
				rc = "dave.jpg";
			}			
		}	
		else // state == loser
		{
			this.state = BadgeServerThread.START;
			rc = "loser.jpg";
		}
		return rc;
	}
    /* 
     * This method will take the first 128x64 pixels of an image and convert it for display
     * on the OLED screen of the badge.  There is a problem in this method that will cause a NullPointerException
     * if the base image is smaller than 128x64 pixels.  Will fix in a future release.
     */
    public byte[] thresholdConverterAndCompress(BufferedImage image) {
        byte[] rc = new byte[1024];
        int bitcounter = 0;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color color = new Color(image.getRGB(x, y));
                int grayValue = (color.getBlue() + color.getRed() + color.getGreen()) / 3;
                if (grayValue > 127) {
                	// do nothing
                } else {
                	rc[bitcounter/8] = twiddleBits(rc[bitcounter/8], bitcounter);
                }
                bitcounter++;
            }
        }
        return rc;
    }
    /*
     * This method simply ORs bits when a pixel needs to be turned on.  
     * Could have been done inline, but for demonstration purposes it was
     * much clearer to set it here
     */
	private byte twiddleBits(byte b, int bitcounter) {
		switch (bitcounter%8)
		{
			case 0:
				b = (byte) (b | 0x80);
				break;
			case 1:
				b = (byte) (b | 0x40);
				break;
			case 2:
				b = (byte) (b | 0x20);
				break;
			case 3:
				b = (byte) (b | 0x10);
				break;
			case 4:
				b = (byte) (b | 0x08);
				break;
			case 5:
				b = (byte) (b | 0x04);
				break;
			case 6:
				b = (byte) (b | 0x02);
				break;
			case 7:
				b = (byte) (b | 0x01);
				break;
			default:	
		}
		return b;
	}
	/* 
	 * A simple test program to see that the image to 1-bit 128x64 conversion was working properly.
	 */

    public static void main(String[] args) {
    	String filename = "./start2.jpg";
    	BadgeServerThread tom = new BadgeServerThread();
    	
    	try {
    		byte[] rc = (tom.thresholdConverterAndCompress(ImageIO.read(new File(filename)).getSubimage(0, 0, 128, 64)));
			System.out.println(rc.length);
    		File outfile = new File("./end.jpg");
			FileOutputStream fos = new FileOutputStream(outfile);
			fos.write(rc);
			fos.close();
    	} catch (IOException e) {
			e.printStackTrace();
		}
    }
}