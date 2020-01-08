package org.terasology.gestalt.example.asteroids.java;

import com.google.common.io.CharStreams;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.matthiasmann.twl.utils.PNGDecoder;

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
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Main {

    private static final boolean DEBUG = true;

    private long window;
    private int vba;
    private int vbo;
    private int uvo;
    private int texture;
    private int program;

    public static void main(String[] args) throws Exception {
        new Main().run();
    }

    private void run() throws IOException {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();

        GL.createCapabilities();
        System.out.println("OpenGL version: " + glGetString(GL_VERSION));

        initObjects();
        loop();
        shutdown();


    }

    private void initObjects() throws IOException {
        try (InputStream pngIn = getClass().getResourceAsStream("/ship.png")) {
            PNGDecoder decoder = new PNGDecoder(pngIn);
            ByteBuffer textureBuffer = BufferUtils.createByteBuffer(decoder.getHeight() * decoder.getWidth() * 4);
            decoder.decode(textureBuffer, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
            textureBuffer.flip();
            texture = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, texture);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, textureBuffer);
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glBindTexture(GL_TEXTURE_2D,0);
        }



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

        vba = glGenVertexArrays();
        glBindVertexArray(vba);
        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        uvo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, uvo);
        glBufferData(GL_ARRAY_BUFFER, uvBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER,0);
        glBindVertexArray(0);

        program = glCreateProgram();
        int vertShader = glCreateShader(GL_VERTEX_SHADER);
        int fragShader = glCreateShader(GL_FRAGMENT_SHADER);

        try (Reader vertReader = new InputStreamReader(getClass().getResourceAsStream("/sprite.vert"))) {
            glShaderSource(vertShader, CharStreams.toString(vertReader));
        }
        try (Reader fragReader = new InputStreamReader(getClass().getResourceAsStream("/sprite.frag"))) {
            glShaderSource(fragShader, CharStreams.toString(fragReader));
        }
        glCompileShader(vertShader);
        if (glGetShaderi(vertShader, GL_COMPILE_STATUS) == 0) {
            System.out.println(glGetShaderInfoLog(vertShader));
        }
        glCompileShader(fragShader);
        if (glGetShaderi(fragShader, GL_COMPILE_STATUS) == 0) {
            System.out.println(glGetShaderInfoLog(fragShader));
        }
        glAttachShader(program, vertShader);
        glAttachShader(program, fragShader);
        glLinkProgram(program);
        if (glGetProgrami(program, GL_LINK_STATUS) == 0) {
            System.out.println(glGetProgramInfoLog(program));
        }
        if (DEBUG) {
            glValidateProgram(program);
            if (glGetProgrami(program, GL_VALIDATE_STATUS) == 0) {
                System.out.println(glGetProgramInfoLog(program));
            }
        }

    }

    private void shutdown() {
        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!glfwWindowShouldClose(window)) {
            glFrontFace(GL_CCW);
            glCullFace(GL_BACK);
            glEnable(GL_CULL_FACE);
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            glUseProgram(program);
            glBindVertexArray(vba);
            glEnableVertexAttribArray(0);
            glEnableVertexAttribArray(1);
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, texture);
            int loc = glGetUniformLocation(program, "sample");
            glUniform1i(loc, 0);
            glDrawArrays(GL_TRIANGLES, 0, 6);
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

    private void init() {
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

    }
}

