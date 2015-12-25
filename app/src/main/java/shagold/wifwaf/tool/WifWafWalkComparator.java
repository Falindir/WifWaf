package shagold.wifwaf.tool;

import java.util.ArrayList;
import java.util.List;

import shagold.wifwaf.dataBase.Dog;
import shagold.wifwaf.dataBase.Walk;

public class WifWafWalkComparator {

    private Walk walk;

    public WifWafWalkComparator(Walk walk) {
        this.walk = walk;
    }

    public boolean isSameWalk(Walk other) {

        if(!walk.getTitle().equals(other.getTitle()))
            return false;

        if(!walk.getDescription().equals(other.getDescription()))
            return false;

        if(!walk.getCity().equals(other.getCity()))
            return false;

        if(!walk.getDeparture().equals(other.getDeparture()))
            return false;

        if(walk.getDogs().size() != other.getDogs().size())
            return false;

        List<Integer> dogWalkID = new ArrayList<>();
        for(Dog dog : walk.getDogs()) {
            dogWalkID.add(dog.getIdDog());
        }

        List<Integer> dogOtherID = new ArrayList<>();
        for(Dog dog : other.getDogs()) {
            dogOtherID.add(dog.getIdDog());
        }

        for(Integer id : dogWalkID) {
            if(!dogOtherID.contains(id)) {
                return false;
            }
        }

        for(Integer id : dogOtherID) {
            if(!dogWalkID.contains(id)) {
                return false;
            }
        }

        return true;
    }
}
