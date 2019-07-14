// This is the full code for an ESP relay (ID: esp1actuator). 
// Here, we're implementing the IP discovery process. It uses the wifi manager library to set up wifi, the necessary libraries for the HTTP get request and the EEPROM library (network algorithm).
// It also sets up the MQTT (uses pubsub library)
// ...and sets up the interface with the sensor (uses DHT library) 

// *** LIBRARIES ***
#include <ESP8266WiFi.h> //https://github.com/esp8266/Arduino
#include <ESP8266WebServer.h>
#include <WiFiManager.h>         //https://github.com/tzapu/WiFiManager
#include <ESP8266HTTPClient.h>
#include <EEPROM.h>
#include <PubSubClient.h>
#include <DNSServer.h>

// *** GLOBAL VARIABLES ***
// 1) Control flow flags
bool network_flag = false; 
bool initialize_connection = true; //when we call EEPROM for the first time 
bool IP_discovery_needed = false; //to control when we need to call the IP discovery function
bool MQTT_flag = false; //for MQTT connection

// 2) WIFI manager object 
WiFiManager wifiManager;

// 3) IP discovery function variables
String startingIP = "192.168.";
String currentIP = "192.168.";
int i = 0; //for "something" - please see format in IP discovery function
int j = 0; // for "HERE"

//4) HTTP client configuration
HTTPClient http;    //Declare object of class HTTPClient 
//GET Data
// if it doesn't work, remove the :8080 below - tested: works with 8080
// entire link is: http://192.168.43.128:8080/setting/device_discovery
String Link_beginning = "http://";
String Link_ending = ":8080/setting/device_discovery";
String full_address;
String response = "iamrpi";
String payload;

// 5) EEPROM variables for repeating last functioning IP 
String lastIP = "192.168.";

// 6) MQTT configuration variables
const char* mqttServer;
const int mqttPort = 1883;
const char* mqttUser = "";
const char* mqttPassword = "";
//for testing on CLOUDMqtt
//const char* mqttServer = "postman.cloudmqtt.com";
//const int mqttPort = 17813;
//const char* mqttUser = "vlahtrqf";
//const char* mqttPassword = "SUWYSs2A731K";

// wifi and pub-sub objects
WiFiClient espClient;
PubSubClient client(espClient);

// 7) RELAY VARIABLES
int relay = 2; // D4 - 2, follow pinout

// *** FUNCTIONS ***
void reconnect_ESP(){ // function for checking the connection - to be checked frequenctly
    if (WiFi.status() != WL_CONNECTED) { // checking wifi connection
      network_flag = true;
      //original sketch (which was used to run tests): jul04a.ino
      // in the case there's a system failure (the modem fails), we'll need to give the ESP some time to attempt a reconnection otherwise we'll need to reset everything up again (please read below)
      // tests indicate that there's no need for a timeout function, the ESP actually reconnects on its own. We're giving the user 3 min to set up the same modem again in case it 
      // momentarily failed. Otherwise, a new network will need to be configured when the autoconnect function is reached, or the old one after the 3 min.
      // Also if it's a new one, it will wait indefinately for a new network since it won't be able to reconnect during the waiting state (the 3 min). 
      // Keep in mind that if we don't flush the credentials first, attempting to reconnect to the old network even if it's available again will result in failure
      // yield is used to keep the watchdog under control and avoid having a false exception raised (like in for loops)
      yield();
      Serial.println("Connection lost... we will try to reconnect now for 30 seconds. Otherwise, WiFi will need to be configured again.");
      delay(30000); // give it a small delay - max value is 32000 something.. so combine several to achieve 3 minutes
      //delay(30000);
      //delay(30000);
      //delay(30000);
      //delay(30000);
      //delay(30000); 
      yield();
      // the following is required if it fails to reconnect to the wifi (assuming the modem failed - if it's a new network, it will need the next part anyway) 
      if(WiFi.status() != WL_CONNECTED){
        WiFi.disconnect(); // erase credentials otherwise it won't let us reconnect to the same network (this is a possibility) again on the HTML page 
        yield();
        int(wifiManager.autoConnect("AutoConnectAP")); //configure either new network or old one, if it's available again
      }
      initialize_connection = true;
  } // if it's not connected
}

void IP_discovery(){
  // IP format: 192.168.(HERE).(something)
  // range is (16 bit): 192.168.0.0 to 192.168.255.255
  // without internet, HTPP response is -1 (tested). Also -1 for failed attempt (with internet)
  // restarting configuraton
  startingIP = "192.168.";
  currentIP = "192.168.";
  for (int k = 0; k <= 255; k++){ // this is for "HERE" - j
    Serial.println("entered outter loop");
    j = k;
    for(int l = 0; l<= 255; l++){ //this is for the something - i
      Serial.println("entered inner loop"); 
      reconnect_ESP(); //check network state during IP discovery process
      if(network_flag == true){ // end the discovery process, we'll need to restart again 
        break;
      }
      i = l; //for something
      currentIP = (startingIP + String(j) + "." + String(i)).c_str(); 
      //Serial.println(currentIP); // entire IP address of the current iteration  - it's working
      full_address = (Link_beginning + currentIP + Link_ending).c_str();
      Serial.println(full_address); //it's working
      http.begin(full_address);     //Specify request destination
      int httpCode = http.GET();            //Send the request
      payload = http.getString();    //Get the response payload
      Serial.println(httpCode);   //Print HTTP return code
      Serial.println(payload);    //Print request response payload
      http.end();  //Close connection
      if(payload == response) {
        break;
      }
      yield();
    } // bracket of inner for 
    if(payload == response){ // save credentials to EEPROM before exiting process
      Serial.println("entered if payload == response");
      int addr = 300; // initialize address 
      // eg address: IP: 192.168.200.101 indexes are: 0 1 2 3(.) 4 5 6 7(.) 8 9 10 11(.) 12 13 14 
      char val; // remember this will be in ASCII, 0's at 48 
      yield();
      for (int k = 8; k <= 15; k++){ // in case of having the maximum length for the IP
        val = currentIP.charAt(k); //get the char value
        if(val == '\0'){ // if it's the end of the string
          EEPROM.write(addr, '*'); //our termination signature
          EEPROM.commit();
          MQTT_flag = true; // connect to MQTT
          IP_discovery_needed = false;
          break;
        } else{
          EEPROM.write(addr,val);
          addr = addr + 1;
        } 
      }// for (internal)
     break; //break from external for 
    } // break of if payload == response
    if(network_flag == true){ // reset original control flow conditions when we need torestart process, for when we call again the function
      Serial.println("entered network_flag = true"); 
      network_flag = false; // eg. modem fails, connection is changed during the IP discovery
      break;
    }
  } // external for
  Serial.println("Out of the loop, everyone!!");
} // IP discovery function

void read_EEPROM(){ //check for previously stored address 
lastIP = "192.168.";
char last_data;
int addr = 300;
// note to self (addresses and m index):
// 300 -> 1 m = 8
// 301 -> 2 m = 9
// 303 -> 3 m = 10
// 304 -> 4 m  = 12
// 305 -> 5 m = 13
// 306 -> 6 m = 14
  for(int m = 8; m <= 15; m++){
    last_data = EEPROM.read(addr);
    Serial.println(addr);
    Serial.println(last_data);
    addr = addr + 1;
    // now combine the strings
    if(last_data != '*'){
      lastIP = (lastIP + last_data).c_str();
    } else{ // meaning we read *
      break;  
    }
  }
  Serial.println("Printing last IP...");
  Serial.println(lastIP);
}

void setup() {
  delay(1000); // initial delay for initialization
  Serial.begin(115200);
  
  //Sensor configuration
  pinMode(relay, OUTPUT); //initialize D4 as OUTPUT - for the relay 
  digitalWrite(relay, HIGH);

  // initialize EEPROM mem
  EEPROM.begin(512);

  // connection to WIFI using the WIFI manager library
  // INITIAL CONFIGURATION (CAN BE CHANGED LATER DURING OPERATION)
  wifiManager.autoConnect("AutoConnectAP");
  // if successful...
  Serial.println("Connected to the WiFi network");
  
} // void function

void loop() { 
  // initially, we should try seeing if we can connect to the previously working IP. (eg. RPI still connected to the same network, ESP failed and had to be reset).
  // for this we should retrieve the information from the EEPROM
  // EEPROM DATA: 512 BYTES (CURRENT ESP VERSION), store data as chars (bytes), in ASCII
  // We'll write to addresses 300, 301, 302, 303, 304, 305 to store the previous IP 
  // call EEPROM function upon booting up the ESP 
  if (initialize_connection == true){
    Serial.println("Attempting to connect to last IP");
    read_EEPROM();
    // now generating the entire String
    full_address = (Link_beginning + lastIP + Link_ending).c_str();
    Serial.println(full_address);
    http.begin(full_address); //Specify request destination
    int httpCode = http.GET(); //Send the request
    payload = http.getString();  //Get the response payload
    Serial.println(httpCode);  //Print HTTP return code
    Serial.println(payload);  //Print request response payload
    http.end();  //Close connection
    if(httpCode != 200){ //200: successful
      IP_discovery_needed = true;
      network_flag = false;
    } else { // if it was succesful, httpCode = 200, we don't need to rerun the IP discovery process
      network_flag = false; // we don't need to call IP_discovery with network_flag = true again
      IP_discovery_needed = false; 
      //and connect to the MQTT
      // now, address and port of MQTT server. Call setServer 
      // method on the pubsubclient object.
      // here we're not connecting to the server, just specifying
      // its address and port (which one it is).
      mqttServer = lastIP.c_str();
      client.setServer(mqttServer, mqttPort);
      // now, setcalback on the same object to specify a handling
      //function to be executed when an MQTT message is received
      client.setCallback(callback);
      // now, connect to server
      while (!client.connected()) {
        Serial.println("Connecting to MQTT broker...");
        if (client.connect("ESP8266Client", mqttUser, mqttPassword )) {
          Serial.println("Connected to MQTT!");  
        } else {
          Serial.print("Failed with state: ");
          Serial.println(client.state());
          delay(2000);
        }
      }
      client.publish("/esp8266/esp1actuator-esp", "heater");
      client.subscribe("/esp1actuator-esp/actuator",1); //commands to this particular esp (a heater - simulating using the relay)
    } // else
    initialize_connection = false; //bring down the flag - no need for initialization from EEPROM again
   }

  // now if the previous wasn't successful, we'll need to initiate the IP discovery process
  if(IP_discovery_needed == true){
    Serial.println("IP discovery process required...");
    IP_discovery(); //call the IP discovery process
    // the IP_discovery_needed flag is made false inside the IP_discovery() after a succesful connection + writing to EEPROM 
    if(MQTT_flag == true){
      mqttServer = currentIP.c_str();
      client.setServer(mqttServer, mqttPort);
      client.setCallback(callback);
      while (!client.connected()) {
        Serial.println("Connecting to MQTT broker...");
        if (client.connect("ESP8266Client", mqttUser, mqttPassword )) {
          Serial.println("Connected to MQTT!");  
        } else {
          Serial.print("Failed with state: ");
          Serial.println(client.state());
          delay(2000);
        }
      }
      client.publish("/esp8266/esp1actuator-esp", "heater");
      client.subscribe("/esp1actuator-esp/actuator",1); //commands to this particular esp (a heater - simulating using the relay)
      MQTT_flag = false;
    }
  }

  //once we connect to the correct IP, we need to keep checking that the ESP is connected
  if(initialize_connection == false){
    if(IP_discovery_needed == false){
      reconnect_ESP(); // here we check for connection. If it's ok; keep going; otherwise, we need to connect to the new network or the same one (if it failed, and didn't connect within 3 min)
      if(network_flag == true){
        network_flag = false; //to only call IP_discovery() once in case IP_discovery_needed = true; if the connection fails within IP_discovery, it'll be managed by the flag
      } else{ // this means we've got normal operation, the network is running ok
         if(client.loop()){ // refresh connection to broker  
          Serial.println("Connected to MQTT");
         } else{
           Serial.println("MQTT connection lost - will attempt to reconnect");
           if(client.connect("ESP8266Client", mqttUser, mqttPassword)){
              Serial.println("Reonnected to MQTT");
            } else {
              Serial.print("Failing with state: ");
              Serial.println(client.state());
            }
         }
      } // from the else
      // in both cases, we need to try reconnecting to the last working IP anyway or discover the new one
    } // IP_discovery_needed
  }// initialize_connection
  
  delay(1000); // a small delay of 1 second during operation     
}

void callback(char* topic, byte* payload, unsigned int length) {
  String message_to_esp = ""; //empty message, from the start  
  Serial.print("Message arrived for topic: ");
  Serial.println(topic);
 
  Serial.print("Message:");
  for (int i = 0; i < length; i++) {
    Serial.print((char)payload[i]);
    message_to_esp.concat((char)payload[i]);
  }
  Serial.println('\n');
  Serial.print("Current value of the string: ");
  Serial.println(message_to_esp);

  if(message_to_esp == "on"){
    digitalWrite(relay, LOW);
  }
  else if (message_to_esp == "off"){
    digitalWrite(relay, HIGH);
    }
 
  Serial.println();
  Serial.println("-----------------------");
 
}
