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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.reemafinal2.data.AppDatabase;
import com.example.reemafinal2.data.MyUser.MyUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
            myUser.setFullName(name);
            myUser.setEmail(email);
            myUser.setPassword(password);
            AppDatabase.getDp(this).myUserQuery().insert(myUser);
            Toast.makeText(this,"user registered successfully",Toast.LENGTH_SHORT).show();

            if (isValid) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUp.this, "Authentication successful.",
                                    Toast.LENGTH_SHORT).show();
                            Intent i=new Intent(SignUp.this,MainActivity.class);
                            startActivity(i);
                            finish();
                        }
                        else {
                            Toast.makeText(SignUp.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
                }
            }
        return isValid;
        }

    public void saveUser(MyUser myUser) {// الحصول على مرجع إلى عقدة "users" في قاعدة البيانات

        // تهيئة Firebase Realtime Database    //مؤشر لقاعدة البيانات
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    // ‏مؤشر لجدول المستعملين
        DatabaseReference usersRef = database.child("users");
        // إنشاء مفتاح فريد للمستخدم الجديد
        DatabaseReference newUserRef = usersRef.push();
        // تعيين معرف المستخدم في كائن MyUser
        myUser.setKeyid(Long.parseLong(newUserRef.getKey()));
        // حفظ بيانات المستخدم في قاعدة البيانات
        //اضافة كائن "لمجموعة" المستعملين ومعالج حدث لفحص نجاح المطلوب
       // معالج حدث لفحص هل تم المطلوب من قاعدة البيانات //
        newUserRef.setValue(myUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(SignUp.this, "Succeeded to add User",  Toast.LENGTH_SHORT).show();
                        finish();




                        // تم حفظ البيانات بنجاح
                        Log.d(TAG, "تم حفظ المستخدم بنجاح: " + myUser.getKeyid());
                        // تحديث واجهة المستخدم أو تنفيذ إجراءات أخرى
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // معالجة الأخطاء
                        Log.e(TAG, "خطأ في حفظ المستخدم: " + e.getMessage(), e);
                        Toast.makeText(SignUp.this, "Failed to add User", Toast.LENGTH_SHORT).show();
                        // عرض رسالة خطأ للمستخدم
                    }
                });
        EditText nameEditText = findViewById(R.id.TV_name);
        EditText emailEditText = findViewById(R.id.TV_email);
        EditText passwrdEditText = findViewById(R.id.TV_password);
        Button addButton = findViewById(R.id.btn_signup);


        addButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString();
            String email = emailEditText.getText().toString();


            if (!name.isEmpty() && !email.isEmpty()) {
                MyUser newUser = new MyUser();
                saveUser(newUser);


                // مسح حقول الإدخال
                nameEditText.setText("");
                emailEditText.setText("");
            } else {
                Log.w(TAG, "الرجاء إدخال الاسم والبريد الإلكتروني.");
            }
        });


    }
    }




