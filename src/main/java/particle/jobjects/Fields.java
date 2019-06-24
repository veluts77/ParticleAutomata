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
            addOneParticle((int)(Math.random() * COUPLING.length),
                    (float)(Math.random() * w),
                    (float)(Math.random() * h));
        }
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
        for (Link link: links) {
            consumer.accept(link);
        }
    }

    private void addOneParticle(int type, float x, float y) {
        Particle p = new Particle(ParticleType.values()[type], x, y);
        fieldFor(p).add(p);
    }

    private Field fieldFor(Particle p) {
        return fields[(int) (p.x / MAX_DIST)][(int) (p.y / MAX_DIST)];
    }

    void logic() {
        eachParticleDo(a -> {
            a.adjustPosition();
            a.slowDownVelocity();
            a.normalizeVelocity();
        });

        for (int i = 0; i < links.size(); i++) {
            Link link = links.get(i);
            Particle a = link.a;
            Particle b = link.b;
            float d2 = (a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y);
            if(d2 > MAX_DIST2 / 4f) {
                a.links--;
                b.links--;
                a.bonds.remove(b);
                b.bonds.remove(a);
                links.remove(link);
                i--;
            }
            else {
                if(d2 > NODE_RADIUS * NODE_RADIUS * 4) {
                    double angle = Math.atan2(a.y - b.y, a.x - b.x);
                    a.sx += (float)Math.cos(angle) * LINK_FORCE * SPEED;
                    a.sy += (float)Math.sin(angle) * LINK_FORCE * SPEED;
                    b.sx -= (float)Math.cos(angle) * LINK_FORCE * SPEED;
                    b.sy -= (float)Math.sin(angle) * LINK_FORCE * SPEED;
                }
            }
        }
        // moving particle to another field
        for (int i = 0; i < fw; i++) {
            for (int j = 0; j < fh; j++) {
                Field field = fields[i][j];
                for (int i1 = 0; i1 < field.totalParticles(); i1++) {
                    Particle a = field.particleByIndex(i1);
                    if(((int)(a.x / MAX_DIST) != i) || ((int)(a.y / MAX_DIST) != j)) {
                        field.remove(a);
                        fieldFor(a).add(a);
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
                        if(d2 != -1 && d2 < particleToLinkMinDist2) {
                            particleToLinkMinDist2 = d2;
                            particleToLink = b;
                        }
                    }
                    if(i < fw - 1) {
                        int iNext = i + 1;
                        Field field1 = fields[iNext][j];
                        for (int j1 = 0; j1 < field1.totalParticles(); j1++) {
                            Particle b = field1.particleByIndex(j1);
                            float d2 = applyForce(a, b);
                            if(d2 != -1 && d2 < particleToLinkMinDist2) {
                                particleToLinkMinDist2 = d2;
                                particleToLink = b;
                            }
                        }
                    }
                    if(j < fh - 1) {
                        int jNext = j + 1;
                        Field field1 = fields[i][jNext];
                        for (int j1 = 0; j1 < field1.totalParticles(); j1++) {
                            Particle b = field1.particleByIndex(j1);
                            float d2 = applyForce(a, b);
                            if(d2 != -1 && d2 < particleToLinkMinDist2) {
                                particleToLinkMinDist2 = d2;
                                particleToLink = b;
                            }
                        }
                        if(i < fw - 1) {
                            int iNext = i + 1;
                            Field field2 = fields[iNext][jNext];
                            for (int j1 = 0; j1 < field2.totalParticles(); j1++) {
                                Particle b = field2.particleByIndex(j1);
                                float d2 = applyForce(a, b);
                                if(d2 != -1 && d2 < particleToLinkMinDist2) {
                                    particleToLinkMinDist2 = d2;
                                    particleToLink = b;
                                }
                            }
                        }
                    }
                    if(particleToLink != null) {
                        a.bonds.add(particleToLink);
                        particleToLink.bonds.add(a);
                        a.links++;
                        particleToLink.links++;
                        links.add(new Link(a, particleToLink));
                    }
                }
            }
        }
    }

    private float applyForce(Particle a, Particle b) {
        float d2 = (a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y);
        boolean canLink = false;
        if(d2 < MAX_DIST2) {
            float dA = COUPLING[a.getType()][b.getType()] / d2;
            float dB = COUPLING[b.getType()][a.getType()] / d2;
            if (a.links < LINKS[a.getType()] && b.links < LINKS[b.getType()]) {
                if(d2 < MAX_DIST2 / 4f) {
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
            if(d2 < NODE_RADIUS * NODE_RADIUS * 4) {
                dA = 1 / d2;
                dB = 1 / d2;
            }
            a.sx += (float)Math.cos(angle) * dA * SPEED;
            a.sy += (float)Math.sin(angle) * dA * SPEED;
            b.sx -= (float)Math.cos(angle) * dB * SPEED;
            b.sy -= (float)Math.sin(angle) * dB * SPEED;
        }
        if(canLink) return d2;
        return -1;
    }


}
