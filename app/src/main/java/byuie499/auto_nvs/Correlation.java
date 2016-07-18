package byuie499.auto_nvs;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Correlation {
    public static double peakThresh = -55;
    public static double peakTolerance = 2;
    public HashMap<String, Integer> occurrences;

    public DataPoint[] findPeaks(DataPoint[] data) {
        int max = 0;
        int numPeaks = 0 ;
        int[] indexes = new int[data.length];
        DataPoint[] peaks;
        // Checking for the conditions of the peaks
        // if the change is ocurring, then graph.
        for(int i = 1; i < data.length; i++) {
            while(i < data.length && data[i].getY() < data[i-1].getY()){
                i++;
            }
            while(i < data.length && data[i].getY() > data[i-1].getY()){
                max = i++;
            }
            if(numPeaks < indexes.length && data[max].getY() > peakThresh)
                indexes[numPeaks++]= max;
        }

        if(numPeaks > 0) {
            peaks = new DataPoint[numPeaks];
            for (int i = 0; i < numPeaks; i++)
                peaks[i] = data[indexes[i]];
        }
        else
            peaks = new DataPoint[]{};
        return peaks;
    }

    public void count_occurrence(DataPoint[] peaks) {
        double val;
        String sval;
        int occ;
        occurrences = new HashMap<>();
        for (int i = 0; i < peaks.length; i++) {
            for (int j = i + 1; j < peaks.length; j++) {
                val = peaks[j].getX() - peaks[i].getX();
                sval = String.format("%.0f", val);
                occ = (occurrences.get(sval) == null ? 1 : occurrences.get(sval) + 1);
                occurrences.put(sval, occ);
            }
        }
    }

    public DataPoint[] markPeaks(DataPoint[] peaks, double freq, String tag){
        double secondOrder = freq * 2;
        double thirdOrder = freq * 3;
        double fourthOrder = freq * 4;
        double x;
        String xp;
        DataPoint[] temp = new DataPoint[] {
                new DataPoint(freq, -200),
                new DataPoint(secondOrder, -200),
                new DataPoint(thirdOrder, -200),
                new DataPoint(fourthOrder, -200),
        };

        for(int i = 0; i < peaks.length; i++){
            x = peaks[i].getX();
            xp = String.format("%.0f", x * 2);
            if (x > (freq - peakTolerance) && x < (freq + peakTolerance)) {
                if (temp[0] != null) {
                    if (peaks[i].getY() > temp[0].getY()) {
                        temp[0] = peaks[i];
                        temp[0].tag = "1" + tag;
                        temp[0].occ = occurrences.containsKey(xp);
                    }
                } else {
                    temp[0] = peaks[i];
                    temp[0].tag = "1" + tag;
                    temp[0].occ = occurrences.containsKey(xp);
                }
            } else if (x > (secondOrder - peakTolerance) && x < (secondOrder + peakTolerance)) {
                if (temp[1] != null) {
                    if (peaks[i].getY() > temp[1].getY()) {
                        temp[1] = peaks[i];
                        temp[1].tag = "2" + tag;
                        temp[1].occ = occurrences.containsKey(xp);
                    }
                } else {
                    temp[1] = peaks[i];
                    temp[1].tag = "2" + tag;
                    temp[1].occ = occurrences.containsKey(xp);
                }
            } else if (x > (thirdOrder - peakTolerance) && x < (thirdOrder + peakTolerance)) {
                if (temp[2] != null) {
                    if (peaks[i].getY() > temp[2].getY()) {
                        temp[2] = peaks[i];
                        temp[2].tag = "3" + tag;
                        temp[2].occ = occurrences.containsKey(xp);
                    }
                } else {
                    temp[2] = peaks[i];
                    temp[2].tag = "3" + tag;
                    temp[2].occ = occurrences.containsKey(xp);
                }
            } else if (x > (fourthOrder - peakTolerance) && x < (fourthOrder + peakTolerance)) {
                if (temp[3] != null) {
                    if (peaks[i].getY() > temp[3].getY()) {
                        temp[3] = peaks[i];
                        temp[3].tag = "4" + tag;
                        temp[3].occ = occurrences.containsKey(xp);
                    }
                } else {
                    temp[3] = peaks[i];
                    temp[3].tag = "4" + tag;
                    temp[3].occ = occurrences.containsKey(xp);
                }
            }
        }
        return temp;
    }

}
