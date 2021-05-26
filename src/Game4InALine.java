import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;

public class Game4InALine extends JFrame implements KeyListener
{
    private GameHandler handler;
    private JTextArea textArea = new JTextArea();

    public Game4InALine()
    {
        setTitle("Let's play 4 In A Line");
        setSize(600,400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        textArea.setFont(new Font("Consolas", Font.PLAIN, 30));
        textArea.addKeyListener(this);
        textArea.setEditable(false);
        add(textArea);
        setVisible(true);

        handler = new GameHandler(textArea);
        new Thread(new GameThread()).start();

    }

    public static void main(String[] args)
    {
        new Game4InALine();
    }

    public void replay()
    {
        handler.initData();
        new Thread(new GameThread()).start();
    }

    class GameThread implements Runnable
    {
        @Override
        public void run()
        {
            // game loop
            while(!handler.isGameOver())
            {
                // 게임을 만들기 위한 4단계
                // 1. Game Timing ------------------------
                handler.gameTiming();

                // 3. Game logic -------------------------
                handler.gameLogic();

                // 4. Render Output ----------------------
                handler.DrawAll();
            }
        }
    }

    // 2. Get Input --------------------------
    @Override
    public void keyPressed(KeyEvent e)
    {
        switch(e.getKeyCode())
        {
            case KeyEvent.VK_RIGHT:
                handler.moveRight();
                break;
            case KeyEvent.VK_LEFT:
                handler.moveLeft();
                break;
            case KeyEvent.VK_DOWN:
                if(!handler.getWinner()) handler.moveDown();
                break;
            case KeyEvent.VK_Y:
                if(handler.getWinner()) replay();
                break;
            case KeyEvent.VK_N:
                if(handler.getWinner())
                {
                    handler.writeScore();
                    System.exit(0);
                }

                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e)
    {
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
    }
}


