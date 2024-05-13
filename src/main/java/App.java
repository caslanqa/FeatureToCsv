import javax.swing.*;

public class App {
    public static void main(String[] args)  {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    Converter c = new Converter();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
