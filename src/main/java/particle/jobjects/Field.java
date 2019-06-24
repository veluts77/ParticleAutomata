package particle.jobjects;

import java.util.ArrayList;

public class Field {

    private ArrayList<Particle> particles;

    Field() {
        particles = new ArrayList<>();
    }

    void add(Particle p) {
        particles.add(p);
    }

    int totalParticles() {
        return particles.size();
    }

    Particle particleByIndex(int i) {
        return particles.get(i);
    }

    void remove(Particle p) {
        particles.remove(p);
    }
}