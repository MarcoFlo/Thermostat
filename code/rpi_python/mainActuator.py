import RPi.GPIO as GPIO
import paho.mqtt.client as mqtt

GPIO.setwarnings(False)
GPIO.setmode(GPIO.BCM)
GPIO.setup(17, GPIO.OUT)

def on_message(client, userdata, message):
    msg_decoded = str(message.payload.decode("utf-8"));

    if msg_decoded is "on":
        print("on")
        GPIO.output(17, GPIO.HIGH)
    else:
        print("off")
        GPIO.output(17, GPIO.LOW)


client = mqtt.Client("mainActuator")
client.on_message=on_message
client.connect("localhost")
client.subscribe("/mainActuator/actuator")
client.loop_forever()
