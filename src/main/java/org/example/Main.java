package org.example;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("GeometryDash (Java Edition)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.setContentPane(new GeometryDashPanel());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

class GeometryDashPanel extends JPanel {
    private static final int WIDTH = 1000;
    private static final int HEIGHT = 560;

    private static final int GROUND_Y = 430;
    private static final int PLAYER_SIZE = 42;
    private static final int PLAYER_X = 190;

    private static final double GRAVITY = 0.72;
    private static final double JUMP_FORCE = -14.8;
    private static final int BASE_SCROLL_SPEED = 7;

    private static final int FRAME_TIME_MS = 16;

    private final Timer timer;
    private final Random random;

    private final Player player;
    private final List<Spike> spikes;
    private final List<Orb> orbs;
    private final List<Platform> platforms;

    private boolean running;
    private boolean gameOver;
    private int score;
    private int bestScore;
    private int distanceCounter;

    GeometryDashPanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(18, 18, 28));
        setFocusable(true);

        this.random = new Random();
        this.player = new Player(PLAYER_X, GROUND_Y - PLAYER_SIZE, PLAYER_SIZE);
        this.spikes = new ArrayList<>();
        this.orbs = new ArrayList<>();
        this.platforms = new ArrayList<>();

        resetRun();
        this.running = true;

        this.timer = new Timer(FRAME_TIME_MS, e -> {
            updateGame();
            repaint();
        });
        this.timer.start();

        registerInputHandlers();
    }

    private void registerInputHandlers() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.VK_SPACE || event.getKeyCode() == KeyEvent.VK_UP) {
                    pressJump();
                }
                if (gameOver && event.getKeyCode() == KeyEvent.VK_R) {
                    restart();
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                if (gameOver) {
                    restart();
                } else {
                    pressJump();
                }
            }
        });
    }

    private void resetRun() {
        player.reset(PLAYER_X, GROUND_Y - PLAYER_SIZE);
        spikes.clear();
        orbs.clear();
        platforms.clear();

        gameOver = false;
        score = 0;
        distanceCounter = 0;
    }

    private void restart() {
        resetRun();
        running = true;
    }

    private void endRun() {
        running = false;
        gameOver = true;
        bestScore = Math.max(bestScore, score);
    }

    private void pressJump() {
        if (!running) {
            return;
        }

        Orb hitOrb = getTouchedOrb();
        if (hitOrb != null && player.canJump()) {
            player.jump(JUMP_FORCE * 0.85);
            hitOrb.used = true;
            return;
        }

        if (player.canJump()) {
            player.jump(JUMP_FORCE);
        }
    }

    private Orb getTouchedOrb() {
        Rectangle playerBounds = player.getBounds();
        for (Orb orb : orbs) {
            Rectangle orbBounds = new Rectangle(orb.x, orb.y, orb.size, orb.size);
            if (!orb.used && playerBounds.intersects(orbBounds)) {
                return orb;
            }
        }
        return null;
    }

    private void updateGame() {
        if (!running) {
            return;
        }

        beginFrameHistory();
        player.applyGravity(GRAVITY);

        spawnObstacles();
        moveWorld();
        resolvePlayerLanding();
        cleanupOffscreenObjects();

        if (intersectsAnySpikeSwept()) {
            endRun();
        }
    }

    private void beginFrameHistory() {
        player.beginFrame();

        for (Spike spike : spikes) {
            spike.beginFrame();
        }

        for (Platform platform : platforms) {
            platform.beginFrame();
        }
    }

    private void resolvePlayerLanding() {
        player.resolveGroundCollision(GROUND_Y);

        Rectangle currentPlayer = player.getBounds();
        Rectangle previousPlayer = player.getPreviousBounds();

        for (Platform platform : platforms) {
            Rectangle platformBounds = platform.getBounds();
            boolean crossedTopThisFrame = previousPlayer.y + previousPlayer.height <= platformBounds.y
                    && currentPlayer.y + currentPlayer.height >= platformBounds.y;
            boolean overlapsHorizontally = currentPlayer.x + currentPlayer.width > platformBounds.x
                    && currentPlayer.x < platformBounds.x + platformBounds.width;

            if (crossedTopThisFrame && overlapsHorizontally) {
                player.landOn(platformBounds.y);
                currentPlayer = player.getBounds();
            }
        }
    }

    private void spawnObstacles() {
        if (spikes.isEmpty()) {
            addInitialSpike();
            return;
        }

        Spike lastSpike = spikes.get(spikes.size() - 1);
        int gap = 150 + random.nextInt(220);
        if (lastSpike.x >= WIDTH - gap) {
            return;
        }

        int startX = WIDTH + 80;
        spawnSpikeGroup(startX);
        maybeSpawnOrb(startX);
        maybeSpawnPlatform();
    }

    private void addInitialSpike() {
        int x = WIDTH + 150;
        int height = 35 + random.nextInt(40);
        spikes.add(new Spike(x, GROUND_Y - height, 38, height));
    }

    private void spawnSpikeGroup(int startX) {
        int count = random.nextDouble() < 0.3 ? 2 : 1;
        for (int i = 0; i < count; i++) {
            int height = 35 + random.nextInt(45);
            spikes.add(new Spike(startX + i * 46, GROUND_Y - height, 38, height));
        }
    }

    private void maybeSpawnOrb(int startX) {
        if (random.nextDouble() < 0.4) {
            int orbX = startX + 40;
            int orbY = GROUND_Y - 120 - random.nextInt(90);
            orbs.add(new Orb(orbX, orbY, 22));
        }
    }

    private void maybeSpawnPlatform() {
        if (random.nextDouble() < 0.35) {
            int platformX = WIDTH + 120;
            int platformY = GROUND_Y - (95 + random.nextInt(65));
            int platformWidth = 120 + random.nextInt(80);
            platforms.add(new Platform(platformX, platformY, platformWidth, 16));
        }
    }

    private void moveWorld() {
        int speed = BASE_SCROLL_SPEED + Math.min(5, score / 800);

        for (Spike spike : spikes) {
            spike.x -= speed;
        }
        for (Orb orb : orbs) {
            orb.x -= speed;
        }
        for (Platform platform : platforms) {
            platform.x -= speed;
        }

        distanceCounter += speed;
        score = distanceCounter;
    }

    private void cleanupOffscreenObjects() {
        spikes.removeIf(spike -> spike.x + spike.width < -20);
        platforms.removeIf(platform -> platform.x + platform.width < -20);

        Iterator<Orb> iterator = orbs.iterator();
        while (iterator.hasNext()) {
            Orb orb = iterator.next();
            if (orb.x + orb.size < -20 || orb.used) {
                iterator.remove();
            }
        }
    }

    private boolean intersectsAnySpikeSwept() {
        Rectangle playerNow = player.getBounds();
        Rectangle playerPrevious = player.getPreviousBounds();
        Rectangle sweptPlayer = unionRect(playerNow, playerPrevious);

        for (Spike spike : spikes) {
            Rectangle spikeNow = spike.getBounds();
            Rectangle spikePrevious = spike.getPreviousBounds();
            Rectangle sweptSpike = unionRect(spikeNow, spikePrevious);

            if (sweptPlayer.intersects(sweptSpike)) {
                return true;
            }
        }

        return false;
    }

    private Rectangle unionRect(Rectangle a, Rectangle b) {
        int left = Math.min(a.x, b.x);
        int top = Math.min(a.y, b.y);
        int right = Math.max(a.x + a.width, b.x + b.width);
        int bottom = Math.max(a.y + a.height, b.y + b.height);
        return new Rectangle(left, top, right - left, bottom - top);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2 = (Graphics2D) graphics;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawBackground(g2);
        drawGround(g2);
        drawObjects(g2);
        drawHud(g2);

        if (gameOver) {
            drawGameOver(g2);
        }
    }

    private void drawBackground(Graphics2D g2) {
        g2.setColor(new Color(38, 46, 84));
        g2.fillRect(0, 0, WIDTH, HEIGHT);

        g2.setColor(new Color(48, 58, 104));
        for (int i = 0; i < WIDTH; i += 80) {
            int y = 80 + (int) (20 * Math.sin((distanceCounter + i) * 0.015));
            g2.fillRect(i, y, 56, 6);
        }

        g2.setColor(new Color(60, 70, 120));
        for (int i = 0; i < WIDTH; i += 140) {
            int y = 180 + (int) (25 * Math.cos((distanceCounter + i) * 0.01));
            g2.fillRect(i, y, 100, 8);
        }
    }

    private void drawGround(Graphics2D g2) {
        g2.setColor(new Color(245, 180, 40));
        g2.fillRect(0, GROUND_Y, WIDTH, HEIGHT - GROUND_Y);

        g2.setColor(new Color(255, 210, 55));
        for (int i = -50; i < WIDTH + 50; i += 40) {
            int x = i - (distanceCounter % 40);
            g2.fillRect(x, GROUND_Y - 5, 24, 5);
        }
    }

    private void drawObjects(Graphics2D g2) {
        player.draw(g2);

        for (Platform platform : platforms) {
            platform.draw(g2);
        }

        for (Spike spike : spikes) {
            spike.draw(g2);
        }

        for (Orb orb : orbs) {
            orb.draw(g2);
        }
    }

    private void drawHud(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Monospaced", Font.BOLD, 24));
        g2.drawString("Score: " + score, 20, 36);
        g2.drawString("Best: " + bestScore, 20, 66);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 15));
        g2.drawString("Space/Up/Click to jump. Hit yellow orbs in air for extra jump.", 20, HEIGHT - 18);
    }

    private void drawGameOver(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRect(0, 0, WIDTH, HEIGHT);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 56));
        g2.drawString("GAME OVER", WIDTH / 2 - 170, HEIGHT / 2 - 30);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 24));
        g2.drawString("Press R or Click to restart", WIDTH / 2 - 145, HEIGHT / 2 + 20);
    }
}

class Player {
    private static final int MAX_JUMPS = 2;

    private double x;
    private double y;
    private final int size;
    private double velocityY;
    private double rotation;
    private boolean grounded;
    private double previousY;
    private int jumpsUsed;

    Player(double x, double y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.velocityY = 0;
        this.rotation = 0;
        this.grounded = true;
        this.previousY = y;
        this.jumpsUsed = 0;
    }

    void reset(double newX, double newY) {
        this.x = newX;
        this.y = newY;
        this.velocityY = 0;
        this.rotation = 0;
        this.grounded = true;
        this.previousY = newY;
        this.jumpsUsed = 0;
    }

    void beginFrame() {
        previousY = y;
    }

    void applyGravity(double gravity) {
        velocityY += gravity;
        y += velocityY;

        grounded = false;
        if (velocityY != 0) {
            rotation += 8;
        }
    }

    void resolveGroundCollision(int groundY) {
        if (y + size >= groundY) {
            landOn(groundY);
        }
    }

    void landOn(int surfaceY) {
        y = surfaceY - size;
        velocityY = 0;
        grounded = true;
        rotation = 0;
        jumpsUsed = 0;
    }

    void jump(double jumpForce) {
        if (!canJump()) {
            return;
        }

        velocityY = jumpForce;
        grounded = false;
        jumpsUsed++;
    }

    boolean canJump() {
        return jumpsUsed < MAX_JUMPS;
    }

    boolean onGround() {
        return grounded;
    }

    Rectangle getBounds() {
        return new Rectangle((int) Math.round(x), (int) Math.round(y), size, size);
    }

    Rectangle getPreviousBounds() {
        return new Rectangle((int) Math.round(x), (int) Math.round(previousY), size, size);
    }

    void draw(Graphics2D g2) {
        int centerX = (int) x + size / 2;
        int centerY = (int) y + size / 2;

        g2.translate(centerX, centerY);
        g2.rotate(Math.toRadians(rotation));

        g2.setColor(new Color(40, 255, 210));
        g2.fillRect(-size / 2, -size / 2, size, size);

        g2.setColor(new Color(10, 150, 130));
        g2.fillRect(-size / 2 + 7, -size / 2 + 7, size - 14, size - 14);

        g2.setColor(Color.WHITE);
        g2.fillRect(-8, -8, 6, 6);
        g2.fillRect(2, -8, 6, 6);

        g2.rotate(Math.toRadians(-rotation));
        g2.translate(-centerX, -centerY);
    }
}

class Spike {
    int x;
    final int y;
    final int width;
    final int height;
    private int previousX;

    Spike(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.previousX = x;
    }

    void beginFrame() {
        previousX = x;
    }

    Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    Rectangle getPreviousBounds() {
        return new Rectangle(previousX, y, width, height);
    }

    void draw(Graphics2D g2) {
        int[] px = {x, x + width / 2, x + width};
        int[] py = {y + height, y, y + height};

        g2.setColor(new Color(255, 90, 90));
        g2.fillPolygon(px, py, 3);

        g2.setColor(new Color(180, 40, 40));
        g2.drawPolygon(px, py, 3);
    }
}

class Platform {
    int x;
    final int y;
    final int width;
    final int height;
    private int previousX;

    Platform(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.previousX = x;
    }

    void beginFrame() {
        previousX = x;
    }

    Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    Rectangle getPreviousBounds() {
        return new Rectangle(previousX, y, width, height);
    }

    void draw(Graphics2D g2) {
        g2.setColor(new Color(110, 230, 255));
        g2.fillRoundRect(x, y, width, height, 10, 10);

        g2.setColor(new Color(70, 170, 210));
        g2.drawRoundRect(x, y, width, height, 10, 10);
    }
}

class Orb {
    int x;
    final int y;
    final int size;
    boolean used;

    Orb(int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.used = false;
    }

    void draw(Graphics2D g2) {
        g2.setColor(new Color(255, 230, 60));
        g2.fillOval(x, y, size, size);

        g2.setColor(new Color(255, 190, 20));
        g2.drawOval(x, y, size, size);

        g2.setColor(new Color(255, 255, 190));
        g2.fillOval(x + 6, y + 6, size - 12, size - 12);
    }
}
