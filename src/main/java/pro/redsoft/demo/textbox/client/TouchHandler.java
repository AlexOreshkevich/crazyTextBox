package pro.redsoft.demo.textbox.client;

import com.google.gwt.event.dom.client.*;

/**
 * @author Alexander Novik
 *         Date: 16.01.14
 */
public class TouchHandler implements TouchStartHandler, TouchEndHandler, TouchMoveHandler {

    private final CustomTextBox textBox;

    public TouchHandler(CustomTextBox textBox) {
        this.textBox = textBox;
        textBox.addTouchStartHandler(this);
        textBox.addTouchEndHandler(this);
        textBox.hiddenBox.addKeyPressHandler(textBox.inputHandler);
        textBox.hiddenBox.addKeyDownHandler(textBox.inputHandler);
    }

    @Override
    public void onTouchStart(TouchStartEvent touchStartEvent) {

    }

    @Override
    public void onTouchEnd(TouchEndEvent touchEndEvent) {
        textBox.hiddenBox.setFocus(true);
    }

    @Override
    public void onTouchMove(TouchMoveEvent touchMoveEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
