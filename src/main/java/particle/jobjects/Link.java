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

    int screenX1() {
        return a.screenX();
    }

    int screenY1() {
        return a.screenY();
    }

    int screenX2() {
        return b.screenX();
    }

    int screenY2() {
        return b.screenY();
    }
}