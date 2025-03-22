package com.manofern.managerclassesetesteautomatizado;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertTrue;

import android.util.Log;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;


import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.InputStreamReader;

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
}








