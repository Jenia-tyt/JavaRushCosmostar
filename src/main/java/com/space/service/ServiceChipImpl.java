package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.RepositoryShip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ServiceChipImpl implements ServiceShip{

    private RepositoryShip repositoryShip;

    @Autowired
    public ServiceChipImpl(RepositoryShip repositoryShip) {
        super();                                //???????????????
        this.repositoryShip = repositoryShip;
    }

    @Override
    public List<Ship> getListShip(String name, String planet,
                                  ShipType shipType, Long after,
                                  Long before, Boolean isUsed,
                                  Double minSpeed, Double maxSpeed,
                                  Integer minCrewSize, Integer maxCrewSize,
                                  Double minRating, Double maxRating) {
        ArrayList <Ship> ships = new ArrayList<>();
        for (Ship ship : repositoryShip.findAll()){
            if (name != null && !ship.getName().contains(name)) continue;
            if (planet != null && !ship.getPlanet().contains(planet)) continue;
            if (shipType != null && !ship.getShipType().equals(shipType)) continue;
            if (isUsed != null && ship.getUsed() != isUsed) continue;
            if (minCrewSize != null && !(ship.getCrewSize() >= minCrewSize)) continue;
            if (maxCrewSize != null && !(ship.getCrewSize() <= maxCrewSize)) continue;
            if (minRating != null && !(ship.getRating() >= minRating)) continue;
            if (maxRating != null && !(ship.getRating() <= maxRating)) continue;
            if (minSpeed != null && !(ship.getSpeed() >= minSpeed)) continue;
            if (maxSpeed != null && !(ship.getSpeed() <= maxSpeed)) continue;
            if (before != null && ship.getProdDate().after(new Date(before))) continue;
            if (after != null && ship.getProdDate().before(new Date(after))) continue;
            ships.add(ship);
        }
    return ships;
    }

    @Override
    public List<Ship> getPage(List<Ship> ships, Integer pageNumber, Integer pageSize) {
        final int page = pageNumber == null ? 0 : pageNumber;
        final int size = pageSize == null ? 3 : pageSize;
        final int from = page * size; //
        int to = from + size;
        if (to > ships.size()) to = ships.size();
        return ships.subList(from, to);
    }

    @Override
    public List<Ship> listShipSort(List<Ship> ships, ShipOrder order) {
        if (order != null) {
            ships.sort((ship1, ship2) -> {
                switch (order){
                    case ID: return ship1.getId().compareTo(ship2.getId());
                    case RATING: return ship1.getRating().compareTo(ship2.getRating());
                    case SPEED: return ship1.getSpeed().compareTo(ship2.getSpeed());
                    case DATE: return ship1.getProdDate().compareTo(ship2.getProdDate());
                    default:return 0;
                }
            });
        }
        return ships;
    }

    @Override
    public Ship getShip(Long id) {
        return repositoryShip.findById(id).orElse(null);
    }

    @Override
    public void deleteShip(Ship ship) {
        repositoryShip.delete(ship);
    }

    @Override
    public boolean isValidShip(Ship ship) {
        return ship != null
                && validString(ship.getName()) && validString(ship.getPlanet())
                && validSpeed(ship.getSpeed())
                && validSizeCrew(ship.getCrewSize())
                && validData(ship.getProdDate())
                ;
    }

    @Override
    public double calculetReting(Double speed, Date prod, Boolean isUsed) {
        double k = (isUsed ? 0.5 : 1);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(prod);
        int dateProd = calendar.get(Calendar.YEAR);
        double tempReting = (80 * speed * k) / (3019 - dateProd + 1);
        return round(tempReting);
    }

    @Override
    public Ship saveShip(Ship ship) {
       return repositoryShip.save(ship);
    }

    @Override
    public Ship upDateShip(Ship oldShip, Ship newShip) throws IllegalArgumentException{
        boolean shouldChangeRating = false;

        String name = newShip.getName();
        if (name != null) {
            if (validString(name)) {
                oldShip.setName(name);
            } else {
                throw new IllegalArgumentException();
            }
        }

        String planet = newShip.getPlanet();
        if (planet != null) {
            if (validString(planet)) {
                oldShip.setPlanet(planet);
            } else {
                throw new IllegalArgumentException();
            }
        }

        if (newShip.getShipType() != null){
            oldShip.setShipType(newShip.getShipType());
        }

        Date prodDate = newShip.getProdDate();
        if (prodDate != null) {
            if (validData(prodDate)) {
                oldShip.setProdDate(prodDate);
                shouldChangeRating = true;
            } else {
                throw new IllegalArgumentException();
            }
        }

        if (newShip.getUsed() != null){
            oldShip.setUsed(newShip.getUsed());
            shouldChangeRating = true;
        }

        Double speed = newShip.getSpeed();
        if (speed != null) {
            if (validSpeed(speed)) {
                oldShip.setSpeed(speed);
                shouldChangeRating = true;
            } else {
                throw new IllegalArgumentException();
            }
        }

        Integer size = newShip.getCrewSize();
        if (size != null) {
            if (validSizeCrew(size)) {
                oldShip.setCrewSize(size);
            } else {
                throw new IllegalArgumentException();
            }
        }

        if (shouldChangeRating) {
            final double rating = calculetReting(oldShip.getSpeed(), oldShip.getProdDate(), oldShip.getUsed());
            oldShip.setRating(rating);
        }
        repositoryShip.save(oldShip);
        return oldShip;
    }



    /*
     + Имя и название планеты длиной не более 50 символов И НЕ ПУСТОЕ
     + Дата выпуска в пределе 2800 - 3019
     + Скорость корабля в диапазоне 0,01 - 0,99
     + Размер команды 1 - 9999
     + Рейтинг округлить до сотых
     */

    private boolean validString (String str){
        int size = 50;
        return str != null && !str.isEmpty() && str.length() <= size;
    }

    private boolean validSpeed (Double speed){
        return speed != null && speed >= 0.01 && speed <= 0.99;
    }

    private boolean validSizeCrew (Integer sizeCrew){
        return sizeCrew != null && sizeCrew > 0 && sizeCrew < 10000;
    }

    private double round (double volue){
        return Math.round(volue * 100) / 100D;
    }

    private boolean validData (Date prodDate){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2800);
        Date start = calendar.getTime();
        calendar.set(Calendar.YEAR, 3019);
        Date end = calendar.getTime();
        return prodDate.after(start) && prodDate.before(end);
    }
}
