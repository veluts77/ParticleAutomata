package particle.jobjects;

import static particle.jobjects.Settings.LINK_FORCE;

public class Link {

    Particle a;
    Particle b;

    public Link(Particle a, Particle b) {
        this.a = a;
        this.b = b;
        doLink();
    }

    private void doLink() {
        a.bonds.add(b);
        b.bonds.add(a);
        a.links++;
        b.links++;
    }

    void unlink() {
        a.links--;
        b.links--;
        a.bonds.remove(b);
        b.bonds.remove(a);
    }

    float squaredDistance() {
        return a.squaredDistanceTo(b);
    }

    double angle() {
        return a.angleTo(b);
    }

    void adjustParticlesVelocity() {
        double angle = angle();
        a.addVelocityToPositiveDirection(angle, LINK_FORCE);
        b.addVelocityToNegativeDirection(angle, LINK_FORCE);
    }
}