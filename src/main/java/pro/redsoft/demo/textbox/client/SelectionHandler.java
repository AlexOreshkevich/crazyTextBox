package pro.redsoft.demo.textbox.client;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.CanvasElement;
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
  boolean isDown, hasWordSelection;

  SelectionHandler(CustomTextBox textBox) {

    this.textBox = textBox;

    textBox.addMouseMoveHandler(this);
    textBox.addMouseDownHandler(this);
    textBox.addMouseUpHandler(this);
    textBox.addDoubleClickHandler(this);
    textBox.addClickHandler(this);
  }

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

    // skip all actions while user don't press the mouse button
    if (!isDown) {
      return;
    }

    // load current dx
    CanvasElement canvasElement = textBox.canvas.getCanvasElement();
    int x = event.getRelativeX(canvasElement);

    // if x is less than textBox.dx greater than 25%
    // execute selection and move cursor
    double dx = x - textBox.dx;
    boolean skip = Math.abs((dx / textBox.symbolWidth)) < 0.25;

    if (skip) {
      return;
    }

    if (dx < 0) { // moving to left
      selectLeft();
    } else {
      selectRight();
    }
  }

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
    String[] words = textBox.textBuilder.toString().split(" ");
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
      dx += (relWordWidth + textBox.symbolWidth);
    }
  }

  @Override
  public void onClick(ClickEvent event) {

    // request focus
    textBox.setFocus(true);

    // remove existing selection
    if (hasWordSelection) {
      clearSelection();
      hasWordSelection = false;
    }

    // load relative coordinate of click
    int relativeX = event.getRelativeX(textBox.canvas.getCanvasElement());
    int symbolUnderCLick = (int) (Math.ceil(relativeX / textBox.symbolWidth) + 1);
    double curDx = textBox.symbolWidth * symbolUnderCLick;

    // change cursor position
    textBox.cursor.moveTo(curDx);
  }

  void selectLeft() {
    if (textBox.dx > 0) {
      double dx = textBox.dx;
      textBox.cursor.moveTo(textBox.dx - textBox.symbolWidth);
      drawSelection(dx - textBox.symbolWidth, textBox.symbolWidth, false);
    }
  }

  void selectRight() {
    if (textBox.dx <= (textBox.getMaxTextWidth() - textBox.symbolWidth)) {
      double dx = textBox.dx;
      textBox.cursor.moveTo(textBox.dx + textBox.symbolWidth);
      drawSelection(dx, textBox.symbolWidth, false);
    }
  }

  private void drawSelection(double x, double w, boolean remove) {

    Context2d ctx = textBox.context;
    int canvasHeight = textBox.canvas.getCanvasElement().getHeight();
    double startY = 0.1 * canvasHeight;
    double endY = 0.99 * canvasHeight;

    if (remove) {
      ctx.clearRect(x, 0, w, canvasHeight);
      textBox.setText(textBox.textBuilder.toString());
    } else {
      ctx.save();
      ctx.setFillStyle("blue");
      ctx.setGlobalAlpha(0.3);
      ctx.fillRect(x, startY, w, endY);
      ctx.restore();
    }
  }

  void clearSelection() {
    drawSelection(0, textBox.canvas.getCanvasElement().getWidth(), true);
  }

  native void removeSelection() /*-{
		if ($wnd.getSelection) {
			$wnd.getSelection().removeAllRanges();
		} else if ($wnd.selection) {
			$wnd.selection.empty();
		}
  }-*/;
}