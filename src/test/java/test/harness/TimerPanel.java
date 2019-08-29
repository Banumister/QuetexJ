/*
 * License : The MIT License
 * Copyright(c) 2019 Olyutorskii
 */

package test.harness;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

/**
 * Timer controll GUI.
 */
@SuppressWarnings("serial")
class TimerPanel extends JComponent{

    private final Timer timer;

    private final JComponent intervalPanel;

    private final JComponent counterPanel;
    private final JToggleButton onOffButton;
    private final JLabel eventNumLabel;
    private int eventNum;

    private final Map<String, Integer> msecCommand = new HashMap<>();


    /**
     * Constructor.
     *
     * @param ticks interval infos
     */
    TimerPanel(IntervalTick... ticks){
        super();

        this.eventNumLabel = new JLabel();
        this.eventNum = 0;
        updateEventCounter();

        this.onOffButton = new JToggleButton();
        updateOnOffLabel();
        this.onOffButton.addItemListener(ev -> {
            eventOnOffSwitch();
        });

        this.intervalPanel = buildIntervalPanel(ticks);
        this.counterPanel = buildCounterPanel();

        design();

        for(IntervalTick tick : ticks){
            this.msecCommand.put(tick.label, tick.msec);
        }

        this.timer = buildTimer();
        this.timer.addActionListener(ev -> {
            eventCounterIncrement();
        });

        return;
    }


    /**
     * Build timer.
     *
     * @return timer
     */
    private static Timer buildTimer(){
        Timer result = new Timer(0, null);

        result.setInitialDelay(1);

        result.setCoalesce(true);
        result.setRepeats(true);

        return result;
    }


    /**
     * Build interval radio button panel.
     *
     * @param ticks interval definition
     * @return interval radio button panel
     */
    private JComponent buildIntervalPanel(IntervalTick... ticks){
        JRadioButton defaultInterval = null;
        ButtonGroup buttonGroup = new ButtonGroup();

        JComponent panel = Box.createVerticalBox();

        for(IntervalTick tick : ticks){
            JRadioButton radioButton = new JRadioButton();

            String label = tick.label;
            radioButton.setText(label);
            radioButton.setActionCommand(label);

            radioButton.addActionListener(ev -> {
                eventIntervalChange(ev);
            });

            buttonGroup.add(radioButton);
            panel.add(radioButton);

            if(tick.isDefault){
                defaultInterval = radioButton;
            }
        }

        if(defaultInterval != null){
            EventQueue.invokeLater(defaultInterval::doClick);
        }

        return panel;
    }

    /**
     * Build timer event counter panel.
     *
     * @return timer event counter panel
     */
    private JComponent buildCounterPanel(){
        Box number = Box.createVerticalBox();
        this.eventNumLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        number.add(this.eventNumLabel);

        JComponent cardComp = new JPanel();
        cardComp.setLayout(new CardLayout());
        cardComp.add(number);
        cardComp.add(new JLabel("99999"));

        JComponent result;
        result = Box.createHorizontalBox();

        result.add(new JLabel("Event #:"));
        result.add(cardComp);

        return result;
    }

    /**
     * Design layout.
     */
    private void design(){
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        setLayout(layout);

        Border border = new TitledBorder("");
        this.intervalPanel.setBorder(border);

        constraints.gridheight = GridBagConstraints.REMAINDER;
        add(this.intervalPanel, constraints);

        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weighty = 0.5;
        constraints.insets = new Insets(3, 3, 3, 3);
        add(this.onOffButton, constraints);

        add(this.counterPanel, constraints);

        EventQueue.invokeLater(() -> {
            this.onOffButton.requestFocusInWindow();
        });

        return;
    }

    /**
     * Receive event counter increment event.
     */
    private void eventCounterIncrement(){
        this.eventNum++;
        updateEventCounter();
        return;
    }

    /**
     * Update event counter label.
     */
    private void updateEventCounter(){
        String label = Integer.toString(this.eventNum);
        this.eventNumLabel.setText(label);
        return;
    }

    /**
     * Receive START/STOP button event.
     */
    private void eventOnOffSwitch(){
        boolean isSelected = updateOnOffLabel();

        if(isSelected) this.timer.start();
        else           this.timer.stop();

        return;
    }

    /**
     * Update START/STOP button label.
     *
     * @return true if selected
     */
    private boolean updateOnOffLabel(){
        String label;
        boolean isSelected = this.onOffButton.isSelected();

        if(isSelected) label = "STOP";
        else           label = "START";

        this.onOffButton.setText(label);

        return isSelected;
    }

    /**
     * Receive interval change event.
     *
     * @param ev action event
     */
    private void eventIntervalChange(ActionEvent ev){
        int msec = parseActionEvent(ev);
        this.timer.setDelay(msec);
        if(this.timer.isRunning()){
            this.timer.restart();
        }
        return;
    }

    /**
     * Parse action event to milli sec interval value.
     *
     * @param ev action event
     * @return milli sec interval
     */
    private int parseActionEvent(ActionEvent ev){
        String actionCmd = ev.getActionCommand();
        return parseActionCommand(actionCmd);
    }

    /**
     * Parse action command to milli sec interval value.
     *
     * @param actionCmd command
     * @return milli sec interval
     */
    private int parseActionCommand(String actionCmd){
        Integer iVal;
        iVal = this.msecCommand.get(actionCmd);
        if(iVal == null) return 0;
        return iVal;
    }

    /**
     * Return Swing timer.
     *
     * @return timer
     */
    Timer getTimer(){
        return this.timer;
    }


    /**
     * Interval information.
     */
    static class IntervalTick{
        final int msec;
        final String label;
        final boolean isDefault;

        /**
         * Constructor.
         *
         * @param msec milli second
         * @param label radio button label
         */
        public IntervalTick(int msec, String label){
            this(msec, label, false);
            return;
        }

        /**
         * Constructor.
         *
         * @param msec milli second
         * @param label radio button label
         * @param isDefault true if default radio button
         */
        public IntervalTick(int msec, String label, boolean isDefault){
            super();
            this.label = label.intern();
            this.msec = msec;
            this.isDefault = isDefault;
            return;
        }

    }

}
