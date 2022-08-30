import os
import requests
import time

# créer un paquet
# créer une station
# créer un warehouse
# créer un drone

STATIONS_MANAGER_URL = f"http://{os.getenv('STATION_MANAGER_HOST', 'localhost')}:{os.getenv('STATION_MANAGER_PORT', '8084')}"

CUSTOMER_CARE_URL = f"http://{os.getenv('CUSTOMER_CARE_HOST', 'localhost')}:{os.getenv('CUSTOMER_CARE_PORT', '8081')}"

DELIVERY_PLANNER_URL = f"http://{os.getenv('DELIVERY_PLANNER_HOST', 'localhost')}:{os.getenv('DELIVERY_PLANNER_PORT', '8082')}"

paquet = {
    "weight": 200.6,
    "dimensions": {
        "height": 100,
        "width": 60,
        "length": 200
    },
    "destination": {"x": 17, "y": 22},
    "origin": {"x": 5.6, "y": 8.7}
}

stations = [{
    "terminalCount": 1,
    "terminals": [
        {
            "status": "AVAILABLE"
        }
    ],
    "location": {
        "x": 10,
        "y": 15
    }

}, {
    "terminalCount": 1,
    "terminals": [
        {
            "status": "AVAILABLE"
        }
    ],
    "location": {
        "x": 13.5,
        "y": 20.0
    }

}, {
    "terminalCount": 1,
    "terminals": [
        {
            "status": "AVAILABLE"
        }
    ],
    "location": {
        "x": -16.0,
        "y": 8
    }

}, {
    "terminalCount": 1,
    "terminals": [
        {
            "status": "AVAILABLE"
        }
    ],
    "location": {
        "x": 23.0,
        "y": 31.5
    }

}
]

"""
warehouse={
    "location":{
        "x":900.6, "y":800
    }
}"""

"""drone={
    "name":"jlyone",
    "unloadedWeight":100,
    "autonomyMaximum":10,
    "batteryLevelInPercent":90,
    "status":"AVAILABLE",
    "maxWeighingCapacity":250
}"""
warehouse = {
    "location": {"x": 8.6, "y": 7.7},
}
drone = {
    "name": "jarrow",
    "unloadedWeight": 100,
    "autonomyMaximum": 60,
    "batteryLevelInPercent": 90,
    "status": "AVAILABLE",
    "maxWeighingCapacity": 250
}


def initialize():
    for station in stations:
        tmp = requests.post(f"{STATIONS_MANAGER_URL}/chargingStations", json=station)
        print(tmp.json())
        print(tmp.status_code)

    tmp = requests.post(f"{DELIVERY_PLANNER_URL}/warehouses", json=warehouse)

    print(tmp.status_code)
    assert tmp.status_code is 201
    drone_stationned = tmp.json()['_links']["dronesStationned"]["href"]
    print("Drone stationned link", drone_stationned)
    tmp = requests.post(drone_stationned, json=drone)
    print(tmp.status_code)
    print(tmp.json())


def main():
    res = requests.post(f"{CUSTOMER_CARE_URL}/customers/package/cost", json=paquet)

    cost_estimation = res.json()
    temp_id = cost_estimation["temporaryId"]
    res = requests.post(f"{CUSTOMER_CARE_URL}/customers/package/confirm/{temp_id}", json={
        'cvv': '000',
        'cardNumber': '8888999944445555',
        'expirationDate': '08-23'
    })
    print(res.status_code)
    print(res.json())
    assert res.status_code in range(200, 300)
    confirmation_result = res.json()

    print(confirmation_result)
    tracking_number = confirmation_result["trackingNumber"]


if __name__ == '__main__':
    # A Exécuter une seule fois
    initialize()
    main()
    time.sleep(5)
