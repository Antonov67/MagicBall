package com.example.magicball;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private Button askButton;
    private ImageView ballImageView;
    private String[] answers;
    private Random random = new Random();

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Vibrator vibrator;

    private long lastShakeTime = 0;
    private long lastUpdate = 0;
    private float lastX, lastY, lastZ;

    // Константы для определения встряхивания
    private static final int SHAKE_THRESHOLD = 800; // Минимальная сила встряхивания
    private static final int SHAKE_TIMEOUT = 1000; // Минимальное время между встряхиваниями (мс)

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
        ballImageView = findViewById(R.id.imageView);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (!vibrator.hasVibrator()) {
            Toast.makeText(this, "Вибромотора нет", Toast.LENGTH_SHORT).show();
        }

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

            if (accelerometer != null) {
                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }

        //обработчик нажатия на кнопку
        askButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMagicAnswer();
                startVibration();
                startAnimation();
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void startAnimation() {
        ObjectAnimator rotation = ObjectAnimator.ofFloat(ballImageView, "rotation", 0f, 360f);
        rotation.setDuration(800);
        rotation.setRepeatCount(2);
        rotation.start();
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
                .setPositiveButton("Закрыть", (dialog, which) ->
                {
                    dialog.dismiss();
                });


        //Показываем диалог
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        try {
//            // меняем цвет заголовка
//            int titleId = getResources().getIdentifier("alertTitle", "id", "android");
//            if (titleId > 0) {
//                TextView titleTextView = alertDialog.findViewById(titleId);
//                if (titleTextView != null) {
//                    titleTextView.setTextColor(titleColor);
//                }
//            }

            Button posButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            if (posButton != null) {
                posButton.setBackgroundColor(getColor(titleColor));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long currentTime = System.currentTimeMillis();

            // Проверяем, прошло ли достаточно времени с последнего обновления
            if ((currentTime - lastUpdate) > 100) {
                long timeDifference = currentTime - lastUpdate;
                lastUpdate = currentTime;

                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                // Вычисляем разницу с предыдущими значениями
                float speed = Math.abs(x + y + z - lastX - lastY - lastZ) / timeDifference * 10000;

                // Если скорость превышает порог и прошло достаточно времени с последнего встряхивания
                if (speed > SHAKE_THRESHOLD && (currentTime - lastShakeTime) > SHAKE_TIMEOUT) {
                    lastShakeTime = currentTime;

                    startVibration();

                    // Запускаем в UI потоке
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showMagicAnswer();
                        }
                    });
                }

                // Сохраняем текущие значения для следующего сравнения
                lastX = x;
                lastY = y;
                lastZ = z;
            }
        }
    }

    private void startVibration() {
        if (vibrator == null || !vibrator.hasVibrator()) return;

        int duration = 500; // полсекунды

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            VibrationEffect effect = VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE);
            vibrator.vibrate(effect);
        } else {
            vibrator.vibrate(duration);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager != null && accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }
}