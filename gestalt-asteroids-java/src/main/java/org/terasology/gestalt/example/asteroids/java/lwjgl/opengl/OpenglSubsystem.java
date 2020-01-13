package org.terasology.gestalt.example.asteroids.java.lwjgl.opengl;

import com.google.common.io.CharStreams;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryStack;
import org.terasology.gestalt.assets.AssetType;
import org.terasology.gestalt.assets.module.ModuleAwareAssetTypeManager;
import org.terasology.gestalt.entitysystem.entity.EntityIterator;
import org.terasology.gestalt.entitysystem.entity.EntityManager;
import org.terasology.gestalt.example.asteroids.common.engine.AssetSubsystem;
import org.terasology.gestalt.example.asteroids.common.engine.NamedThread;
import org.terasology.gestalt.example.asteroids.common.engine.entitysystem.EntitySubsystem;
import org.terasology.gestalt.example.asteroids.common.rendering.TextureData;
import org.terasology.gestalt.example.asteroids.common.rendering.formats.PngTextureFormat;
import org.terasology.gestalt.example.asteroids.common.rendering.formats.TextureInfoFormat;
import org.terasology.gestalt.example.asteroids.common.engine.Engine;
import org.terasology.gestalt.example.asteroids.common.engine.Subsystem;
import org.terasology.gestalt.example.asteroids.modules.core.components.Location;
import org.terasology.gestalt.example.asteroids.modules.core.components.Sprite;
import org.terasology.gestalt.module.ModuleEnvironment;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;


public class OpenglSubsystem implements Subsystem {

    private static final boolean DEBUG = true;

    private Engine engine;

    private long window;
    private int spriteVba;
    private int spriteShaderProgram;

    private int spriteShaderUniformTextureIndex;
    private int spriteShaderUniformWVPIndex;

    private AssetType<OpenGLTexture, TextureData> textureAssetType;
    private OpenGLTexture spriteTexture;
    private NamedThread displayThread;
    private AssetSubsystem assetSubsystem;

    private EntitySubsystem entitySubsystem;


    @Override
    public void initialise(Engine engine) {
        this.engine = engine;
        assetSubsystem = engine.getSubsystemOfType(AssetSubsystem.class);
        entitySubsystem = engine.getSubsystemOfType(EntitySubsystem.class);
        displayThread = new NamedThread("GameThread", Thread.currentThread());
        initWindow();
        initSpriteElements();
        System.out.println("OpenGL version: " + glGetString(GL_VERSION));
    }

    private void initSpriteElements() {
        List<Vector3f> verts = new ArrayList<>(Arrays.asList(new Vector3f(-0.5f, 0.5f, 0), new Vector3f(-0.5f, -0.5f, 0), new Vector3f(0.5f, -0.5f, 0),
                new Vector3f(0.5f, -0.5f, 0), new Vector3f(0.5f, 0.5f, 0), new Vector3f(-0.5f, 0.5f, 0)));
        FloatBuffer vertBuffer = BufferUtils.createFloatBuffer(verts.size() * 3);
        for (Vector3f vert : verts) {
            vertBuffer.put(vert.x);
            vertBuffer.put(vert.y);
            vertBuffer.put(vert.z);
        }
        vertBuffer.flip();

        List<Vector2f> uvs = new ArrayList<>(Arrays.asList(new Vector2f(0f, 0f), new Vector2f(0, 1), new Vector2f(1f, 1f),
                new Vector2f(1f, 1f), new Vector2f(1f, 0f), new Vector2f(0f, 0f)));
        FloatBuffer uvBuffer = BufferUtils.createFloatBuffer(uvs.size() * 2);
        for (Vector2f uv : uvs) {
            uvBuffer.put(uv.x);
            uvBuffer.put(uv.y);
        }
        uvBuffer.flip();

        spriteVba = glGenVertexArrays();
        glBindVertexArray(spriteVba);
        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        int uvo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, uvo);
        glBufferData(GL_ARRAY_BUFFER, uvBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        spriteShaderProgram = glCreateProgram();
        int vertShader = glCreateShader(GL_VERTEX_SHADER);
        int fragShader = glCreateShader(GL_FRAGMENT_SHADER);

        try (Reader vertReader = new InputStreamReader(getClass().getResourceAsStream("/sprite.vert"))) {
            glShaderSource(vertShader, CharStreams.toString(vertReader));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load sprite vert shader", e);
        }
        try (Reader fragReader = new InputStreamReader(getClass().getResourceAsStream("/sprite.frag"))) {
            glShaderSource(fragShader, CharStreams.toString(fragReader));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load sprite vert shader", e);
        }
        glCompileShader(vertShader);
        if (glGetShaderi(vertShader, GL_COMPILE_STATUS) == 0) {
            System.out.println(glGetShaderInfoLog(vertShader));
        }
        glCompileShader(fragShader);
        if (glGetShaderi(fragShader, GL_COMPILE_STATUS) == 0) {
            System.out.println(glGetShaderInfoLog(fragShader));
        }
        glAttachShader(spriteShaderProgram, vertShader);
        glAttachShader(spriteShaderProgram, fragShader);
        glLinkProgram(spriteShaderProgram);
        if (glGetProgrami(spriteShaderProgram, GL_LINK_STATUS) == 0) {
            System.out.println(glGetProgramInfoLog(spriteShaderProgram));
        }
        spriteShaderUniformTextureIndex = glGetUniformLocation(spriteShaderProgram, "sample");
        spriteShaderUniformWVPIndex = glGetUniformLocation(spriteShaderProgram,"worldviewperpective");

        if (DEBUG) {
            glValidateProgram(spriteShaderProgram);
            if (glGetProgrami(spriteShaderProgram, GL_VALIDATE_STATUS) == 0) {
                System.out.println(glGetProgramInfoLog(spriteShaderProgram));
            }
        }
    }

    private void initWindow() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        window = glfwCreateWindow(600, 600, "Hello World!", NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        });

        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);

        GL.createCapabilities();
    }

    @Override
    public void registerAssetTypes(ModuleAwareAssetTypeManager assetTypeManager) {
        textureAssetType = assetTypeManager.createAssetType(OpenGLTexture.class, (resourceUrn, assetType, textureData) -> {
            try {
                return new OpenGLTexture(resourceUrn, assetType, textureData, new OpenGLTextureResource(displayThread.asyncFuture(GL33::glGenTextures).get(), displayThread));
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException("Failed to generate texture", e);
            }
        }, "textures");

        assetTypeManager.getAssetFileDataProducer(textureAssetType).addAssetFormat(new PngTextureFormat());
        assetTypeManager.getAssetFileDataProducer(textureAssetType).addSupplementFormat(new TextureInfoFormat());

    }

    @Override
    public void tick(int delta) {

        if (glfwWindowShouldClose(window)) {
            engine.exit();
        } else {
            displayThread.processWaitingProcesses();

            // Set the clear color
            glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

            // Run the rendering loop until the user has attempted to close
            // the window or has pressed the ESCAPE key.
            glFrontFace(GL_CCW);
            glCullFace(GL_BACK);
            glEnable(GL_CULL_FACE);
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            glUseProgram(spriteShaderProgram);
            glBindVertexArray(spriteVba);
            glEnableVertexAttribArray(0);
            glEnableVertexAttribArray(1);
            glActiveTexture(GL_TEXTURE0);

            Location location = new Location();
            Sprite sprite = new Sprite();
            EntityIterator entityIterator = entitySubsystem.getEntityManager().iterate(location, sprite);
            Matrix4f viewProj = new Matrix4f().ortho(-50, 50, -50, 50, 1, 0, false);
            Matrix4f world = new Matrix4f();
            while (entityIterator.next()) {
                world.set(viewProj);
                world.translate(location.getPosition());
                world.rotate(location.getRotation());
                world.scale(location.getScale());
                float[] rawMat = new float[16];
                world.get(rawMat);
                glBindTexture(GL_TEXTURE_2D, ((OpenGLTexture) sprite.getTexture()).getTextureId());
                glUniform1i(spriteShaderUniformTextureIndex, 0);
                glUniformMatrix4fv(spriteShaderUniformWVPIndex, false, rawMat);
                glDrawArrays(GL_TRIANGLES, 0, 6);
            }
            glDisableVertexAttribArray(1);
            glDisableVertexAttribArray(0);
            glBindVertexArray(0);
            glUseProgram(0);

            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }
    }

    @Override
    public void close() {
        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    @Override
    public void onEnvironmentChanged(ModuleEnvironment environment) {
        Optional<OpenGLTexture> asset = assetSubsystem.getAssetManager().getAsset("core:ship", OpenGLTexture.class);
        spriteTexture = asset.orElseThrow(() -> new RuntimeException("Missing required asset core:ship texture"));
    }
}
