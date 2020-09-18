package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;

import java.util.Date;
import java.util.List;

public interface ServiceShip {

    List <Ship> getListShip (String name,           // получаем лист кораблей
                             String planet,
                             ShipType shipType,
                             Long after,
                             Long before,
                             Boolean isUsed,
                             Double minSpeed,
                             Double maxSpeed,
                             Integer minCrewSize,
                             Integer maxCrewSize,
                             Double minRating,
                             Double maxRating);

    List <Ship> getPage (List <Ship> ships, Integer pageNumber, Integer pageSize);

    Ship getShip (Long id);

    List <Ship> listShipSort (List <Ship> ships, ShipOrder order);

    void deleteShip (Ship ship);

    boolean isValidShip (Ship ship);

    double calculetReting (Double speed, Date prod, Boolean isUsed);

    Ship saveShip (Ship ship);

    Ship upDateShip (Ship oldShip, Ship newShip);
}
