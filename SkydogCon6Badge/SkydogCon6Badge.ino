#include <ESP8266WiFi.h>
#include <DNSServer.h>            //Local DNS Server used for redirecting all requests to the configuration portal
#include <ESP8266WebServer.h>     //Local WebServer used to serve the configuration portal
#include <WiFiManager.h>          //https://github.com/tzapu/WiFiManager WiFi Configuration Magic
#include <WiFiClient.h>

#include "SH1106.h"
#include "Adafruit_MCP23008.h"
#include "ESP.h"

/* Code written by Redvers Davies and Tom Ruff for SkydogCon 6 
 *  
 */
 
ADC_MODE(ADC_VCC);

SH1106 display(0x3c, 5,4);
Adafruit_MCP23008 mcp;

WiFiManager wifiManager;

const char* ssid  = "SkydogCon6"; /*Put your SSID here*/
const char* password = "ThankYouToTrustedSec"; /*Put your WiFi password here*/

int loopMe = 0;

IPAddress server(192,168,0,24); /* Put your server ip address here */

  int bu = 0;
  int bd = 0;
  int bl = 0;
  int br = 0;
  int bp = 0;
  
byte packetBuffer[2000];

WiFiUDP UdpOut;
WiFiClient client;

unsigned int localPort = 2390;

void setup()
{
  mcp.begin();
  mcp.pinMode(0, INPUT);
  mcp.pullUp(0, LOW);
  mcp.pinMode(1, INPUT);
  mcp.pullUp(1, LOW);
  mcp.pinMode(2, INPUT);
  mcp.pullUp(2, LOW);
  mcp.pinMode(3, INPUT);
  mcp.pullUp(3, LOW);
  mcp.pinMode(4, INPUT);
  mcp.pullUp(4, LOW);

  mcp.pinMode(5, OUTPUT);
  mcp.pinMode(6, OUTPUT); 
  mcp.pinMode(7, OUTPUT);

  mcp.digitalWrite(5, HIGH);
  mcp.digitalWrite(6, HIGH);
  mcp.digitalWrite(7, HIGH);

UdpOut.begin(localPort);
  
  Serial.begin(115200);
  while (!Serial);             // Leonardo: wait for serial monitor
  display.init();
  display.flipScreenVertically();
  display.setFont(ArialMT_Plain_10);


  Serial.print("Connecting to ");
  Serial.println(ssid);

  //display.clear();
  display.drawString(0,0,"$ cu -l /dev/ttyS0 -s 57600");
  display.drawString(0,13,"AT#MFR?");
  display.display();
 
  // Turn off local access point
  wifi_set_opmode(0x1);
  WiFi.begin(ssid, password);
  

  int id = ESP.getChipId();
  char message[50];
  sprintf(message, "ATDT9,1555%d", id);
  display.drawString(0,23, "@ruff_tr <tcruff@tcruff.com>");
  display.drawString(0,33, "OK");
  display.display();
  delay(500);
  display.drawString(0,43, message);
  display.display();
  while(WiFi.status() != WL_CONNECTED) {
    delay(100);
    Serial.print(".");
  }

  display.drawString(0,53, "CONNECT 9600 V42bis");
  display.display();
  Serial.println(WiFi.localIP());

  if (client.connect(server, 12345)) {
    Serial.println("Connected to evil.red");
    client.setNoDelay(true);
    int id = ESP.getChipId();
    char message[50];
    sprintf(message, "%010d%s", id, "COLDBOOT");
    client.write_P(message, 18);
  }
}

void loop() {
  float volt = 0.00f;

  int sizeBuf = client.available();

  if (sizeBuf >= 1023) {
    Serial.print(sizeBuf);
    Serial.println(" bytes in buffer waiting for me!");
    
    client.read(packetBuffer, 1);
    client.read(packetBuffer, 1024);
    const char *image = reinterpret_cast<const char*>(packetBuffer);

    display.clear();
    display.drawXbm(0,0,128,64, image);
    display.display();
    memset(packetBuffer,128,2000);
  }

  delay(100);
  loopMe++;

  if (loopMe > 20)
  {
    char pmessage[50];
    sprintf(pmessage, "%s%d", "BATT", ESP.getVcc());
    sendPacket(pmessage);
    
    // Uncomment me if you want to send the Wifi strength back to the server 
    // but can take several hundred milliseconds if there is lots of SSIDs around
    //sprintf(pmessage, "%s%d", "RSS", WiFi.RSSI());
    //sendPacket(pmessage);
    
    loopMe = 0;
  }
  if (!client.connected())
  {
    client.stop();
    client.connect(server, 12345);
  }
  if (loopMe == 100)
  {
    ESP.restart();
  }
 
  if ((mcp.digitalRead(0) == 1) && (bp == 0)) {    
      Serial.println("P down");
      sendPacket("PD");
      bp = 1;
  }
  if ((mcp.digitalRead(0) == 0) && (bp == 1)) {    
      Serial.println("P up");
      sendPacket("PU");
      bp = 0;
  }
  if ((mcp.digitalRead(1) == 1) && (bu == 0)) {    
      Serial.println("U down");
      sendPacket("UD");
      bu = 1;
  }
  if ((mcp.digitalRead(1) == 0) && (bu == 1)) {    
      Serial.println("U up");
      sendPacket("UU");
      bu = 0;
  }
  if ((mcp.digitalRead(3) == 1) && (bd == 0)) {    
      Serial.println("D down");
      sendPacket("DD");
      bd = 1;
  }
  if ((mcp.digitalRead(3) == 0) && (bd == 1)) {    
      Serial.println("D up");
      sendPacket("DU");
      bd = 0;
  }
  if ((mcp.digitalRead(2) == 1) && (bl == 0)) {    
      Serial.println("L down");
      sendPacket("LD");
      bl = 1;
  }
  if ((mcp.digitalRead(2) == 0) && (bl == 1)) {    
      Serial.println("L up");
      sendPacket("LU");
      bl = 0;
  }
  if ((mcp.digitalRead(4) == 1) && (br == 0)) {    
      Serial.println("R down");
      sendPacket("RD");
      br = 1;
  }
  if ((mcp.digitalRead(4) == 0) && (br == 1)) {    
      Serial.println("R up");
      sendPacket("RU");
      br = 0;
  }

}

void sendPacket(const char *data) {
  
  int id = ESP.getChipId();
  char message[50];
  sprintf(message, "%010d%s", id, data);
  client.write_P(message, 18);
}

