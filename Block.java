package com.company;

import java.awt.*;
import java.awt.geom.*;

public class Block {
    int blockX, blockY, width, height;
    boolean isTopBlock;
    String type;
    public Block(int blockX, int blockY, int width, int height, String type) {
        this.blockX = blockX;
        this.blockY = blockY;
        this.width = width;
        this.height = height;
        this.type = type;
    }

    public int getX() {
        return blockX;
    }

    public int getY() {
        return blockY;
    }

    public void updateX(int num) {
        blockX+=num;
        if(blockX == -50)
            blockX+= 1000;
    }

    public boolean isTopBlock() {
        return type.equals("B");
    }


    public Rectangle2D getCollisionBox() {
        return new Rectangle2D.Double(getX(), getY(), width, height);
    }




}
