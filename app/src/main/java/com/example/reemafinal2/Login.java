package com.example.reemafinal2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns; // لاستخدامه في التحقق من صيغة الايميل
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast; // لعرض رسائل للمستخدم

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.reemafinal2.data.AppDatabase;
import com.example.reemafinal2.data.MyUser.MyPlayer;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    // تم تغيير اسماء المتغيرات لتكون أوضح
    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private Button loginButton;
    private TextView signUpText;
    private Button signUpButton;
    private FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        // --- ربط المتغيرات بواجهة المستخدم ---
        // تأكد من أن هذه هي الـ IDs الصحيحة في ملف activity_login.xml
        emailLayout = findViewById(R.id.emailLayout); // يجب إضافة هذا ID في ملف XML
        passwordLayout = findViewById(R.id.passwordLayout); // يجب إضافة هذا ID في ملف XML
        emailEditText = findViewById(R.id.Etext1);
        passwordEditText = findViewById(R.id.Etext2);
        loginButton = findViewById(R.id.btn_log);
        signUpButton = findViewById(R.id.btn_sign);

        if(mAuth.getCurrentUser() != null){
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        // --- التعامل مع ضغطات الأزرار ---
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // استدعاء دالة التحقق قبل محاولة تسجيل الدخول
                if (validateFields()) {
                    String email = emailEditText.getText().toString().trim();
                    String password = passwordEditText.getText().toString().trim();
                    loginWithFirebase(email, password);

                    //الكود يقوم بتسجيل دخول المستخدم عبر Firebase،
                    // وإذا نجح ينقله للصفحة الرئيسية، وإذا فشل يعرض رسالة خطأ.
                }
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void loginWithFirebase(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Login success
                        Toast.makeText(Login.this, "Login successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Login.this, MainActivity.class);
                        startActivity(intent);
                        finish(); // Close login activity so user can't go back
                    } else {
                        // Login failed
                        String error = task.getException() != null ? task.getException().getMessage() : "Authentication failed";
                        Toast.makeText(Login.this, "Error: " + error, Toast.LENGTH_LONG).show();
                        passwordLayout.setError("Incorrect email or password");
                    }
                });
    }
    /**
     * دالة للتحقق من صحة حقول الإدخال (الايميل وكلمة المرور).
     * @return true إذا كانت جميع الحقول صالحة, و false إذا كان هناك خطأ.
     */
    private boolean validateFields() {
        boolean isValid = true;

        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // 1. التحقق من حقل الايميل
        if (email.isEmpty()) {
            emailLayout.setError("Valid email is required");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // التحقق من أن صيغة الايميل صحيحة
            emailLayout.setError("email is not correct");
            isValid = false;
        } else {
            // إذا كان الحقل صحيحاً، قم بإزالة رسالة الخطأ
            emailLayout.setError(null);
        }

        // 2. التحقق من حقل كلمة المرور
        if (password.isEmpty()|| email.isEmpty()) {
            passwordLayout.setError("Valid password is required");
            emailLayout.setError("Valid email is required");
            isValid = false;
        } else if (password.length() < 6) {
            passwordLayout.setError("password at least 8 characters");
            isValid = false;
        } else {
            // إذا كان الحقل صحيحاً، قم بإزالة رسالة الخطأ
            passwordLayout.setError(null);
        }

//todo add firebase auth for sign in
        MyPlayer myPlayer = AppDatabase.getDp(this).myUserQuery().checkEmailPassword(email,password);
        if (myPlayer != null){
            Toast.makeText(this,"login successful",Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(Login.this, MainActivity.class);
//            startActivity(intent);
            return true;
        }

        return isValid;
    }
}
