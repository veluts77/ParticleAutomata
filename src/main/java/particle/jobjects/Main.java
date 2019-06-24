package particle.jobjects;

public class Main {

    public static void main(String[] args) {
        Form f = new Form();
        f.setTitle("Java Objects");
        new Thread(f).start();
    }
}