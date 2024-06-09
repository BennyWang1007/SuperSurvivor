package main;

import entity.Player;
import utils.ImageTools;

import java.awt.*;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

public class TileManager {
    private final Game game;
    private final GamePanel gp;
    private Tile [] tile;
    private int mapTileNum[][];
    private ArrayList<String> fileNames = new ArrayList<>();
    private ArrayList<String> collisionStatus = new ArrayList<>();

    public TileManager(Game game, GamePanel gp){
        this.game = game;
        this.gp = gp;
        tile = new Tile[100];
        mapTileNum = new int[game.maxWorldCol][game.maxWorldRow];
        InputStream is = getClass().getResourceAsStream("/maps/tile.txt");
        Scanner sc = new Scanner(new InputStreamReader(is));
        while (sc.hasNext()) {
            String fileName = sc.next();
            String collsion = sc.next();
            fileNames.add(fileName);
            collisionStatus.add(collsion);
        }
        sc.close();

        getTileImage();
    }

    private void getTileImage(){
        for (int i = 0; i < fileNames.size(); i++) {
            String fileName;
            boolean collisionstatus = false;
            fileName = fileNames.get(i);
            if (collisionStatus.get(i).equals("true")) {
                collisionstatus = true;
            }
            setup(i, fileName, collisionstatus);
            // System.out.println("setup: " + i + " | " + fileName);
        }
    }

    public void setup(int index, String imagePath, boolean collision) {
        tile[index] = new Tile();
        tile[index].image = ImageTools.readImage("/tiles/" + imagePath);
        tile[index].image = ImageTools.scaleImage(tile[index].image, game.tileSize, game.tileSize);
        tile[index].collision = collision;
    }

    public void loadMap(String filePath){
        try {
            InputStream is = getClass().getResourceAsStream(filePath);
            Scanner sc = new Scanner(new InputStreamReader(is));

            for (int r = 0; r < game.maxWorldRow; r++) {
                for (int c = 0; c < game.maxWorldCol; c++) {
                    int num = sc.nextInt();
                    mapTileNum[r][c] = num;
                }
            }
            sc.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void draw(Graphics g){
        Graphics2D g2 = (Graphics2D) g;
        int screenWidth = gp.getWidth();
        int screenHeight = gp.getHeight();

        for (int r = 0; r < game.maxWorldRow; r++) {
            for (int c = 0; c < game.maxWorldCol; c++) {
                int tileNum = mapTileNum[r][c];
                int tileWorldX = c * game.tileSize;
                int tileWorldY = r * game.tileSize;
                int tileScreenX = game.translateToScreenX(tileWorldX);
                int tileScreenY = game.translateToScreenY(tileWorldY);

                if(tileWorldX + game.tileSize > game.screenCenterX - screenWidth/2 &&
                        tileWorldX - game.tileSize < game.screenCenterX + screenWidth/2 &&
                        tileWorldY + game.tileSize > game.screenCenterY - screenHeight/2 &&
                        tileWorldY - game.tileSize < game.screenCenterY + screenHeight/2) {
                    g2.drawImage(tile[tileNum].image, tileScreenX, tileScreenY, game.tileSize, game.tileSize, null);
                }
            }
        }

    }
}
