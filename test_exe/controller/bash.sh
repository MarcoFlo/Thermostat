#!/bin/bash
lxterminal --command java -jar controllermd-0.2.6-SNAPSHOT.jar
lxterminal --command python3 ./mainSensor.py
sleep 70
lxterminal --command python3 ./mainActuator.py
chromium-browser --incognito --noerrdialogs --disable-infobars --kiosk http://localhost:8080
