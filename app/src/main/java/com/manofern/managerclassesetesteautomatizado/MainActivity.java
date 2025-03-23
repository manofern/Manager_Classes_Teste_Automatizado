package com.manofern.managerclassesetesteautomatizado;

import android.app.ActivityManager;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
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

        Button btnSimulatePause = findViewById(R.id.btnSimulatePause);
        btnSimulatePause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simulateOnPause();
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

        // Diminui o brilho da tela para 50%
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = 0.5f; // 0.5f representa 50% do brilho
        getWindow().setAttributes(layoutParams);

        Log.d("MainActivity", "Brilho da tela ajustado para 50%");
    }

    @Override
    protected void onRestart() {
        Log.d("MainActivity", "onRestart chamado");
        super.onRestart();

        // Restaura o brilho da tela para o valor do sistema
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = -1.0f; // -1.0f significa usar o brilho do sistema
        getWindow().setAttributes(layoutParams);

        Log.d("MainActivity", "Brilho da tela restaurado para o valor padrão");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("MainActivity", "onResume chamado");

        // Restaurar o brilho original quando a activity voltar ao foco
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = -1.0f; // Usar o brilho do sistema
        getWindow().setAttributes(layoutParams);

        Log.d("MainActivity", "Brilho da tela restaurado em onResume");

        // Obtendo o serviço ActivityManager
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        // Obtendo a lista de processos em execução
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = activityManager.getRunningAppProcesses();

        // Exibindo no log o nome do primeiro processo em execução (caso haja)
        if (runningProcesses != null && !runningProcesses.isEmpty()) {
            Log.d("MainActivity", "Primeiro processo em execução: " + runningProcesses.get(0).processName);
        }
    }

    private void simulateOnPause() {
        // Chamar onPause() explicitamente
        onPause();

        // Criar diálogo com listener para o botão OK
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Simulando onPause()")
                .setMessage("O brilho da tela foi reduzido para 10%. Clique em OK para restaurar (onResume).")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Chamar onResume() explicitamente quando o usuário clicar em OK
                        onResume();
                        Log.d("MainActivity", "onResume chamado manualmente pelo clique no OK");
                    }
                })
                .setCancelable(false) // Para evitar que o usuário cancele o diálogo pressionando fora dele
                .show();

        Log.d("MainActivity", "onPause chamado explicitamente e diálogo exibido");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("MainActivity", "onPause chamado");

        // Alterar o brilho da tela para 10% on pause
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = 0.1f; // 10% do brilho
        getWindow().setAttributes(layoutParams);

        Log.d("MainActivity", "Brilho da tela ajustado para 10% em onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("MainActivity", "onDestroy chamado");

        Log.d("MainActivity", "Recursos liberados em onDestroy");
        // Liberar recursos e evitar vazamentos de memória
        if (batteryReceiver != null) {
            unregisterReceiver(batteryReceiver);
            batteryReceiver = null;
        }

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
