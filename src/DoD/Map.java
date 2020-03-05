package DoD;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Map {

    private String mapName = "";
    private int goldRequired = 0;

    private int width = 0;
    private int height = 0;

    private char[][] gameMap;

    /**
     * A function to return the view area of an entity with a given radius
     * @param entity The entity requesting its view area
     * @param entities The current entities on the map
     * @param radius The radius of the view area
     * @return The view area
     */
    public char[][] getViewArea (Player entity, Player[] entities, int radius) {
        int x = entity.getX();
        int y = entity.getY();
        radius--;

        char[][] viewArea = new char[(radius*2)+1][(radius*2)+1]; // create a temporary view area based on the radius provided

        for (int i = y - radius; i <= y + radius; i++) { // populate the view area by translating the main game map into the view area, depending on the players position
            for (int j = x - radius; j <= x + radius; j++) {
                if (isInBounds(i, j)) {
                    viewArea[i-(y-radius)][j-(x-radius)] = gameMap[i][j];
                } else {
                    viewArea[i-(y-radius)][j-(x-radius)] = '#';
                }
            }
        }

        for (Player p : entities) { // replace tiles with the relevant entities that occupy them
            int translatedY = (p.getY() - y) + radius;
            int translatedX = (p.getX() - x) + radius;
            if (translatedY < (radius*2)+1 && translatedY >= 0 && translatedX < (radius*2)+1 && translatedX >= 0) {
                viewArea[translatedY][translatedX] = p.identifier;
            }
        }

        return viewArea;
    }

    /**
     * A function which reads a map from a file into an array
     * @param fileName The file name of the file to read from
     */
    protected void readMap(String fileName) {
        File mapFile = new File(fileName);
        Scanner scanner = null;

        String tempLine;

        List<String> lines = new ArrayList<>(); // load the map as an array list

        try {
            scanner = new Scanner(mapFile);

            while (scanner.hasNext()) {
                tempLine = scanner.nextLine();

                switch (tempLine.charAt(0)) {
                    case 'n': // find the mapname
                        this.mapName = tempLine.substring(5);
                        break;
                    case 'w': // find the amount of gold needed to win
                        this.goldRequired = Integer.parseInt(tempLine.substring(4));
                        break;
                    case '#': // a wall denotes the next line of the map
                        lines.add(tempLine);
                        break;
                    default:
                        System.out.println("Invalid line");
                        break;
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }

        gameMap = new char[lines.size()][]; // create a fixed array from the arraylist

        for (int i = 0; i < gameMap.length; i++) { // populate the fixed array
            gameMap[i] = lines.get(i).toCharArray();
        }

        height = gameMap.length; // save the height and width for later calculations
        width = gameMap[0].length;

    }

    /**
     * A function to check whether a position is in the bounds of the map
     * @param y The y coordinate of the position
     * @param x The x coordinate of the position
     * @return Whether the position is in the bounds of the map
     */
    public boolean isInBounds(int y, int x) {
        if (y >= 0 && x >= 0) {
            return x < width && y < height;
        }
        return false;
    }

    /**
     * A function to generate and return a random position in the bounds of the map
     * @return A random position in the bounds of the map
     */
    public int[] getEmptyPos () {
        int[] pos = Utils.getRandomPosInRange(1, width-1, 1, height-1); // get a random tile within bounds of the map

        if (getTile(pos[1], pos[0]) == 'E' || getTile(pos[1], pos[0]) == '.') { // ensure the random tile is empty (E or .)
            return pos;
        } else {
            return getEmptyPos(); // if the tile we tried was occupied, recurse until we find an empty one
        }
    }

    /**
     * A function to return the map name
     * @return The map name
     */
    public String getMapName() {
        return mapName;
    }

    /**
     * A function to return the tile represented by the position passed to the function
     * @param y The y coordinate of the tile
     * @param x The x coordinate of the tile
     * @return The tile represented by the position
     */
    public char getTile(int y, int x) {
        return gameMap[y][x];
    }

    /**
     * A function to return the tile that the player is on
     * @param p The player that the tile is being requested for
     * @return The tile that the player is on
     */
    public char getTile(Player p) {
        return gameMap[p.getY()][p.getX()];
    }

    /**
     * A function to change th etile that the player is on to the tile passed to the function
     * @param p The player that is on the tile to be changed
     * @param c What the tile will be changed to
     */
    public void setTile (Player p, char c) {
        gameMap[p.getY()][p.getX()] = c;
    }

    /**
     * A function to return the amount of gold required to win the map
     * @return The amount of gold required to win the map
     */
    public int getGoldRequired  () {
        return goldRequired;
    }
}