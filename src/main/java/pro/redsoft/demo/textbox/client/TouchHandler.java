package pro.redsoft.demo.textbox.client;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.TextBox;

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
    }

    @Override
    public void onTouchStart(TouchStartEvent touchStartEvent) {

        TextBox er = (TextBox) textBox.panel.getWidget(textBox.panel.getWidgetIndex(textBox.hiddenBox));
        er.setFocus(true);

    }

    @Override
    public void onTouchEnd(TouchEndEvent touchEndEvent) {
//        Window.alert("TouchEventEnd");
//        TextBox focus = TextBox.wrap(DOM.getElementById("touchText"));
//        focus.setFocus(true);
    }

    @Override
    public void onTouchMove(TouchMoveEvent touchMoveEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
