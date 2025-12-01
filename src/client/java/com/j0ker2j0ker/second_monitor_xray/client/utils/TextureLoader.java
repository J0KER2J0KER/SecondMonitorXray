package com.j0ker2j0ker.second_monitor_xray.client.utils;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

public class TextureLoader {

    public static final Map<Block, Integer> blockTextures = new HashMap<>();
    /**
     * Loads a PNG texture from the mod resources into OpenGL
     * @param path Path inside the jar, e.g. "assets/second_monitor_xray/textures/block/diamond_ore.png"
     * @return OpenGL texture ID
     */
    public static int loadTexture(String path) {
        try (InputStream is = TextureLoader.class.getClassLoader().getResourceAsStream(path)) {
            if (is == null) throw new RuntimeException("Texture not found: " + path);

            byte[] bytes = is.readAllBytes();
            ByteBuffer buffer = BufferUtils.createByteBuffer(bytes.length);
            buffer.put(bytes).flip();

            IntBuffer width = BufferUtils.createIntBuffer(1);
            IntBuffer height = BufferUtils.createIntBuffer(1);
            IntBuffer comp = BufferUtils.createIntBuffer(1);

            ByteBuffer image = STBImage.stbi_load_from_memory(buffer, width, height, comp, 4);
            if (image == null) throw new RuntimeException("Failed to load texture: " + path);

            int texID = GL11.glGenTextures();
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texID);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width.get(0), height.get(0), 0,
                    GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, image);

            STBImage.stbi_image_free(image);

            return texID;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void loadBlockTextures() {
        blockTextures.put(Blocks.DIAMOND_ORE, TextureLoader.loadTexture("assets/second_monitor_xray/textures/block/diamond_ore.png"));
        blockTextures.put(Blocks.DEEPSLATE_DIAMOND_ORE, TextureLoader.loadTexture("assets/second_monitor_xray/textures/block/deepslate_diamond_ore.png"));
        blockTextures.put(Blocks.GOLD_ORE, TextureLoader.loadTexture("assets/second_monitor_xray/textures/block/gold_ore.png"));
        blockTextures.put(Blocks.DEEPSLATE_GOLD_ORE, TextureLoader.loadTexture("assets/second_monitor_xray/textures/block/deepslate_gold_ore.png"));
        blockTextures.put(Blocks.NETHER_GOLD_ORE, TextureLoader.loadTexture("assets/second_monitor_xray/textures/block/nether_gold_ore.png"));
        blockTextures.put(Blocks.IRON_ORE, TextureLoader.loadTexture("assets/second_monitor_xray/textures/block/iron_ore.png"));
        blockTextures.put(Blocks.DEEPSLATE_IRON_ORE, TextureLoader.loadTexture("assets/second_monitor_xray/textures/block/deepslate_iron_ore.png"));
        blockTextures.put(Blocks.REDSTONE_ORE, TextureLoader.loadTexture("assets/second_monitor_xray/textures/block/redstone_ore.png"));
        blockTextures.put(Blocks.DEEPSLATE_REDSTONE_ORE, TextureLoader.loadTexture("assets/second_monitor_xray/textures/block/deepslate_redstone_ore.png"));
        blockTextures.put(Blocks.LAPIS_ORE, TextureLoader.loadTexture("assets/second_monitor_xray/textures/block/lapis_ore.png"));
        blockTextures.put(Blocks.DEEPSLATE_LAPIS_ORE, TextureLoader.loadTexture("assets/second_monitor_xray/textures/block/deepslate_lapis_ore.png"));
        blockTextures.put(Blocks.COAL_ORE, TextureLoader.loadTexture("assets/second_monitor_xray/textures/block/coal_ore.png"));
        blockTextures.put(Blocks.DEEPSLATE_COAL_ORE, TextureLoader.loadTexture("assets/second_monitor_xray/textures/block/deepslate_coal_ore.png"));
        blockTextures.put(Blocks.EMERALD_ORE, TextureLoader.loadTexture("assets/second_monitor_xray/textures/block/emerald_ore.png"));
        blockTextures.put(Blocks.DEEPSLATE_EMERALD_ORE, TextureLoader.loadTexture("assets/second_monitor_xray/textures/block/deepslate_emerald_ore.png"));
        blockTextures.put(Blocks.NETHER_QUARTZ_ORE, TextureLoader.loadTexture("assets/second_monitor_xray/textures/block/nether_quartz_ore.png"));
        blockTextures.put(Blocks.ANCIENT_DEBRIS, TextureLoader.loadTexture("assets/second_monitor_xray/textures/block/ancient_debris.png"));
    }
}