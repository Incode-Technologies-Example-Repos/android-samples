package com.incode.starter;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import com.incode.welcome_sdk.FlowConfig;
import com.incode.welcome_sdk.IncodeWelcome;
import com.incode.welcome_sdk.SessionConfig;
import com.incode.welcome_sdk.listeners.OnboardingSessionListener;
import com.incode.welcome_sdk.modules.IdScan;
import com.incode.welcome_sdk.modules.ProcessId;
import com.incode.welcome_sdk.modules.exceptions.ModuleConfigurationException;
import timber.log.Timber;
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new IncodeWelcome.Builder(
                getApplication(),
                "https://demo-api.incodesmile.com",
                "<your-api-key>")
                .build();
        init();
        setContentView(R.layout.activity_main);
    }
    private void init() {
        SessionConfig sessionConfig = new SessionConfig.Builder()
                .setConfigurationId("<your-flow-id>")
                .build();

        IncodeWelcome.getInstance().setupOnboardingSession(sessionConfig, new OnboardingSessionListener() {
            @Override
            public void onOnboardingSessionCreated(String token, String interviewId, String region) {
                startPhone(interviewId);
            }
            @Override
            public void onError(Throwable throwable) {
                Timber.d("Incode:: Session Error");
            }

            @Override
            public void onUserCancelled() {}
        });
    }
    public class SectionOnboardingListener extends IncodeWelcome.OnboardingListener { }
    private void startPhone(String interviewId) {
        try {
            FlowConfig flowConfig = new FlowConfig.Builder()
                    .setFlowTag("Phone section")
                    .addPhone()
                    .build();
            IncodeWelcome.getInstance().startOnboardingSection(this, flowConfig, new SectionOnboardingListener() {
                @Override
                public void onOnboardingSectionCompleted(String flowTag) {
                    Timber.d("Incode:: Phone section is done: %s", flowTag);
                    startIdScan(interviewId);
                }
            });
        } catch (ModuleConfigurationException e) {
            e.printStackTrace();
        }
    }
    private void startIdScan(String interviewId) {
        try {
            FlowConfig flowConfig = new FlowConfig.Builder()
                    .setFlowTag("ID scan section")
                    .addID(new IdScan.Builder()
                            .setShowIdTutorials(false)
                            .build())
                    .addProcessId(new ProcessId.Builder()
                            .build())
                    .build();
            IncodeWelcome.getInstance().startOnboardingSection(this, flowConfig, new SectionOnboardingListener() {
                @Override
                public void onOnboardingSectionCompleted(String flowTag) {
                    Timber.d("Incode:: ID scan section is done: %s", flowTag);
                    startSelfie(interviewId);
                }
            });
        } catch (ModuleConfigurationException e) {
            e.printStackTrace();
        }
    }
    private void startSelfie(String interviewId) {
        try {
            FlowConfig flowConfig = new FlowConfig.Builder()
                    .setFlowTag("Selfie scan section")
                    .addSelfieScan()
                    .addFaceMatch()
                    .build();
            IncodeWelcome.getInstance().startOnboardingSection(this, flowConfig, new SectionOnboardingListener() {
                @Override
                public void onOnboardingSectionCompleted(String flowTag) {
                    Timber.d("Incode:: Selfie section is done: %s", flowTag);
                    Intent intent = new Intent(MainActivity.this, DoneActivity.class);
                    startActivity(intent);
                }
            });
        } catch (ModuleConfigurationException e) {
            e.printStackTrace();
        }
    }
}