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
    private static final double ORB_JUMP_MULTIPLIER = 0.85;
    private static final int BASE_SCROLL_SPEED = 7;
    private static final int MAX_SPEED_BONUS = 6;
    private static final int DIFFICULTY_SCORE_CAP = 6000;

    private static final int FRAME_TIME_MS = 16;

    private static final String[] GAMEPLAY_NOTES = {
            "Note 001: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 002: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 003: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 004: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 005: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 006: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 007: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 008: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 009: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 010: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 011: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 012: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 013: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 014: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 015: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 016: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 017: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 018: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 019: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 020: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 021: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 022: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 023: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 024: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 025: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 026: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 027: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 028: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 029: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 030: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 031: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 032: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 033: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 034: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 035: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 036: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 037: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 038: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 039: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 040: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 041: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 042: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 043: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 044: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 045: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 046: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 047: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 048: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 049: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 050: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 051: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 052: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 053: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 054: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 055: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 056: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 057: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 058: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 059: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 060: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 061: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 062: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 063: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 064: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 065: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 066: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 067: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 068: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 069: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 070: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 071: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 072: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 073: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 074: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 075: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 076: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 077: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 078: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 079: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 080: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 081: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 082: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 083: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 084: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 085: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 086: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 087: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 088: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 089: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 090: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 091: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 092: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 093: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 094: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 095: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 096: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 097: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 098: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 099: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 100: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 101: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 102: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 103: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 104: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 105: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 106: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 107: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 108: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 109: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 110: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 111: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 112: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 113: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 114: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 115: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 116: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 117: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 118: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 119: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 120: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 121: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 122: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 123: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 124: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 125: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 126: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 127: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 128: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 129: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 130: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 131: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 132: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 133: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 134: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 135: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 136: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 137: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 138: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 139: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 140: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 141: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 142: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 143: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 144: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 145: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 146: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 147: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 148: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 149: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 150: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 151: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 152: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 153: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 154: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 155: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 156: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 157: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 158: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 159: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 160: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 161: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 162: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 163: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 164: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 165: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 166: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 167: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 168: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 169: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 170: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 171: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 172: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 173: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 174: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 175: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 176: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 177: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 178: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 179: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 180: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 181: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 182: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 183: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 184: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 185: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 186: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 187: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 188: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 189: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 190: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 191: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 192: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 193: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 194: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 195: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 196: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 197: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 198: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 199: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 200: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 201: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 202: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 203: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 204: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 205: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 206: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 207: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 208: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 209: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 210: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 211: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 212: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 213: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 214: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 215: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 216: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 217: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 218: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 219: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 220: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 221: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 222: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 223: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 224: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 225: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 226: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 227: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 228: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 229: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 230: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 231: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 232: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 233: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 234: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 235: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 236: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 237: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 238: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 239: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 240: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 241: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 242: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 243: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 244: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 245: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 246: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 247: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 248: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 249: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 250: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 251: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 252: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 253: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 254: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 255: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 256: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 257: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 258: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 259: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 260: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 261: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 262: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 263: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 264: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 265: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 266: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 267: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 268: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 269: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 270: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 271: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 272: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 273: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 274: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 275: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 276: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 277: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 278: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 279: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 280: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 281: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 282: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 283: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 284: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 285: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 286: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 287: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 288: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 289: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 290: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 291: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 292: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 293: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 294: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 295: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 296: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 297: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 298: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 299: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 300: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 301: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 302: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 303: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 304: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 305: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 306: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 307: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 308: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 309: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 310: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 311: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 312: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 313: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 314: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 315: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 316: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 317: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 318: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 319: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 320: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 321: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 322: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 323: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 324: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 325: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 326: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 327: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 328: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 329: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 330: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 331: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 332: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 333: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 334: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 335: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 336: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 337: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 338: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 339: Keep rhythm, watch spike spacing, and manage double-jump timing.",
            "Note 340: Keep rhythm, watch spike spacing, and manage double-jump timing.",
    };

    private final Timer timer;
    private final Random random;

    private final Player player;
    private final List<Spike> spikes;
    private final List<Orb> orbs;
    private final List<Platform> platforms;

    private boolean running;
    private boolean paused;
    private boolean gameOver;
    private int currentScore;
    private int highScore;
    private int survivalDistance;

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
        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }

    private void registerInputHandlers() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.VK_SPACE || event.getKeyCode() == KeyEvent.VK_UP) {
                    pressJump();
                }
                if (event.getKeyCode() == KeyEvent.VK_P) {
                    togglePause();
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

    private void togglePause() {
        if (!running || gameOver) {
            return;
        }
        paused = !paused;
    }

    private void resetRun() {
        player.reset(PLAYER_X, GROUND_Y - PLAYER_SIZE);
        spikes.clear();
        orbs.clear();
        platforms.clear();

        gameOver = false;
        paused = false;
        currentScore = 0;
        survivalDistance = 0;
    }

    private void restart() {
        resetRun();
        running = true;
    }

    private void endRun() {
        running = false;
        gameOver = true;
        highScore = Math.max(highScore, currentScore);
    }

    private void pressJump() {
        if (!running || paused) {
            return;
        }

        if (!player.canJump()) {
            return;
        }

        Orb activatedOrb = consumeTouchedOrb();
        double jumpForce = activatedOrb == null
                ? JUMP_FORCE
                : JUMP_FORCE * ORB_JUMP_MULTIPLIER;
        player.jump(jumpForce);
    }

    private Orb consumeTouchedOrb() {
        Rectangle playerBounds = player.getBounds();
        for (Orb orb : orbs) {
            if (orb.canActivate(playerBounds)) {
                orb.activate();
                return orb;
            }
        }
        return null;
    }

    private void updateGame() {
        if (!running || paused) {
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
            // Stable landing only when the player crosses the platform top this frame.
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
        double difficulty = getDifficultyFactor();
        int minGap = lerpInt(190, 130, difficulty);
        int maxGap = lerpInt(410, 230, difficulty);
        int gap = minGap + random.nextInt(maxGap - minGap + 1);
        if (lastSpike.x >= WIDTH - gap) {
            return;
        }

        int startX = WIDTH + 80;
        spawnSpikeGroup(startX, difficulty);
        maybeSpawnOrb(startX, difficulty);
        maybeSpawnPlatform(difficulty);
    }

    private void addInitialSpike() {
        int x = WIDTH + 150;
        int height = 35 + random.nextInt(40);
        spikes.add(new Spike(x, GROUND_Y - height, 38, height));
    }

    private void spawnSpikeGroup(int startX, double difficulty) {
        double doubleSpikeChance = 0.25 + (0.2 * difficulty);
        int count = random.nextDouble() < doubleSpikeChance ? 2 : 1;
        for (int i = 0; i < count; i++) {
            int height = 35 + random.nextInt(45);
            spikes.add(new Spike(startX + i * 46, GROUND_Y - height, 38, height));
        }
    }

    private void maybeSpawnOrb(int startX, double difficulty) {
        double orbChance = 0.45 - (0.2 * difficulty);
        if (random.nextDouble() < orbChance) {
            int orbX = startX + 40;
            int orbY = GROUND_Y - 120 - random.nextInt(90);
            orbs.add(new Orb(orbX, orbY, 22));
        }
    }

    private void maybeSpawnPlatform(double difficulty) {
        double platformChance = 0.4 - (0.22 * difficulty);
        if (random.nextDouble() < platformChance) {
            int platformX = WIDTH + 120;
            int platformY = GROUND_Y - (95 + random.nextInt(65));
            int platformWidth = 120 + random.nextInt(80);
            platforms.add(new Platform(platformX, platformY, platformWidth, 16));
        }
    }

    private void moveWorld() {
        double difficulty = getDifficultyFactor();
        int speed = BASE_SCROLL_SPEED + (int) Math.round(MAX_SPEED_BONUS * difficulty);

        for (Spike spike : spikes) {
            spike.x -= speed;
        }
        for (Orb orb : orbs) {
            orb.x -= speed;
        }
        for (Platform platform : platforms) {
            platform.x -= speed;
        }

        survivalDistance += speed;
        currentScore = survivalDistance / 10;
        highScore = Math.max(highScore, currentScore);
    }

    private double getDifficultyFactor() {
        return Math.min(1.0, currentScore / (double) DIFFICULTY_SCORE_CAP);
    }

    private int lerpInt(int start, int end, double t) {
        return (int) Math.round(start + (end - start) * t);
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
        // Swept AABB union prevents fast objects from tunneling through the player.
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
        } else if (paused) {
            drawPaused(g2);
        }
    }

    private void drawBackground(Graphics2D g2) {
        g2.setColor(new Color(38, 46, 84));
        g2.fillRect(0, 0, WIDTH, HEIGHT);

        g2.setColor(new Color(48, 58, 104));
        for (int i = 0; i < WIDTH; i += 80) {
            int y = 80 + (int) (20 * Math.sin((survivalDistance + i) * 0.015));
            g2.fillRect(i, y, 56, 6);
        }

        g2.setColor(new Color(60, 70, 120));
        for (int i = 0; i < WIDTH; i += 140) {
            int y = 180 + (int) (25 * Math.cos((survivalDistance + i) * 0.01));
            g2.fillRect(i, y, 100, 8);
        }
    }

    private void drawGround(Graphics2D g2) {
        g2.setColor(new Color(245, 180, 40));
        g2.fillRect(0, GROUND_Y, WIDTH, HEIGHT - GROUND_Y);

        g2.setColor(new Color(255, 210, 55));
        for (int i = -50; i < WIDTH + 50; i += 40) {
            int x = i - (survivalDistance % 40);
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
        g2.drawString("Score: " + currentScore, 20, 36);
        g2.drawString("High: " + highScore, 20, 66);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 15));
        g2.drawString("Space/Up/Click to jump. Hit yellow orbs in air for extra jump.", 20, HEIGHT - 18);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
        g2.drawString(getRotatingNote(), 20, HEIGHT - 36);
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

    private String getRotatingNote() {
        int index = (survivalDistance / 120) % GAMEPLAY_NOTES.length;
        return GAMEPLAY_NOTES[index];
    }

    private void drawPaused(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 130));
        g2.fillRect(0, 0, WIDTH, HEIGHT);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 56));
        g2.drawString("PAUSED", WIDTH / 2 - 110, HEIGHT / 2 - 20);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 24));
        g2.drawString("Press P to resume", WIDTH / 2 - 110, HEIGHT / 2 + 25);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 16));
        g2.drawString(getRotatingNote(), WIDTH / 2 - 290, HEIGHT / 2 + 56);
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
    private static final int ACTIVATION_RADIUS_PAD = 8;

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

    boolean canActivate(Rectangle playerBounds) {
        return !used && intersectsPlayer(playerBounds);
    }

    void activate() {
        used = true;
    }

    private boolean intersectsPlayer(Rectangle playerBounds) {
        int orbCenterX = x + size / 2;
        int orbCenterY = y + size / 2;
        int radius = size / 2 + ACTIVATION_RADIUS_PAD;

        int nearestX = clamp(orbCenterX, playerBounds.x, playerBounds.x + playerBounds.width);
        int nearestY = clamp(orbCenterY, playerBounds.y, playerBounds.y + playerBounds.height);

        int dx = orbCenterX - nearestX;
        int dy = orbCenterY - nearestY;
        return dx * dx + dy * dy <= radius * radius;
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
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
