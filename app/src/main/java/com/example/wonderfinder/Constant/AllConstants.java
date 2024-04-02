package com.example.wonderfinder.Constant;

import com.example.wonderfinder.LandmarkModel;
import com.example.wonderfinder.R;

import java.util.ArrayList;
import java.util.Arrays;

public interface AllConstants {

    //Declare all variables as private variables with their respective types
    int LOCATION_REQUEST_CODE = 2000;

    //Task 3 code
    int STORAGE_REQUEST_CODE = 1000;
    String IMAGE_PATH = "/Profile/image_profile.jpg";

    //Array of all the types of landmarks the user can select as preferred
    ArrayList<LandmarkModel> landmarksName = new ArrayList<>(
            Arrays.asList(
                    new LandmarkModel(1, R.drawable.ic_restaurant, "Restaurant", "restaurant"),
                    new LandmarkModel(2, R.drawable.ic_atm, "ATM", "atm"),
                    new LandmarkModel(3, R.drawable.ic_petrol_station, "Gas", "gas_station"),
                    new LandmarkModel(4, R.drawable.ic_groceries, "Groceries", "supermarket"),
                    new LandmarkModel(5, R.drawable.ic_hotel, "Hotels", "hotel"),
                    new LandmarkModel(6, R.drawable.ic_pharmacy, "Pharmacies", "pharmacy"),
                    new LandmarkModel(7, R.drawable.ic_hospital, "Hospitals", "hospital"),
                    new LandmarkModel(8, R.drawable.ic_car_wash, "Car Wash", "car_wash"),
                    new LandmarkModel(9, R.drawable.ic_beauty_salon, "Beauty Salons", "beauty_salon")
            )
    );
}
