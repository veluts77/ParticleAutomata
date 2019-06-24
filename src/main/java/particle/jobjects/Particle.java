package particle.jobjects;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

import static particle.jobjects.Settings.*;
import static particle.jobjects.Settings.h;

public class Particle {

    private ParticleType type;
    public float x;
    public float y;
    public float sx;
    public float sy;
    public int links;
    public Set<Particle> bonds;

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

    public void adjustPosition() {
        x += sx;
        y += sy;
    }

    public void slowDownVelocity() {
        sx *= 0.98f;
        sy *= 0.98f;
    }

    public void normalizeVelocity() {
        // velocity normalization
        // idk if it is still necessary
        float magnitude = (float)Math.sqrt(sx * sx + sy * sy);
        if(magnitude > 1f) {
            sx /= magnitude;
            sy /= magnitude;
        }
        // border repulsion
        if(x < BORDER) {
            sx += SPEED * 0.05f;
            if(x < 0) {
                x = -x;
                sx *= -0.5f;
            }
        }
        else if(x > w - BORDER) {
            sx -= SPEED * 0.05f;
            if(x > w) {
                x = w * 2 - x;
                sx *= -0.5f;
            }
        }
        if(y < BORDER) {
            sy += SPEED * 0.05f;
            if(y < 0) {
                y = -y;
                sy *= -0.5f;
            }
        }
        else if(y > h - BORDER) {
            sy -= SPEED * 0.05f;
            if(y > h) {
                y = h * 2 - y;
                sy *= -0.5f;
            }
        }
    }
}