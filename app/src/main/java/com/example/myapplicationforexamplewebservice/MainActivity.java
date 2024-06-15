package com.example.myapplicationforexamplewebservice;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "web";
    private static final int NOTIFICATION_ID = 1;
    private static final int REQUEST_CODE_NOTIFICATION_PERMISSION = 100;

    Button btn;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE_NOTIFICATION_PERMISSION);
            }
        }

        //crear el canal de notificaciones
        createNotificationChannel(); // Es para las notificaciones

        btn = findViewById(R.id.button);
        tv = findViewById(R.id.textView);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readWS();
            }
        });
    }

    private void readWS() {
        // Here go the URL of your API Root Path
        String url = "";

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String d1 = jsonObject.getString("maquina");
                            String d2 = jsonObject.getString("human");
                            tv.setText("La maquina dijo: " + d1 + "\nEl humano responde: "
                                    + d2);
                            mostrarNotificacion("Se logro invocar");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            mostrarNotificacion("Error al parsear el JSON.");
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error", error.getMessage());
                        mostrarNotificacion("Error: " + error.getMessage());
                    }
                }
        );

        Volley.newRequestQueue(this).add(request);
    }

    // OPCIONAL
    public void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence nombre = "Webservice";
            String descripcion = "Aplicacion de Web services";
            int importancia = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel canal = new NotificationChannel(
                    CHANNEL_ID,
                    nombre,
                    importancia
            );
            canal.setDescription(descripcion);
            NotificationManager manejadorNotificacion = getSystemService(
                    NotificationManager.class
            );
            manejadorNotificacion.createNotificationChannel(canal);
        }
    }

    private void mostrarNotificacion(String contenido) {
        CharSequence title = getString(R.string.app_name);
        Uri sonido = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this,
                CHANNEL_ID
        ).setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(contenido)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(sonido);

        NotificationManager manejadorNotificaciones =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        manejadorNotificaciones.notify(NOTIFICATION_ID, builder.build());
    }
}