import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;



public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 360;
    int boardHeight = 640;

    //images.
    Image backgroundImage;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    //bird
    int birdX = boardWidth/8;
    int birdY = boardHeight/2;
    int birdWidth = 34;
    int birdHeight = 24;

    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img){
            this.img = img;
        }
    }


    //Pipes
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    class Pipe{
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img){
            this.img = img;
        }
    }

    //game logic.
    Bird bird;
    int velocityX = -4; //moves pipe to the left (make it looks like the bird is moving right)
    int velocityY = 0;  //moves the bird up and down
    int gravity = 1;

    ArrayList<Pipe> pipes;
    Random random = new Random();

    Timer gameLoop;
    Timer placePipTimer;
    boolean gameOver = false;
    double score = 0;

    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        //setBackground(Color.blue);
        setFocusable(true);
        addKeyListener(this);

        //load images.
        backgroundImage = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        //bird
        bird = new Bird(birdImg);
        pipes = new ArrayList<Pipe>();

        //place pipe timer
        placePipTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placePipTimer.start();

        //gamer timer
        gameLoop = new Timer(1000/60, this);    // 1000/60 = 16.6
        gameLoop.start();
    
    }

    public void placePipes() {
        //(0-1) * pipeHeight/2 -> (0-256)
        //128
        //0 - 128 - (0-256) --> pipeHeight/4 -> 3/4 pipeHeight
        int randomPipeY = (int) (pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));
        int openingSpace = boardHeight/4;

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g){
        // background.
        g.drawImage(backgroundImage, 0, 0, boardWidth, boardHeight, null);
    
        //bird
        g.drawImage(birdImg, bird.x, bird.y, bird.width, bird.height, null);
        
        //pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        //score
        g.setColor(Color.white);
        g.setFont(new Font("Ariel", Font.PLAIN, 32));
        if(gameOver){
            g.drawString("GAME OVER : " + String.valueOf((int) score), 10, 35);

        }else{
            g.drawString(String.valueOf((int) score), 10, 35);
        }
    }   

    public void move(){
        //bird
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);

        //pipe
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if(!pipe.passed && bird.x > pipe.x +pipe.width){
                pipe.passed = true;
                score += 0.5;   //0.5 because there are 2 pipes
            }

            if(collision(bird, pipe)){
                gameOver = true;
            }
        }

        if(bird.y > boardHeight) {
            gameOver = true;
        }
    }

    public boolean collision(Bird a, Pipe b){
        return a.x < b.x + b.width &&   //a's top left corner doesnt reach b's top right corner.
               a.x + a.width > b.x &&   //a's top right corner passes b's top left corner.
               a.y < b.y + b.height &&  //a's top left corner doesnt reach b's bottom left corner. 
               a.y + a.height > b.y;    //a's bottom right corner passes b's top left corner.
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if(gameOver){
            placePipTimer.stop();
            gameLoop.stop();
        }
    }

    
                      
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE){
            velocityY = -9;
            if(gameOver){
                //restart the game by resetting the condition
                bird.y = birdY;
                velocityY = 0;
                pipes.clear();
                score = 0;
                gameOver = false;
                gameLoop.start();
                placePipTimer.start();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    
}
