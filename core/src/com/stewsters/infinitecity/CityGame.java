package com.stewsters.infinitecity;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.math.MathUtils;

public class CityGame extends ApplicationAdapter {
    SpriteBatch spriteBatch;
    BitmapFont font;
    ModelBatch modelBatch;
    PerspectiveCamera camera;
    Environment lights;
    FirstPersonCameraController controller;
    VoxelWorld voxelWorld;

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        font = new BitmapFont();
        modelBatch = new ModelBatch();
        DefaultShader.defaultCullFace = GL20.GL_FRONT;
        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.near = 0.5f;
        camera.far = 1000;

        camera.direction.set(0, 1, 0);
        camera.up.set(0, 0, 1);
        controller = new FirstPersonCameraController(camera);
        controller.setVelocity(50);

        Gdx.input.setInputProcessor(controller);

        lights = new Environment();
        lights.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1.f));
        lights.add(new DirectionalLight().set(1, 1, 1, 0.5f, 0.3f, 1));

        Texture texture = new Texture(Gdx.files.internal("data/tiles.png"));
        TextureRegion[][] tiles = TextureRegion.split(texture, 32, 32);

        MathUtils.random.setSeed(0);
        voxelWorld = new VoxelWorld(tiles[0], 5, 5, 1);
        CityChunkGenerator.generateVoxels(voxelWorld);

        float camX = voxelWorld.voxelsX / 2f;
        float camY = voxelWorld.voxelsY / 2f; //getHighest(camX, camZ) + 1.5f;
        float camZ = voxelWorld.voxelsZ / 2f;
        camera.position.set(camX, camY, camZ);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.4f, 0.4f, 0.4f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        modelBatch.begin(camera);

        modelBatch.render(voxelWorld, lights);
        modelBatch.end();
        controller.update();

        spriteBatch.begin();
        font.draw(spriteBatch, "fps: " + Gdx.graphics.getFramesPerSecond() + ", #visible chunks: " + voxelWorld.renderedChunks
                + "/" + voxelWorld.numChunks, 0, 20);
        spriteBatch.end();
    }

    @Override
    public void resize(int width, int height) {
        spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }
}
