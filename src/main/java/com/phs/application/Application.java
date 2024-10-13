package com.phs.application;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })
public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    public static void main(String[] args) {
//        ClassLoader classLoader = Application.class.getClassLoader();
//        File file = new File(Objects.requireNonNull(classLoader.getResource("serviceAccountKey.json").getFile()));
//
//        FileInputStream serviceAccount = null;
//        try {
//            serviceAccount = new FileInputStream(file.getAbsoluteFile());
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//        FirebaseOptions options = null;
//        try {
//            options = new FirebaseOptions.Builder()
//                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//                    .setDatabaseUrl("https://healthy-basis-387202-default-rtdb.asia-southeast1.firebasedatabase.app")
//                    .build();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        FirebaseApp.initializeApp(options);
        SpringApplication.run(Application.class, args);
        if (FirebaseApp.getApps().isEmpty()) {
            try {
                FileInputStream serviceAccount =
                        new FileInputStream("src/main/resources/serviceAccountKey.json"); // Đường dẫn đến file JSON của bạn

                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .setDatabaseUrl("https://healthy-basis-387202-default-rtdb.asia-southeast1.firebasedatabase.app")
                        .build();

                FirebaseApp.initializeApp(options);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            sendDataToFirebase();
            listenToFirebaseDataChanges();
            logger.info("vcl");
        }

    }
    static class ShipperLocation {
        private String shipperId;
        private double latitude;
        private double longitude;

        public ShipperLocation(String shipperId, double latitude, double longitude) {
            this.shipperId = shipperId;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        // Getters và setters (nếu cần)
        public String getShipperId() {
            return shipperId;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }
    }

    private static void sendDataToFirebase() {
        // Lấy reference đến Firebase Realtime Database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("shippers");

        // Tạo một đối tượng dữ liệu với shipperId và tọa độ
        String shipperId = "shipper_123"; // Đây là ID của shipper
        double latitude = 10.776; // Tọa độ latitude
        double longitude = 106.695; // Tọa độ longitude

        ShipperLocation shipperLocation = new ShipperLocation(shipperId, latitude, longitude);

        // Thêm dữ liệu vào database
        databaseReference.child(shipperId).setValueAsync(shipperLocation).addListener(() -> {
            System.out.println("Data sent to Firebase successfully for shipperId: " + shipperId);
        }, Runnable::run);
    }
    private static void listenToFirebaseDataChanges() {
        logger.info("Starting to listen to Firebase data changes.");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("shippers");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                logger.info("Data changed in Firebase.");
                for (DataSnapshot shipperSnapshot : dataSnapshot.getChildren()) {
                    String shipperId = shipperSnapshot.getKey(); // Lấy key của shipper
                    double latitude = shipperSnapshot.child("latitude").getValue(Double.class); // Lấy giá trị latitude
                    double longitude = shipperSnapshot.child("longitude").getValue(Double.class); // Lấy giá trị longitude

                    // Ghi log thông tin shipper
                    logger.info("Shipper ID: {}, Latitude: {}, Longitude: {}", shipperId, latitude, longitude);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                logger.error("Error occurred: {}", databaseError.getMessage());
            }
        });
    }
}
