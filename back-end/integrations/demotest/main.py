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
    "destination": {"x": 969.6, "y": 996.3},
    "origin": {"x": 469.6, "y": 101.7}
}

station = {
    "terminalCount": 1,
    "location": {
        "x": 500.6, "y": 500
    },
    "terminals": [
        {
            "status": "AVAILABLE"
        }
    ],
    "stationName": "antibe_station"
}

warehouse = {
    "location": {
        "x": 900.6, "y": 800
    }
}

drone = {
    "name": "jlyone",
    "unloadedWeight": 100,
    "autonomyMaximum": 1000,
    "batteryLevelInPercent": 90,
    "status": "AVAILABLE",
    "maxWeighingCapacity": 250
}


# TODO add location to the drone

def initialize():
    tmp = requests.post(f"{STATIONS_MANAGER_URL}/chargingStations", json=station)
    print(tmp.json())
    print(tmp.status_code)

    tmp = requests.post(f"{DELIVERY_PLANNER_URL}/drones", json=drone)
    print(tmp.json())
    print(tmp.status_code)

    tmp = requests.post(f"{DELIVERY_PLANNER_URL}/warehouses", json=warehouse)
    print(tmp.json())
    print(tmp.status_code)


def main():
    res = requests.post(f"{CUSTOMER_CARE_URL}/customers/package/cost", json=paquet)

    cost_estimation = res.json()
    temp_id = cost_estimation["temporaryId"]
    res = requests.post(f"{CUSTOMER_CARE_URL}/customers/package/confirm/{temp_id}", json={
        'cvv': '000',
        'cardNumber': '8888999944445555',
        'expirationDate': '08-23'
    })
    assert res.status_code in range(200, 300)
    confirmation_result = res.json()

    print(confirmation_result)
    tracking_number = confirmation_result["trackingNumber"]
    time.sleep(3)
    res = requests.get(f'{CUSTOMER_CARE_URL}/customers/delivery/{tracking_number}/status')

    print(res.text)
    assert res.status_code in range(200, 300)

    print(f'Delivery Status {res.json()}')


if __name__ == '__main__':
    initialize()
    main()
    time.sleep(8)
