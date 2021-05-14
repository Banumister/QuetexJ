/*
 * License : The MIT License
 * Copyright(c) 2019 Olyutorskii
 */

package io.github.olyutorskii.quetexj;

import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Objects;
import javax.swing.BoundedRangeModel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

/**
 * Automatically keep height of text component
 * by chopping head of Document model.
 *
 * <p>Component is chopped row by row.
 *
 * <p>Physical text-line layout is preserved.
 *
 * <p>Relative position of vertical BoundedRangeModel is adjusted
 * to keep JViewport view in JScrollPane
 * as much as possible.
 *
 * <p>PlainDocument only supported.
 */
public class HeightKeeper {

    /** Default height limit. */
    public static final int DEF_HEIGHTLIMIT = 3000;
    /** Default new height. */
    public static final int DEF_NEWHEIGHT   = 2500;

    private static final Rectangle DMY_RECT = new Rectangle();


    private final JTextArea textComp;

    private final BoundedRangeModel rangeModel;

    private int heightLimit;
    private int newHeight;
    private final Object condLock = new Object();

    private final SizeWatcher watcher = new SizeWatcher();


    /**
     * Constructor.
     *
     * <p>Condition parameters are default value.
     *
     * @param textComp text component
     * @param rangeModel bounded range model
     */
    public HeightKeeper(JTextArea textComp, BoundedRangeModel rangeModel) {
        this(textComp, rangeModel, DEF_HEIGHTLIMIT, DEF_NEWHEIGHT);
        return;
    }

    /**
     * Constructor.
     *
     * <ul>
     * <li>newHeight must be positive integer value.
     * <li>newHeight must be smaller than heightLimit.
     * </ul>
     *
     * @param textComp text component
     * @param rangeModel bounded range model
     * @param heightLimit height limit condition
     * @param newHeight new height when over limit
     * @throws IllegalArgumentException illegal integer argument
     */
    public HeightKeeper(JTextArea textComp, BoundedRangeModel rangeModel,
            int heightLimit, int newHeight) {
        super();

        Objects.requireNonNull(rangeModel);

        if (newHeight <= 0) throw new IllegalArgumentException();
        if (heightLimit <= newHeight) throw new IllegalArgumentException();

        this.textComp = textComp;
        this.textComp.addComponentListener(this.watcher);

        this.heightLimit = heightLimit;
        this.newHeight = newHeight;

        this.rangeModel = rangeModel;

        return;
    }

    /**
     * Return associated text component.
     *
     * @return text component
     */
    public JTextComponent getTextComponent() {
        return this.textComp;
    }

    /**
     * Return associated BoundedRangeModel.
     *
     * @return BoundedRangeModel
     */
    public BoundedRangeModel getBoundedRangeModel() {
        return this.rangeModel;
    }

    /**
     * Return height limit condition.
     *
     * @return height limit
     */
    public int getHeightLimit() {
        return this.heightLimit;
    }

    /**
     * Return new height.
     *
     * @return new height
     */
    public int getNewHeight() {
        return this.newHeight;
    }

    /**
     * Set height condition values.
     *
     * <ul>
     * <li>newHeightArg must be positive integer value.
     * <li>newHeightArg must be smaller than heightLimitArg.
     * </ul>
     *
     * @param heightLimitArg height limit condition
     * @param newHeightArg new height when over limit
     * @throws IllegalArgumentException illegal integer argument
     */
    public void setConditions(int heightLimitArg, int newHeightArg)
            throws IllegalArgumentException {
        if (newHeightArg <= 0) {
            throw new IllegalArgumentException();
        }
        if (heightLimitArg <= newHeightArg) {
            throw new IllegalArgumentException();
        }

        synchronized (this.condLock) {
            this.heightLimit = heightLimitArg;
            this.newHeight = newHeightArg;
        }

        if (EventQueue.isDispatchThread()) {
            eventResized();
        } else {
            EventQueue.invokeLater(() -> {
                eventResized();
            });
        }

        return;
    }

    /**
     * Receive component resized event.
     *
     * <p>If component height is smaller than height limit, do nothing.
     */
    void eventResized() {
        int condHeightLimit;
        int condNewHeight;
        synchronized (this.condLock) {
            condHeightLimit = this.heightLimit;
            condNewHeight   = this.newHeight;
        }

        int compHeight = this.textComp.getHeight();
        if (compHeight < condHeightLimit) return;

        int chopHeight = compHeight - condNewHeight;
        int oldRangeVal = this.rangeModel.getValue();

        chopHeadHeightRowBounds(chopHeight);

        adjustBoundedRangeModel(chopHeight, oldRangeVal);

        return;
    }

    /**
     * Chop text component height from ceiling.
     *
     * <p>Component height will be shrunk between row bounds.
     *
     * <p>Text component will be resized later EventQueue.
     *
     * @param chopRegionHeight Chopping height from ceiling.
     */
    private void chopHeadHeightRowBounds(int chopRegionHeight) {
        int docLastPos = chopHeightToLinedOffset(chopRegionHeight);
        chopHeadHeightByDocPos(docLastPos);
        return;
    }

    /**
     * Convert from head chop height
     * to physical line-end offset in Document model.
     *
     * @param chopHeight head chop height in text component
     * @return offset in Document model. -1 if undefined.
     */
    private int chopHeightToLinedOffset(int chopHeight) {
        int chopWidth  = this.textComp.getWidth();

        // Diagonal corner of shrink region
        Point edgePoint = new Point(chopWidth  - 1, chopHeight - 1);

        int docOffset = this.textComp.viewToModel(edgePoint);

        return docOffset;
    }

    /**
     * Chop text component height by chopping head of Document model.
     *
     * <p>If Document position is negative, do nothing.
     *
     * <p>Text component will be resized later EventQueue.
     *
     * @param docLastPos last char position of chop-text in Document model.
     */
    private void chopHeadHeightByDocPos(int docLastPos) {
        if (docLastPos < 0) return;

        Document document = this.textComp.getDocument();
        int docLength = document.getLength();
        if (docLength <= 0) return;

        int regionLength = Integer.min(docLastPos + 1, docLength);

        try {
            document.remove(0, regionLength);
        } catch (BadLocationException e) {
            assert false;
        }

        return;
    }

    /**
     * Adjust BoundedRangeModel to keep JViewport view in JScrollPane.
     *
     * @param chopHeight chop height
     * @param oldRangeVal BoundedRangeModel value before chopping
     * @return adjusted BoundedRangeModel value.
     */
    private int adjustBoundedRangeModel(int chopHeight, int oldRangeVal) {
        int realChopHeight = getRealChopHeight(chopHeight);
        int newRangeVal = oldRangeVal - realChopHeight;
        this.rangeModel.setValue(newRangeVal);
        return newRangeVal;
    }

    /**
     * Convert head height to row-bounds height.
     *
     * <p>Result is multiple of row height.
     *
     * @param chopHeight chop height
     * @return row-bounds height
     */
    private int getRealChopHeight(int chopHeight) {
        int insetsTop = this.textComp.getInsets().top;
        int bodyHeight = chopHeight - insetsTop;
        int rowHeight = getRowHeight();
        int chopRows = bodyHeight / rowHeight + 1;

        int realChopHeight = rowHeight * chopRows + insetsTop;

        return realChopHeight;
    }

    /**
     * Get row height of text component.
     *
     * @return row height
     * @see JTextArea#getRowHeight()
     */
    private int getRowHeight() {
        int result =
                this.textComp.getScrollableUnitIncrement(
                        DMY_RECT, SwingConstants.VERTICAL, 0);
        return result;
    }


    /**
     * Component resize watcher.
     */
    private class SizeWatcher extends ComponentAdapter {

        /**
         * Constructor.
         */
        SizeWatcher() {
            super();
            return;
        }

        /**
         * {@inheritDoc}
         * @param ev {@inheritDoc}
         */
        @Override
        public void componentResized(ComponentEvent ev) {
            eventResized();
            return;
        }

    }

}
