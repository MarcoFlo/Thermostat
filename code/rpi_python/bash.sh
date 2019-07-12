#!/bin/bash

chromium-browser --noerrdialogs --incognito --start-maximized --disable-infobars --kiosk https://localhost:8080
sleep 5
xdotool key F11
