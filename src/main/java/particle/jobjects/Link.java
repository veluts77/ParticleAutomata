package particle.jobjects;

import static particle.jobjects.Settings.LINK_FORCE;

public class Link {

    private Particle a;
    private Particle b;

    public Link(Particle a, Particle b) {
        this.a = a;
        this.b = b;
        doLink();
    }

    private void doLink() {
        a.linkWith(b);
    }

    void unlink() {
        a.unlinkFrom(b);
    }

    float squaredDistance() {
        return a.squaredDistanceTo(b);
    }

    void adjustParticlesVelocity() {
        double angle = a.angleTo(b);
        a.addVelocityToPositiveDirection(angle, LINK_FORCE);
        b.addVelocityToNegativeDirection(angle, LINK_FORCE);
    }

    int x1() {
        return (int) a.x;
    }

    int y1() {
        return (int) a.y;
    }

    int x2() {
        return (int) b.x;
    }

    int y2() {
        return (int) b.y;
    }
}