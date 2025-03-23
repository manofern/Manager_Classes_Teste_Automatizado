package com.manofern.managerclassesetesteautomatizado;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;


import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);


    @Test
    public void testButtonCheckProcesses() {
        // Clicar no botão "Ver Processos Ativos"
        onView(withId(R.id.btnCheckProcesses)).perform(click());

        // Verificar se o TextView tvResults foi atualizado
        onView(withId(R.id.tvResults)).check(matches(isDisplayed()));
    }

    @Test
    public void testButtonCheckApps() {
        // Clicar no botão "Listar Aplicativos"
        onView(withId(R.id.btnCheckApps)).perform(click());

        // Verificar se o TextView tvResults foi atualizado com "Apps Instalados"
        onView(withId(R.id.tvResults)).check(matches(withText(containsString("Apps Instalados:"))));

    }

    // Método auxiliar para capturar logs do Logcat
    private boolean isLogPresent(String searchText) {
        try {
            // Executa o comando Logcat para capturar os logs da aplicação
            Process process = Runtime.getRuntime().exec("logcat -d MainActivity:D *:S");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            // Lê linha por linha do Logcat
            while ((line = reader.readLine()) != null) {
                if (line.contains(searchText)) {
                    return true;
                }
            }
        } catch (Exception e) {
            Log.e("LogTest", "Erro ao capturar Logcat", e);
        }
        return false;
    }

    /**
     * Testes focados no ciclo de vida e alteração de brilho
     */
    @Test
    public void testLifecycleMethodsAndBrightness() {
        ActivityScenario<MainActivity> scenario = activityRule.getScenario();

        // Verificar se o brilho está no valor padrão
        scenario.onActivity(activity -> {
            WindowManager.LayoutParams params = activity.getWindow().getAttributes();
            assertEquals(-1.0f, params.screenBrightness, 0.01f);
        });

        // Clicar no botão que simula onPause
        onView(withId(R.id.btnSimulatePause)).perform(click());

        // Verificar se o brilho foi reduzido para 10% após onPause
        scenario.onActivity(activity -> {
            WindowManager.LayoutParams params = activity.getWindow().getAttributes();
            assertEquals(0.1f, params.screenBrightness, 0.01f);
        });

        // Verificar se o diálogo foi exibido
        onView(withText("Simulando onPause()")).check(matches(isDisplayed()));

        // Clicar no botão OK do diálogo para acionar onResume
        onView(withText("OK")).perform(click());

        // Verificar se o brilho voltou ao valor do sistema após onResume
        scenario.onActivity(activity -> {
            WindowManager.LayoutParams params = activity.getWindow().getAttributes();
            assertEquals(-1.0f, params.screenBrightness, 0.01f);
        });
    }

    /**
     * Testes focados no PackageManager (listagem de apps)
     */
    @Test
    public void testPackageManagerListApps() {
        // Clicar no botão para listar apps instalados
        onView(withId(R.id.btnCheckApps)).perform(click());

        // Verificar se o TextView exibe a lista de apps
        onView(withId(R.id.tvResults)).check(matches(withText(Matchers.containsString("Apps Instalados:"))));

        // Verificar se pelo menos o package do app em teste está presente
        String packageName = InstrumentationRegistry.getInstrumentation().getTargetContext().getPackageName();
        onView(withId(R.id.tvResults)).check(matches(withText(Matchers.containsString(packageName))));

        // Teste mais profundo que verifica o número de apps listados vs. número real
        activityRule.getScenario().onActivity(activity -> {
            TextView tvResult = activity.findViewById(R.id.tvResults);
            String resultText = tvResult.getText().toString();

            // Contar quantas vezes a string "📦 Pacote:" aparece no resultado
            int count = 0;
            int index = 0;
            while ((index = resultText.indexOf("📦 Pacote:", index)) != -1) {
                count++;
                index += 10; // Avançar além da string encontrada
            }

            // Obter a contagem real de apps instalados
            List<ApplicationInfo> installedApps = activity.getPackageManager().getInstalledApplications(0);
            assertEquals("Número de apps listados diferente do esperado", installedApps.size(), count);
        });
    }

    /**
     * Testes focados no WindowManager (ajuste dinâmico de layout)
     */
    @Test
    public void testWindowManagerScreenSizeAdjustment() {
        activityRule.getScenario().onActivity(activity -> {
            TextView tvResult = activity.findViewById(R.id.tvResults);

            // Obter a largura da tela através do WindowManager
            WindowManager windowManager = activity.getWindowManager();
            int width = 0;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                width = windowManager.getCurrentWindowMetrics().getBounds().width();
            } else {
                android.util.DisplayMetrics displayMetrics = new android.util.DisplayMetrics();
                windowManager.getDefaultDisplay().getMetrics(displayMetrics);
                width = displayMetrics.widthPixels;
            }

            // Verificar se o tamanho do texto está correto de acordo com a largura
            float expectedTextSize = (width > 1000) ? 20f : 16f;
            assertEquals("Tamanho do texto não ajustado corretamente", expectedTextSize, tvResult.getTextSize() / activity.getResources().getDisplayMetrics().scaledDensity, 0.1f);
        });
    }

    /**
     * Teste para verificar os processos em execução
     */
    @Test
    public void testCheckRunningProcesses() {
        // Clicar no botão para verificar processos
        onView(withId(R.id.btnCheckProcesses)).perform(click());

        // Verificar se o resultado mostra pelo menos um ID de tarefa
        onView(withId(R.id.tvResults)).check(matches(withText(Matchers.containsString("ID:"))));

        // Verificar se a activity atual está entre os processos
        String activityName = MainActivity.class.getName();
        activityRule.getScenario().onActivity(activity -> {
            TextView tvResult = activity.findViewById(R.id.tvResults);
            String resultText = tvResult.getText().toString();

            // A verificação exata depende do formato específico da saída
            // No mínimo, verificamos se o texto contém informações sobre tarefas
            assertTrue("Resultado não contém informações sobre tarefas",
                    resultText.contains("ID:") && resultText.contains("BaseActivity:"));
        });
    }


    // TESTE DOS LOGS

    @Test
    public void testOnCreateLog() {
        assertTrue("Log onCreate não encontrado!", isLogPresent("onCreate chamado"));
    }

    @Test
    public void testOnStartLog() {
        assertTrue("Log onStart não encontrado!", isLogPresent("onStart chamado"));
    }

    @Test
    public void testOnResumeLog() {
        assertTrue("Log onResume não encontrado!", isLogPresent("onResume chamado"));
    }

    @Test
    public void testOnStopLog() {
        // Fecha a Activity para testar o onStop
        activityRule.getScenario().close();
        assertTrue("Log onStop não encontrado!", isLogPresent("onStop chamado"));
    }

    @Test
    public void testOnPauseLog() {
        activityRule.getScenario().onActivity(activity -> activity.moveTaskToBack(true));
        assertTrue("Log onPause não encontrado!", isLogPresent("onPause chamado"));
    }

    @Test
    public void testOnRestartLog() {
        ActivityScenario<MainActivity> scenario = activityRule.getScenario();
        scenario.moveToState(androidx.lifecycle.Lifecycle.State.CREATED);
        scenario.moveToState(androidx.lifecycle.Lifecycle.State.RESUMED);
        assertTrue("Log onRestart não encontrado!", isLogPresent("onRestart chamado"));
    }

    @Test
    public void testOnDestroyLog() {
        activityRule.getScenario().close();
        assertTrue("Log onDestroy não encontrado!", isLogPresent("onDestroy chamado"));
    }
}








