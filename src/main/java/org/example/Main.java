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

    private final Timer timer;
    private final Player player;
    private final List<Spike> spikes;
    private final List<Orb> orbs;
    private final Random random;

    private boolean running;
    private boolean gameOver;
    private int score;
    private int bestScore;
    private int distanceCounter;

    GeometryDashPanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(18, 18, 28));
        setFocusable(true);

        this.player = new Player(PLAYER_X, GROUND_Y - PLAYER_SIZE, PLAYER_SIZE);
        this.spikes = new ArrayList<>();
        this.orbs = new ArrayList<>();
        this.random = new Random();

        resetRun();
        this.running = true;

        this.timer = new Timer(16, e -> {
            updateGame();
            repaint();
        });
        this.timer.start();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_UP) {
                    pressJump();
                }
                if (gameOver && e.getKeyCode() == KeyEvent.VK_R) {
                    restart();
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
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
        gameOver = false;
        score = 0;
        distanceCounter = 0;
    }

    private void restart() {
        resetRun();
        running = true;
    }

    private void pressJump() {
        if (!running) {
            return;
        }

        Orb hitOrb = getTouchedOrb();
        if (hitOrb != null) {
            player.forceJump(JUMP_FORCE * 0.85);
            hitOrb.used = true;
            return;
        }

        if (player.onGround()) {
            player.forceJump(JUMP_FORCE);
        }
    }

    private Orb getTouchedOrb() {
        for (Orb orb : orbs) {
            if (!orb.used && player.intersects(orb.x, orb.y, orb.size, orb.size)) {
                return orb;
            }
        }
        return null;
    }

    private void updateGame() {
        if (!running) {
            return;
        }

        player.applyPhysics(GRAVITY, GROUND_Y);

        spawnObstacles();
        moveWorld();
        cleanupObjects();

        if (checkCollision()) {
            running = false;
            gameOver = true;
            bestScore = Math.max(bestScore, score);
        }
    }

    private void spawnObstacles() {
        if (spikes.isEmpty()) {
            int x = WIDTH + 150;
            int height = 35 + random.nextInt(40);
            spikes.add(new Spike(x, GROUND_Y - height, 38, height));
            return;
        }

        Spike last = spikes.get(spikes.size() - 1);
        int gap = 150 + random.nextInt(220);
        if (last.x < WIDTH - gap) {
            int count = random.nextDouble() < 0.3 ? 2 : 1;
            int startX = WIDTH + 80;
            for (int i = 0; i < count; i++) {
                int height = 35 + random.nextInt(45);
                spikes.add(new Spike(startX + i * 46, GROUND_Y - height, 38, height));
            }

            if (random.nextDouble() < 0.35) {
                int orbX = startX + 40;
                int orbY = GROUND_Y - 120 - random.nextInt(90);
                orbs.add(new Orb(orbX, orbY, 22));
            }
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

        distanceCounter += speed;
        score = distanceCounter;
    }

    private void cleanupObjects() {
        spikes.removeIf(spike -> spike.x + spike.width < -20);

        Iterator<Orb> iterator = orbs.iterator();
        while (iterator.hasNext()) {
            Orb orb = iterator.next();
            if (orb.x + orb.size < -20 || orb.used) {
                iterator.remove();
            }
        }
    }

    private boolean checkCollision() {
        for (Spike spike : spikes) {
            if (player.intersects(spike.x + 4, spike.y + 3, spike.width - 8, spike.height - 3)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
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
    private double x;
    private double y;
    private final int size;
    private double velocityY;
    private double rotation;
    private boolean grounded;

    Player(double x, double y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.velocityY = 0;
        this.rotation = 0;
        this.grounded = true;
    }

    void reset(double newX, double newY) {
        this.x = newX;
        this.y = newY;
        this.velocityY = 0;
        this.rotation = 0;
        this.grounded = true;
    }

    void applyPhysics(double gravity, int groundY) {
        velocityY += gravity;
        y += velocityY;

        if (y + size >= groundY) {
            y = groundY - size;
            velocityY = 0;
            grounded = true;
            rotation = 0;
        } else {
            grounded = false;
            rotation += 8;
        }
    }

    void forceJump(double jumpForce) {
        velocityY = jumpForce;
        grounded = false;
    }

    boolean onGround() {
        return grounded;
    }

    boolean intersects(int ox, int oy, int ow, int oh) {
        return x < ox + ow && x + size > ox && y < oy + oh && y + size > oy;
    }

    void draw(Graphics2D g2) {
        int cx = (int) x + size / 2;
        int cy = (int) y + size / 2;

        g2.translate(cx, cy);
        g2.rotate(Math.toRadians(rotation));

        g2.setColor(new Color(40, 255, 210));
        g2.fillRect(-size / 2, -size / 2, size, size);

        g2.setColor(new Color(10, 150, 130));
        g2.fillRect(-size / 2 + 7, -size / 2 + 7, size - 14, size - 14);

        g2.setColor(Color.WHITE);
        g2.fillRect(-8, -8, 6, 6);
        g2.fillRect(2, -8, 6, 6);

        g2.rotate(Math.toRadians(-rotation));
        g2.translate(-cx, -cy);
    }
}

class Spike {
    int x;
    final int y;
    final int width;
    final int height;

    Spike(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
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
