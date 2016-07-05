package byuie499.auto_nvs;

import com.jjoe64.graphview.series.DataPoint;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Correlation {
    public static int peakThresh = -60;
    public static boolean ASC = true;
    public static boolean DESC = false;

    private static int getFreqIndexCeil (double frequency, double freq_step) {
        return (int)( Math.ceil(freq_step * frequency) );
    }

    private static int getFreqIndexFloor (double frequency, double freq_step) {
        return (int)( Math.floor(freq_step * frequency) );
    }

    private static double interpolateMagnitude(double frequency, double freq_step, DataPoint[] datapoints)
    {
        int ceil = getFreqIndexCeil(frequency, freq_step);
        int floor = getFreqIndexFloor(frequency, freq_step);
        double slope = (datapoints[ceil].getY() - datapoints[floor].getY()) / (datapoints[ceil].getX() - datapoints[floor].getX());
        double mag = datapoints[floor].getY() + slope * (frequency - datapoints[floor].getX());
        return mag;
    }

    public DataPoint[] findPeaks(DataPoint[] data) {
        int max = 0;
        int numPeaks = 0 ;
        int[] indexes = new int[(data.length + 1)/2];
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
            if(i < indexes.length && data[max].getY() > peakThresh)
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

    private List<Map.Entry<String, Integer>> count_occurrence(DataPoint[] peaks, String[] mval) {
        double val;
        String sval;
        HashMap<String, Integer> occurrences = new HashMap<>();
        for (int i = 0; i < peaks.length; i++) {
            for (int j = i + 1; j < peaks.length; j++) {
                val = peaks[j].getX() - peaks[i].getX();
                sval = String.format("%.2fHz", val);
                occurrences.put(sval, occurrences.get(sval) + 1);
            }
        }
        List<Map.Entry<String, Integer>> sorted_occurrences = sortByComparator(occurrences, DESC);

        return sorted_occurrences;
    }

    private static List<Map.Entry<String, Integer>> sortByComparator(Map<String, Integer> unsortMap, final boolean order)
    {
        List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>()
        {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2)
            {
                if (order)
                {
                    return o1.getValue().compareTo(o2.getValue());
                }
                else
                {
                    return o2.getValue().compareTo(o1.getValue());
                }
            }
        });

        return list;
    }

}
