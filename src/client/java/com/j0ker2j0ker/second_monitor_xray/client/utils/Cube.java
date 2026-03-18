package com.j0ker2j0ker.second_monitor_xray.client.utils;


import net.minecraft.world.level.block.Block;

public class Cube {
    public double x,y,z;
    public net.minecraft.world.level.block.Block block;

    public Cube(double x, double y, double z, Block block) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.block = block;
    }
}
