/*
 * License : The MIT License
 * Copyright(c) 2019 Olyutorskii
 */

package test.harness;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Track &amp; display component size.
 *
 * <p>Tracking by ComponentListener.
 */
@SuppressWarnings("serial")
class DimDisp extends JComponent{

    private static final String MAX_NUM = "99999";
    private static final String DELIM = " : ";


    private final JLabel dimWidth  = new JLabel();
    private final JLabel dimHeight = new JLabel();

    private final ComponentListener resizeWatcher = new ResizeWatcher();


    /**
     * Constructor.
     */
    DimDisp(){
        super();
        design();
        updateSize(0, 0);
        return;
    }


    /**
     * Align component to right in container.
     *
     * @param comp component
     * @return container
     */
    private static JComponent rightAlignedContainer(JComponent comp){
        Box result = Box.createVerticalBox();
        comp.setAlignmentX(Component.RIGHT_ALIGNMENT);
        result.add(comp);
        return result;
    }

    /**
     * Align component to right in container.
     *
     * <p>Union with hidden component is ensured.
     *
     * @param comp component
     * @param hidden hidden component
     * @return container
     */
    private static JComponent rightAlignedUnion(
            JComponent comp, JComponent hidden){
        JPanel result = new JPanel();
        CardLayout layout = new CardLayout();
        result.setLayout(layout);

        JComponent aligned = rightAlignedContainer(comp);

        result.add(aligned);
        result.add(hidden);

        return result;
    }


    /**
     * Layout design.
     */
    private void design(){
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        setLayout(layout);

        JLabel caption;
        JComponent valLabel;

        constraints.anchor = GridBagConstraints.LINE_END;

        constraints.gridwidth = 1;
        constraints.weightx = 1.0;
        caption = new JLabel("width");
        add(caption, constraints);

        constraints.weightx = 0.0;
        caption = new JLabel(DELIM);
        add(caption, constraints);

        constraints.gridwidth = GridBagConstraints.REMAINDER;
        valLabel = rightAlignedUnion(this.dimWidth, new JLabel(MAX_NUM));
        add(valLabel, constraints);

        constraints.gridwidth = 1;
        constraints.weightx = 1.0;
        caption = new JLabel("height");
        add(caption, constraints);

        constraints.weightx = 0.0;
        caption = new JLabel(DELIM);
        add(caption, constraints);

        constraints.gridwidth = GridBagConstraints.REMAINDER;
        valLabel = rightAlignedUnion(this.dimHeight, new JLabel(MAX_NUM));
        add(valLabel, constraints);

        return;
    }

    /**
     * Return resize-watcher ComponentListener.
     *
     * @return ComponentListener
     */
    ComponentListener getResizeWatcher(){
        return this.resizeWatcher;
    }

    /**
     * Update component size information.
     *
     * @param ev resize event
     */
    void updateSize(ComponentEvent ev){
        Component comp = ev.getComponent();
        int newWidth  = comp.getWidth();
        int newHeight = comp.getHeight();

        updateSize(newWidth, newHeight);

        return;
    }

    /**
     * Update component size information.
     *
     * @param newWidth width
     * @param newHeight height
     */
    private void updateSize(int newWidth, int newHeight){
        String txtWidth  = Integer.toString(newWidth);
        String txtHeight = Integer.toString(newHeight);

        this.dimWidth .setText(txtWidth);
        this.dimHeight.setText(txtHeight);

        return;
    }


    /**
     * Resize watcher.
     */
    private class ResizeWatcher extends ComponentAdapter{

        /**
         * Constructor.
         */
        ResizeWatcher(){
            super();
        }

        /**
         * {@inheritDoc}
         * @param ev {@inheritDoc}
         */
        @Override
        public void componentResized(ComponentEvent ev){
            updateSize(ev);
            return;
        }

    }

}
