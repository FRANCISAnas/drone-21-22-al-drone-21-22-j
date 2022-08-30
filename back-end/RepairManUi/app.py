import os
import requests
from flask import Flask, request

AVAILABLE_STATUS = 'AVAILABLE'
maintenance_reparation_url = os.getenv('MAINTENANCE_REPARATION_URL', 'http://localhost:8085')

app = Flask(__name__)


@app.route('/', methods=["POST"])
def home():
    return "SUCCESS"


import threading


def thread_function_drone(drone):
    message = "Press Enter to continue drone " + str(drone) + " repairing ..."
    input(message)
    res = requests.post(maintenance_reparation_url + '/j/drone/repair', json=drone)
    print(res.status_code)
    print(res.json())


def thread_function_station(station):
    message = "Press Enter to continue staion " + station["name"] + " repairing ..."
    for i in range(station["terminalcount"]):
        print("Repairing terminal...")
    input(message)
    res = requests.post(maintenance_reparation_url + '/j/station/repair', json=station)
    print(res.status_code)
    print(res.json())


@app.route('/j/station/<stationName>/terminals', methods=["POST"])
def repairsTerminal(stationName):
    print("station name", stationName)
    print(request.json)

    station = request.json

    print('Repaired station ', stationName)
    x = threading.Thread(target=thread_function_station, args=(station,))
    x.start()

    return station


@app.route('/j/drone', methods=["POST"])
def repairsDrone():
    drone = request.json
    print("Begin drone", drone, "repairement ...")

    drone['status'] = AVAILABLE_STATUS

    print('Repaired drone ', drone)
    x = threading.Thread(target=thread_function_drone, args=(drone,))
    x.start()
    return drone


if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=int(os.getenv('PORT', 5001)))
