package com.example.reemafinal2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

/**
 * QuestsFragment
 * هذا الـ Fragment مسؤول عن عرض قائمة المهام (Quests) في التطبيق.
 * يمكن إنشاء نسخة جديدة منه باستخدام الدالة newInstance().
 */
public class QuestsFragment extends Fragment {

    // ---------------------------------------------------------------------------------------------
    // الثوابت التي تُستخدم كـ Keys لتمرير البيانات إلى Fragment عند إنشائه
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // ---------------------------------------------------------------------------------------------
    // المتغيرات التي ستستقبل القيم الممررة عند إنشاء Fragment
    private String mParam1;
    private String mParam2;

    // ---------------------------------------------------------------------------------------------
    // Constructor فارغ مطلوب للـ Fragment
    public QuestsFragment() {
        // Required empty public constructor
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * طريقة المصنع (Factory Method) لإنشاء نسخة جديدة من QuestsFragment مع تمرير بارامترات
     * @param param1 المعطى الأول
     * @param param2 المعطى الثاني
     * @return نسخة جديدة من QuestsFragment
     */
    public static QuestsFragment newInstance(String param1, String param2) {
        QuestsFragment fragment = new QuestsFragment();

        // إنشاء Bundle لتخزين البيانات الممررة للـ Fragment
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1); // تخزين param1
        args.putString(ARG_PARAM2, param2); // تخزين param2

        // ربط الـ Bundle بالـ Fragment
        fragment.setArguments(args);

        return fragment;
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // قراءة القيم الممررة عند إنشاء الـ Fragment، إذا كانت موجودة
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // ربط الـ Fragment بالـ layout الخاص به (fragment_quests.xml)
        return inflater.inflate(R.layout.fragment_quests, container, false);
    }
}
//ARG_PARAM1 / ARG_PARAM2 → مفاتيح لتمرير البيانات للـ Fragment.
//
//mParam1 / mParam2 → المتغيرات التي تخزن البيانات الممررة.
//
//Constructor فارغ → مطلوب دائمًا للـ Fragment.
//
//newInstance() → لإنشاء نسخة جديدة من الـ Fragment مع تمرير أي بيانات.
//
//onCreate() → يتم استدعاؤه عند إنشاء Fragment، ويقرأ البيانات الممررة.
//
//onCreateView() → يعرض الـ layout الخاص بالـ Fragment.