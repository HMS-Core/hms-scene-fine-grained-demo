/*
 * Copyright 2021 Huawei Technologies Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huawei.hms.scene.demo.render;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.huawei.hms.scene.common.base.error.exception.UpdateNeededException;
import com.huawei.hms.scene.sdk.render.SceneKit;

/**
 * MainActivity.
 *
 * @author HUAWEI
 * @since 2021-8-18
 */
public class MainActivity extends AppCompatActivity {
    private static final int REQ_CODE_UPDATE_SCENE_KIT = 10001;
    private static final int RES_CODE_UPDATE_SUCCESS = -1;

    private boolean initialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Starts the SampleActivity, a callback method which is called upon a tap on the "RENDERVIEW DEMO" button.
     *
     * @param view View that is tapped.
     */
    public void onBtnRenderViewDemoClicked(View view) {
        if (!initialized) {
            initializeSceneKit();
            return;
        }
        startActivity(new Intent(this, SampleActivity.class));
    }

    /**
     * Initializing the SceneKit
     */
    private void initializeSceneKit() {
        if (initialized) {
            return;
        }
        SceneKit.Property property = SceneKit.Property.builder()
            .setAppId("${app_id}")
            // Create a SceneKit.Property class and set the app ID and the graphics backend API.
            .setGraphicsBackend(SceneKit.Property.GraphicsBackend.GLES)
            .build();
        try {
            SceneKit.getInstance()
                .setProperty(property)
                // Use the synchronous API for initialization.
                .initializeSync(getApplicationContext());
            initialized = true;
            Toast.makeText(this, "SceneKit initialized", Toast.LENGTH_SHORT).show();
        } catch (UpdateNeededException exception) {
            startActivityForResult(exception.getIntent(), REQ_CODE_UPDATE_SCENE_KIT);
        } catch (Exception exception) {
            Toast.makeText(this,
                "failed to initialize SceneKit: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // If the update is successful, re-initialize the SceneKit class.
        if (requestCode == REQ_CODE_UPDATE_SCENE_KIT
            && resultCode == RES_CODE_UPDATE_SUCCESS) {
            try {
                SceneKit.getInstance()
                    .initializeSync(getApplicationContext());
                initialized = true;
                Toast.makeText(this, "SceneKit initialized", Toast.LENGTH_SHORT).show();
            } catch (Exception exception) {
                Toast.makeText(this,
                    "failed to initialize SceneKit: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}