package particle.jbetter;

public class Main {

    public static void main(String[] args) {
        Form f = new Form();
        f.setTitle("Better Java");
        new Thread(f).start();
    }
}