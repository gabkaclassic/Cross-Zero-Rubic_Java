import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

public class Cub extends JPanel {

    private static final int SIZE = 3;

    private final Edge front;
    private final Edge left;
    private final Edge right;
    private final Edge top;
    private final Edge bottom;
    private final Edge back;

    private int countZero = 0;
    private int countCross = 0;

    private int[] currentLine;
    private Value currentValue;
    private Orientation currentOrientation;

    private int cages;

    Cub() {

        setLayout(null);

        cages = SIZE * SIZE * 6;

        front = new Edge();
        back = new Edge();
        left = new Edge();
        right = new Edge();
        top = new Edge();
        bottom = new Edge();

        currentOrientation = Orientation.UP;
        currentLine = new int[SIZE];

        for(int i = 0; i < SIZE; i++) currentLine[i] = i;

        currentValue = Value.ZERO;

        addMouseMotionListener(new MouseMotionAdapter() {

            public void mouseMoved(MouseEvent mouseEvent) {

                int x = mouseEvent.getX();
                int y = mouseEvent.getY();

                if((x < 310) && (x > 160)) {      //костыль

                    if((y > 10) && (y < 160)) currentLine = new int[]{0, 1, 2};
                    else if((y > 310) && (y < 460)) currentLine = new int[]{6, 7, 8};
                    else if((y < 310) && (y > 160)) currentLine = new int[]{1, 4, 7};
                }
                else if((y < 310) && (y > 160)) {

                    if((x > 10) && (x < 160)) currentLine = new int[]{0, 3, 6};
                    else if((x > 310) && (x < 460)) currentLine = new int[]{2, 5, 8};
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent mouseEvent) {

                switch(mouseEvent.getButton()) {

                    case 1 -> {

                        int x = (mouseEvent.getX() - 10 ) / 150;
                        int y = (mouseEvent.getY() - 10) / 150;
                        int index = 0;

                        if(x == 0) {

                            if(y == 1) index = 3;
                            else if(y == 2) index = 6;
                        }
                        else if(x == 1) {

                            if(y == 0) index = 1;
                            else if(y == 1) index = 4;
                            else if(y == 2) index = 7;
                        }
                        else if(x == 2) {

                            if(y == 0) index = 2;
                            else if(y == 1) index = 5;
                            else if(y == 2) index = 8;
                        }

                        motion(index);

                        repaint();
                    }
                    case 2 -> rotation();
                    case 3 -> shift();
                }
            }
        });

        JButton upButton = new JButton("^");
        upButton.setBounds(510, 300, 50, 50);
        upButton.setVisible(true);
        upButton.addActionListener(actionEvent -> currentOrientation = Orientation.UP);
        add(upButton);

        JButton downButton = new JButton("V");
        downButton.setBounds(510, 350, 50, 50);
        downButton.setVisible(true);
        downButton.addActionListener(actionEvent -> currentOrientation = Orientation.DOWN);
        add(downButton);

        JButton leftButton = new JButton("<");
        leftButton.setBounds(460, 325, 50, 50);
        leftButton.setVisible(true);
        leftButton.addActionListener(actionEvent -> currentOrientation = Orientation.LEFT);
        add(leftButton);

        JButton rightButton = new JButton(">");
        rightButton.setBounds(560, 325, 50, 50);
        rightButton.setVisible(true);
        rightButton.addActionListener(actionEvent -> currentOrientation = Orientation.UP);
        add(rightButton);
    }

    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D gr = (Graphics2D)g;

        paintField(gr);
        paintFigures(gr);
    }

    private void paintField(Graphics2D gr) {

        if(currentValue.equals(Value.CROSS)) {

            gr.drawLine(470, 10, 570, 110);
            gr.drawLine(570, 10, 470, 110);
        }
        else gr.drawOval(470, 10, 100, 100);

        gr.drawRect(10, 10, (150 * SIZE), (150 * SIZE));

        for(int i = 0; i < SIZE; i++)
            gr.drawLine((10 + 150 * i), 10, (10 + 150 * i), 460);
        for(int i = 0; i < SIZE; i++)
            gr.drawLine(10, (10 + 150 * i), 460, (10 + 150 * i));
    }

    private void paintFigures(Graphics2D gr) {

        for(int i = 0; front.cages.length > i; i++) {

            Value value = front.cages[i];

            int x = (i % SIZE) * 150 + 10;
            int y = (i / SIZE) * 150 + 10;

            if(value.equals(Value.CROSS)) {

                gr.drawLine(x, y, (x + 100), (y + 100));
                gr.drawLine((x + 100), y, x, (y + 100));
            }
            else if(value.equals(Value.ZERO))
                gr.drawOval(x, y, 100, 100);
        }
    }

    private void motion(int index) {

        if(front.cages[index] == Value.NULL) {

            front.cages[index] = currentValue;
            changeMotion();
            cages--;

            if(cages == 0) {

                checkWinner();

               JOptionPane.showMessageDialog(null, ("Cross: " + countCross + ", zero: " + countZero));
            }
        }
    }

    private void rotation() {

        Value[] cashCages = Arrays.copyOf(front.cages, front.cages.length);
        int[] indexes = new int[SIZE * SIZE];

        for(int i = 0; i < indexes.length; i++) indexes[i] = i;

        switch(currentOrientation) {

            case UP -> {

                front.setCages(indexes, bottom.getCages(indexes));
                bottom.setCages(indexes, back.getCages(indexes));
                back.setCages(indexes, top.getCages(indexes));
                top.setCages(indexes, cashCages);

                left.rotation(Orientation.LEFT);
                right.rotation(Orientation.RIGHT);
            }
            case DOWN -> {

                front.setCages(indexes, top.getCages(indexes));
                top.setCages(indexes, back.getCages(indexes));
                back.setCages(indexes, bottom.getCages(indexes));
                bottom.setCages(indexes, cashCages);

                left.rotation(Orientation.RIGHT);
                right.rotation(Orientation.LEFT);
            }
            case LEFT -> {

                front.setCages(indexes, right.getCages(indexes));
                right.setCages(indexes, back.getCages(indexes));
                back.setCages(indexes, left.getCages(indexes));
                left.setCages(indexes, cashCages);

                top.rotation(Orientation.RIGHT);
                bottom.rotation(Orientation.LEFT);
            }
            case RIGHT -> {

                front.setCages(indexes, left.getCages(indexes));
                left.setCages(indexes, back.getCages(indexes));
                back.setCages(indexes, right.getCages(indexes));
                right.setCages(indexes, cashCages);

                top.rotation(Orientation.LEFT);
                bottom.rotation(Orientation.RIGHT);
            }
        }

        repaint();
    }

    private void shift() {

        Value[] cashValues = new Value[SIZE];

        for(int i = 0; i < currentLine.length; i++)
            cashValues[i] = front.cages[currentLine[i]];

        switch(currentOrientation) {

            case UP -> {

                front.setCages(currentLine, bottom.getCages(currentLine));
                bottom.setCages(currentLine, back.getCages(currentLine));
                back.setCages(currentLine, top.getCages(currentLine));
                top.setCages(currentLine, cashValues);

                if(Arrays.equals(currentLine, new int[]{0, 3, 6})) left.rotation(Orientation.LEFT);
                else if(Arrays.equals(currentLine, new int[]{2, 5, 8})) right.rotation(Orientation.RIGHT);
            }
            case DOWN -> {

                front.setCages(currentLine, top.getCages(currentLine));
                top.setCages(currentLine, back.getCages(currentLine));
                back.setCages(currentLine, bottom.getCages(currentLine));
                bottom.setCages(currentLine, cashValues);

                if(Arrays.equals(currentLine, new int[]{0, 3, 6})) left.rotation(Orientation.RIGHT);
                else if(Arrays.equals(currentLine, new int[]{2, 5, 8})) right.rotation(Orientation.LEFT);
            }
            case LEFT -> {

                front.setCages(currentLine, right.getCages(currentLine));
                right.setCages(currentLine, back.getCages(currentLine));
                back.setCages(currentLine, left.getCages(currentLine));
                left.setCages(currentLine, cashValues);

                if(Arrays.equals(currentLine, new int[]{0, 1, 2})) top.rotation(Orientation.RIGHT);
                else if(Arrays.equals(currentLine, new int[]{6, 7, 8})) bottom.rotation(Orientation.LEFT);
            }
            case RIGHT -> {

                front.setCages(currentLine, left.getCages(currentLine));
                left.setCages(currentLine, back.getCages(currentLine));
                back.setCages(currentLine, right.getCages(currentLine));
                right.setCages(currentLine, cashValues);

                if(Arrays.equals(currentLine, new int[]{0, 1, 2})) top.rotation(Orientation.LEFT);
                else if(Arrays.equals(currentLine, new int[]{6, 7, 8})) bottom.rotation(Orientation.RIGHT);
            }
        }

        changeMotion();

        repaint();
    }

    private void checkWinner() {

        checkEdge(front);
        checkEdge(back);
        checkEdge(top);
        checkEdge(bottom);
        checkEdge(left);
        checkEdge(right);
    }

    private void checkEdge(Edge edge) {  //Костыли

        Value[] cages = edge.cages;
        boolean flag;

        flag = (cages[0] == cages[1]) && (cages[1] == cages[2]);
        checkCage(flag, cages[0]);
        flag = (cages[3] == cages[4]) && (cages[4] == cages[5]);
        checkCage(flag, cages[3]);
        flag = (cages[6] == cages[7]) && (cages[7] == cages[8]);
        checkCage(flag, cages[6]);
        flag = (cages[0] == cages[3]) && (cages[3] == cages[6]);
        checkCage(flag, cages[0]);
        flag = (cages[1] == cages[4]) && (cages[4] == cages[7]);
        checkCage(flag, cages[1]);
        flag = (cages[2] == cages[5]) && (cages[5] == cages[8]);
        checkCage(flag, cages[2]);
        flag = (cages[0] == cages[4]) && (cages[4] == cages[8]);
        checkCage(flag, cages[0]);
        flag = (cages[2] == cages[4]) && (cages[4] == cages[6]);
        checkCage(flag, cages[2]);
    }

    private void checkCage(boolean b, Value val) {

        if(b) {
            if(val == Value.CROSS) countCross++;
            else countZero++;
        }
    }

    private void changeMotion() {

        if(currentValue == Value.CROSS) currentValue = Value.ZERO;
        else currentValue = Value.CROSS;
    }

 private static class Edge {

    private final Value[] cages;

    private Edge() {

        cages = new Value[9];

        Arrays.fill(cages, Value.NULL);
    }

    private void setCages(int[] indexes, Value[] values) {

        for(int i = 0; i < indexes.length; i++)
            cages[indexes[i]] = values[i];
    }

    private void rotation(Orientation orientation) {

        Value cashValue = cages[0];

        if(orientation.equals(Orientation.LEFT)) {

            for(int i = 1; i < SIZE; i++) cages[i - 1] = cages[i];
            for(int i = ((SIZE * 2) - 1); i < cages.length; i += SIZE) cages[i - SIZE] = cages[i];
            for(int i = (cages.length - 2); i >= (SIZE * (SIZE - 1)); i--) cages[i + 1] = cages[i];
            for(int i = (SIZE * (SIZE - 2)); i > 0; i -= SIZE) cages[i + SIZE] = cages[i];

            cages[SIZE] = cashValue;
        }
        else {

            for(int i = SIZE; i <= (SIZE * (SIZE - 1)); i += SIZE) cages[i - SIZE] = cages[i];
            for(int i = ((SIZE * (SIZE - 1)) + 1); i < (SIZE * SIZE); i++) cages[i - 1] = cages[i];
            for(int i = ((SIZE * 2) - 1); i > 0; i -= SIZE) cages[i + SIZE] = cages[i];
            for(int i = (SIZE - 2); i > 0; i--) cages[i + 1] = cages[i];

            cages[1] = cashValue;
        }
    }

     private Value[] getCages(int[] indexes) {

        Value[] values = new Value[indexes.length];

         for(int i = 0; i < indexes.length; i++)
             values[i] = cages[indexes[i]];

         return values;
     }
 }
}

enum Value {
    CROSS, ZERO, NULL
}

enum Orientation {
    UP, DOWN, LEFT, RIGHT
}