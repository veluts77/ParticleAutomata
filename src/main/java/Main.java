public class Main {

    public static void main(String[] args) {
        Form f = new Form();
        f.setTitle("Original Java");
        new Thread(f).start();
    }
}