package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ServiceShip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
public class ShipController {

    private ServiceShip serviceShip;

    public ShipController() {
    }

    @Autowired
    public ShipController(ServiceShip serviceShip) {
         this.serviceShip = serviceShip;
    }

    @RequestMapping (path = "/rest/ships", method = RequestMethod.GET)
    public List <Ship> getAllShips(
        @RequestParam (value = "name", required = false) String name,
        @RequestParam (value = "planet", required = false) String planet,
        @RequestParam (value = "shipType", required = false) ShipType shipType,
        @RequestParam (value = "after", required = false) Long after,
        @RequestParam (value = "before", required = false) Long before,
        @RequestParam (value = "isUsed", required = false) Boolean isUsed,
        @RequestParam (value = "minSpeed", required = false) Double minSpeed,
        @RequestParam (value = "maxSpeed", required = false) Double maxSpeed,
        @RequestParam (value = "minCrewSize", required = false) Integer minCrewSize,
        @RequestParam (value = "maxCrewSize", required = false) Integer maxCrewSize,
        @RequestParam (value = "minRating", required = false) Double minRating,
        @RequestParam (value = "maxRating", required = false) Double maxRating,
        @RequestParam (value = "order", required = false) ShipOrder order,
        @RequestParam (value = "pageNumber", required = false) Integer pageNumber,
        @RequestParam (value = "pageSize", required = false) Integer pageSize
    ){
        List <Ship> ships = serviceShip.getListShip(name, planet,
                shipType, after,
                before, isUsed,
                minSpeed, maxSpeed,
                minCrewSize, maxCrewSize,
                minRating, maxRating);

        List <Ship> sortShips = serviceShip.listShipSort(ships, order);

        List <Ship> pagShips = serviceShip.getPage(sortShips, pageNumber, pageSize);
        return pagShips;
    }

    @RequestMapping (path = "rest/ships/count", method = RequestMethod.GET)
    public Integer getCountShip(
            @RequestParam (value = "name", required = false) String name,
            @RequestParam (value = "planet", required = false) String planet,
            @RequestParam (value = "shipType", required = false) ShipType shipType,
            @RequestParam (value = "after", required = false) Long after,
            @RequestParam (value = "before", required = false) Long before,
            @RequestParam (value = "isUsed", required = false) Boolean isUse,
            @RequestParam (value = "minSpeed", required = false) Double minSpeed,
            @RequestParam (value = "maxSpeed", required = false) Double maxSpeed,
            @RequestParam (value = "minCrewSize", required = false) Integer minCrewSize,
            @RequestParam (value = "maxCrewSize", required = false) Integer maxCrewSize,
            @RequestParam (value = "minRating", required = false) Double minRating,
            @RequestParam (value = "maxRating", required = false)Double maxRating) {
        return serviceShip.getListShip(name, planet, shipType, after, before, isUse, minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating).size();
    }

    @RequestMapping (path = "rest/ships", method = RequestMethod.POST)
    public ResponseEntity <Ship> createShip (@RequestBody Ship ship){
        if (!serviceShip.isValidShip(ship)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (ship.getUsed() == null) ship.setUsed(false);
        double reting = serviceShip.calculetReting(ship.getSpeed(), ship.getProdDate(), ship.getUsed());
        ship.setRating(reting);
        return new ResponseEntity<>(serviceShip.saveShip(ship), HttpStatus.OK);
    }

    @RequestMapping (path = "rest/ships/{id}", method = RequestMethod.GET)
    public ResponseEntity<Ship> getShip (@PathVariable (value = "id") String idPathShip){
        Long idShip = convertId(idPathShip);
        if (idShip == null || idShip <= 0) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Ship ship = serviceShip.getShip(idShip);
        if (ship == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(ship, HttpStatus.OK);
    }

    @RequestMapping (path = "rest/ships/{id}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Ship> upDateShip(@PathVariable (value = "id") String idPathShip, @RequestBody Ship ship){
        ResponseEntity <Ship> entity = getShip(idPathShip);
        Ship saveShip = entity.getBody();
        if (saveShip == null) return entity;
        Ship result;
        try {
            result = serviceShip.upDateShip(saveShip, ship);
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping (path = "rest/ships/{id}", method = RequestMethod.DELETE)
    public ResponseEntity <Ship> deleteShip (@PathVariable (value = "id") String idPathShip){
        ResponseEntity <Ship> entity = getShip(idPathShip);
        Ship ship = entity.getBody();
        if (ship == null){
            return entity;
        }
        serviceShip.deleteShip(ship);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //========================================================
    private Long convertId (String idStr){
        if (idStr == null){
            return null;
        } else try {
            return Long.parseLong(idStr);
        } catch (NumberFormatException e){
            return null;
        }
    }
}

