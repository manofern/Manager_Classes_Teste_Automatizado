package com.manofern.managerclassesetesteautomatizado;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.containsString;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;


import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

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
}
