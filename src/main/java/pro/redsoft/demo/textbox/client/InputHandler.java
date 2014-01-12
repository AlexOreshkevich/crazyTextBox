package pro.redsoft.demo.textbox.client;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;

class InputHandler implements KeyDownHandler, KeyPressHandler {

  private final CustomTextBox textBox;

  public InputHandler(CustomTextBox textBox) {
    this.textBox = textBox;
    textBox.addKeyDownHandler(this);
    textBox.addKeyPressHandler(this);
  }

  @Override
  public void onKeyDown(KeyDownEvent event) {

    switch (event.getNativeKeyCode()) {

    // remove last char
    case KeyCodes.KEY_BACKSPACE:
      textBox.removeChar();
      event.preventDefault();
      break;

    case KeyCodes.KEY_ENTER:
      event.preventDefault();
      break;
    }
  }

  @Override
  public void onKeyPress(KeyPressEvent event) {
    textBox.addChar((char) event.getUnicodeCharCode());
  }
}