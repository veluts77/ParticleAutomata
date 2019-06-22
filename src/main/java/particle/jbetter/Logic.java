package particle.jbetter;

import java.util.ArrayList;

public class Logic {

    private final Force force;
    private Field[][] fields;
    private final ArrayList<Link> links = new ArrayList<>();


    public Logic(Field[][] fields, Force force) {
        this.fields = fields;
        this.force = force;
    }

    public ArrayList<Link> getLinks() {
        return links;
    }

    public void logic() {
        for (int i = 0; i < Settings.fw; i++) {
            for (int j = 0; j < Settings.fh; j++) {
                Field field = fields[i][j];
                for (int i1 = 0; i1 < field.particles.size(); i1++) {
                    Particle a = field.particles.get(i1);
                    a.x += a.sx;
                    a.y += a.sy;
                    a.sx *= 0.98f;
                    a.sy *= 0.98f;
                    // velocity normalization
                    // idk if it is still necessary
                    float magnitude = (float) Math.sqrt(a.sx * a.sx + a.sy * a.sy);
                    if (magnitude > 1f) {
                        a.sx /= magnitude;
                        a.sy /= magnitude;
                    }
                    // border repulsion
                    if (a.x < Settings.BORDER) {
                        a.sx += Settings.SPEED * 0.05f;
                        if (a.x < 0) {
                            a.x = -a.x;
                            a.sx *= -0.5f;
                        }
                    } else if (a.x > Settings.w - Settings.BORDER) {
                        a.sx -= Settings.SPEED * 0.05f;
                        if (a.x > Settings.w) {
                            a.x = Settings.w * 2 - a.x;
                            a.sx *= -0.5f;
                        }
                    }
                    if (a.y < Settings.BORDER) {
                        a.sy += Settings.SPEED * 0.05f;
                        if (a.y < 0) {
                            a.y = -a.y;
                            a.sy *= -0.5f;
                        }
                    } else if (a.y > Settings.h - Settings.BORDER) {
                        a.sy -= Settings.SPEED * 0.05f;
                        if (a.y > Settings.h) {
                            a.y = Settings.h * 2 - a.y;
                            a.sy *= -0.5f;
                        }
                    }
                }
            }
        }
        for (int i = 0; i < links.size(); i++) {
            Link link = links.get(i);
            Particle a = link.a;
            Particle b = link.b;
            float d2 = (a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y);
            if (d2 > Settings.MAX_DIST2 / 4f) {
                a.links--;
                b.links--;
                a.bonds.remove(b);
                b.bonds.remove(a);
                links.remove(link);
                i--;
            } else {
                if (d2 > Settings.NODE_RADIUS * Settings.NODE_RADIUS * 4) {
                    double angle = Math.atan2(a.y - b.y, a.x - b.x);
                    a.sx += (float) Math.cos(angle) * Settings.LINK_FORCE * Settings.SPEED;
                    a.sy += (float) Math.sin(angle) * Settings.LINK_FORCE * Settings.SPEED;
                    b.sx -= (float) Math.cos(angle) * Settings.LINK_FORCE * Settings.SPEED;
                    b.sy -= (float) Math.sin(angle) * Settings.LINK_FORCE * Settings.SPEED;
                }
            }
        }
        // moving particle to another field
        for (int i = 0; i < Settings.fw; i++) {
            for (int j = 0; j < Settings.fh; j++) {
                Field field = fields[i][j];
                for (int i1 = 0; i1 < field.particles.size(); i1++) {
                    Particle a = field.particles.get(i1);
                    if (((int) (a.x / Settings.MAX_DIST) != i) || ((int) (a.y / Settings.MAX_DIST) != j)) {
                        field.particles.remove(a);
                        fields[(int) (a.x / Settings.MAX_DIST)][(int) (a.y / Settings.MAX_DIST)].particles.add(a);
                    }
                }
            }
        }
        // dividing scene into parts to reduce complexity
        for (int i = 0; i < Settings.fw; i++) {
            for (int j = 0; j < Settings.fh; j++) {
                Field field = fields[i][j];
                for (int i1 = 0; i1 < field.particles.size(); i1++) {
                    Particle a = field.particles.get(i1);
                    Particle particleToLink = null;
                    float particleToLinkMinDist2 = (Settings.w + Settings.h) * (Settings.w + Settings.h);
                    for (int j1 = i1 + 1; j1 < field.particles.size(); j1++) {
                        Particle b = field.particles.get(j1);
                        float d2 = force.applyForce(a, b);
                        if (d2 != -1 && d2 < particleToLinkMinDist2) {
                            particleToLinkMinDist2 = d2;
                            particleToLink = b;
                        }
                    }
                    if (i < Settings.fw - 1) {
                        int iNext = i + 1;
                        Field field1 = fields[iNext][j];
                        for (int j1 = 0; j1 < field1.particles.size(); j1++) {
                            Particle b = field1.particles.get(j1);
                            float d2 = force.applyForce(a, b);
                            if (d2 != -1 && d2 < particleToLinkMinDist2) {
                                particleToLinkMinDist2 = d2;
                                particleToLink = b;
                            }
                        }
                    }
                    if (j < Settings.fh - 1) {
                        int jNext = j + 1;
                        Field field1 = fields[i][jNext];
                        for (int j1 = 0; j1 < field1.particles.size(); j1++) {
                            Particle b = field1.particles.get(j1);
                            float d2 = force.applyForce(a, b);
                            if (d2 != -1 && d2 < particleToLinkMinDist2) {
                                particleToLinkMinDist2 = d2;
                                particleToLink = b;
                            }
                        }
                        if (i < Settings.fw - 1) {
                            int iNext = i + 1;
                            Field field2 = fields[iNext][jNext];
                            for (int j1 = 0; j1 < field2.particles.size(); j1++) {
                                Particle b = field2.particles.get(j1);
                                float d2 = force.applyForce(a, b);
                                if (d2 != -1 && d2 < particleToLinkMinDist2) {
                                    particleToLinkMinDist2 = d2;
                                    particleToLink = b;
                                }
                            }
                        }
                    }
                    if (particleToLink != null) {
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

}
