/*
 * License : The MIT License
 * Copyright(c) 2019 Olyutorskii
 */

package io.github.olyutorskii.quetexj;

import java.awt.event.ActionEvent;
import java.util.Objects;
import javax.swing.AbstractAction;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 * Clear document action.
 */
@SuppressWarnings("serial")
class ClearDocumentAction extends AbstractAction {

    private final Document document;


    /**
     * Constructor.
     *
     * @param document target document
     */
    ClearDocumentAction(Document document) {
        super();

        Objects.requireNonNull(document);
        this.document = document;

        return;
    }

    /**
     * Receive clear document event.
     */
    private void eventClearDocument() {
        int len = this.document.getLength();
        try {
            this.document.remove(0, len);
        } catch (BadLocationException e) {
            assert false;
        }

        return;
    }

    /**
     * {@inheritDoc}
     *
     * @param ev {@inheritDoc}
     */
    @Override
    public void actionPerformed(ActionEvent ev) {
        // assert EventQueue.isDispatchThread();
        eventClearDocument();
        return;
    }

}
