import Adafruit_DHT
import paho.mqtt.client as mqtt
from time import sleep

DHT_SENSOR = Adafruit_DHT.DHT22
DHT_PIN = 4

client = mqtt.Client("mainRoomSensor")
client.connect("localhost")

while True:
    humidity, temperature = Adafruit_DHT.read_retry(DHT_SENSOR, DHT_PIN)

    if humidity is not None and temperature is not None:
        client.publish("/mainRoomSensor/sensor", str(temperature) + "_" + str(humidity))
        print("Temp={0:0.1f}*C  Humidity={1:0.1f}%".format(temperature, humidity))
    else:
        print("Failed to retrieve data from humidity sensor")

    sleep(1)
