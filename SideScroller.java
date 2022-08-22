package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;

public class SideScroller extends JPanel implements KeyListener, Runnable{
    JFrame frame;
    BufferedImage aladdinSheet, smallCity, bigCity, clouds, sky, blockImage;
    BufferedImage[] aladdin = new BufferedImage[13];
    BufferedImage[] aladdinJumping = new BufferedImage[12];

    HashMap<Integer, ArrayList<Block>> map = new HashMap<Integer, ArrayList<Block>>();


    int aladdinCount = 0, count = 0, scCount = 0, bcCount = 0, cloudCount = 0, currentColumn = 0, heroStartColumn;
    boolean right = false;
    Thread timer;
    Hero hero;
    Block block;

    public SideScroller() {
        frame = new JFrame("Aladdin's Not So Exciting Adventure");
        frame.add(this);

        int[][] locsAndDims = new int[][] {
                //x, y, width, height
                {337, 3, 23, 53}, //standing
                {4, 64, 31, 53},
                {34, 64, 31, 51},
                {62, 64, 31, 51},
                {92, 64, 31, 51},
                {127, 64, 37, 51},
                {166, 64, 31, 51},
                {205, 64, 31, 51},
                {233, 64, 30, 51},
                {263, 61, 30, 56},
                {292, 61, 34, 56},
                {325, 60, 41, 56},
                {367, 60, 36, 56},
        };

        int[][] jumpLocsAndDims = new int[][] {
                //x, y, width, height
                {4, 294, 31, 59}, //standing
                {35, 300, 29, 58}, //jumping
                {62, 301, 38, 56},
                {100, 301, 36, 56},
                {140, 303, 41, 50},
                {183, 304, 49, 47},
                {230, 303, 42, 50}, //falling
                {278, 302, 37, 54},
                {321, 303, 33, 56},
                {4, 363, 35, 64},
                {42, 365, 36, 63},
                {168, 361, 25, 55},
        };

        try {
            aladdinSheet = ImageIO.read(new File("Aladdin.png"));
            smallCity = ImageIO.read(new File("smallCity.png"));
            bigCity = ImageIO.read(new File("bigCity.png"));
            clouds = ImageIO.read(new File("clouds.png"));
            sky = ImageIO.read(new File("sunset.png"));
            blockImage = ImageIO.read(new File("box.png"));
            blockImage = resize(blockImage, 50, 50);


            File file = new File("/Users/abhishekthakare/Desktop/forAbhi/Data Structures Projects/src/com/company/map");
            BufferedReader input = new BufferedReader(new FileReader(file));
            String text;
            int row = 0;
            System.out.println(input);
            while((text = input.readLine()) != null) {
                String[] pieces = text.split("");
                for(int x = 0; x < pieces.length; x++) {
                    if (!pieces[x].equals("-")) {
                        if (pieces[x].equals("H")) {
                            hero = new Hero(50 * x, 450, locsAndDims, jumpLocsAndDims);
                            heroStartColumn = x;
                        }
                        else {
                            if (!map.containsKey(x))
                                map.put(x, new ArrayList<Block>());
                            map.get(x).add(new Block(50 * x, 50 * row + 6, 50, 50, pieces[x]));
                        }
                    }
                }
                row++;
            }

        }catch (IOException e) {
            System.out.println("a");
        }

        System.out.println(map);

        for(int x = 0; x < 13; x++) {
            aladdin[x] = aladdinSheet.getSubimage(locsAndDims[x][0], locsAndDims[x][1], locsAndDims[x][2], locsAndDims[x][3]);
            aladdin[x] = resize(aladdin[x], aladdin[x].getWidth() * 2, aladdin[x].getHeight() * 2);
        }
        for(int x = 0; x < 12; x++) {
            aladdinJumping[x] = aladdinSheet.getSubimage(jumpLocsAndDims[x][0], jumpLocsAndDims[x][1], jumpLocsAndDims[x][2], jumpLocsAndDims[x][3]);
            aladdinJumping[x] = resize(aladdinJumping[x], aladdinJumping[x].getWidth() * 2, aladdinJumping[x].getHeight() * 2);
        }

        frame.addKeyListener(this);
        frame.setSize(900, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        timer = new Thread(this);
        timer.start();

    }

    public BufferedImage resize(BufferedImage image, int width, int height) {
        Image temp = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage scaledVersion = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = scaledVersion.createGraphics();
        g2.drawImage(temp, 0, 0, null);
        g2.dispose();
        return scaledVersion;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.fillRect(0, 0, 900, 600);

        g.drawImage(sky, 0, 0, this);

        g.drawImage(clouds, cloudCount+960, 0, this);
        g.drawImage(clouds, cloudCount-960, 0, this);

        g.drawImage(bigCity, bcCount+960, 100, this);
        g.drawImage(bigCity, bcCount-960, 100, this);

        g.drawImage(smallCity, scCount+960, -40, this);
        g.drawImage(smallCity, scCount-960, -40, this);

        Graphics2D g2 = (Graphics2D)g;
        g2.draw(hero.getCollisionBox());

        for(int index = currentColumn-11; index < currentColumn+12; index++) {
            try{
                ArrayList<Block> blocks = map.get(index);
                for(Block b : blocks)
                    g.drawImage(blockImage, b.getX(), b.getY(), this);

            }catch(NullPointerException e) {}
        }

        if(hero.isJumping() || hero.isFalling())
            g.drawImage(aladdinJumping[hero.getJumpCount()], hero.getX(), hero.getY(), this);
        else
            g.drawImage(aladdin[hero.getAladdinCount()], hero.getX(), hero.getY(), this);

    }


    public boolean collidingBelow() {
        for(int c = currentColumn; c <= currentColumn+1; c++) {
            try {
                ArrayList<Block> blocks = map.get(c);
                for(Block b : blocks) {
                    if(hero.collisionBelow().intersects(b.getCollisionBox()) && hero.isAbove(b) && b.isTopBlock()) {
                        return true;
                    }
                }

            }catch (NullPointerException e) {}
        }
        return false;
    }



    @Override
    public void run() {
        while(true) {

            if(hero.isJumping()) {
                hero.updateJumping();
            }
            else if(hero.isFalling()) {
                boolean hitBlock = collidingBelow();
                if(!hitBlock && hero.getY() != hero.getOriginalY()) {
                    hero.updateFalling();
                }
                else {
                    hero.setFalling(false);
                    if(hitBlock)
                        hero.setOnBox(true);
                    hero.zeroJumpCount();
                }
            }
            else if(hero.isOnBox()) {
                boolean hitBlock = collidingBelow();
                if(!hitBlock) {
                    hero.setFalling(true);
                    hero.setOnBox(false);
                }
            }


            if(right) {
                boolean hitBlock = false;
                for(int c = currentColumn; c <= currentColumn + 1; c++) {
                    try {
                        ArrayList<Block> blocks = map.get(c);
                        for(Block b : blocks) {
                            if(hero.getCollisionBox().intersects(b.getCollisionBox()) && hero.sameLevel(b) && b.isTopBlock()) {
                                hitBlock = true;
                                break;
                            }
                        }

                    } catch(NullPointerException e) {}
                }
                if(!hitBlock) {
                        count++;
                        if (count % 25 == 0) {
                            hero.updateAladdinCount(hero.getAladdinCount() + 1);
                        }

                        for (Integer index : map.keySet()) {
                            for(Block b : map.get(index))
                                b.updateX(-1);
                        }
                        currentColumn = (int)(count/50.0) + heroStartColumn;

                        scCount--;
                        if (scCount < -1920)
                            scCount += 1920;

                        if (count % 3 == 0)
                            bcCount--;
                        if (bcCount < -1920)
                            bcCount += 1920;

                        if (count % 10 == 0)
                            cloudCount--;
                        if (cloudCount < -1920)
                            cloudCount += 1920;

                }
            }
                try {
                    timer.sleep(3);
                } catch (InterruptedException e) {

                }
                repaint();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == 39)
            right = true;

        //up arrow - 38
        //spacebar - 32

        if(e.getKeyCode() == 32)
            if(!hero.isJumping() && !hero.isFalling()) {
                hero.setJumping(true);
                hero.setOnBox(false);
            }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode() == 39) {
            right = false;
            hero.updateAladdinCount(0);
            aladdinCount = 0;
        }
    }

    public static void main(String args[]) {
        SideScroller app = new SideScroller();
    }

}
