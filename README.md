# Folder overview
  - code/backend/controllermd contains the main code
  - code/backend/controllermddoc contains the documentation of the main code
  - code/backend/tester contains the tester code
  - test_exe contains the executable

# System requirements
- Java
- Mosquitto broker  
- ....?....

# How to test
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
    
      The tester at every execution will:
      - delete the old esp
      - create 3 new one (sensor, cooler, heater) for each room (MainRoom, Kitchen,Living and Bathroom)
      - produce data every 10 seconds

 



# How to set up github
https://www.youtube.com/watch?v=J_Clau1bYco
First found, if you have problem cloning this repository check youtube or ask me   
Before pushing(sending the code to git), remember to pull(download the new code if there is some)

# HTTP endpoint description
https://docs.google.com/document/d/1TF6zZ8DTbSKHRjFNf6_r4kNfpuSn5rqgrSfVmJvNIGA/edit#heading=h.38xql5hyg87g

# EXTRA
If you want to check out the auto generated doc start from the index.html file

To open a cmdline very fast go the folder and shift+right click and then choose Open PowerShell here  
Keep the jar in different folder  
If windows asks, allow access in private network  

Simple tool to test the http endpoint -> https://www.getpostman.com/downloads/
