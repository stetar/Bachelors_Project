package com.example.srikate.ibeacondemo.model;

import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by srikate on 10/9/2017 AD.
 */

public class BeaconDeviceModel implements Comparable {
    private int minor, major, signal, average, smallest, largest, mostFrequent, tx;
    private ArrayList<Integer> measures = new ArrayList<>();

    public BeaconDeviceModel(int major, int minor, int signal, int tx){
        this.minor = minor;
        this.major = major;
        this.signal = signal;
        this.tx = tx;
        average = 0;
        smallest = 0;
        largest = 0;
        mostFrequent = 0;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getSignal() {
        return signal;
    }

    public void setSignal(int signal) {
        this.signal = signal;
    }

    public int getAverage() {return average;}

    public void setAverage(int average) {this.average = average;}

    public int getSmallest() {return smallest;}

    public void setSmallest(int smallest) {this.smallest = smallest;}

    public int getLargest() {return largest;}

    public void setLargest(int largest) {this.largest = largest;}

    public int getMostFrequent() {return mostFrequent;}

    public void setMostFrequent(int mostFrequent) {this.mostFrequent = mostFrequent;}

    public void addToMeasures(int value){measures.add(value);}

    public int getTx() {return tx;}

    public void setTx(int tx) {this.tx = tx;}

    @Override
    public int compareTo(Object b) {
        if (b instanceof BeaconDeviceModel){
            int compareSignal = ((BeaconDeviceModel) b).getSignal();
            return compareSignal - this.getSignal() ;
        }
        return 0;
    }

    public void updateResults(){
        if (measures.size() > 0){
            smallest = Collections.min(measures);
            largest = Collections.max(measures);
            int temp = 0;
            for (Integer i : measures){
                temp = temp + i;
            }
            average = temp / measures.size();
            mostFrequent = mostCommon(measures);
            measures.clear();
        }
    }


    //https://stackoverflow.com/questions/19031213/java-get-most-common-element-in-a-list
    public static <T> T mostCommon(List<T> list) {
        Map<T, Integer> map = new HashMap<>();

        for (T t : list) {
            Integer val = map.get(t);
            map.put(t, val == null ? 1 : val + 1);
        }

        Map.Entry<T, Integer> max = null;

        for (Map.Entry<T, Integer> e : map.entrySet()) {
            if (max == null || e.getValue() > max.getValue())
                max = e;
        }

        return max.getKey();
    }

}
