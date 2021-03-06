package com.example.clgproject1.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;

import com.example.clgproject1.Admin.activity.AdminLoginActivity;
import com.example.clgproject1.Admin.activity.AdminMainActivity;
import com.example.clgproject1.Admin.fragments.AdminActivityFragment;
import com.example.clgproject1.Admin.fragments.AdminHomeFragment;
import com.example.clgproject1.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegistrationActivity extends AppCompatActivity {
      FirebaseAuth firebaseAuth;
      ProgressDialog progressDialog;
      EditText NAME,EMAIL,PASSWORD,DESIGNATION,PHONE;
      //TextView Sign_in;
      Spinner ORGANIZATION;
      Button Create_account;
      ActionBar actionBar;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regestration_activity);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Create Account");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        init();
             Create_account.setOnClickListener(new CreateonclickListener());
             OrganizationSpinner();
             //DesignationSpinner();
    }

   // initializing view items
    void init() {
        ActionBar actionBar= getSupportActionBar();
        actionBar.hide();
        PHONE = findViewById(R.id.register_phone);
        Create_account = findViewById(R.id.create_account);
        //Sign_in = findViewById(R.id.sign_login_in);
        DESIGNATION = findViewById(R.id.registration_designation);
        ORGANIZATION = findViewById(R.id.registration_organization);
         NAME = findViewById(R.id.register_name);
         EMAIL = findViewById(R.id.register_email);
         PASSWORD =findViewById(R.id.register_password);
         progressDialog = new ProgressDialog(RegistrationActivity.this);
         progressDialog.setMessage("Register");
         firebaseAuth = FirebaseAuth.getInstance();
    }


    // Spinner to select organization
    void OrganizationSpinner(){
        String  organization [] = { "VI" };
        ArrayAdapter arrayAdapter = new ArrayAdapter(RegistrationActivity.this,R.layout.support_simple_spinner_dropdown_item,organization);
        ORGANIZATION.setAdapter(arrayAdapter);
    }



    // Spinner to designation
    /*void DesignationSpinner(){
     String designation [] = {"Mr. Subhash Tatale","Mr. Atul Kulkarni",};
        ArrayAdapter arrayAdapter = new ArrayAdapter(RegistrationActivity.this,R.layout.support_simple_spinner_dropdown_item,designation);
        DESIGNATION.setAdapter(arrayAdapter);

    }*/


    // create button on click listener
    private  class CreateonclickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String organization = (String) ORGANIZATION.getSelectedItem();
            String phone = PHONE.getText().toString().trim();
            String designation = DESIGNATION.getText().toString().trim();
            String email = EMAIL.getText().toString().trim();
            String password = PASSWORD.getText().toString().trim();
            String name = NAME.getText().toString().trim();
            if (name.isEmpty()){
               NAME.setError("Enter your name");
               NAME.setFocusable(true);
            }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
              EMAIL.setError("INVALID MAIL");
              EMAIL.setFocusable(true);
            }else if (password.length() < 6){
                PASSWORD.setError("PASSWORD SHOULD BE Atleast 6 digits ");
                PASSWORD.setFocusable(true);

            }else  if (designation.isEmpty()) {
                DESIGNATION.setError("Enter Designation");
                DESIGNATION.setFocusable(true);

            }else if (!Patterns.PHONE.matcher(phone).matches() && phone.length() ==10){
                PHONE.setError("Enter Correct Phone Number");
                PHONE.setFocusable(true);
            } else {
                registeruser(name,email,password,organization,designation,phone);

            }

        }
    }

    // Sign in Text on click listener
    private class Signin implements View.OnClickListener{
        @Override
        public void onClick(View v) {
             Intent intent = new Intent(RegistrationActivity.this, AdminMainActivity.class);
             startActivity( intent);
        }
    }


    //Register user method (add to async task class)


    private void registeruser(final String Name, final String Email , String Password, final String Organization, final String Designation , final String Phone){

     runOnUiThread(new Runnable() {
         @Override
         public void run() {


       progressDialog.show();
         }
     });

       firebaseAuth.createUserWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
           @Override
           public void onComplete(@NonNull  Task<AuthResult> task) {
               // if Email is valid
               if (task.isSuccessful()){
                   runOnUiThread(new Runnable() {
                       @Override
                       public void run() {


                    progressDialog.dismiss();
                       }
                   });

                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    String email = user.getEmail();
                    String uid = user.getUid();
                    HashMap<Object ,String> hashMap = new HashMap<>();
                    hashMap.put("email", email);
                    hashMap.put("name",Name);
                    hashMap.put("uid",uid);
                    hashMap.put("phone",Phone);
                    hashMap.put("organization", Organization );
                    hashMap.put("designation", Designation);
                    hashMap.put("image","");
                    FirebaseDatabase database = FirebaseDatabase.getInstance();


                    //creating database node "Users" for storing data
                    DatabaseReference databaseReference = database.getReference("Users");

                    //setting Data
                    databaseReference.child(uid).setValue(hashMap);


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                    Toast.makeText(RegistrationActivity.this, "Registered User " + user.getEmail(), Toast.LENGTH_LONG).show();

                        }
                    });

                    Intent mainIntent = new Intent(RegistrationActivity.this, AdminMainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainIntent);
                    finish();



                  }
             }
         }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull  Exception e) {
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {


               progressDialog.dismiss();
               Toast.makeText(RegistrationActivity.this, "Error Occured", Toast.LENGTH_LONG).show();

                   }
               });
                   }
         });
    }






}
