package com.example.magicball;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private Button askButton;
    private String[] answers;
    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        //инициализация массива ответов
        answers = new String[]{
                "Бесспорно",
                "Предрешено",
                "Никаких сомнений",
                "Определённо да",
                "Можешь быть уверен в этом",
                "Мне кажется — да",
                "Вероятнее всего",
                "Хорошие перспективы",
                "Знаки говорят — да",
                "Да",
                "Пока не ясно, попробуй снова",
                "Спроси позже",
                "Лучше не рассказывать",
                "Сейчас нельзя предсказать",
                "Сконцентрируйся и спроси опять",
                "Даже не думай",
                "Мой ответ — нет",
                "По моим данным — нет",
                "Перспективы не очень хорошие",
                "Весьма сомнительно"
        };

        askButton = findViewById(R.id.askButton);

        //обработчик нажатия на кнопку
        askButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMagicAnswer();
            }
        });



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void showMagicAnswer() {
        //выбор случайного ответа
        int index = random.nextInt(answers.length);
        String answer = answers[index];

        //меняем цвет заголовка
        int titleColor = android.R.color.holo_green_dark;
        if (index >= 10 && index <= 14) {
            titleColor = android.R.color.holo_orange_dark;
        } else if (index >= 15) {
            titleColor = android.R.color.holo_red_dark;
        }

        //Создаем диалог
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ответ магического шара")
                .setMessage(answer)
                .setPositiveButton("Спросить снова", (dialog, which) ->
                {
                    dialog.dismiss();
                })
                .setNegativeButton("Закрыть",(dialog, which) ->
        {
            dialog.dismiss();
        });

        //Показываем диалог
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        try {
            // меняем цвет заголовка
            int titleId = getResources().getIdentifier("alertTitle", "id", "android");
            if (titleId > 0) {
                android.widget.TextView titleTextView = alertDialog.findViewById(titleId);
                if (titleTextView != null){
                    titleTextView.setTextColor(getResources().getColor(titleColor));
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }



    }
}