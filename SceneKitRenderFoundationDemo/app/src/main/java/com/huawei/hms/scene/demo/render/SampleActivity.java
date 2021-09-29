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

import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.huawei.hms.scene.math.Quaternion;
import com.huawei.hms.scene.math.Vector3;
import com.huawei.hms.scene.sdk.render.Animator;
import com.huawei.hms.scene.sdk.render.Camera;
import com.huawei.hms.scene.sdk.render.Light;
import com.huawei.hms.scene.sdk.render.Model;
import com.huawei.hms.scene.sdk.render.Node;
import com.huawei.hms.scene.sdk.render.RenderView;
import com.huawei.hms.scene.sdk.render.Renderable;
import com.huawei.hms.scene.sdk.render.Resource;
import com.huawei.hms.scene.sdk.render.ResourceFactory;
import com.huawei.hms.scene.sdk.render.Texture;
import com.huawei.hms.scene.sdk.render.Transform;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * SampleActivity.
 *
 * @author HUAWEI
 * @since 2021-8-18
 */
public class SampleActivity extends AppCompatActivity {
    private static final class ModelLoadEventListener implements Resource.OnLoadEventListener<Model> {
        private final WeakReference<SampleActivity> weakRef;

        ModelLoadEventListener(WeakReference<SampleActivity> weakRef) {
            this.weakRef = weakRef;
        }

        /**
         * Model loading event callback.
         * @param model model.
         */
        @Override
        public void onLoaded(Model model) {
            SampleActivity sampleActivity = weakRef.get();
            if (sampleActivity == null || sampleActivity.destroyed) {
                Model.destroy(model);
                return;
            }

            sampleActivity.model = model;
            // Load the model to the scene.
            sampleActivity.modelNode = sampleActivity.renderView.getScene().createNodeFromModel(model);
            sampleActivity.modelNode.getComponent(Transform.descriptor())
                .setPosition(new Vector3(0.f, 0.f, 0.f))
                .scale(new Vector3(0.02f, 0.02f, 0.02f));

            sampleActivity.modelNode.traverseDescendants(descendant -> {
                Renderable renderable = descendant.getComponent(Renderable.descriptor());
                if (renderable != null) {
                    renderable
                        .setCastShadow(true)
                        // Enable the function of receiving shadows for a descendant node.
                        .setReceiveShadow(true);
                }
            });
            // Obtain the Animator component.
            Animator animator = sampleActivity.modelNode.getComponent(Animator.descriptor());
            if (animator != null) {
                List<String> animations = animator.getAnimations();
                if (animations.isEmpty()) {
                    return;
                }
                // Set the animation playback parameters.
                animator
                    .setInverse(false)
                    .setRecycle(true)
                    .setSpeed(1.0f)
                    .play(animations.get(0));
            }
        }

        @Override
        public void onException(Exception exception) {
            SampleActivity sampleActivity = weakRef.get();
            if (sampleActivity == null || sampleActivity.destroyed) {
                return;
            }
            Toast.makeText(sampleActivity,
                "failed to load model: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * SkyBoxTextureLoadEventListener
     */
    private static final class SkyBoxTextureLoadEventListener implements Resource.OnLoadEventListener<Texture> {
        private final WeakReference<SampleActivity> weakRef;

        SkyBoxTextureLoadEventListener(WeakReference<SampleActivity> weakRef) {
            this.weakRef = weakRef;
        }

        /**
         * Skybox texture loading event callback.
         * @param texture Skybox texture.
         */
        @Override
        public void onLoaded(Texture texture) {
            SampleActivity sampleActivity = weakRef.get();
            if (sampleActivity == null || sampleActivity.destroyed) {
                Texture.destroy(texture);
                return;
            }

            sampleActivity.skyBoxTexture = texture;
            // Set the skybox texture for the scene.
            sampleActivity.renderView.getScene().setSkyBoxTexture(texture);
        }

        @Override
        public void onException(Exception exception) {
            SampleActivity sampleActivity = weakRef.get();
            if (sampleActivity == null || sampleActivity.destroyed) {
                return;
            }
            Toast.makeText(sampleActivity,
                "failed to load texture: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * SpecularEnvTextureLoadEventListener
     */
    private static final class SpecularEnvTextureLoadEventListener implements Resource.OnLoadEventListener<Texture> {
        private final WeakReference<SampleActivity> weakRef;

        SpecularEnvTextureLoadEventListener(WeakReference<SampleActivity> weakRef) {
            this.weakRef = weakRef;
        }

        /**
         * Specular texture loading event callback.
         * @param texture Specular texture.
         */
        @Override
        public void onLoaded(Texture texture) {
            SampleActivity sampleActivity = weakRef.get();
            if (sampleActivity == null || sampleActivity.destroyed) {
                Texture.destroy(texture);
                return;
            }

            sampleActivity.specularEnvTexture = texture;
            // Set the specular texture for the scene.
            sampleActivity.renderView.getScene().setSpecularEnvTexture(texture);
        }

        @Override
        public void onException(Exception exception) {
            SampleActivity sampleActivity = weakRef.get();
            if (sampleActivity == null || sampleActivity.destroyed) {
                return;
            }
            Toast.makeText(sampleActivity,
                "failed to load texture: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * DiffuseEnvTextureLoadEventListener
     */
    private static final class DiffuseEnvTextureLoadEventListener implements Resource.OnLoadEventListener<Texture> {
        private final WeakReference<SampleActivity> weakRef;

        DiffuseEnvTextureLoadEventListener(WeakReference<SampleActivity> weakRef) {
            this.weakRef = weakRef;
        }

        /**
         * Diffuse texture loading event callback.
         * @param texture Diffuse texture.
         */
        @Override
        public void onLoaded(Texture texture) {
            SampleActivity sampleActivity = weakRef.get();
            if (sampleActivity == null || sampleActivity.destroyed) {
                Texture.destroy(texture);
                return;
            }

            sampleActivity.diffuseEnvTexture = texture;
            // Set the diffuse texture for the scene.
            sampleActivity.renderView.getScene().setDiffuseEnvTexture(texture);
        }

        @Override
        public void onException(Exception exception) {
            SampleActivity sampleActivity = weakRef.get();
            if (sampleActivity == null || sampleActivity.destroyed) {
                return;
            }
            Toast.makeText(sampleActivity,
                "failed to load texture: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean destroyed = false;

    private RenderView renderView;

    private Model model;
    private Texture skyBoxTexture;
    private Texture specularEnvTexture;
    private Texture diffuseEnvTexture;
    private Node modelNode;

    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        renderView = findViewById(R.id.render_view);
        prepareScene();
        loadModel();
        loadTextures();
        addGestureEventListener();
    }

    /**
     * Resume rendering in RenderView.
     */
    @Override
    protected void onResume() {
        super.onResume();
        renderView.resume();
    }

    /**
     * Pause rendering in RenderView.
     */
    @Override
    protected void onPause() {
        super.onPause();
        renderView.pause();
    }

    /**
     * Destroy the RenderView.
     */
    @Override
    protected void onDestroy() {
        destroyed = true;
        renderView.destroy();
        if (model != null) {
            Model.destroy(model);
        }
        if (skyBoxTexture != null) {
            Texture.destroy(skyBoxTexture);
        }
        if (specularEnvTexture != null) {
            Texture.destroy(specularEnvTexture);
        }
        if (diffuseEnvTexture != null) {
            Texture.destroy(diffuseEnvTexture);
        }
        ResourceFactory.getInstance().gc();
        super.onDestroy();
    }

    /**
     * Load model.
     */
    private void loadModel() {
        Model.builder()
            .setUri(Uri.parse("Spinosaurus_animation/scene.gltf"))
            .load(this, new ModelLoadEventListener(new WeakReference<>(this)));
    }

    /**
     * Load skybox & specular & diffuse texture.
     */
    private void loadTextures() {
        Texture.builder()
            .setUri(Uri.parse("Forest/output_skybox.dds"))
            .load(this, new SkyBoxTextureLoadEventListener(new WeakReference<>(this)));
        Texture.builder()
            .setUri(Uri.parse("Forest/output_specular.dds"))
            .load(this, new SpecularEnvTextureLoadEventListener(new WeakReference<>(this)));
        Texture.builder()
            .setUri(Uri.parse("Forest/output_diffuse.dds"))
            .load(this, new DiffuseEnvTextureLoadEventListener(new WeakReference<>(this)));
    }

    /**
     * Prepare scene.
     */
    private void prepareScene() {
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        // Add the Camera component to the node.
        Node cameraNode = renderView.getScene().createNode("mainCameraNode");
        cameraNode.addComponent(Camera.descriptor())
            .setProjectionMode(Camera.ProjectionMode.PERSPECTIVE)
            .setNearClipPlane(.1f)
            .setFarClipPlane(1000.f)
            .setFOV(60.f)
            .setAspect((float) displayMetrics.widthPixels / displayMetrics.heightPixels)
            .setActive(true);
        cameraNode.getComponent(Transform.descriptor())
            .setPosition(new Vector3(0, 5.f, 30.f));

        // Add the Light component to the node.
        Node lightNode = renderView.getScene().createNode("mainLightNode");
        lightNode.addComponent(Light.descriptor())
            .setType(Light.Type.POINT)
            .setColor(new Vector3(1.f, 1.f, 1.f))
            .setIntensity(1.f)
            .setCastShadow(false);
        lightNode.getComponent(Transform.descriptor())
            .setPosition(new Vector3(3.f, 3.f, 3.f));
    }

    /**
     * addGestureEventListener.
     */
    private void addGestureEventListener() {
        // Create a slide gesture processor.
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (modelNode != null) {
                    modelNode.getComponent(Transform.descriptor())
                        .rotate(new Quaternion(Vector3.UP, -0.001f * distanceX));
                }
                return true;
            }
        });
        // Create a slide gesture processor.
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                if (modelNode != null) {
                    float factor = detector.getScaleFactor();
                    modelNode.getComponent(Transform.descriptor())
                        .scale(new Vector3(factor, factor, factor));
                }
                return true;
            }
        });
        // Add gesture event listener to RenderView.
        renderView.addOnTouchEventListener(motionEvent -> {
            boolean result = scaleGestureDetector.onTouchEvent(motionEvent);
            result = gestureDetector.onTouchEvent(motionEvent) || result;
            return result;
        });
    }
}
