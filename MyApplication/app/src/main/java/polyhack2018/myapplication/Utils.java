package polyhack2018.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import android.support.annotation.NonNull;
import android.util.Log;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.URI;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.util.EntityUtils;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;


/**
 * Created by sam on 10/12/18.
 */
public class Utils {

    public static void imageUpload(Uri imagUri, String name, final boolean isForContact, String contactName, String contactPhone){
        UploadTask uploadTask;

        if (imagUri != null) {

            if(isForContact){
                uploadTask = Constants.storageRef.child(name).child("Contacts").child(contactName + "_" + contactPhone).putFile(imagUri);
            }else{
                uploadTask = Constants.storageRef.child(name).child("Shared").child("12345").putFile(imagUri);
            }

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    Uri downloadUri = taskSnapshot.getMetadata().getDownloadUrl();
                    String path = downloadUri.toString();
                    checkForSimilarFaces(path);
                    Log.e("UPLOAD", "SUCCESS");
                }
            });

        }
    }

    public static JSONObject getFaceData(String imageURL){
        JSONObject faceData = new JSONObject();
        HttpClient httpclient = new DefaultHttpClient();

        try
        {

            URIBuilder builder = new URIBuilder(Constants.uriBase);

            // Request parameters. All of them are optional.
            builder.setParameter("returnFaceId", "true");
            builder.setParameter("returnFaceLandmarks", "false");
            builder.setParameter("returnFaceAttributes", Constants.faceAttributes);

            // Prepare the URI for the REST API call.
            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);

            // Request headers.
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Ocp-Apim-Subscription-Key", Constants.subscriptionKey);

            // Request body.
            StringEntity reqEntity = new StringEntity(Constants.imageWithFaces);
            request.setEntity(reqEntity);

            // Execute the REST API call and get the response entity.
            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null)
            {
                // Format and display the JSON response.
                System.out.println("REST Response:\n");

                String jsonString = EntityUtils.toString(entity).trim();
                if (jsonString.charAt(0) == '[') {
                    JSONArray jsonArray = new JSONArray(jsonString);
                    System.out.println(jsonArray.toString(2));
                }
                else if (jsonString.charAt(0) == '{') {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    faceData = jsonObject;
                    System.out.println(jsonObject.toString(2));
                } else {
                    System.out.println(jsonString);
                }
            }
        }
        catch (Exception e)
        {
            // Display error message.
            System.out.println(e.getMessage());
        }

        return faceData;
    }


    public static void checkForSimilarFaces(String shareImageURL) {
        final ArrayList<String> matchingFriends = new ArrayList<>();
        Map<String,?> keys = Constants.contacts.getAll();
        final JSONObject shareImageJSON = getFaceData(shareImageURL);
        for(final Map.Entry<String,?> entry : keys.entrySet()){
            Constants.storageRef.child(Constants.pref.getString("USER", "") + "/Contacts" + entry.getValue().toString()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    JSONObject contactPerson = getFaceData(uri.toString());
                    if (compareJSONs(shareImageJSON, contactPerson)) {
                        matchingFriends.add(entry.getValue().toString());
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        }

    }

    public static boolean compareJSONs(JSONObject object1, JSONObject object2){
        //COMPARE AND SEE HOW MUCH TWO JSONS SHARE SIMILARITIES
        boolean doesMatch = false;

        //todo Iterate through each nested JSON Object inside object1 and compare with object2
        //todo if one matches/is most similar to object2, change value of doesMatch to true

        return doesMatch;


    }


}
