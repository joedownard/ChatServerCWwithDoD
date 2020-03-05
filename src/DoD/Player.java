package DoD;

public abstract class Player {

    private int x;
    private int y;

    public abstract void processResult(char[][] viewArea);
    public abstract void processResult(String result);

    protected char identifier;

    /**
     * A function to get the x coordinate of the player
     * @return The x coordinate of the player
     */
    public int getX () {
        return x;
    }

    /**
     * A function to get the y coordinate of the player
     * @return The y coordinate of the player
     */
    public int getY () {
        return y;
    }

    /**
     * A function to set the x coordinate of the player
     */
    public void setX (int x) {
        this.x = x;
    }

    /**
     * A function to set the y coordinate of the player
     */
    public void setY (int y) {
        this.y = y;
    }


}
