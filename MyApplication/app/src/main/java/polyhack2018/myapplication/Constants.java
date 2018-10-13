package polyhack2018.myapplication;

import android.content.SharedPreferences;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by sam on 10/12/18.
 */
public class Constants {
    public static final String uriBase =
            "https://westcentralus.api.cognitive.microsoft.com/face/v1.0/detect";

    public static final String imageWithFaces =
            "{\"url\":\"https://upload.wikimedia.org/wikipedia/commons/c/c3/RH_Louise_Lillian_Gish.jpg\"}";

    public static final String faceAttributes =
            "age,gender,headPose,smile,facialHair,glasses,emotion,hair,makeup,occlusion,accessories,blur,exposure,noise";
    public static FirebaseDatabase dataBase = FirebaseDatabase.getInstance();;
    public static DatabaseReference ref = dataBase.getReference();
    public static FirebaseStorage storage = FirebaseStorage.getInstance();
    public static final String subscriptionKey = "<Subscription Key>";
    public static StorageReference storageRef = storage.getReferenceFromUrl("gs://polyhack2018.appspot.com");
    public static SharedPreferences pref;
    public static SharedPreferences contacts;
    public static SharedPreferences.Editor getContacts;
    public static SharedPreferences.Editor editor;

}
