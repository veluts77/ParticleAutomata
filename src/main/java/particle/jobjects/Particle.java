package particle.jobjects;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

import static particle.jobjects.Settings.*;

public class Particle {

    private ParticleType type;
    private float x;
    private float y;
    private float sx;
    private float sy;
    private int links;
    private Set<Particle> bonds;

    public Particle(ParticleType type, float x, float y) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.sx = 0;
        this.sy = 0;
        this.links = 0;
        this.bonds = new HashSet<>();
    }

    public int getType() {
        return type.type - 1;
    }

    public Color getColor() {
        return type.color;
    }

    int screenX() {
        return (int) x;
    }

    int screenY() {
        return (int) y;
    }

    int xField() {
        return (int) (x / MAX_DIST);
    }

    int yField() {
        return (int) (y / MAX_DIST);
    }

    float squaredDistanceTo(Particle b) {
        return (x - b.x) * (x - b.x) + (y - b.y) * (y - b.y);
    }

    double angleTo(Particle another) {
        return Math.atan2(y - another.y, x - another.x);
    }

    float couplingWith(Particle another) {
        return COUPLING[getType()][another.getType()];
    }

    boolean isNotLinkedTo(Particle another) {
        return !bonds.contains(another);
    }

    boolean freeLinksAvailable() {
        return links < LINKS[getType()];
    }

    boolean mayLinkTo(Particle another) {
        int typeCountA = 0;
        for (Particle p : bonds) {
            if (p.getType() == another.getType()) typeCountA++;
        }
        int typeCountB = 0;
        for (Particle p : another.bonds) {
            if (p.getType() == getType()) typeCountB++;
        }
        return typeCountA < LINKS_POSSIBLE[getType()][another.getType()] &&
                typeCountB < LINKS_POSSIBLE[another.getType()][getType()];
    }

    void adjustPosition() {
        x += sx;
        y += sy;
    }

    void slowDownVelocity() {
        sx *= 0.98f;
        sy *= 0.98f;
    }

    void addVelocityToPositiveDirection(double angle, float d) {
        sx += (float)Math.cos(angle) * d * SPEED;
        sy += (float)Math.sin(angle) * d * SPEED;
    }

    void addVelocityToNegativeDirection(double angle, float d) {
        addVelocityToPositiveDirection(angle, -d);
    }

    void normalizeVelocity() {
        // velocity normalization
        // idk if it is still necessary
        float magnitude = (float) Math.sqrt(sx * sx + sy * sy);
        if (magnitude > 1f) {
            sx /= magnitude;
            sy /= magnitude;
        }
        // border repulsion
        if (x < BORDER) {
            sx += SPEED * 0.05f;
            if (x < 0) {
                x = -x;
                sx *= -0.5f;
            }
        } else if (x > w - BORDER) {
            sx -= SPEED * 0.05f;
            if (x > w) {
                x = w * 2 - x;
                sx *= -0.5f;
            }
        }
        if (y < BORDER) {
            sy += SPEED * 0.05f;
            if (y < 0) {
                y = -y;
                sy *= -0.5f;
            }
        } else if (y > h - BORDER) {
            sy -= SPEED * 0.05f;
            if (y > h) {
                y = h * 2 - y;
                sy *= -0.5f;
            }
        }
    }

    void linkWith(Particle another) {
        this.bonds.add(another);
        another.bonds.add(this);
        this.links++;
        another.links++;
    }

    void unlinkFrom(Particle another) {
        this.links--;
        another.links--;
        this.bonds.remove(another);
        another.bonds.remove(this);
    }
}