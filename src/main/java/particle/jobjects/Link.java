package particle.jobjects;

import static particle.jobjects.Settings.*;

public class Link {

    Particle a;
    Particle b;

    public Link(Particle a, Particle b) {
        this.a = a;
        this.b = b;
    }

    float squaredDistance() {
        return a.squaredDistanceTo(b);
    }

    double angle() {
        return a.angleTo(b);
    }

    void unlink() {
        a.links--;
        b.links--;
        a.bonds.remove(b);
        b.bonds.remove(a);
    }
}