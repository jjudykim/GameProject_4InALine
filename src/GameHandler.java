import javax.swing.*;
import java.io.*;
import java.util.Scanner;

public class GameHandler
{
    private final int SCREEN_WIDTH = 35;
    private final int SCREEN_HEIGHT = 20;
    private final int FIELD_WIDTH = 13;
    private final int FIELD_HEIGHT = 6;
    private final int UP_PADDING = 3;
    private final int LEFT_PADDING = 1;

    private JTextArea textArea;
    private char[][] buffer;
    private int[][] field;

    private int currentX, currentY;
    private int BlackScore = 0, WhiteScore = 0;
    private int checkNum;
    private int gameCount;
    private String previousScore;
    private boolean winner = false;
    private boolean isGameOver = false;

    Scanner sc = new Scanner(System.in);


    public GameHandler(JTextArea ta)
    {
        textArea = ta;
        field = new int[FIELD_HEIGHT][FIELD_WIDTH];
        buffer = new char[SCREEN_HEIGHT][SCREEN_WIDTH];
        initData();
    }

    public void gameTiming()
    {
        // Game tick
        try
        {
            Thread.sleep(50);
        }
        catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public void initData()
    {
        SetField();
        clearBuffer();
        currentX = FIELD_WIDTH / 2;
        currentY = 0;
        gameCount = 0;
        checkNum = 0;
        winner = false;
        isGameOver = false;
        readScore();
    }

    private void readScore()
    {
        try
        {
            BufferedReader in = new BufferedReader(new FileReader("previousScore.txt"));
            previousScore = in.readLine();
            in.close();
        }
        catch(FileNotFoundException e)
        {
            previousScore = "";
        }
        catch(IOException e)
        {
            //TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void writeScore()
    {
        BufferedWriter out;
        try
        {
            out = new BufferedWriter(new FileWriter("previousScore.txt"));
            out.write("●:" + BlackScore + "   ○:" + WhiteScore);
            out.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public void SetField()
    {
        for(int y = 0; y < FIELD_HEIGHT; y++)
        {
            for(int x = 0; x < FIELD_WIDTH; x++)
            {
                if(y == 0)
                {
                    if(x == 0) field[y][x] = 0;                                             // 0 : ┌
                    else if(x == FIELD_WIDTH - 1) field[y][x] = 1;                          // 1 : ┐
                    else if(x % 2 == 1) field[y][x] = 2;                                    // 2 : ─
                    else field[y][x] = 3;                                                   // 3 : ┬
                }
                else if(y == FIELD_HEIGHT- 1)
                {
                    if(x == 0) field[y][x] = 4;                                             // 4 : └
                    else if(x == FIELD_WIDTH - 1) field[y][x] = 5;                          // 5 : ┘
                    else if(x % 2 == 1) field[y][x] = 2;
                    else field[y][x] = 6;                                                   // 6 : ┴
                }
                else if(x == 0) field[y][x] = 7;                                            // 7 : ├
                else if(x == FIELD_WIDTH - 1) field[y][x] = 8;                              // 8 : ┤
                else
                {
                    if(x % 2 == 1) field[y][x] = 2;
                    else field[y][x] = 9;                                                   // 9 : ┼
                    // 0||10 : ●
                    // 1||11 : ○
                }
            }
        }
    }

    private void clearBuffer()
    {
        for(int y = 0; y < SCREEN_HEIGHT; y++)
        {
            for(int x = 0; x < SCREEN_WIDTH; x++)
            {
                buffer[y][x] = ' ';
            }
        }
    }

    private void drawToBuffer(int px, int py, String c)
    {
        for(int x = 0; x < c.length(); x++)
        {
            buffer[py + UP_PADDING][px + x + LEFT_PADDING] = c.charAt(x);
        }
    }

    private void drawToBuffer(int px, int py, char c)
    {
        buffer[py + UP_PADDING][px + LEFT_PADDING] = c;
    }

    public boolean isGameOver()
    {
        return isGameOver;
    }

    public boolean getWinner()
    {
        return winner;
    }

    private int playerCheck()
    {
        if(gameCount % 2 == 0) return 0;
        else return 1;
    }

    private void drawCurrentRock()
    {
        drawToBuffer(currentX,-1, "●○".charAt(playerCheck()));
    }

    public void moveRight()
    {
        currentX += (currentX + 2 < FIELD_WIDTH) ? 2 : 0;
    }

    public void moveLeft()
    {
        currentX -= (currentX - 2 >= 0) ? 2 : 0;
    }

    public void moveDown()
    {
        for(int i = 0; i < FIELD_HEIGHT; i++)
        {
            if(field[i][currentX] != 10 && field[i][currentX] != 11) currentY = i;
            else
            {
                currentY = i-1;
                break;
            }
        }
        if(field[0][currentX] != 10 && field[0][currentX] != 11)
        {
            if(playerCheck() == 1) field[currentY][currentX] = 11;
            else field[currentY][currentX] = 10;
            gameCount++;
        }
    }

    public void gameLogic()
    {
        isWinner(playerCheck());

        if(winner && checkNum == 0)
        {
            if (playerCheck() == 1) BlackScore++;
            else WhiteScore++;
            checkNum++;
        }
    }

    public void isWinner(int player)
    {
        int check;
        int tempX, tempY;

        if(player == 0) check = 11;
        else check = 10;

        for(int y = 0; y < FIELD_HEIGHT; y++)
        {
            for(int x = 1; x*2-2 < FIELD_WIDTH; x++)
            {
                tempY = (y < 3)? 1 : -1;
                tempX = ((x+3)*2-2 < FIELD_WIDTH)? 1 : -1;
                if(field[y][x*2-2] == check && field[y][(x+tempX)*2-2] == check && field[y][(x+tempX*2)*2-2] == check && field[y][(x+tempX*3)*2-2] == check) winner = true;  // 가로 줄 검사
                if(field[y][x*2-2] == check && field[y+tempY][x*2-2] == check && field[y+tempY*2][x*2-2] == check && field[y+tempY*3][x*2-2] == check) winner = true; // 세로 줄 검사
                if(field[y][x*2-2] == check && field[y+tempY][(x+tempX)*2-2] == check && field[y+tempY*2][(x+tempX*2)*2-2] == check && field[y+tempY*3][(x+tempX*3)*2-2] == check) winner = true; // 대각선 / 검사
                if(field[y+tempY*3][x*2-2] == check && field[y+tempY*2][(x+tempX)*2-2] == check && field[y+tempY][(x+tempX*2)*2-2] == check && field[y][(x+tempX*3)*2-2] == check) winner = true; // 대각선 \ 검사
            }
        }
    }

    public void DrawAll()
    {
        // draw field
        for (int y = 0; y < FIELD_HEIGHT; y++)
        {
            for (int x = 0; x < FIELD_WIDTH; x++)
            {
                drawToBuffer(x, y, "┌┐─┬└┘┴├┤┼●○".charAt(field[y][x]));
            }
        }
        for(int x = 0; x < FIELD_WIDTH; x++)
        {
            drawToBuffer(x,-1, ' ');
        }
        drawCurrentScore();
        drawPreviousScore();
        if(winner)
        {
            drawWinner();
        }
        else drawCurrentRock();
        drawToBuffer(16, 6, "made by jjudy<3");
        render();
    }

    private void render()
    {
        StringBuilder sb = new StringBuilder();
        for(int y = 0; y < SCREEN_HEIGHT; y++)
        {
            for(int x = 0; x < SCREEN_WIDTH; x++)
            {
                sb.append(buffer[y][x]);
            }
            sb.append("\n");
        }
        textArea.setText(sb.toString());
    }

    private void drawCurrentScore()
    {
        drawToBuffer(FIELD_WIDTH + LEFT_PADDING + 2, -2, "┌───CURRENT───┐");
        drawToBuffer(FIELD_WIDTH + LEFT_PADDING + 2, -1, "│             │");
        drawToBuffer(FIELD_WIDTH + LEFT_PADDING + 2, 0, "└─────────────┘");
        drawToBuffer(FIELD_WIDTH + LEFT_PADDING + 5, -1, "●:" + BlackScore + "   ○:" + WhiteScore);
    }

    private void drawPreviousScore()
    {
        drawToBuffer(FIELD_WIDTH + LEFT_PADDING + 2, 2, "┌───PREVIOUS──┐");
        drawToBuffer(FIELD_WIDTH + LEFT_PADDING + 2, 3, "│             │");
        drawToBuffer(FIELD_WIDTH + LEFT_PADDING + 2, 4, "└─────────────┘");
        drawToBuffer(FIELD_WIDTH + LEFT_PADDING + 5, 3, previousScore);
    }

    private void drawWinner()
    {
        drawToBuffer(FIELD_WIDTH + LEFT_PADDING + 2, 1, "===============");
        drawToBuffer(FIELD_WIDTH + LEFT_PADDING + 2, 2, "    " + "●○".charAt(playerCheck() == 0? 1: 0) + "'s Win!!  ");
        drawToBuffer(FIELD_WIDTH + LEFT_PADDING + 2, 3,"                ");
        drawToBuffer(FIELD_WIDTH + LEFT_PADDING + 2, 4, " Again?  (Y/N) ");
        drawToBuffer(FIELD_WIDTH + LEFT_PADDING + 2, 5, "===============");
    }



}



