package com.example.reemafinal2;

import static android.content.ContentValues.TAG;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.reemafinal2.data.AppDatabase;
import com.example.reemafinal2.data.MyUser.MyPlayer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * SignUp: كلاس إدارة إنشاء حسابات المستخدمين الجدد.
 * يربط بين Firebase Authentication و Realtime Database و Room.
 */
public class SignUp extends AppCompatActivity {

    // تعريف عناصر واجهة المستخدم (Material Design)
    private TextInputLayout nameLayout, emailLayout, passwordLayout;
    private TextView nameInput, emailInput, passwordInput;
    private Button btnSignUp, btnLogin;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // تفعيل عرض الحافة إلى الحافة
        setContentView(R.layout.activity_sign_up);

        // 1. ربط العناصر البرمجية بالواجهة
        nameLayout = findViewById(R.id.nameLayout);
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        nameInput = findViewById(R.id.TV_name);
        emailInput = findViewById(R.id.TV_email);
        passwordInput = findViewById(R.id.TV_password);
        btnSignUp = findViewById(R.id.btn_signup);
        btnLogin = findViewById(R.id.btn_Login);

        // 2. برمجة زر "إنشاء حساب"
        btnSignUp.setOnClickListener(v -> {
            if (validateFields()) {
                // ملاحظة: الانتقال يتم برمجياً بعد نجاح Firebase في دالة saveUser
            }
        });

        // 3. الانتقال لشاشة تسجيل الدخول (إذا كان لديه حساب بالفعل)
        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(SignUp.this, Login.class);
            startActivity(intent);
        });
    }

    /**
     * التحقق من صحة المدخلات وحفظ المستخدم محلياً ثم في Firebase.
     */
    private boolean validateFields() {
        boolean isValid = true;
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // فحص حقل الاسم
        if (name.isEmpty()) {
            nameLayout.setError("Name is required");
            isValid = false;
        } else {
            nameLayout.setError(null);
        }

        // فحص صيغة البريد الإلكتروني
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError("Valid email is required");
            isValid = false;
        } else {
            emailLayout.setError(null);
        }

        // فحص قوة كلمة المرور
        if (password.isEmpty() || password.length() < 8) {
            passwordLayout.setError("Password must be at least 8 characters");
            isValid = false;
        } else {
            passwordLayout.setError(null);
        }

        if (isValid) {
            // تجهيز كائن اللاعب
            MyPlayer myPlayer = new MyPlayer();
            myPlayer.setFullName(name);
            myPlayer.setEmail(email);
            myPlayer.setPassword(password);

            // التحقق من أن البريد غير موجود مسبقاً في قاعدة بيانات Room المحلية
            if (AppDatabase.getDp(getApplicationContext()).myUserQuery().checkEmail(email) == null) {
                // حفظ المستخدم محلياً (Offline First)
                AppDatabase.getDp(this).myUserQuery().insert(myPlayer);

                // البدء بعملية التسجيل في Firebase Authentication
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignUp.this, "Auth Succeeded!", Toast.LENGTH_SHORT).show();
                        saveUser(myPlayer); // حفظ بقية البيانات في قاعدة البيانات السحابية
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Failed";
                        Toast.makeText(SignUp.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                emailLayout.setError("Email already exists locally!");
                isValid = false;
            }
        }
        return isValid;
    }

    /**
     * حفظ بيانات المستخدم الإضافية في Firebase Realtime Database.
     */
    public void saveUser(MyPlayer myPlayer) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersRef = database.child("users");

        // توليد معرّف فريد من نوع String وتحويله لـ Long (إذا كان الكيان يتطلب ذلك)
        DatabaseReference newUserRef = usersRef.push();
        String key = newUserRef.getKey();

        if (key != null) {
            // ملاحظة: تعتمد الطريقة هنا على نوع البيانات في MyPlayer
            // newUserRef.setValue(myPlayer)...

            newUserRef.setValue(myPlayer).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // الانتقال للرئيسية بعد اكتمال كل خطوات التسجيل
                    Intent i = new Intent(SignUp.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            });
        }
    }
}



