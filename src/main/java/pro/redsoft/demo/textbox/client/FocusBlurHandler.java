package pro.redsoft.demo.textbox.client;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.Timer;

class FocusBlurHandler implements FocusHandler, BlurHandler {

  private final static int CURSOR_INTERVAL = 1000;

  private CustomTextBox textBox;
  private final Timer cursorTimer = new Timer() {

    @Override
    public void run() {
      textBox.cursorHandler.run(CURSOR_INTERVAL);
    }
  };

  FocusBlurHandler(CustomTextBox textBox) {
    this.textBox = textBox;

    // init handlers
    textBox.addFocusHandler(this);
    textBox.addBlurHandler(this);
  }

  @Override
  public void onFocus(FocusEvent event) {
    cursorTimer.scheduleRepeating(CURSOR_INTERVAL);
  }

  @Override
  public void onBlur(BlurEvent event) {
    cursorTimer.cancel();
    textBox.cursorHandler.removeCursor(textBox.dx);
  }
}
