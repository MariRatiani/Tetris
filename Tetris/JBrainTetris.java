import java.awt.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


//@SuppressWarnings("serial")
public class JBrainTetris extends JTetris {
    protected JCheckBox drawFall;
    protected JCheckBox ModeOfB;
    protected JLabel goodS;
    protected JSlider adv;
    private int curC;
    private DefaultBrain brain;

    private Brain.Move calculatedBestM;
    private Brain.Move move;


    private boolean DEBUG = false;
    public JBrainTetris(int pixels) {
        super(pixels);
        calculatedBestM = new Brain.Move();

        brain = new DefaultBrain();
        curC = 0;
    }


    private Piece helper1(){
        double worseScore =0;
        int count = 0;
        Piece worstPiece= null;
        int len = pieces.length;
        for (int i = 0 ; i < len; i++)
        {
            move = brain.bestMove(board, pieces[i], HEIGHT, move);

            if(move==null)
                return super.pickNextPiece();

            boolean ifSt = worseScore < move.score;
            if(ifSt){
                worseScore = move.score;
                worstPiece = pieces[i];
            }
            count++;
        }
        return worstPiece;
    }

    private Piece showT(boolean r){
        if(r){
            goodS.setText("* ok *");
            return this.helper1();
        }else {
            goodS.setText(" ok ");
            return super.pickNextPiece();
        }
    }
    @Override
    public Piece pickNextPiece() {
        int adversaryValue = adv.getValue();
        int rand = random.nextInt(100);

        boolean r = rand < adversaryValue;
        return showT(r);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }

        JBrainTetris tetris = new JBrainTetris(16);
        JFrame frame = JBrainTetris.createFrame(tetris);
        frame.setVisible(true);
    }


    private void helper2(boolean comp){
        if(comp){
            curC = count;
            board.undo();
            calculatedBestM = brain.bestMove(board, currentPiece, HEIGHT, calculatedBestM);
        }
    }

    private int helper3(int verb) {
        boolean eqv = !currentPiece.equals(calculatedBestM.piece);
        if (eqv)
            currentPiece = currentPiece.fastRotation();
        boolean eqv2 = currentX < calculatedBestM.x;
        if (eqv2) {
            currentX++;
        } else if (currentX > calculatedBestM.x) {
            int newX = currentX - 1;
            currentX = newX;
        } else {
            boolean eq1 =currentPiece.equals(calculatedBestM.piece);
            boolean eq2 = !drawFall.isSelected();
            boolean eq3 = calculatedBestM.y != currentY;
            boolean eq4 = calculatedBestM.x == currentX;
            if ( eq1&& eq2 && eq3 && eq4) {
                verb = DROP;
            }
        }

        return verb;
    }

    @Override
    public void tick(int verb) {

        boolean comp = ModeOfB.isSelected() && verb == DOWN;
        if(comp){
            boolean comp2 = count != curC;
            helper2(comp2);

            boolean a = calculatedBestM != null;
            if(a){
                verb = helper3(verb);
            }
        }
        super.tick(verb);
    }


    @Override
    public JComponent createControlPanel() {

        JPanel panel = (JPanel)super.createControlPanel();

        JPanel row = (JPanel)panel.getComponent(7);
        row.setMaximumSize(new Dimension(400,100));

        JCheckBox test = (JCheckBox)panel.getComponent(8);
        test.setVisible(DEBUG);

        drawButtons(panel);

        JPanel little = new JPanel();
        drawOnLittlePanel(little, panel);


        JPanel okP = new JPanel();
        drawOkPanel(okP, panel);


        return panel;
    }

    private void drawButtons(JPanel panel){
        drawFall = new JCheckBox("draw falling ");
        ModeOfB = new JCheckBox("On/Off ");
        panel.add(Box.createVerticalStrut(15));
        panel.add(new JLabel("Brain: "));

        panel.add(drawFall);
        panel.add(ModeOfB);

        drawFall.setSelected(true);
    }

    private void drawOnLittlePanel(JPanel little, JPanel panel){
        little.add(Box.createVerticalStrut(30));

        adv = new JSlider(0, 110, 0);
        adv.setPreferredSize(new Dimension(100, 20));
        little.add(new JLabel("adversary: "));
        little.setMaximumSize(new Dimension(400,100));
        little.add(adv);
        panel.add(little);
    }

    private void drawOkPanel(JPanel okP, JPanel panel){
        goodS = new JLabel("ok");
        okP.add(goodS);

        panel.add(okP);
    }
}