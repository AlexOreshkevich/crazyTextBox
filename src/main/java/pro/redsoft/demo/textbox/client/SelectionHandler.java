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

  /**
   * @param textBox
   */
  SelectionHandler(CustomTextBox textBox) {
    this.textBox = textBox;
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
    int relativeX = event.getRelativeX(this.textBox.canvas.getCanvasElement());

    // current displacement by X axis that depends on words
    int dx = 0;

    // search and select word that relates to this click
    String[] words = this.textBox.textBuilder.toString().split(" ");
    for (String word : words) {

      // word length in absolute coordinates (with displacement)
      int absoluteX = (word.length() * this.textBox.symbolWidth) + dx;

      // search word matching click and draw selection
      if (absoluteX >= relativeX) {
        drawSelection(0, 0, absoluteX, this.textBox.canvas.getCanvasElement()
            .getHeight(), false);
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
      drawSelection(0, 0, this.textBox.canvas.getCanvasElement().getWidth(),
          this.textBox.canvas.getCanvasElement().getHeight(), true);
      hasWordSelection = false;
    }
  }

  private void drawSelection(double x, double y, double w, double h,
      boolean remove) {

    if (remove) {
      textBox.context.clearRect(x, y, w, h);
      textBox.setText(textBox.textBuilder.toString());
    } else {
      textBox.context.save();
      textBox.context.setFillStyle("blue");
      textBox.context.setGlobalAlpha(0.3);
      textBox.context.fillRect(x, y, w, h);
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