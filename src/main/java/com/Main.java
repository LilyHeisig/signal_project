package com;
import com.data_management.DataManager;
import com.cardio_generator.HealthDataSimulator;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("DataStorage")) {
            DataManager.main(new String[]{});
        } else {
            try {
                HealthDataSimulator.main(new String[]{});
            } catch (IOException e) {
                e.printStackTrace();
            }
} }
}