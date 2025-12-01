package com.j0ker2j0ker.second_monitor_xray.client.utils;


import net.minecraft.block.Block;

public class Cube {
    public double x,y,z;
    public Block block;

    public Cube(double x, double y, double z, Block block) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.block = block;
    }
}
