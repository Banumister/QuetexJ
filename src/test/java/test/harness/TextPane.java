/*
 * License : The MIT License
 * Copyright(c) 2019 Olyutorskii
 */

package test.harness;

import io.github.olyutorskii.quetexj.MaxTracker;
import io.github.olyutorskii.quetexj.MvcFacade;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Pseudo endless text component.
 */
@SuppressWarnings("serial")
class TextPane extends JComponent{

    private final JScrollPane scrollPane;
    private final JButton clearBtn;
    private final JCheckBox trackBtn;


    /**
     * Constructor.
     *
     * @param facade MVC
     */
    TextPane(MvcFacade facade){
        super();

        JTextArea textArea = facade.getTextArea();
        this.scrollPane = new JScrollPane();
        this.scrollPane.setViewportView(textArea);

        JScrollBar vertBar = this.scrollPane.getVerticalScrollBar();
        vertBar.setModel(facade.getVerticalBoundedRangeModel());

        this.clearBtn = new JButton();
        this.clearBtn.setAction(facade.getClearAction());
        this.clearBtn.setText("clear");

        this.trackBtn = new JCheckBox("tracking last");
        this.trackBtn.setModel(facade.getTrackSwitchButtonModel());

        MaxTracker maxTracker = facade.getMaxTracker();
        maxTracker.setTrackingMode(true);

        design();

        return;
    }


    /**
     * design layout.
     */
    private void design(){
        BoxLayout layout;

        JPanel pane = new JPanel();
        layout = new BoxLayout(pane, BoxLayout.LINE_AXIS);
        pane.setLayout(layout);
        pane.add(this.scrollPane);
        pane.add(Box.createVerticalGlue());

        JPanel buttons = new JPanel();
        layout = new BoxLayout(buttons, BoxLayout.LINE_AXIS);
        buttons.setLayout(layout);
        buttons.add(this.clearBtn);
        buttons.add(Box.createHorizontalGlue());
        buttons.add(this.trackBtn);

        layout = new BoxLayout(this, BoxLayout.PAGE_AXIS);
        setLayout(layout);
        add(pane);
        add(buttons);

        return;
    }

}
