package pro.redsoft.demo.textbox.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;

class SelectionHandler implements MouseMoveHandler, MouseDownHandler,
    MouseUpHandler, DoubleClickHandler, ClickHandler {

  private final CustomTextBox textBox;

  SelectionHandler(CustomTextBox textBox) {
    this.textBox = textBox;

    textBox.addMouseMoveHandler(this);
    textBox.addMouseDownHandler(this);
    textBox.addMouseUpHandler(this);
    textBox.addDoubleClickHandler(this);
    textBox.addClickHandler(this);
  }

  boolean isDown;

  @Override
  public void onMouseDown(MouseDownEvent event) {
    isDown = true;
  }

  @Override
  public void onMouseUp(MouseUpEvent event) {
    isDown = false;
  }

  @Override
  public void onMouseMove(MouseMoveEvent event) {
    int x = event.getRelativeX(textBox.canvas.getCanvasElement());
    int y = event.getRelativeY(textBox.canvas.getCanvasElement());
  }

  boolean hasWordSelection;

  @Override
  public void onDoubleClick(DoubleClickEvent event) {

    // remove browser's default selections
    event.preventDefault();
    removeSelection();

    // load relative coordinate of click
    int relativeX = event.getRelativeX(textBox.canvas.getCanvasElement());

    // current displacement by X axis that depends on words
    int dx = 0;

    // search and select word that relates to this click
    String[] words = this.textBox.textBuilder.toString().split(" ");
    for (String word : words) {

      // word length in absolute coordinates (with displacement)
      int relWordWidth = (word.length() * textBox.symbolWidth);
      int absoluteX = relWordWidth + dx;

      // search word matching click and draw selection
      if (absoluteX >= relativeX) {
        drawSelection(dx, relWordWidth, false);
        hasWordSelection = true;
        break; // next words lenght is also greater than relativeX
      }

      // update displacement (symbolWidth = lenght of delimiter(' '))
      dx += absoluteX + this.textBox.symbolWidth;
    }
  }

  @Override
  public void onClick(ClickEvent event) {

    // request focus
    textBox.setFocus(true);

    // remove existing selection
    if (hasWordSelection) {
      drawSelection(0, textBox.canvas.getCanvasElement().getWidth(), true);
      hasWordSelection = false;
    }

    // load relative coordinate of click
    int relativeX = event.getRelativeX(textBox.canvas.getCanvasElement());
    int symbolUnderCLick = (int) (Math.ceil(relativeX / textBox.symbolWidth) + 1);
    double curDx = textBox.symbolWidth * symbolUnderCLick;

    double maxDx = textBox.symbolWidth * textBox.textBuilder.length();
    if (curDx > maxDx) {
      curDx = maxDx;
    }

    // change cursor position
    textBox.animation.moveTo(curDx);
    textBox.dx = curDx;
  }

  private void drawSelection(double x, double w, boolean remove) {

    int canvasHeight = textBox.canvas.getCanvasElement().getHeight();
    double startY = 0.1 * canvasHeight;
    double endY = 0.99 * canvasHeight;

    if (remove) {
      textBox.context.clearRect(x, startY, w, endY);
      textBox.setText(textBox.textBuilder.toString());
    } else {
      textBox.context.save();
      textBox.context.setFillStyle("blue");
      textBox.context.setGlobalAlpha(0.3);
      textBox.context.fillRect(x, startY, w, endY);
      textBox.context.restore();
    }
  }

  native void removeSelection() /*-{
		if ($wnd.getSelection) {
			$wnd.getSelection().removeAllRanges();
		} else if ($wnd.selection) {
			$wnd.selection.empty();
		}
  }-*/;
}