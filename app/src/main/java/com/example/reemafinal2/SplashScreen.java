package com.example.reemafinal2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ربط الـ Activity بالـ layout الخاص بالـ Splash Screen
        setContentView(R.layout.activity_splash_screen);

        // الحصول على العنصر ImageView من XML لعمل تأثير Fade-In (ظهور تدريجي)
        View imageview6 = findViewById(R.id.imageView6);

        // تنفيذ تأثير الـ Fade-In لمدة 2 ثانية (2000 مللي ثانية)
        imageview6.animate().alpha(1.0f).setDuration(2000);

        // استخدام Handler لتأخير الانتقال للشاشة التالية (Login)
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                // إنشاء Intent للانتقال من SplashScreen إلى Login Activity
                Intent intent = new Intent(SplashScreen.this, Login.class);
                startActivity(intent);
                finish(); // حتى لا يعود المستخدم للسبلاش بالرجوع
            }
        }, 3000); // 3 ثوانٍ


    }
}
//الـ Intent هو كائن (Object) يُستخدم في Android لإرسال نوايا (Intentions) بين مكونات التطبيق، مثل:
//
//الانتقال من Activity إلى Activity أخرى
//
//بدء Service
//
//إرسال بيانات بين المكونات
//
//تشغيل عناصر خارجية مثل المتصفح، الكاميرا، أو الهاتف
//
//باختصار: Intent يخبر Android "أنا أريد أن أفعل شيئًا معينًا".
