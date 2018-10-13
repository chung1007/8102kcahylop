package polyhack2018.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    TextView text;
    Button button;
    Boolean clicked;
    EditText nameInput;
    EditText phoneInput;

    Boolean isForContact;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signInAnonymously();
        initiate_homepage();

        isForContact = true;
        clicked = false;

        Constants.pref = getSharedPreferences("my pref", Context.MODE_PRIVATE);
        Constants.contacts = getSharedPreferences("CONTACTS", Context.MODE_PRIVATE);
        Constants.editor = Constants.pref.edit();

        Constants.getContacts = Constants.contacts.edit();
        if(!Constants.pref.contains("USER")) {
            Constants.editor.putString("USER", generateRandomID()).commit();
        }

    }

    public void initiate_homepage(){

        nameInput = (EditText)findViewById(R.id.name_input);
        phoneInput = (EditText)findViewById(R.id.phone_input);

    }

    public String generateRandomID(){
        String random  = UUID.randomUUID().toString();
        return random;
    }

    public void camClicked(View view){
        if(nameInput.getText().toString().equals("") || phoneInput.getText().toString().equals("") ){
            Toast.makeText(this, "Contact Info Needed!", Toast.LENGTH_SHORT).show();
        }else{
            Constants.getContacts.putString(nameInput.getText().toString() + "_" + phoneInput.getText().toString(), "");
            isForContact = true;
            dispatchTakePictureIntent();
        }


    }

    private void dispatchTakePictureIntent() {
        /*Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }*/
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, REQUEST_IMAGE_CAPTURE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            if(data.getData() == null){
                Log.e("???????", "OH NO");
            }
            Uri pickedImage = data.getData();
            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
            String user = Constants.pref.getString("USER", "");
            Utils.imageUpload(pickedImage, user, isForContact, nameInput.getText().toString(), phoneInput.getText().toString());
            Log.e("REACHED", "HERE");
            cursor.close();
        }
    }

    public void share(View view){
        dispatchTakePictureIntent();

    }

    public void signInAnonymously() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // do your stuff
        } else {
            mAuth.signInAnonymously().addOnSuccessListener(this, new  OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    // do your stuff
                }
            })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.e("UPLOAD", "failed");
                        }
                    });
        }

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
