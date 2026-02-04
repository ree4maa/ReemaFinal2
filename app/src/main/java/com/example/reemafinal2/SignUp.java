package com.example.reemafinal2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.reemafinal2.data.AppDatabase;
import com.example.reemafinal2.data.MyUser.MyUser;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class SignUp extends AppCompatActivity {
    private TextInputLayout nameLayout;
    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;
    private  TextView nameInput;
    private  TextView emailInput;
    private TextView passwordInput;
    private Button btnSignUp;
    private Button btnLogin;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        // Initialize views
        nameLayout = findViewById(R.id.nameLayout);
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        nameInput = findViewById(R.id.TV_name);
        emailInput = findViewById(R.id.TV_email);
        passwordInput = findViewById(R.id.TV_password);
        btnSignUp = findViewById(R.id.btn_signup);
        btnLogin = findViewById(R.id.btn_Login);

        // Set click listeners
       btnSignUp.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (validateFields()) {
                   // Navigate to login screen
                   Intent intent = new Intent(SignUp.this, MainActivity.class);
                   startActivity(intent);
               }
           }
       });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to login screen
                Intent intent = new Intent(SignUp.this, Login.class);
                startActivity(intent);
            }
        });

        // Handle window insets for edge-to-edge display
      //  ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
         //   Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
         //   v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
          //  return insets;
      //  });
    }

    private boolean validateFields() {
        boolean isValid = true;
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (name.isEmpty()||email.isEmpty()||password.isEmpty()) {
            nameLayout.setError("Name is required");
            isValid = false;
        } else {
            nameLayout.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError("Valid email is required");
            isValid = false;
        } else {
            emailLayout.setError(null);
        }

        if (password.isEmpty() || password.length() < 8) {
            passwordLayout.setError("Password at least 8 characters");
            isValid = false;
        } else {
            passwordLayout.setError(null);
        }
        if (isValid)
        {
            MyUser myUser = new MyUser();
            myUser.setFullName("fullName");
            myUser.setEmail("email");
            myUser.setPassword("password");
            AppDatabase.getDp(this).myUserQuery().insert(myUser);
            Toast.makeText(this,"user registered successfully",Toast.LENGTH_SHORT).show();

            if (isValid) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Toast.makeText(SignUp.this, "Authentication successful.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(SignUp.this, "Authentication failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }

            finish();
        }
        return isValid;
    }



}
