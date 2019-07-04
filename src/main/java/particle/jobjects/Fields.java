package particle.jobjects;

import java.util.ArrayList;
import java.util.function.Consumer;

import static particle.jobjects.Settings.*;

class Fields {

    private final int fw = w / MAX_DIST + 1;
    private final int fh = h / MAX_DIST + 1;
    private final int MAX_DIST2 = MAX_DIST * MAX_DIST;

    // array for dividing scene into parts to reduce complexity
    private final Field[][] fields = new Field[fw][fh];

    private final ArrayList<Link> links = new ArrayList<>();

    Fields() {
        for (int i = 0; i < fw; i++) {
            for (int j = 0; j < fh; j++) {
                fields[i][j] = new Field();
            }
        }
    }

    void addRandomParticles() {
        for (int i = 0; i < NODE_COUNT; i++) {
            addOneParticle((int) (Math.random() * COUPLING.length),
                    (float) (Math.random() * w),
                    (float) (Math.random() * h));
        }
    }

    private void addOneParticle(int type, float x, float y) {
        Particle p = new Particle(ParticleType.values()[type], x, y);
        fieldFor(p).add(p);
    }

    void eachParticleDo(Consumer<Particle> consumer) {
        for (int i = 0; i < fw; i++) {
            for (int j = 0; j < fh; j++) {
                Field field = fields[i][j];
                for (int i1 = 0; i1 < field.totalParticles(); i1++) {
                    Particle p = field.particleByIndex(i1);
                    consumer.accept(p);
                }
            }
        }
    }

    void eachLinkDo(Consumer<Link> consumer) {
        for (Link link : links) {
            consumer.accept(link);
        }
    }

    private Field fieldFor(Particle p) {
        return fields[p.xField()][p.yField()];
    }

    void logic() {
        eachParticleDo(particle -> {
            particle.adjustPosition();
            particle.slowDownVelocity();
            particle.normalizeVelocity();
        });

        for (int i = 0; i < links.size(); i++) {
            Link link = links.get(i);
            float d2 = link.squaredDistance();
            if (d2 > MAX_DIST2 / 4f) {
                link.unlink();
                links.remove(link);
                i--;
            } else if (d2 > NODE_RADIUS * NODE_RADIUS * 4) {
                link.adjustParticlesVelocity();
            }
        }
        // moving particle to another field
        for (int i = 0; i < fw; i++) {
            for (int j = 0; j < fh; j++) {
                Field field = fields[i][j];
                for (int i1 = 0; i1 < field.totalParticles(); i1++) {
                    Particle p = field.particleByIndex(i1);
                    if ((p.xField() != i) || (p.yField() != j)) {
                        field.remove(p);
                        fieldFor(p).add(p);
                    }
                }
            }
        }
        // dividing scene into parts to reduce complexity
        for (int i = 0; i < fw; i++) {
            for (int j = 0; j < fh; j++) {
                Field field = fields[i][j];
                for (int i1 = 0; i1 < field.totalParticles(); i1++) {
                    Particle a = field.particleByIndex(i1);
                    Particle particleToLink = null;
                    float particleToLinkMinDist2 = (w + h) * (w + h);
                    for (int j1 = i1 + 1; j1 < field.totalParticles(); j1++) {
                        Particle b = field.particleByIndex(j1);
                        float d2 = applyForce(a, b);
                        if (d2 != -1 && d2 < particleToLinkMinDist2) {
                            particleToLinkMinDist2 = d2;
                            particleToLink = b;
                        }
                    }
                    if (i < fw - 1) {
                        int iNext = i + 1;
                        Field field1 = fields[iNext][j];
                        for (int j1 = 0; j1 < field1.totalParticles(); j1++) {
                            Particle b = field1.particleByIndex(j1);
                            float d2 = applyForce(a, b);
                            if (d2 != -1 && d2 < particleToLinkMinDist2) {
                                particleToLinkMinDist2 = d2;
                                particleToLink = b;
                            }
                        }
                    }
                    if (j < fh - 1) {
                        int jNext = j + 1;
                        Field field1 = fields[i][jNext];
                        for (int j1 = 0; j1 < field1.totalParticles(); j1++) {
                            Particle b = field1.particleByIndex(j1);
                            float d2 = applyForce(a, b);
                            if (d2 != -1 && d2 < particleToLinkMinDist2) {
                                particleToLinkMinDist2 = d2;
                                particleToLink = b;
                            }
                        }
                        if (i < fw - 1) {
                            int iNext = i + 1;
                            Field field2 = fields[iNext][jNext];
                            for (int j1 = 0; j1 < field2.totalParticles(); j1++) {
                                Particle b = field2.particleByIndex(j1);
                                float d2 = applyForce(a, b);
                                if (d2 != -1 && d2 < particleToLinkMinDist2) {
                                    particleToLinkMinDist2 = d2;
                                    particleToLink = b;
                                }
                            }
                        }
                    }
                    if (particleToLink != null) {
                        links.add(new Link(a, particleToLink));
                    }
                }
            }
        }
    }

    private float applyForce(Particle a, Particle b) {
        float d2 = a.squaredDistanceTo(b);
        boolean canLink = false;
        if (d2 < MAX_DIST2) {
            float dA = a.couplingWith(b) / d2;
            float dB = b.couplingWith(a) / d2;
            if (a.freeLinksAvailable() && b.freeLinksAvailable()) {
                canLink = (d2 < MAX_DIST2 / 4f) &&
                        notYetLinked(a, b) &&
                        a.mayLinkTo(b);
            } else {
                if (notYetLinked(a, b)) {
                    dA = 1 / d2;
                    dB = 1 / d2;
                }
            }
            double angle = a.angleTo(b);
            if (d2 < 1) d2 = 1;
            if (d2 < NODE_RADIUS * NODE_RADIUS * 4) {
                dA = 1 / d2;
                dB = 1 / d2;
            }
            a.addVelocityToPositiveDirection(angle, dA);
            b.addVelocityToNegativeDirection(angle, dB);
        }
        if (canLink) return d2;
        return -1;
    }

    private boolean notYetLinked(Particle a, Particle b) {
        return a.isNotLinkedTo(b) && b.isNotLinkedTo(a);
    }
}
