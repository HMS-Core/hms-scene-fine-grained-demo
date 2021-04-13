package com.huawei.hms.scene.demo.render;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.huawei.hms.scene.common.base.error.exception.UpdateNeededException;
import com.huawei.hms.scene.sdk.render.SceneKit;

public class MainActivity extends AppCompatActivity {
    private static final int REQ_CODE_UPDATE_SCENE_KIT = 10001;
    private static final int RES_CODE_UPDATE_SUCCESS = -1;

    private boolean initialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onBtnRenderViewDemoClicked(View view) {
        if (!initialized) {
            initializeSceneKit();
            return;
        }
        startActivity(new Intent(this, SampleActivity.class));
    }

    private void initializeSceneKit() {
        if (initialized) {
            return;
        }
        SceneKit.Property property = SceneKit.Property.builder()
            .setAppId("${app_id}")
            .setGraphicsBackend(SceneKit.Property.GraphicsBackend.GLES)
            .build();
        try {
            SceneKit.getInstance()
                .setProperty(property)
                .initializeSync(getApplicationContext());
            initialized = true;
            Toast.makeText(this, "SceneKit initialized", Toast.LENGTH_SHORT).show();
        } catch (UpdateNeededException e) {
            startActivityForResult(e.getIntent(), REQ_CODE_UPDATE_SCENE_KIT);
        } catch (Exception e) {
            Toast.makeText(this, "failed to initialize SceneKit: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_UPDATE_SCENE_KIT
            && resultCode == RES_CODE_UPDATE_SUCCESS) {
            try {
                SceneKit.getInstance()
                    .initializeSync(getApplicationContext());
                initialized = true;
                Toast.makeText(this, "SceneKit initialized", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "failed to initialize SceneKit: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}