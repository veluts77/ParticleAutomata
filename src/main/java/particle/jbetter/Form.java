package particle.jbetter;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Form extends JFrame implements Runnable {

    private int frame = 0;

    private BufferedImage img = new BufferedImage(
            Settings.w,
            Settings.h,
            BufferedImage.TYPE_INT_RGB);

    // array for dividing scene into parts to reduce complexity
    private final Field[][] fields = new Field[Settings.fw][Settings.fh];
    private Logic logic;
    private Force force;

    public Form() {
        this.force = new Force();

        for (int i = 0; i < Settings.fw; i++) {
            for (int j = 0; j < Settings.fh; j++) {
                fields[i][j] = new Field();
            }
        }
        // put particles randomly
        for (int i = 0; i < Settings.NODE_COUNT; i++) {
            add(
                    (int)(Math.random() * force.couplingLength()),
                    (float)(Math.random() * Settings.w),
                    (float)(Math.random() * Settings.h)
            );
        }

        this.setSize(Settings.w + 16, Settings.h + 38);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocation(50, 50);
        this.add(new JLabel(new ImageIcon(img)));
        this.logic = new Logic(fields, force);
    }

    private Particle add(int type, float x, float y) {
        Particle p = new Particle(ParticleType.values()[type], x, y);
        fields[(int) (p.x / Settings.MAX_DIST)][(int) (p.y / Settings.MAX_DIST)].particles.add(p);
        return p;
    }

    @Override
    public void run() {
        while(true) {
            this.repaint();
        }
    }

    @Override
    public void paint(Graphics g) {
        long beginTime = System.currentTimeMillis();
        drawScene(img);
        for (int i = 0; i < Settings.SKIP_FRAMES; i++) logic.logic();
        ((Graphics2D)g).drawImage(img, null, 8, 30);
        frame++;
        long endTime = System.currentTimeMillis();
        String time = String.valueOf(endTime - beginTime);
        g.setColor(Color.yellow);
        g.drawString(time, 30, 42);
    }

    private void drawScene(BufferedImage image) {
        long beginTime = System.currentTimeMillis();
        Graphics2D g2 = image.createGraphics();
        g2.setColor(Settings.BG);
        g2.fillRect(0, 0, Settings.w, Settings.h);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (int i = 0; i < Settings.fw; i++) {
            for (int j = 0; j < Settings.fh; j++) {
                Field field = fields[i][j];
                for (int i1 = 0; i1 < field.particles.size(); i1++) {
                    Particle a = field.particles.get(i1);
                    g2.setColor(a.getColor());
                    g2.fillOval(
                            (int) a.x - Settings.NODE_RADIUS,
                            (int) a.y - Settings.NODE_RADIUS,
                            Settings.NODE_RADIUS * 2,
                            Settings.NODE_RADIUS * 2);
                }
            }
        }
        g2.setColor(Settings.LINK);
        for (Link link: logic.getLinks()) {
            g2.drawLine((int) link.a.x, (int) link.a.y, (int) link.b.x, (int) link.b.y);
        }
        long endTime = System.currentTimeMillis();
        String time = String.valueOf(endTime - beginTime);
        g2.setColor(Color.white);
        g2.drawString(time, 25, 25);
    }
}