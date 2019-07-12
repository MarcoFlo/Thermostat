#!/bin/bash
sleep 10
lxterminal --command echo ciao
lxterminal --command chromium-browser --incognito --noerrdialogs --disable-infobars --kiosk http://localhost:8080
