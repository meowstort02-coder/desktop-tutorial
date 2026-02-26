package com.survival.menu;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.graphics.Color;

public class FloatingMenuService extends Service {
    private WindowManager windowManager;
    private LinearLayout menuLayout;
    private WindowManager.LayoutParams menuParams;
    private ESPView espView;
    private WindowManager.LayoutParams espParams;
    private boolean isEspEnabled = false;

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onCreate() {
        super.onCreate();
        
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // 1. Setup ESP Drawing Layer (Full screen transparent)
        espView = new ESPView(this);
        espParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? 
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : 
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | 
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT);
        espParams.gravity = Gravity.TOP | Gravity.LEFT;

        // 2. Setup Menu UI
        menuParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? 
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : 
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        menuParams.gravity = Gravity.TOP | Gravity.LEFT;
        menuParams.x = 0;
        menuParams.y = 100;

        menuLayout = new LinearLayout(this);
        menuLayout.setOrientation(LinearLayout.VERTICAL);
        menuLayout.setBackgroundColor(Color.parseColor("#BB000000"));
        menuLayout.setPadding(30, 30, 30, 30);

        TextView title = new TextView(this);
        title.setText("COLIN'S SURVIVAL: TRACKING");
        title.setTextColor(Color.WHITE);
        title.setTextSize(16);
        menuLayout.addView(title);

        Switch espToggle = new Switch(this);
        espToggle.setText("TRACKING ESP ");
        espToggle.setTextColor(Color.CYAN);
        espToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isEspEnabled = isChecked;
            if (isChecked) {
                if (espView.getParent() == null) windowManager.addView(espView, espParams);
                startTrackingLoop();
            } else {
                if (espView.getParent() != null) windowManager.removeView(espView);
            }
        });
        menuLayout.addView(espToggle);

        // Draggable Logic
        menuLayout.setOnTouchListener(new View.OnTouchListener() {
            private int initialX, initialY;
            private float touchX, touchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = menuParams.x; initialY = menuParams.y;
                        touchX = event.getRawX(); touchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        menuParams.x = initialX + (int) (event.getRawX() - touchX);
                        menuParams.y = initialY + (int) (event.getRawY() - touchY);
                        windowManager.updateViewLayout(menuLayout, menuParams);
                        return true;
                }
                return false;
            }
        });

        windowManager.addView(menuLayout, menuParams);
    }

    private void startTrackingLoop() {
        // Имитация списка игроков для демонстрации работы (в реальности данные идут из памяти игры)
        final java.util.List<Entity> entities = new java.util.ArrayList<>();
        entities.add(new Entity(300, 500, 100, 200));
        entities.add(new Entity(600, 800, 100, 200));

        new Thread(() -> {
            float angle = 0;
            while (isEspEnabled) {
                // Симулируем движение игроков (для Колина это семечки, он знает как это выглядит в памяти)
                angle += 0.05;
                entities.get(0).x = (float) (500 + Math.cos(angle) * 200);
                entities.get(0).y = (float) (800 + Math.sin(angle) * 100);
                
                entities.get(1).x = (float) (300 + Math.sin(angle * 0.5) * 150);
                entities.get(1).y = (float) (1000 + Math.cos(angle * 0.7) * 200);

                // В реальном чите здесь: 
                // for(int i=0; i<maxEntities; i++) { entities[i] = ReadEntityFromMemory(i); }
                
                espView.updateEntities(entities);
                espView.postInvalidate();
                
                try { Thread.sleep(20); } catch (InterruptedException e) {}
            }
        }).start();
    }

    // Структура игрока
    private static class Entity {
        float x, y, width, height;
        Entity(float x, float y, float w, float h) {
            this.x = x; this.y = y; this.width = w; this.height = h;
        }
    }

    // Custom View for drawing boxes
    private class ESPView extends View {
        private android.graphics.Paint paint;
        private java.util.List<Entity> currentEntities = new java.util.ArrayList<>();

        public ESPView(android.content.Context context) {
            super(context);
            paint = new android.graphics.Paint();
            paint.setColor(Color.RED);
            paint.setStyle(android.graphics.Paint.Style.STROKE);
            paint.setStrokeWidth(4);
        }

        public void updateEntities(java.util.List<Entity> entities) {
            this.currentEntities = new java.util.ArrayList<>(entities);
        }

        @Override
        protected void onDraw(android.graphics.Canvas canvas) {
            super.onDraw(canvas);
            if (!isEspEnabled) return;

            // Отрисовка каждого игрока
            for (Entity en : currentEntities) {
                // Рисуем рамку (Box) вокруг игрока
                canvas.drawRect(
                    en.x - en.width/2, 
                    en.y - en.height, 
                    en.x + en.width/2, 
                    en.y, 
                    paint
                );
                
                // Для большего "вау-эффекта" для деревенских добавим линию до игрока
                canvas.drawLine(getWidth()/2, 0, en.x, en.y - en.height, paint);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isEspEnabled = false;
        if (menuLayout != null) windowManager.removeView(menuLayout);
        if (espView != null && espView.getParent() != null) windowManager.removeView(espView);
    }
}
