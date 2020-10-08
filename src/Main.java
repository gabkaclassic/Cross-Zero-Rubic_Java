
import ui.Cub;
import javax.swing.*;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class Main {

    public static void main(String[] args) {

        new Main().start();
    }

    private void start() {
    
        JFrame frame = new JFrame();
    
        frame.setVisible(true);
        frame.setBounds(40, 20, 700, 600);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setContentPane(new Cub());
    }

}
