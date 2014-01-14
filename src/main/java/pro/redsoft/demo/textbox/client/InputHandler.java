package pro.redsoft.demo.textbox.client;

import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;

class InputHandler implements KeyDownHandler, KeyPressHandler,
    ContextMenuHandler {

  private final CustomTextBox textBox;

  public InputHandler(CustomTextBox textBox) {
    this.textBox = textBox;
    textBox.addKeyDownHandler(this);
    textBox.addKeyPressHandler(this);
    textBox.addDomHandler(this, ContextMenuEvent.getType());
  }

  @Override
  public void onKeyDown(KeyDownEvent event) {

    switch (event.getNativeKeyCode()) {

    // remove last char
    case KeyCodes.KEY_BACKSPACE:
      textBox.removeChar();
      event.preventDefault();
      break;

    case KeyCodes.KEY_LEFT:
      if (event.isShiftKeyDown()) {
        textBox.operationHandler.selectLeft();
      } else {
        textBox.cursorHandler.moveLeft();
      }
      event.preventDefault();
      break;

    case KeyCodes.KEY_RIGHT:
      if (event.isShiftKeyDown()) {
        textBox.operationHandler.selectRight();
      } else {
        textBox.cursorHandler.moveRight();
      }
      event.preventDefault();
      break;

    case KeyCodes.KEY_ENTER:
    case KeyCodes.KEY_UP:
    case KeyCodes.KEY_DOWN:
      event.preventDefault();
      break;
    }

    // Operations and behavior
    // Clipboard operations: copy, cut, paste, select all
    if (event.isControlKeyDown()) {
      switch (event.getNativeKeyCode()) {
      case 'c':
      case 'C':
        textBox.operationHandler.copy();
        event.preventDefault();
        break;

      case 'x':
      case 'X':
        textBox.operationHandler.cut();
        event.preventDefault();
        break;

      case 'v':
      case 'V':
        textBox.operationHandler.paste();
        event.preventDefault();
        break;

      case 'a':
      case 'A':
        textBox.operationHandler.selectAll();
        event.preventDefault();
        break;
      }
    }
  }

  @Override
  public void onKeyPress(KeyPressEvent event) {
    textBox.addChar((char) event.getUnicodeCharCode());
  }

  @Override
  public void onContextMenu(ContextMenuEvent event) {
    event.preventDefault();
    textBox.menu.setPopupPosition(event.getNativeEvent().getClientX(), event
        .getNativeEvent().getClientY());
    textBox.menu.show();
  }
}
