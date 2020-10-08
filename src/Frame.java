import javax.swing.*;

public class Frame extends JFrame {

    Frame() {

        setVisible(true);
        setBounds(40, 20, 700, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(new Cub());
    }
}
