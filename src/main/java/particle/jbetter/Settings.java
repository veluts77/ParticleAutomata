package particle.jbetter;

import java.awt.*;

public class Settings {
    static final int w = 1000;
    static final int h = 800;

    static final Color BG = new Color(20, 55, 75, 255);
    static final Color LINK = new Color(255, 230, 0, 100);

    static final int NODE_RADIUS = 5;
    static final int NODE_COUNT = 800;
    static final int MAX_DIST = 100;
    static final int MAX_DIST2 = MAX_DIST * MAX_DIST;
    static final float SPEED = 4f;
    static final int SKIP_FRAMES = 1;
    static final int BORDER = 30;

    static final int fw = w / MAX_DIST + 1;
    static final int fh = h / MAX_DIST + 1;

    static final float LINK_FORCE = -0.015f;
}
