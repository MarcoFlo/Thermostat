# Folder overview
  - code/backend/controllermd contains the main code
  - code/backend/controllermd-doc contains the documentation of the main code
  - code/backend/tester contains the tester code
  - exe contains the executable

# System requirements
- Java
- python if sensor/actuator directly connected
- Mosquitto broker or online one 
- Redis server or online one

# How to test on windows
- Open a cmd and run "net stop mosquitto"
- Add this line to the end of "C:\Program Files\mosquitto\mosquitto.conf":  

      listener 1883
      listener 9001
      protocol websockets
- Restart the system
- execute the controllermd  
    Open a cmd and run "java -jar controllermd-0.0.1-SNAPSHOT.jar" 
  
- execute the tester   
    Open an other cmd and run "java -jar "tester-0.0.1-SNAPSHOT.jar
    The tester at every execution will create data for the esp
      
      
# How to test it on a new rpi
```
install the last raspbian OS from https://www.raspberrypi.org/downloads/raspbian/
sudo apt-get update --allow-releaseinfo-change if there are still problem with the buster release
sudo apt-get update  sudo apt-get upgrade
sudo apt-get install default-jdk
sudo apt-get install mosquitto
sudo systemctl stop mosquitto.service
sudo nano /etc/mosquitto/mosquitto.conf adding this line at the end:
  listener 1883
  listener 9001
  protocol websockets
sudo systemctl restart mosquitto.service
sudo apt-get install redis-server
sudo apt-get install python3-dev python3-pip
sudo pip3 install Adafruit_DHT
sudo pip3 install paho-mqtt
this complete set of command except sudo nano /etc/dhcpcd.conf https://www.raspberrypi.org/documentation/configuration/wireless/access-point.md
sudo nano /etc/network/interfaces add 
  auto lo
  iface lo inet loopback
  auto eth0
  iface eth0 inet manual
  allow-hotplug wlan0
  iface wlan0 inet manual
  wpa-conf /etc/wpa_supplicant/wpa_supplicant.conf
cd /home/pi/Documents
git clone https://github.com/MarcoFlo/Thermostat.git
cd Thermostat/exe/controller/
sudo chmod a+x chromium.sh
sudo cp thermostat_sensor.service /etc/systemd/system/thermostat_sensor.service
sudo cp thermostat_actuator.service /etc/systemd/system/thermostat_actuator.service
sudo cp thermostat_controller.service /etc/systemd/system/thermostat_controller.service
cd /etc/systemd/system
sudo chmod a+x thermostat_actuator.service
sudo chmod a+x thermostat_sensor.service
sudo chmod a+x thermostat_controller.service
sudo systemctl daemon-reload
sudo systemctl enable thermostat_actuator.service
sudo systemctl enable thermostat_sensor.service
sudo systemctl enable thermostat_controller.service
sudo systemctl daemon-reload
sudo nano /home/pi/.config/lxsession/LXDE-pi/autostart
  @xscreensaver -no-splash
  @xset s off
  @xset -dpms
  @xset s noblank
  @unclutter
  @sh /home/pi/Documents/Thermostat/exe/controller/chromium.sh
  sudo reboot
```
# How to test on an rpi without the auto-running feature enabled 
Simply java -jar ecc

# How to set up github
https://www.youtube.com/watch?v=J_Clau1bYco
First found, if you have problem cloning this repository check youtube or ask me   
Before pushing(sending the code to git), remember to pull(download the new code if there is some)

# HTTP endpoint description
Not updated right now    
https://docs.google.com/document/d/1TF6zZ8DTbSKHRjFNf6_r4kNfpuSn5rqgrSfVmJvNIGA/edit#heading=h.38xql5hyg87g

# EXTRA
To open a cmdline very fast go the folder and shift+right click and then choose Open PowerShell here  
Keep the jar in different folder  
If windows asks, allow access in private network  

Simple tool to test the http endpoint -> https://www.getpostman.com/downloads/
