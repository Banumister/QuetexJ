/*
 * License : The MIT License
 * Copyright(c) 2019 Olyutorskii
 */

package io.github.olyutorskii.quetexj;

import javax.swing.Action;
import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JTextArea;
import javax.swing.JToggleButton.ToggleButtonModel;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 * Facade of MVC complexes.
 */
public class MvcFacade {

    private final Document document;
    private final BoundedRangeModel vertRangeModel;
    private final ToggleButtonModel trackSwitchButtonModel;

    private final JTextArea textArea;

    private final HeightKeeper heightKeeper;
    private final MaxTracker maxTracker;

    private final Action clearAction;


    /**
     * Constructor.
     *
     * <p>PlainDocument, DefaultBoundedRangeModel, and ToggleButtonModel
     * instances are used as default model.
     */
    public MvcFacade() {
        this(
                new PlainDocument(),
                new DefaultBoundedRangeModel(),
                new ToggleButtonModel());
        return;
    }

    /**
     * Constructor.
     *
     * @param document text document model
     * @param vertRangeModel vertical scrollbar model
     * @param trackSwitchButtonModel tracking on-off switch button model
     */
    public MvcFacade(
            Document document,
            BoundedRangeModel vertRangeModel,
            ToggleButtonModel trackSwitchButtonModel) {
        super();

        this.document = document;
        this.vertRangeModel = vertRangeModel;
        this.trackSwitchButtonModel = trackSwitchButtonModel;

        this.textArea = buildTextArea(this.document);

        this.heightKeeper =
                new HeightKeeper(this.textArea, this.vertRangeModel);
        this.maxTracker =
                new MaxTracker(
                        this.vertRangeModel, this.trackSwitchButtonModel);

        this.clearAction = new ClearDocumentAction(this.document);

        return;
    }


    /**
     * Build text area.
     *
     * @param doc document model
     * @return text area
     */
    private static JTextArea buildTextArea(Document doc) {
        JTextArea textComp = new JTextArea();

        textComp.setEditable(false);
        textComp.setLineWrap(true);

        DefaultCaret caret = new DefaultCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        textComp.setCaret(caret);

        textComp.setDocument(doc);

        return textComp;
    }


    /**
     * Return document model.
     *
     * @return document model
     */
    public Document getDocument() {
        return this.document;
    }

    /**
     * Return text area view.
     *
     * @return text area view
     */
    public JTextArea getTextArea() {
        return this.textArea;
    }

    /**
     * Return vertical BoundedRangeModel.
     *
     * @return vertical BoundedRangeModel
     */
    public BoundedRangeModel getVerticalBoundedRangeModel() {
        return this.vertRangeModel;
    }

    /**
     * Return HeightKeeper instance.
     *
     * @return HeightKeeper instance
     */
    public HeightKeeper getHeightKeeper() {
        return this.heightKeeper;
    }

    /**
     * Return MaxTracker instance.
     *
     * @return MaxTracker instance
     */
    public MaxTracker getMaxTracker() {
        return this.maxTracker;
    }

    /**
     * Return tracking on-off switch ButtonModel.
     *
     * @return tracking on-off switch ButtonModel
     */
    public ToggleButtonModel getTrackSwitchButtonModel() {
        return this.trackSwitchButtonModel;
    }

    /**
     * Return clear document Action.
     *
     * @return clear document Action
     */
    public Action getClearAction() {
        return this.clearAction;
    }

}
