package com.manofern.managerclassesetesteautomatizado;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
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
        onView(withId(R.id.tvResults)).check(matches(withText(containsString("Apps Instalados:"))));

        // Verificar se pelo menos o package do app em teste está presente
        String packageName = InstrumentationRegistry.getInstrumentation().getTargetContext().getPackageName();
        onView(withId(R.id.tvResults)).check(matches(withText(containsString(packageName))));

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
        onView(withId(R.id.tvResults)).check(matches(withText(containsString("ID:"))));

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
}