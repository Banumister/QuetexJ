/*
 * License : The MIT License
 * Copyright(c) 2019 Olyutorskii
 */

package test.harness;

import io.github.olyutorskii.quetexj.MvcFacade;
import io.github.olyutorskii.quetexj.SwingLogHandler;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ComponentListener;
import java.util.Locale;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.Document;

/**
 * Test Harness GUI.
 */
public final class Main {

    private static final TimerPanel.IntervalTick[] TBL_TICKS = {
        new TimerPanel.IntervalTick(  10,  "10 ms"),
        new TimerPanel.IntervalTick( 125, "125 ms"),
        new TimerPanel.IntervalTick( 250, "250 ms"),
        new TimerPanel.IntervalTick( 500, "500 ms"),
        new TimerPanel.IntervalTick(1000,  "1 sec", true),
        new TimerPanel.IntervalTick(2000,  "2 sec"),
        new TimerPanel.IntervalTick(4000,  "4 sec"),
    };


    /**
     * Hidden constructor.
     */
    private Main(){
        assert false;
    }


    private static JFrame buildTextFrame(){
        JFrame frame = new JFrame();

        frame.setSize(420, 600);
        frame.setLocationByPlatform(true);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        return frame;
    }

    private static JFrame buildCntlPanel(JComponent dim, JComponent cntl){
        JFrame opt = new JFrame();
        opt.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Border border;

        border = new TitledBorder("text area");
        dim.setBorder(border);

        JComponent gc = new GcMeter();
        border = new EtchedBorder(EtchedBorder.RAISED);
        gc.setBorder(border);

        border = new TitledBorder("Timer Control");
        cntl.setBorder(border);

        Container cont = opt.getContentPane();
        GridBagLayout layout = new GridBagLayout();
        cont.setLayout(layout);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.fill = GridBagConstraints.NONE;
        cont.add(dim, constraints);

        constraints.gridwidth = GridBagConstraints.REMAINDER;
        cont.add(cntl, constraints);

        constraints.weightx = 1.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        cont.add(gc, constraints);

        return opt;
    }

    /**
     * Entry.
     *
     * @param args args
     */
    public static void main(String[] args){
        Locale.setDefault(Locale.ROOT);

        EventQueue.invokeLater(() -> {
            kickSwing();
        });

        return;
    }

    /**
     * EDT entry.
     */
    private static void kickSwing(){
        MvcFacade facade = new MvcFacade();

        Document doc = facade.getDocument();

        SwingLogHandler dh = new SwingLogHandler(doc);
        Logger logger = Logger.getGlobal();
        logger.setUseParentHandlers(false);
        logger.addHandler(dh);
        logger.info("Let's start logging");
        logger.info("Let's start logging twice");

        TimerPanel timerPanel = new TimerPanel(TBL_TICKS);
        Timer timer = timerPanel.getTimer();
        timer.addActionListener(ev -> {
            RandomLog.putRandomLog();
        });

        DimDisp dimDisp = new DimDisp();
        ComponentListener resizeWatcher = dimDisp.getResizeWatcher();

        TextPane tp = new TextPane(facade);
        JTextArea textArea = facade.getTextArea();
        textArea.addComponentListener(resizeWatcher);

        JFrame.setDefaultLookAndFeelDecorated(true);

        JFrame win = buildTextFrame();
        win.add(tp);

        JFrame opt = buildCntlPanel(dimDisp, timerPanel);
        opt.setLocationRelativeTo(win);
        opt.pack();

        win.setVisible(true);
        opt.setVisible(true);

        return;
    }

}
