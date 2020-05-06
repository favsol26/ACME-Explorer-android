package us.master.acme_explorer.common;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import us.master.acme_explorer.entity.Trip;

public class FirebaseDatabaseService {
    private static String userId;
    private static FirebaseDatabaseService service;
    private static FirebaseDatabase database;

    public static FirebaseDatabaseService getServiceInstance() {
        if (service == null || database == null) {
            service = new FirebaseDatabaseService();
            database = FirebaseDatabase.getInstance();
            database.setPersistenceEnabled(true);
        }
        if (userId == null || userId.isEmpty()) {
            userId = FirebaseAuth.getInstance().getCurrentUser() != null
                    ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                    : "";
        }
        return service;
    }

    public void saveTrip(Trip trip, DatabaseReference.CompletionListener listener) {
        database.getReference("trips").push().setValue(trip, listener);
    }

    public DatabaseReference getTrips() {
        return database.getReference("trips").getRef();
    }

    public DatabaseReference getTravelById(String travelId) {
        return database.getReference(String.format("trips/%s", travelId)).getRef();
    }

    public DatabaseReference updateTravelById(String travelId) {
        //TODO complete update method
        return database.getReference(String.format("trips/%s", travelId)).getRef();
    }

    public DatabaseReference deleteTravelById(String travelId) {
        //TODO complete delete method
        return database.getReference(String.format("trips/%s", travelId)).getRef();
    }
}