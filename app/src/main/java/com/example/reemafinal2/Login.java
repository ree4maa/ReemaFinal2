package com.example.reemafinal2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
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
import com.example.reemafinal2.data.MyUser.MyPlayer;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Login Activity: الشاشة المسؤولة عن تسجيل دخول المستخدمين.
 * تدعم التحقق من البيانات عبر Firebase وعرض الأخطاء بشكل تفاعلي.
 */
public class Login extends AppCompatActivity {

    // تعريف عناصر واجهة المستخدم باستخدام Material Design
    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private Button loginButton;
    private Button signUpButton;

    // مرجع المصادقة من Firebase
    private FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // تفعيل العرض ملء الشاشة
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        // 1. ربط المتغيرات بالمعرفات (IDs) في ملف XML
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        emailEditText = findViewById(R.id.Etext1);
        passwordEditText = findViewById(R.id.Etext2);
        loginButton = findViewById(R.id.btn_log);
        signUpButton = findViewById(R.id.btn_sign);

        // 2. فحص الحالة: إذا كان المستخدم مسجلاً مسبقاً، لا داعي لطلب الدخول ثانية
        if(mAuth.getCurrentUser() != null){
            navigateToMain();
        }

        // 3. مستمع حدث النقر لزر تسجيل الدخول
        loginButton.setOnClickListener(v -> {
            if (validateFields()) { // استدعاء دالة التحقق
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                loginWithFirebase(email, password);
            }
        });

        // 4. الانتقال لشاشة إنشاء حساب جديد
        signUpButton.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, SignUp.class);
            startActivity(intent);
        });

        // ضبط هوامش النظام (StatusBar & NavigationBar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * تنفيذ عملية المصادقة عبر سيرفر Firebase.
     */
    private void loginWithFirebase(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Login.this, "Login successful ✨", Toast.LENGTH_SHORT).show();
                        navigateToMain();
                    } else {
                        // عرض رسالة الخطأ القادمة من السيرفر
                        String error = task.getException() != null ? task.getException().getMessage() : "Authentication failed";
                        Toast.makeText(Login.this, "Error: " + error, Toast.LENGTH_LONG).show();
                        passwordLayout.setError("Incorrect email or password");
                    }
                });
    }

    /**
     * فحص مدخلات المستخدم والتأكد من مطابقتها للمعايير قبل إرسالها.
     */
    private boolean validateFields() {
        boolean isValid = true;
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // التحقق من صحة البريد الإلكتروني
        if (email.isEmpty()) {
            emailLayout.setError("Valid email is required");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError("Format is incorrect (e.g. name@example.com)");
            isValid = false;
        } else {
            emailLayout.setError(null);
        }

        // التحقق من قوة كلمة المرور
        if (password.isEmpty()) {
            passwordLayout.setError("Password is required");
            isValid = false;
        } else if (password.length() < 6) {
            passwordLayout.setError("Password must be at least 6 characters");
            isValid = false;
        } else {
            passwordLayout.setError(null);
        }

        // فحص قاعدة البيانات المحلية (Room) لضمان مزامنة المستخدم
        MyPlayer myPlayer = AppDatabase.getDp(this).myUserQuery().checkEmailPassword(email, password);
        if (myPlayer != null) {
            return true;
        }

        return isValid;
    }

    /**
     * دالة مساعدة للانتقال للشاشة الرئيسية وإغلاق شاشة الدخول.
     */
    private void navigateToMain() {
        Intent intent = new Intent(Login.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}