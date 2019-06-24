package particle.jobjects;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import static particle.jobjects.Settings.*;

public class Form extends JFrame implements Runnable {

    private final Color BG = new Color(20, 55, 75, 255);
    private final Color LINK = new Color(255, 230, 0, 100);

    private BufferedImage img = new BufferedImage(
            w, h, BufferedImage.TYPE_INT_RGB);

    private Fields fields;

    public Form() {
        fields = new Fields();
        fields.addRandomParticles();

        this.setSize(w + 16, h + 38);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocation(50, 50);
        this.add(new JLabel(new ImageIcon(img)));
    }


    @Override
    public void run() {
        while (true) {
            repaint();
            try {
                Thread.sleep(15);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        long beginTime = System.currentTimeMillis();

        drawScene(img);
        for (int i = 0; i < SKIP_FRAMES; i++) logic();
        ((Graphics2D) g).drawImage(img, null, 8, 30);

        long endTime = System.currentTimeMillis();
        showTime(g, endTime - beginTime);
    }

    private void drawScene(BufferedImage image) {
        long beginTime = System.currentTimeMillis();

        Graphics2D g2 = image.createGraphics();
        g2.setColor(BG);
        g2.fillRect(0, 0, w, h);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        fields.eachParticleDo(p -> {
            g2.setColor(p.getColor());
            g2.fillOval(
                    (int) p.x - NODE_RADIUS,
                    (int) p.y - NODE_RADIUS,
                    NODE_RADIUS * 2,
                    NODE_RADIUS * 2
            );
        });

        g2.setColor(LINK);
        fields.eachLinkDo(link ->
            g2.drawLine(
                    (int) link.a.x,
                    (int) link.a.y,
                    (int) link.b.x,
                    (int) link.b.y
            )
        );

        long endTime = System.currentTimeMillis();
        showInnerTime(g2, endTime - beginTime);
    }

    private void logic() {
        fields.logic();
    }

    private void showTime(Graphics g, long time) {
        g.setColor(Color.yellow);
        g.drawString(String.valueOf(time), 30, 42);
    }

    private void showInnerTime(Graphics g, long time) {
        g.setColor(Color.white);
        g.drawString(String.valueOf(time), 25, 25);
    }
}
