package particle.jbetter;

public class Force {

    private static final float[][] COUPLING = {
            {1, 1, -1},
            {1, 1, 1},
            {1, 1, 1}
    };

    private static int[] LINKS = {
            1,
            3,
            2
    };

    private static final float[][] LINKS_POSSIBLE = {
            {0, 1, 1},
            {1, 2, 1},
            {1, 1, 2}
    };

    int couplingLength() {
        return COUPLING.length;
    }

    float applyForce(Particle a, Particle b) {
        float d2 = (a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y);
        boolean canLink = false;
        if(d2 < Settings.MAX_DIST2) {
            float dA = COUPLING[a.getType()][b.getType()] / d2;
            float dB = COUPLING[b.getType()][a.getType()] / d2;
            if (a.links < LINKS[a.getType()] && b.links < LINKS[b.getType()]) {
                if(d2 < Settings.MAX_DIST2 / 4) {
                    if (!a.bonds.contains(b) && !b.bonds.contains(a)) {
                        int typeCountA = 0;
                        for (Particle p : a.bonds) {
                            if (p.getType() == b.getType()) typeCountA++;
                        }
                        int typeCountB = 0;
                        for (Particle p : b.bonds) {
                            if (p.getType() == a.getType()) typeCountB++;
                        }
                        if (typeCountA < LINKS_POSSIBLE[a.getType()][b.getType()] && typeCountB < LINKS_POSSIBLE[b.getType()][a.getType()]) {
                            canLink = true;
                        }
                    }
                }
            }
            else {
                if (!a.bonds.contains(b) && !b.bonds.contains(a)) {
                    dA = 1 / d2;
                    dB = 1 / d2;
                }
            }
            double angle = Math.atan2(a.y - b.y, a.x - b.x);
            if(d2 < 1) d2 = 1;
            if(d2 < Settings.NODE_RADIUS * Settings.NODE_RADIUS * 4) {
                dA = 1 / d2;
                dB = 1 / d2;
            }
            a.sx += (float)Math.cos(angle) * dA * Settings.SPEED;
            a.sy += (float)Math.sin(angle) * dA * Settings.SPEED;
            b.sx -= (float)Math.cos(angle) * dB * Settings.SPEED;
            b.sy -= (float)Math.sin(angle) * dB * Settings.SPEED;
        }
        if(canLink) {
            return d2;
        }
        return -1;
    }

}
