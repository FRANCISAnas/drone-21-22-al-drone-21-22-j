#!/usr/bin/env python3
import os
import requests

# créer un paquet
# créer une station
# créer un warehouse
# créer un drone

STATIONS_MANAGER_URL = f"http://{os.getenv('STATION_MANAGER_HOST', 'localhost')}:{os.getenv('STATION_MANAGER_PORT', '8084')}"

DELIVERY_PLANNER_URL = f"http://{os.getenv('DELIVERY_PLANNER_HOST', 'localhost')}:{os.getenv('DELIVERY_PLANNER_PORT', '8082')}"


# TODO add location to the drone

def initialize_station(name, x, y):
    res = requests.get(f"{STATIONS_MANAGER_URL}/j/createTerminal/{name}+{x}+{y}")
    print(res.status_code)
    print(res.json())


def initialise_warehouse(warehouse):
    tmp = requests.post(f"{DELIVERY_PLANNER_URL}/warehouses", json=warehouse)
    print(tmp.json())
    print(tmp.status_code)
    return tmp


def link_a_drone_to_a_warehouse(warehouse, drone_name, drone_maximum_autonomy):
    print(warehouse.json())
    drone_stationned = warehouse.json()['_links']["dronesStationned"]["href"]
    print("Drone stationned link", drone_stationned)
    res_linked_drone = requests.post(drone_stationned, json={
        "name": drone_name,
        "unloadedWeight": 400,
        "autonomyMaximum": drone_maximum_autonomy,
        "batteryLevelInPercent": 100,
        "status": "AVAILABLE",
        "maxWeighingCapacity": 1000
    })
    print("created drone result code", res_linked_drone.status_code)
    print("created drone result json", res_linked_drone.json())


def create_warehouse_and_drone(location, drone_name, drone_maximum_autonomy):
    created_warehouse = initialise_warehouse({
        "location": location
    })
    link_a_drone_to_a_warehouse(created_warehouse, drone_name, drone_maximum_autonomy)


"""
The flight monitor will execute the the drone_engine.py by passing the deliveryDTO object to the drone
The drone_engine.py will simulate the itinirary of the drone when passing by all the escal points

We will suppose that the 2nd station is not working to simulate what happens if a charging station is not working/

"""

initialize_station("station1", 80, 110)
initialize_station("station11", 411, 29)
initialize_station("station2", 98, 153.5)
initialize_station("station12", 390, 64.7)
initialize_station("station3", 148, 148.3)
initialize_station("station4", 153.3, 158.1)
initialize_station("station13", 331, 33)
initialize_station("station5", 187.4, 209.8)
initialize_station("station14", 242, 50)
initialize_station("station6", 248.8, 250.4)
initialize_station("station15", 139, 59)
initialize_station("station7", 131, 246)
initialize_station("station8", 29.6, 268)

create_warehouse_and_drone({
    "x": 51, "y": 78
}, "JolyDroneHalfWay", 74)

create_warehouse_and_drone({
    "x": 398, "y": -139
}, "JolyDrone432", 40)

create_warehouse_and_drone({
    "x": 448, "y": -68
}, "JolyDroneFarWay", 104)
