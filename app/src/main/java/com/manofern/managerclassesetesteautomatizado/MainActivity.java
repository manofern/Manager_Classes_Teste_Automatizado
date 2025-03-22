package com.manofern.managerclassesetesteautomatizado;

import android.app.ActivityManager;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    // Gerenciadores e utilitários do sistema
    private ActivityManager activityManager;
    private PackageManager packageManager;
    private WindowManager windowManager;
    private TextView tvResult;

    // BroadcastReceiver para monitorar mudanças na bateria
    private BroadcastReceiver batteryReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MainActivity", "onCreate chamado");

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializa o BroadcastReceiver para monitorar o nível da bateria
        batteryReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                Toast.makeText(context, "Nível da bateria: " + level + "%", Toast.LENGTH_SHORT).show();
            }
        };

        // Inicializa botões e o TextView
        Button btnCheckProcesses = findViewById(R.id.btnCheckProcesses);
        Button btnCheckApps = findViewById(R.id.btnCheckApps);
        tvResult = findViewById(R.id.tvResults);

        // Obtém os serviços do sistema necessários
        activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        packageManager = getPackageManager();
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        // Define eventos de clique nos botões
        btnCheckProcesses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //tvResult.setText("btnCheckProcesses");
                CheckRunningProcesses();
            }
        });

        btnCheckApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // tvResult.setText("btnCheckApps");
                ListInstalledApps();
            }
        });

        // Ajusta dinamicamente o layout de acordo com a largura da tela
        adjustLayoutForScreenWidth();
    }

    @Override
    protected void onStart() {
        Log.d("MainActivity", "onStart chamado");
        super.onStart();
        // Registra o BroadcastReceiver quando a Activity se torna visível
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver, filter);
    }

    @Override
    protected void onStop() {
        Log.d("MainActivity", "onStop chamado");
        super.onStop();
        // Remove o BroadcastReceiver para evitar vazamentos de memória
        unregisterReceiver(batteryReceiver);
    }


    // metodo para gerenciar os preocessos
    private void CheckRunningProcesses(){
        List<ActivityManager.AppTask> appTasks = activityManager.getAppTasks();
        StringBuffer result = new StringBuffer();

        for ( ActivityManager.AppTask app : appTasks){

            result.append("ID: ")
                    .append(app.getTaskInfo().taskId)
                    .append(" - BaseActivity: ")
                    .append(app.getTaskInfo().baseActivity)
                    .append("\n");

        }
        // Atualiza a UI com os processos encontrados
        tvResult.setText(result.toString());

    }

    // metodo para Listar os App instalados
    private void ListInstalledApps() {
        List<ApplicationInfo> installedApps = packageManager.getInstalledApplications(0);

        StringBuilder result = new StringBuilder("Apps Instalados:\n\n");

        // Itera sobre todos os aplicativos instalados
        for (ApplicationInfo appInfo : installedApps) {
            String appName = appInfo.loadLabel(packageManager).toString();  // Nome amigável
            String packageName = appInfo.packageName;  // Nome do pacote

            // Adiciona informações formatadas à string de resultado
            result.append("📌 ").append(appName).append("\n")  // Nome do app
                    .append("📦 Pacote: ").append(packageName).append("\n\n"); // Nome do pacote
        }
        // Atualiza a UI com os Apps instalados
        tvResult.setText(result.toString());
    }

    // Método para ajustar dinamicamente o tamanho da fonte do TextView com base na largura da tela
    private void adjustLayoutForScreenWidth(){

        DisplayMetrics displayMetrics = new DisplayMetrics();

        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;

        // Ajusta tamanho da fonte conforme a largura da tela
        if (width > 1000) {
            tvResult.setTextSize(20);
        } else {
            tvResult.setTextSize(16);
        }
    }
}
