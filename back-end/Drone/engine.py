#!/usr/bin/env python
import json
import logging
import os
import pika
import threading

new_flight_plan = None

i = 0


class FlightPlan(object):
    def __init__(self, data):
        self.__dict__ = data

    def execute(self):
        i = 0
        while i < len(self.flightPlan.refillStops):
            displacementVector =
            i += 1


class Drone(object):
    def __init__(self, _unloaded_weight, _autonomy_maximum, _battery_level_in_percent, _status,
                 _maximum_weighing_capacity, _name):
        self.unloaded_weight = _unloaded_weight
        self.autonomy_maximum = _autonomy_maximum
        self.battery_level_in_percent = _battery_level_in_percent
        self.status = _status
        self.maximum_weighing_capacity = _maximum_weighing_capacity
        self.name = _name
        self.new_flight_plan = None
        self.launch_channel = None
        self.update_channel = None
        self.setup()

    def listen_to_flight_plan_update(self):
        prefixed_print("##########\tDrone %r is listenning for %r\t##########" % (
            self.name, 'update flight plan'))
        connection = pika.BlockingConnection(pika.ConnectionParameters(host=os.getenv(
            'RABBIT_HOST', 'localhost'), port=int(os.getenv('PORT', '5672')), heartbeat=0))
        self.update_channel = connection.channel()

        self.update_channel.exchange_declare(
            exchange='dronecom', exchange_type='topic', durable=True)

        result = self.update_channel.queue_declare(queue='', exclusive=True)
        queue_name = result.method.queue

        self.update_channel.queue_bind(exchange='dronecom', queue=queue_name,
                                       routing_key=f"drone.{self.name}.update_flight_plan")

        prefixed_print(' [*] Waiting for logs. To exit press CTRL+C')
        self.update_channel.basic_consume(
            queue=queue_name, on_message_callback=self.update_callback, auto_ack=True)
        self.update_channel.start_consuming()

    def wait_for_launch(self):
        prefixed_print(
            "##########\tDrone %r is listenning for %r\t##########" % (self.name, 'start'))
        connection = pika.BlockingConnection(pika.ConnectionParameters(host=os.getenv(
            'RABBIT_HOST', 'localhost'), port=int(os.getenv('PORT', '5672')), heartbeat=0))
        self.launch_channel = connection.channel()

        self.launch_channel.exchange_declare(
            exchange='dronecom', exchange_type='topic', durable=True)

        result = self.launch_channel.queue_declare(queue='', exclusive=True)
        queue_name = result.method.queue

        self.launch_channel.queue_bind(
            exchange='dronecom', queue=queue_name, routing_key=f"drone.{self.name}.launch")

        prefixed_print(' [*] Waiting for logs. To exit press CTRL+C')
        self.launch_channel.basic_consume(
            queue=queue_name,
            on_message_callback=lambda channel, method, properties, body: self.launch_callback(channel, method,
                                                                                               properties, body),
            auto_ack=True)
        self.launch_channel.start_consuming()

    def setup(self):
        x1 = threading.Thread(target=lambda: self.wait_for_launch())
        x2 = threading.Thread(
            target=lambda: self.listen_to_flight_plan_update())
        logging.info("Main    : before running thread")

        x1.start()
        x2.start()

        logging.info("Main    : wait for the thread to finish")

        x1.join()
        x2.join()

    def update_callback(self, ch, method, properties, body):
        pass

    def launch_callback(self, ch, method, properties, body):
        print('Launch')
        print(body.decode("utf-8"))


def prefixed_print(*values, **kwargs):
    print(os.getenv('DRONE_NAME'), ' :', *values, **kwargs)


def main(drone_name, routing_key):
    global new_flight_plan

    def send_delivery_progression_in_the_bus(tracking_number, signal_type, message):
        for _ in range(5):
            try:
                channel.basic_publish(exchange='dronecom', routing_key='drone.special.events', body=json.dumps({
                    'type': signal_type,
                    'trackingNumber': tracking_number
                }))
                prefixed_print(message)
                break
            except:
                prefixed_print("Connection lost retrying...")
                # init_rabbit_connection(the_drone.name)

    def init_objects_for_flight_plan(deleveryFlightPlan):
        origin = deleveryFlightPlan["origin"]
        refillStops = deleveryFlightPlan["refillStops"]
        refillStopsLocations = [elm["chargingStation"]
                                ["location"] for elm in refillStops]

        prefixed_print('refillStops = ', refillStopsLocations)

        stationNames = [refillStop["chargingStation"]["stationName"]
                        for refillStop in refillStops]
        print('stations names : ', stationNames)
        l = len(refillStopsLocations)
        return l, origin, refillStops, refillStopsLocations, stationNames

    def at_least_one_term_is_available(terms: list):
        global i
        if i == 2 and drone_name == 'JolyDroneHalfWay':
            i += 1
            return False
        i += 1
        for term in terms:
            if term["status"] == "AVAILABLE":
                return True

        return False

    def init_rabbit_connection(drone_name):
        global channel, result, result_update_flight_plan
        connection = pika.BlockingConnection(
            pika.ConnectionParameters(host=os.getenv('RABBIT_HOST', 'localhost'), port=int(os.getenv('PORT', '5672')),
                                      heartbeat=0))
        channel = connection.channel()

        channel.exchange_declare(
            exchange='dronecom', exchange_type='topic', durable=True)

        result = channel.queue_declare(queue='', exclusive=True)
        queue_name = result.method.queue

        channel.queue_bind(
            exchange='dronecom', queue=queue_name, routing_key=f"drone.{drone_name}.{routing_key}")

    def callback(ch, method, properties, body):
        global new_flight_plan

        # print(" [x] %r:%r" % (method.routing_key, body.decode()))

        prefixed_print(f"New_flight_plan: {new_flight_plan}")
        # prefixed_print(" [x] %r:%r" % (method.routing_key, body.decode()))

        if routing_key != "launch":
            tmp_fp = json.loads(body.decode("utf-8"))
            prefixed_print("New Flight plan was received...")
            # prefixed_print("New Flight plan received", tmp_fp)
            new_flight_plan = tmp_fp
        else:
            received_message = json.loads(body.decode("utf-8"))

            prefixed_print(
                "Received message: ", "new flight plan" if "refillStops" in received_message else "start delivery")

            deliveryDTO = received_message

            tracking_number = deliveryDTO["trackingNumber"]

            deleveryFlightPlan = deliveryDTO["flightPlan"]

            drone = deliveryDTO["drone"]

            defaultAutonumMaximum = drone["autonomyMaximum"]

            l, origin, refillStops, refillStopsLocations, stationNames = init_objects_for_flight_plan(
                deleveryFlightPlan)
            prefixed_print(
                "drone taking off from warehouse, coordinates : ", origin["x"], origin["y"])
            prefixed_print("Please press any key to start the navigation ...")

            prefixed_print("Starting Delevery...!")
            send_delivery_progression_in_the_bus(tracking_number, 'DELIVERY_START',
                                                 "Delivery has been started!")
            print("stations name = ", stationNames)
            input()
            station_step = 0
            while station_step < l:
                if refillStops[station_step]["stopoverStatus"] == "IGNORED":
                    prefixed_print("Ignoring station", stationNames[station_step], "station coordinates : ",
                                   refillStopsLocations[station_step]["x"],
                                   refillStopsLocations[station_step]["y"])

                    station_step += 1
                    continue
                prefixed_print("Arriving to station", stationNames[station_step], "station coordinates : ",
                               refillStopsLocations[station_step]["x"],
                               refillStopsLocations[station_step]["y"])

                prefixed_print("Checking if station is available ...")

                prec_point = origin if station_step == 0 else refillStopsLocations[station_step - 1]

                distance_done = ((origin["x"] - refillStopsLocations[station_step]["x"]) ** 2 + (
                        origin["y"] - refillStopsLocations[station_step]["y"]) ** 2) ** 0.5

                if len(refillStops[station_step]["chargingStation"][
                           "terminals"]) == 0 or not at_least_one_term_is_available(
                    refillStops[station_step]["chargingStation"]["terminals"]):
                    prefixed_print(
                        "Mayday ! Station is not available, all terminals are unavailable")
                    prefixed_print("Charging with the emergency terminal ...")
                    # Send a message to recalculate a new itinerary
                    x1, y1 = refillStopsLocations[station_step - 1]["x"] if station_step > 0 else origin["x"], \
                             refillStopsLocations[station_step - 1][
                                 "y"] if station_step > 0 else origin["y"]
                    x2, y2 = refillStopsLocations[station_step]["x"], refillStopsLocations[station_step]["y"]

                    drone["autonomyMaximum"] -= (
                            (((x2 - x1) ** 2) + ((y2 - y1) ** 2)) ** 0.5)

                    prefixed_print("Setting the station at KO")
                    refillStops[station_step]["stopoverStatus"] = "KO"

                    prefixed_print(
                        "Sending may day message to the monitor to change all the flight plans that will go through this station.")
                    for _ in range(5):
                        try:
                            may_day_object = {"droneAndFlightPlan": {'drone': drone,
                                                                     "flightPlan": deleveryFlightPlan},
                                              "damagedStationName": stationNames[station_step],
                                              "damagedStationCoordinates": refillStopsLocations[station_step]
                                              }
                            # prefixed_print("mayday object", may_day_object)
                            channel.basic_publish(exchange='dronecom',
                                                  routing_key='drone.special.may.day',
                                                  body=json.dumps(may_day_object))
                            prefixed_print("May day sent")
                            break
                        except:
                            prefixed_print("Connection lost retrying...")
                            init_rabbit_connection(drone_name)
                    input("Press enter to check for new flight plan")
                    while not new_flight_plan:
                        input(
                            "No flight plan found for now, press enter to check again...")
                    l, origin, refillStops, refillStopsLocations, stationNames = init_objects_for_flight_plan(
                        new_flight_plan)
                    prefixed_print("Flight plan updated!")
                    new_flight_plan = None
                    station_step += 1
                    continue
                    # refillStops = methode_A_implermenter()
                else:
                    refillStops[station_step]["stopoverStatus"] = "OK"
                    prefixed_print("Station is available !")
                    prefixed_print("preparing to land ...")

                prefixed_print("Successful landing!")
                prefixed_print("Updating position")
                # Send updates to flightmonitor
                for _ in range(5):
                    try:
                        channel.basic_publish(exchange='dronecom',
                                              routing_key='drone.regular.update',
                                              body=json.dumps({'drone': drone,
                                                               "flightPlan": deleveryFlightPlan}))
                        prefixed_print("Update sent")
                        break
                    except:
                        prefixed_print("Connection lost retrying...")
                        init_rabbit_connection(the_drone.name)
                prefixed_print("Charging drone ...")

                drone["autonomyMaximum"] = defaultAutonumMaximum
                # time.sleep(2)
                prefixed_print("Charging completed !")

                if station_step + 1 < l:

                    prefixed_print(
                        "Please press any key to take off and navigate to next stop ...")
                    input()
                    if new_flight_plan:
                        l, origin, refillStops, refillStopsLocations, stationNames = init_objects_for_flight_plan(
                            new_flight_plan)
                        prefixed_print("Flight plan updated!")
                        new_flight_plan = None
                station_step += 1
            send_delivery_progression_in_the_bus(tracking_number, 'DELIVERY_FINISHED',
                                                 "Delivery has been completed !")

            prefixed_print("Updates sent to monitor")
            prefixed_print("Drone returning to home...")
            prefixed_print("Drone returned at home")
            exit()


def run(drone_name=os.getenv('DRONE_NAME', None)):
    unloaded_weight = int(os.getenv('UNLOADED_WEIGHT', 0))
    autonomy_maximum = int(os.getenv('AUTONOMY_MAXIMUM', None))
    battery_level_in_percent = int(os.getenv('BATTERY_LEVEL_IN_PERCENT', None))
    status = os.getenv('STATUS', None)
    maximum_weighing_capacity = int(
        os.getenv('MAXIMUM_WEIGHING_CAPACITY', None))
    drone = Drone(unloaded_weight, autonomy_maximum,
                  battery_level_in_percent, status, maximum_weighing_capacity, drone_name)
