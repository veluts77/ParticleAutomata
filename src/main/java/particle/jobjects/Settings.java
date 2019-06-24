package particle.jobjects;

public class Settings {
    final static int NODE_RADIUS = 5;
    final static int NODE_COUNT = 500;
    final static int MAX_DIST = 100;
    final static int w = 1000;
    final static int h = 800;
    final static float SPEED = 15f;
    final static int SKIP_FRAMES = 1;
    final static int BORDER = 30;
    final static float LINK_FORCE = -0.015f;

    final static float[][] COUPLING = {
            {1, 1, -1},
            {1, 1, 1},
            {1, 1, 1}
    };

    final static int[] LINKS = {
            1,
            3,
            2
    };

    final static float[][] LINKS_POSSIBLE = {
            {0, 1, 1},
            {1, 2, 1},
            {1, 1, 2}
    };
}
