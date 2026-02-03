package com.ftn.mobile.presentation.view;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.EditText;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.net.Uri;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.ftn.mobile.R;
import com.ftn.mobile.data.remote.ApiProvider;
import com.ftn.mobile.data.remote.Utils;
import com.ftn.mobile.utils.DialogBox;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register extends AppCompatActivity {
    private Uri selectedImageUri;
    private ImageView imgProfile;

    private final ActivityResultLauncher<String> imagePicker =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    imgProfile.setImageURI(uri); // preview
                }
            });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register_scroll_view), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        EditText etEmail = findViewById(R.id.etEmail);
        EditText etPassword = findViewById(R.id.etPassword);
        EditText etConfirmPassword = findViewById(R.id.etConfirmPassword);
        imgProfile = findViewById(R.id.imgProfile);
        Button btnPickPicture = findViewById(R.id.btnPickPicture);

        btnPickPicture.setOnClickListener(v -> imagePicker.launch("image/*"));
        EditText etName = findViewById(R.id.etName);
        EditText etLastName = findViewById(R.id.etLastName);
        EditText etAddress = findViewById(R.id.etAddress);
        EditText etPhoneNumber = findViewById(R.id.etPhoneNumber);
        Button btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();
            String name = etName.getText().toString().trim();
            String lastName = etLastName.getText().toString().trim();
            String address = etAddress.getText().toString().trim();
            String phoneNumber = etPhoneNumber.getText().toString().trim();
            if(email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || name.isEmpty()
            || lastName.isEmpty() || address.isEmpty() || phoneNumber.isEmpty()){
                //Toast.makeText(this, "Fill all fields!", Toast.LENGTH_SHORT).show();
                DialogBox.showDialog(Register.this, "Invalid input", "Please fill all the fields.");
                return;
            }
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                DialogBox.showDialog(Register.this, "Invalid input!", "Please enter a valid email address.");
                return;
            }
            if(password.length() < 8 || confirmPassword.length() < 8){
                DialogBox.showDialog(Register.this, "Invalid input!", "Password is required in format of at least 8 characters!");
                return;
            }
            if(!password.equals(confirmPassword)){
                //Toast.makeText(this, "Password and confirmed password must be equal!", Toast.LENGTH_SHORT).show();
                DialogBox.showDialog(Register.this, "Invalid input", "Password and confirmed password must be equal!");
                return;
            }
            if(!phoneNumber.matches("\\d{11}")){
                DialogBox.showDialog(Register.this, "Invalid input", "Phone number is required in format of 11 digits!");
                return;
            }
            MultipartBody.Part imagePart = null;
            try{
                if (selectedImageUri != null){
                    imagePart = Utils.uriToPart(this, selectedImageUri, "profilePicture");
                }
            }
            catch(Exception e){
                //Toast.makeText(this, "Failed to read image: " + e.getMessage(), Toast.LENGTH_LONG).show();
                DialogBox.showDialog(Register.this, "Failed to read image", e.getMessage());
                return;
            }
            ApiProvider.auth().register(
                    Utils.textPart(email),
                    Utils.textPart(password),
                    Utils.textPart(confirmPassword),
                    Utils.textPart(name),
                    Utils.textPart(lastName),
                    Utils.textPart(address),
                    Utils.textPart(phoneNumber),
                    imagePart
            ).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        //Toast.makeText(Register.this, "Registered! Check email to activate.", Toast.LENGTH_LONG).show();
                        DialogBox.showDialog(Register.this,"Successful registration!", "Registered! Check email to activate.");
                        startActivity(new Intent(Register.this, Login.class));
                    } else {
                        //Toast.makeText(Register.this, "Register failed: " + response.code(), Toast.LENGTH_LONG).show();
                        String serverMsg = Utils.extractErrorMessage(response);
                        String title;
                        if (response.code() == 409) title = "Email already in use";
                        else if (response.code() == 400) title = "Password and confirmed password need to match!";
                        else title = "Registration failed";
                        DialogBox.showDialog(Register.this, title, serverMsg);
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    //Toast.makeText(Register.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    DialogBox.showDialog(Register.this,"Network error.", t.getMessage());
                }
            });
        });
    }
}