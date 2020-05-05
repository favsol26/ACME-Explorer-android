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

    public static DatabaseReference getTravelById(String travelId) {
        return database.getReference(String.format("user/%s/travel/%s",
                userId, travelId)).getRef();
    }

    public  void saveTrip(Trip trip, DatabaseReference.CompletionListener listener) {
        database.getReference("trips").push().setValue(trip, listener);
    }
}
