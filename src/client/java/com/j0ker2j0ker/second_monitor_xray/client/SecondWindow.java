package com.j0ker2j0ker.second_monitor_xray.client;

import com.j0ker2j0ker.second_monitor_xray.client.utils.Cube;
import com.j0ker2j0ker.second_monitor_xray.client.utils.TextureLoader;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import java.util.*;

public class SecondWindow {

    private long secondWindow;
    private final List<Cube> cubes = new ArrayList<>();
    private final int chunkDistance = 2;


    public SecondWindow() {
        if (!GLFW.glfwInit()) throw new IllegalStateException("Unable to initialize GLFW!");

        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);

        secondWindow = GLFW.glfwCreateWindow(1280, 720, "Xray Second Monitor", 0, 0);
        if (secondWindow == 0) throw new IllegalStateException("Failed to create second window!");

        new Thread(this::runSecondWindow, "Second-Window-Thread").start();
    }

    private void runSecondWindow(){
        GLFW.glfwMakeContextCurrent(secondWindow);
        GL.createCapabilities();

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        TextureLoader.loadBlockTextures();

        while (!GLFW.glfwWindowShouldClose(secondWindow)) {
            GL11.glClearColor(0f, 0f, 0f, 1f);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

            updateCubes();
            renderCubesWithPlayerCamera();

            if (secondWindow != 0 && !GLFW.glfwWindowShouldClose(secondWindow)) {
                GLFW.glfwSwapBuffers(secondWindow);
                GLFW.glfwPollEvents();
            }

            try { Thread.sleep(16*2); } catch (InterruptedException ignored) {}
        }

        GLFW.glfwDestroyWindow(secondWindow);
        GLFW.glfwTerminate();
    }

    public void updateCubes(){
        cubes.clear();
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        int playerChunkX = (int) client.player.getX() >> 4;
        int playerChunkZ = (int) client.player.getZ() >> 4;

        for (int cx = playerChunkX - chunkDistance; cx <= playerChunkX + chunkDistance; cx++) {
            for (int cz = playerChunkZ - chunkDistance; cz <= playerChunkZ + chunkDistance; cz++) {
                if(client.world == null) return;
                var chunk = client.world.getChunk(cx, cz);

                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        for (int y = -64; y < 320; y++) {
                            Block block = chunk.getBlockState(new BlockPos(new Vec3i(x, y, z))).getBlock();
                            if (!isOre(block)) continue;

                            double worldX = (cx << 4) + x;
                            double worldZ = (cz << 4) + z;

                            if (!isInFrustum(worldX, y, worldZ, client)) continue;

                            cubes.add(new Cube(worldX, y, worldZ, block));
                        }
                    }
                }
            }
        }
    }

    private void renderCubesWithPlayerCamera() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        int[] width = new int[1], height = new int[1];
        GLFW.glfwGetFramebufferSize(secondWindow, width, height);
        GL11.glViewport(0, 0, width[0], height[0]);

        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        float aspect = (float) width[0] / height[0];
        float fov = client.options.getFov().getValue();
        float near = 0.1f, far = 1000f;
        float top = (float) Math.tan(Math.toRadians(fov / 2)) * near;
        float bottom = -top;
        float right = top * aspect;
        float left = -right;
        GL11.glFrustum(left, right, bottom, top, near, far);

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();

        Vec3d pos = client.player.getEntityPos();
        float yaw = client.player.getYaw();
        float pitch = client.player.getPitch();

        GL11.glRotated(pitch, 1, 0, 0);
        GL11.glRotated((yaw + 180), 0, 1, 0);
        GL11.glTranslated(-pos.x, -pos.y-1.5, -pos.z);

        for (Cube c : cubes) {
            drawCube(c, c.block);
        }
    }

    private void drawCube(Cube c, Block block) {
        Integer texID = TextureLoader.blockTextures.get(block);
        if (texID == null || texID == 0) return;

        double s = 0.5;

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texID);
        GL11.glPushMatrix();
        GL11.glTranslated(c.x + 0.5, c.y + 0.5, c.z + 0.5);

        GL11.glBegin(GL11.GL_QUADS);

        // FRONT
        GL11.glTexCoord2f(0, 1); GL11.glVertex3d(-s,  s, -s);
        GL11.glTexCoord2f(1, 1); GL11.glVertex3d( s,  s, -s);
        GL11.glTexCoord2f(1, 0); GL11.glVertex3d( s,  s,  s);
        GL11.glTexCoord2f(0, 0); GL11.glVertex3d(-s,  s,  s);

        // BACK
        GL11.glTexCoord2f(0, 1); GL11.glVertex3d(-s, -s,  s);
        GL11.glTexCoord2f(1, 1); GL11.glVertex3d( s, -s,  s);
        GL11.glTexCoord2f(1, 0); GL11.glVertex3d( s, -s, -s);
        GL11.glTexCoord2f(0, 0); GL11.glVertex3d(-s, -s, -s);

        // LEFT
        GL11.glTexCoord2f(0, 1); GL11.glVertex3d(-s, -s, -s);
        GL11.glTexCoord2f(1, 1); GL11.glVertex3d(-s, -s,  s);
        GL11.glTexCoord2f(1, 0); GL11.glVertex3d(-s,  s,  s);
        GL11.glTexCoord2f(0, 0); GL11.glVertex3d(-s,  s, -s);

        // RIGHT
        GL11.glTexCoord2f(0, 1); GL11.glVertex3d( s, -s,  s);
        GL11.glTexCoord2f(1, 1); GL11.glVertex3d( s, -s, -s);
        GL11.glTexCoord2f(1, 0); GL11.glVertex3d( s,  s, -s);
        GL11.glTexCoord2f(0, 0); GL11.glVertex3d( s,  s,  s);

        // TOP
        GL11.glTexCoord2f(0, 1); GL11.glVertex3d(-s, -s,  s);
        GL11.glTexCoord2f(1, 1); GL11.glVertex3d( s, -s,  s);
        GL11.glTexCoord2f(1, 0); GL11.glVertex3d( s,  s,  s);
        GL11.glTexCoord2f(0, 0); GL11.glVertex3d(-s,  s,  s);

        // BOTTOM
        GL11.glTexCoord2f(0, 1); GL11.glVertex3d(-s,  s, -s);
        GL11.glTexCoord2f(1, 1); GL11.glVertex3d( s,  s, -s);
        GL11.glTexCoord2f(1, 0); GL11.glVertex3d( s, -s, -s);
        GL11.glTexCoord2f(0, 0); GL11.glVertex3d(-s, -s, -s);


        GL11.glEnd();
        GL11.glPopMatrix();
    }

    private boolean isOre(Block block) {
        return TextureLoader.blockTextures.containsKey(block);
    }

    private boolean isInFrustum(double cx, double cy, double cz, MinecraftClient client) {
        var player = client.player;
        if (player == null) return false;

        Vec3d camPos = player.getCameraPosVec(1.0f);

        double dx = (cx + 0.5) - camPos.x;
        double dy = (cy + 0.5) - camPos.y;
        double dz = (cz + 0.5) - camPos.z;
        Vec3d toCube = new Vec3d(dx, dy, dz).normalize();

        float yaw = player.getYaw();
        float pitch = player.getPitch();

        double yawRad = Math.toRadians(-yaw);
        double pitchRad = Math.toRadians(-pitch);

        Vec3d forward = new Vec3d(
                Math.sin(yawRad) * Math.cos(pitchRad),
                Math.sin(pitchRad),
                Math.cos(yawRad) * Math.cos(pitchRad)
        ).normalize();

        float fov = client.options.getFov().getValue();
        double cosFov = Math.cos(Math.toRadians(fov));

        return forward.dotProduct(toCube) > cosFov;
    }

}
