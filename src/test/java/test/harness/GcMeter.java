/*
 * License : The MIT License
 * Copyright(c) 2019 Olyutorskii
 */

package test.harness;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.Timer;

/**
 * Heap memory information component.
 *
 * <p>GC will happen if clicked.
 */
@SuppressWarnings("serial")
class GcMeter extends JComponent{

    private static final int INTERVAL = 887;  // msec (PRIME)
    private static final double MB = 1024.0 * 1024.0;
    private static final String GC_LABEL;
    private static final String DMY_LABEL;
    private static final MessageFormat FORM_LABEL;

    static{
        GC_LABEL  = "{0,number,##,##0.0} / {1,number,##,##0.0} MB";
        DMY_LABEL =           "99,999.9 / 99,999.9 MB";
        FORM_LABEL = new MessageFormat(GC_LABEL);
    }

    private final Timer timer;
    private final JLabel label = new JLabel();
    private final Object[] formatArgs = new Object[2];


    /**
     * Constructor.
     */
    GcMeter(){
        super();

        design();

        ClickWatcher watcher = new ClickWatcher();
        this.label.addMouseListener(watcher);

        this.timer = buildTimer();
        timer.addActionListener(ev -> {
            updateMemInfo();
        });
        this.timer.start();

        return;
    }


    /**
     * Build timer instance.
     *
     * @return timer
     */
    private static Timer buildTimer(){
        Timer result = new Timer(0, null);

        result.setRepeats(true);
        result.setCoalesce(true);

        result.setInitialDelay(1);
        result.setDelay(INTERVAL);

        return result;
    }


    /**
     * Design layout.
     *
     * <p>Dummy component is hidden but affect overlapped layout.
     */
    private void design(){
        Box box = Box.createVerticalBox();
        this.label.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(this.label);

        CardLayout layout = new CardLayout();
        setLayout(layout);

        JLabel dummyLabel = new JLabel(DMY_LABEL);

        add(box);
        add(dummyLabel);

        return;
    }

    /**
     * Update heap memory info.
     */
    void updateMemInfo(){
        assert EventQueue.isDispatchThread();

        Runtime runtime = Runtime.getRuntime();
        long totalMem = runtime.totalMemory();
        long freeMem  = runtime.freeMemory();
        long useMem = totalMem - freeMem;

        double totalMb = (double)totalMem / MB;
        double useMb   = (double)useMem   / MB;

        updateMemInfo(useMb, totalMb);

        return;
    }

    /**
     * Update heap memory info.
     *
     * @param useMb used memory (MByte)
     * @param totalMb total memory (MByte)
     */
    private void updateMemInfo(double useMb, double totalMb){
        this.formatArgs[0] = useMb;
        this.formatArgs[1] = totalMb;
        // EDT is safety
        String result = FORM_LABEL.format(this.formatArgs);

        this.label.setText(result);

        return;
    }


    /**
     * Click watcher.
     */
    private class ClickWatcher extends MouseAdapter{

        /**
         * Constructor.
         */
        ClickWatcher(){
            super();
            return;
        }


        /**
         * {@inheritDoc}
         * @param ev {@inheritDoc}
         */
        @Override
        public void mouseClicked(MouseEvent ev){
            System.gc();
            updateMemInfo();
            return;
        }

    }

}
