package com.manofern.managerclassesetesteautomatizado;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.WindowManager;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class MockServiceTest {

    @Mock
    private ActivityManager mockActivityManager;

    @Mock
    private PackageManager mockPackageManager;

    @Mock
    private WindowManager mockWindowManager;

    private Context context;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        context = ApplicationProvider.getApplicationContext();
    }

    @Test
    public void testMockPackageManager() {
        // Configurar o mock do PackageManager
        List<ApplicationInfo> mockAppList = new ArrayList<>();
        ApplicationInfo mockApp = new ApplicationInfo();
        mockApp.packageName = "com.test.app";
        mockAppList.add(mockApp);

        when(mockPackageManager.getInstalledApplications(0)).thenReturn(mockAppList);

        // Verificar se o mock retorna o que esperamos
        List<ApplicationInfo> result = mockPackageManager.getInstalledApplications(0);
        assertEquals(1, result.size());
        assertEquals("com.test.app", result.get(0).packageName);
    }

    @Test
    public void testMockActivityManager() {
        // Configurar o mock do ActivityManager
        List<ActivityManager.AppTask> mockTasks = new ArrayList<>();
        // Configuração adicional seria necessária aqui

        when(mockActivityManager.getAppTasks()).thenReturn(mockTasks);

        // Verificar se o mock retorna o que esperamos
        List<ActivityManager.AppTask> result = mockActivityManager.getAppTasks();
        assertNotNull(result);
    }
}

