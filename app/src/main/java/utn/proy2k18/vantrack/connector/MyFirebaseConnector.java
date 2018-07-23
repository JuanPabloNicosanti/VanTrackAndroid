package utn.proy2k18.vantrack.connector;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Map;

public class MyFirebaseConnector {

    final static FirebaseDatabase database = FirebaseDatabase.getInstance();

    public static <T> void post(List<String> children, T object){
        DatabaseReference dbRef = database.getReference();
        if(children != null){
            for(String child : children){
                dbRef = dbRef.child(child);
            }
        }
        dbRef.setValue(object);
    }

    public static void put(List<String> children, Map<String, Object> objects){
        DatabaseReference dbRef = database.getReference();
        if(children != null){
            for(String child : children){
                dbRef = dbRef.child(child);
            }
        }
        dbRef.updateChildren(objects);
    }

    public static DatabaseReference get(List<String> children){
        DatabaseReference dbRef = database.getReference();
        if(children != null){
            for(String child : children){
                dbRef = dbRef.child(child);
            }
        }
        return dbRef;
    }

    public void delete(List<String> children){
        DatabaseReference dbRef = database.getReference();
        if(children != null){
            for(String child : children){
                dbRef = dbRef.child(child);
            }
        }
        dbRef.removeValue();
    }

    public static <T> void post(String path, T object){
        DatabaseReference dbRef = database.getReference(path);
        dbRef.setValue(object);
    }

    public static void put(String path, Map<String, Object> objects){
        DatabaseReference dbRef = database.getReference(path);
        dbRef.updateChildren(objects);
    }

    public static DatabaseReference get(String path ){
        DatabaseReference dbRef = database.getReference(path);
        return dbRef;
    }

    public void delete(String path){
        DatabaseReference dbRef = database.getReference(path);
        dbRef.removeValue();
    }

}
