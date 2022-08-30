package com.aldrone2122.project.jolydrone.services;

import com.aldrone2122.project.jolydrone.entity.Package;
import com.aldrone2122.project.jolydrone.entity.*;
import com.aldrone2122.project.jolydrone.repositories.PackageDeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;

@Component
public class PathFinder {

    private final Logger logger = Logger.getLogger(PathFinder.class.getName());
    @Autowired
    private GetStationInArea stationInArea;

    @Autowired
    private PackageDeliveryRepository packageDeliveryRepository;

    public static ChargingStation[] removeTheElement(ChargingStation[] arr, int index) {

        // If the array is empty
        // or the index is not in array range
        // return the original array
        if (arr == null || index < 0 || index >= arr.length) {

            return arr;
        }

        // Create another array of size one less
        ChargingStation[] anotherArray = new ChargingStation[arr.length - 1];

        // Copy the elements except the index
        // from original array to the other array
        for (int i = 0, k = 0; i < arr.length; i++) {

            // if the index is
            // the removal element index
            if (i == index) {
                continue;
            }

            // if the index is not
            // the removal element index
            anotherArray[k++] = arr[i];
        }

        // return the resultant array
        return anotherArray;
    }

    private ChargingStation[] optimizeStops(Drone drone, ChargingStation[] chargingStations, int size,
                                            Coordinates destination) {
        HashMap<Integer, ChargingStation> chargingIndexToRemove = new HashMap<>();
        for (int i = 0; i < size; i++) {
            if (i == 0) {
                if (i + 1 < size) {
                    if (drone.getCurrentLocation().distanceToPoint(chargingStations[i + 1].getLocation()) <= drone
                            .getAutonomyMaximum())
                        chargingIndexToRemove.put(i, chargingStations[i]);
                    else {
                        boolean found = lastPointEqualToDepartOptimizer(drone, chargingStations, size,
                                chargingIndexToRemove, i);
                        if (!found) {
                            if (drone.getCurrentLocation().distanceToPoint(destination) <= drone.getAutonomyMaximum()) {
                                for (int k = 0; k < size; k++)
                                    chargingIndexToRemove.put(k, chargingStations[k]);
                                i = size - 1; // mettre fin à la recherche du meilleur
                            }
                        } else
                            break;

                    }
                } else {
                    if (drone.getCurrentLocation().distanceToPoint(destination) <= drone.getAutonomyMaximum())
                        chargingIndexToRemove.put(i, chargingStations[i]);
                }

            } else {
                int j = i - 1;
                while (j > -1 && chargingIndexToRemove.containsKey(j))
                    j--; // Recherche du dernier éléments non skipper de la liste
                if (j >= 0) {
                    // est ce que la dernière station est la dernière qu'elle peut peut visiter?
                    if (i != size - 1) {
                        // est ce qu'il peut atteindre la station qui suit celle actuelle(indice j)?
                        if (chargingStations[j].getLocation()
                                .distanceToPoint(chargingStations[i + 1].getLocation()) <= drone.getAutonomyMaximum())
                            chargingIndexToRemove.put(i, chargingStations[i]);
                        else {
                            boolean found = false;
                            // Est ce que le drone peut atteindre une station dans la liste actuelle
                            for (int k = i + 2; k < size; k++)
                                if (chargingStations[j].getLocation().distanceToPoint(
                                        chargingStations[k].getLocation()) <= drone.getAutonomyMaximum()) {
                                    found = true;
                                    for (int l = j + 1; l < k; l++)
                                        chargingIndexToRemove.put(l, chargingStations[l]);
                                    break;
                                }
                            if (!found) {
                                // est ce le drone peut faire un one shoot depuis la station actuelle(index j)
                                if (chargingStations[j].getLocation().distanceToPoint(destination) <= drone
                                        .getAutonomyMaximum()) {
                                    for (int k = j + 1; k < size; k++)
                                        chargingIndexToRemove.put(k, chargingStations[k]);
                                    i = size - 1; // mettre fin à la recherche du meilleur
                                }
                            } else
                                break;

                        }
                    } else {

                        if (chargingStations[j].getLocation().distanceToPoint(destination) <= drone
                                .getAutonomyMaximum())
                            chargingIndexToRemove.put(i, chargingStations[i]);

                    }
                } else {
                    if (i + 1 != size) {// si l'élément courant n'est pas le dernier sur la liste
                        if (drone.getCurrentLocation().distanceToPoint(chargingStations[i + 1].getLocation()) <= drone
                                .getAutonomyMaximum())
                            chargingIndexToRemove.put(i, chargingStations[i]);
                        else {
                            boolean found = lastPointEqualToDepartOptimizer(drone, chargingStations, size,
                                    chargingIndexToRemove, i);
                            if (!found) {
                                if (drone.getCurrentLocation().distanceToPoint(destination) <= drone
                                        .getAutonomyMaximum()) {
                                    for (int k = j + 1; k < size; k++)
                                        chargingIndexToRemove.put(k, chargingStations[k]);
                                    i = size - 1; // mettre fin à la recherche du meilleur
                                }
                            } else
                                break;
                        }
                    } else {
                        if (drone.getCurrentLocation().distanceToPoint(destination) <= drone.getAutonomyMaximum())
                            chargingIndexToRemove.put(i, chargingStations[i]);
                    }
                }
            }
        }
        for (var cITR : chargingIndexToRemove.values()) {
            int index = -1;
            for (int i = 0; i < chargingStations.length; i++) {
                if (chargingStations[i] != null
                        && Objects.equals(chargingStations[i].getStationName(), cITR.getStationName())) {
                    index = i;
                    break;
                }
            }
            if (index > -1)
                chargingStations = removeTheElement(chargingStations, index);
        }
        return chargingStations;
    }

    private boolean lastPointEqualToDepartOptimizer(Drone drone, ChargingStation[] chargingStations, int size,
                                                    HashMap<Integer, ChargingStation> charginIndexToRemove, int i) {
        boolean found = false;
        for (int k = i + 2; k < size; k++)
            if (drone.getCurrentLocation().distanceToPoint(chargingStations[k].getLocation()) <= drone
                    .getAutonomyMaximum()) {
                found = true;
                charginIndexToRemove.put(k, chargingStations[k]);
                break;
            }
        return found;
    }

    private List<Tuple<FlightPlan, Drone>> fightPlansOversAllDrones(Warehouse warehouse, Coordinates destination) {
        List<Tuple<FlightPlan, Drone>> flightPlans = new ArrayList<>();

        logger.info(String.format("Finding all available stations from warehouse at coordinates %s to destination %s",
                warehouse.getLocation().toString(), destination.toString()));

        List<ChargingStation> chargingStationsInArea = stationInArea.getStationsInArea(warehouse.getLocation(),
                destination);

        logger.info("Computing flight plan with all retrieved data");
        for (Drone drone : warehouse.getDronesStationned()) {
            if (!drone.isAvailable()) {
                continue;
            }
            var flightPlan = optimizedFlightPlans(warehouse.getLocation(), destination, chargingStationsInArea, drone);
            if (flightPlan != null)
                flightPlans.add(new Tuple<>(flightPlan, drone));
        }

        logger.info(
                String.format("flight plans for the warehouse at coordinates %s", warehouse.getLocation().toString()));
        return flightPlans;
    }

    private FlightPlan optimizedFlightPlans(Coordinates origin, Coordinates destination,
                                            List<ChargingStation> chargingStationsInArea, Drone drone) {
        return optimizedFlightPlans(origin, destination, chargingStationsInArea, drone, false);
    }

    private FlightPlan optimizedFlightPlans(Coordinates origin, Coordinates destination,
                                            List<ChargingStation> chargingStationsInArea, Drone drone, boolean considerDroneAutonomy) {
        ChargingStation[] stopsTmp = new ChargingStation[chargingStationsInArea.size()];
        int i = 0;
        for (ChargingStation chargingStation : chargingStationsInArea) {
            /*
             * Si la liste des arrêts déja effectués par le drone est vide c'est à dire
             * actuellement le drone par de son warehouse ajouter comme premier arrêt le
             * premier arrêt sur son trajet
             */
            if (i == 0) {
                /*
                 * si le drone peut atteindre la prmière station de recharge l'ajouter comme
                 * premier arrêt sinon arrêter la plannification
                 */
                if (considerDroneAutonomy) {
                    var distanceTochargingStation = chargingStation.distanceTo(origin);
                    if (drone.getAutonomyMaximum() >= distanceTochargingStation) {
                        logger.info(String.format("Drone %s can reach the nearest station %s, Travel distance: %s.", drone.getName(), chargingStation.getStationName(), distanceTochargingStation));
                        stopsTmp[i++] = chargingStation;
                    }

                } else {
                    if (drone.getDefaultAutonomyMaximum() >= chargingStation.distanceTo(origin))
                        stopsTmp[i++] = chargingStation;
                }

                continue;
            }
            /*
             * Si le drone peut atteindre la prochaine station de recharge l'ajouter comme
             * arrêt, sinon arrêter la plannification
             */
            ChargingStation lastChargingStation = stopsTmp[i - 1];
            if (drone.getAutonomyMaximum() >= lastChargingStation.distanceTo(chargingStation).intValue())
                stopsTmp[i++] = chargingStation;
        }

        // ArrayList<ChargingStation> stops = new
        // ArrayList<>(chargingStationsInArea.size());
        // stops.addAll(Arrays.asList(stopsTmp).subList(0, i));

        stopsTmp = optimizeStops(drone, stopsTmp, i, destination);

        int lastStopCordinatesIndex = stopsTmp.length - 1;
        while (lastStopCordinatesIndex > -1 && stopsTmp[lastStopCordinatesIndex] == null)
            lastStopCordinatesIndex--;

        Coordinates lastStopCordinates = lastStopCordinatesIndex > -1 ? stopsTmp[lastStopCordinatesIndex].getLocation()
                : origin;

        if (drone.getAutonomyMaximum() >= lastStopCordinates.distanceToPoint(destination).intValue()) {
            FlightPlan flightPlan = new FlightPlan();
            flightPlan.setOrigin(origin);
            flightPlan.setDestination(destination);
            if (lastStopCordinatesIndex > -1)
                flightPlan.setRefillStopsFromChargingStations(
                        Arrays.asList(stopsTmp).subList(0, lastStopCordinatesIndex + 1));
            return flightPlan;
        }
        return null;
    }

    Tuple<FlightPlan, Drone> findOptimalPath(List<Warehouse> warehouses, Package paquet) {
        Coordinates dest = paquet.getDestination();
        logger.info(String.format("Computing flight plans for %s warehouses", warehouses.size()));
        List<Tuple<FlightPlan, Drone>> flightPlans = new ArrayList<>();
        for (Warehouse warehouse : warehouses)
            flightPlans.addAll(fightPlansOversAllDrones(warehouse, dest));

        logger.info(String.format("All flight plan found %s", flightPlans.size()));

        Optional<Tuple<FlightPlan, Drone>> optionalFlightPlan = flightPlans.stream()
                .max((o1, o2) -> ((Double) (o2.x.getFlightDistance() - o1.x.getFlightDistance())).intValue());

        var flightPlanDroneTuple = optionalFlightPlan
                .orElseGet(() -> !flightPlans.isEmpty() ? flightPlans.get(0) : null);

        if (flightPlanDroneTuple != null && flightPlanDroneTuple.x != null) {
            logger.info(String.format("Returning best flight plan with distance of %s",
                    flightPlanDroneTuple.x.getFlightDistance()));
        }


        SavedPackage savedPackage = packageDeliveryRepository.findByPackageId(paquet.getId()).orElse(null);

        if (flightPlans.size() == 0) {

            logger.info("Can't do the delivery, saving when a drone is available ...");
            // It means there is no available drone


            if (savedPackage == null) {
                savedPackage = new SavedPackage();

                savedPackage.setOrigin(null); // until Jonas fix this
                savedPackage.setDestination(paquet.getDestination());
                savedPackage.setDimensions(paquet.getDimensions());
                savedPackage.setWeight(paquet.getWeight());
                savedPackage.setPackageId(paquet.getId());
                savedPackage.setTimeStamp(LocalDateTime.now());

                packageDeliveryRepository.save(savedPackage);
            }
        } else {
            if (savedPackage != null) {
                packageDeliveryRepository.deleteByPackageId(paquet.getId());
                logger.info("Delivery Has been deleted !");
            }
        }


        return flightPlanDroneTuple;
    }

    FlightPlan findOptimalPath(Coordinates origin, Coordinates destination, Drone drone) {

        List<ChargingStation> chargingStationsInArea = stationInArea.getStationsInArea(origin, destination);
        logger.info("Computing new flight plan for: " + drone.getName());

        var flightPlan = optimizedFlightPlans(origin, destination, chargingStationsInArea, drone, true);

        logger.info(flightPlan != null
                ? String.format("New Flight plan found with %s stops",
                flightPlan.getRefillStops() != null ? flightPlan.getRefillStops().size() : 0)
                : "No flight plan can be found");

        return flightPlan;
    }

}
