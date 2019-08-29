/*
 * License : The MIT License
 * Copyright(c) 2019 Olyutorskii
 */

package io.github.olyutorskii.quetexj;

import java.awt.EventQueue;
import javax.swing.BoundedRangeModel;
import javax.swing.ButtonModel;
import javax.swing.DefaultButtonModel;

/**
 * Automatic tracker that always tracks last position of BoundedRangeModel.
 * It's usefull for JScrollBar or JSlider view.
 *
 * <p>Tracking mode switching is supported by ButtonModel(optional).
 * If ButtonModel is selected, it is a tracking mode.
 *
 * <p>Tracking mode switch is also supported
 * by special BoundedRangeModel operations.
 * (Just sliding knob to max manually)
 */
public class MaxTracker {

    /** Invalid knob position. */
    private static final int VAL_INVALID = -1;


    private final BoundedRangeModel rangeModel;
    private final ButtonModel trackModeModel;

    /** Knob operation position when tracking-start . */
    private int trackStartPos;


    /**
     * Constructor.
     *
     * @param rangeModel BoundedRangeModel.
     */
    public MaxTracker(BoundedRangeModel rangeModel){
        this(rangeModel, new DefaultButtonModel());
        return;
    }

    /**
     * Constructor.
     *
     * @param rangeModel BoundedRangeModel.
     * @param trackModeModel ButtonModel for tracking mode.
     */
    public MaxTracker(
            BoundedRangeModel rangeModel,
            ButtonModel trackModeModel){
        super();

        this.rangeModel = rangeModel;
        this.trackModeModel = trackModeModel;

        this.rangeModel.addChangeListener(ev -> {
            eventBoundedRangeChanged();
        });

        this.trackModeModel.addItemListener(ev -> {
            eventTrackModeChanged();
        });

        resetTrackStartPos();

        return;
    }


    /**
     * Get associated BoundedRangeModel.
     *
     * @return BoundedRangeModel
     */
    public BoundedRangeModel getBoundedRangeModel(){
        return this.rangeModel;
    }

    /**
     * Get associated ButtonModel.
     *
     * @return ButtonModel
     */
    public ButtonModel getButtonModel(){
        return this.trackModeModel;
    }

    /**
     * Return tracking mode by ButtonModel.
     *
     * <p>If ButtonModel is selected, it is a tracking mode.
     *
     * @return Return true if tracking mode.
     */
    public boolean isTrackingMode(){
        boolean result = this.trackModeModel.isSelected();
        return result;
    }

    /**
     * Set tracking mode to ButtonModel.
     *
     * <p>It will fire ItemEvent from ButtonModel.
     *
     * <p>If ButtonModel is selected, it is a tracking mode.
     *
     * <p>If tracking mode is not changed, do nothing.
     *
     * @param tracking tracking mode
     */
    public void setTrackingMode(boolean tracking){
        if(EventQueue.isDispatchThread()){
            setTrackingModeImpl(tracking);
        }else{
            EventQueue.invokeLater(() -> {
                setTrackingModeImpl(tracking);
            });
        }
        return;
    }

    /**
     * Set tracking mode to ButtonModel.
     *
     * <p>(EDT only.)
     *
     * @param tracking tracking mode
     */
    private void setTrackingModeImpl(boolean tracking){
        boolean oldCond = isTrackingMode();
        if(tracking == oldCond) return;

        this.trackModeModel.setSelected(tracking);

        return;
    }

    /**
     * Return wheteher BoundedRangeModel is currently adjusted with mouse.
     *
     * @return Return true if adjusting.
     */
    private boolean isHandAdjusting(){
        boolean result = this.rangeModel.getValueIsAdjusting();
        return result;
    }

    /**
     * Return whether knob is touching max in BoundedRangeModel.
     *
     * @return Return true if touching.
     */
    private boolean isKnobTouchMax(){
        int val    = this.rangeModel.getValue();
        int max    = this.rangeModel.getMaximum();
        int extent = this.rangeModel.getExtent();

        boolean touchMax = val + extent >= max;
        return touchMax;
    }

    /**
     * Set track-start operation position.
     */
    private void setTrackStartPos(){
        this.trackStartPos = this.rangeModel.getValue();
        return;
    }

    /**
     * Reset track-start operation position.
     */
    private void resetTrackStartPos(){
        this.trackStartPos = VAL_INVALID;
        return;
    }

    /**
     * Return wheteher knob position at track-start operation is keeped.
     *
     * @return Return true if keeping.
     */
    private boolean keepingTrackStartPos(){
        int modelVal = this.rangeModel.getValue();
        boolean result = this.trackStartPos == modelVal;
        return result;
    }

    /**
     * Force knob to be touching max in BoundedRangeModel.
     *
     * <ul>
     * <li>If knob already touches max, do nothing.
     * <li>If adjusting knob (by hand), do nothing.
     * </ul>
     */
    private void forceKnobTouchMax(){
        if(isKnobTouchMax()) return;
        if(isHandAdjusting()) return;

        int max    = this.rangeModel.getMaximum();
        int extent = this.rangeModel.getExtent();
        int newVal = max - extent;
        this.rangeModel.setValue(newVal);

        return;
    }

    /**
     * Determine if tracking mode has been activated
     * by BoundedRangeModel operation.
     *
     * <p>If knob touches max in BoundedRangeModel,
     * tracking mode is activated.
     *
     * <p>Tracking mode is deactivated if knob detaches max again.
     *
     * <p>While holding knob by mouse after touching max,
     * tracking mode is not changed.
     */
    private void checkTrackingByKnob(){
        boolean knobTouchMax = isKnobTouchMax();
        if(knobTouchMax){
            setTrackingMode(true);
            setTrackStartPos();
        }else if(!keepingTrackStartPos()){
            setTrackingMode(false);
        }

        return;
    }

    /**
     * Receive ChangeListener event from BoundedRangeModel.
     */
    private void eventBoundedRangeChanged(){
        if(isHandAdjusting()){
            checkTrackingByKnob();
            return;
        }else{
            resetTrackStartPos();
        }

        if(isTrackingMode()){
            forceKnobTouchMax();
        }

        return;
    }

    /**
     * Receive ItemListener event from ButtonModel.
     *
     * <p>It means changing track mode event with checkbutton-view.
     */
    private void eventTrackModeChanged(){
        if(isTrackingMode()){
            forceKnobTouchMax();
        }
        return;
    }

}
