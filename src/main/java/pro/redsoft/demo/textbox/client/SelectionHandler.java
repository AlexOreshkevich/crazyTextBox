package pro.redsoft.demo.textbox.client;

import java.util.ArrayList;
import java.util.List;

import pro.redsoft.demo.textbox.client.SelectionHandler.SelectionArea.Symbol;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.NativeEvent;
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
  boolean isDown;

  class SelectionArea {

    class Symbol {
      int ind;
      boolean selected;
      char value;
    }

    boolean isAllSelected;
    List<Symbol> chars = new ArrayList<SelectionHandler.SelectionArea.Symbol>();

    void selectItems(int startInd, int endInd) {

      // something can be selected..
      clearSelection();

      for (int i = startInd; i <= endInd; i++) {
        chars.get(i - 1).selected = true;
      }

      textBox.cursor.moveTo(endInd * textBox.symbolWidth);
      selectArea((startInd - 1) * textBox.symbolWidth,
          ((endInd - startInd) + 1) * textBox.symbolWidth);
    }

    int getCurrentInd() {
      return (int) ((1.1 * textBox.dx) / textBox.symbolWidth);
    }

    void selectItem(int ind, boolean select, boolean cursorLeft) {

      ind--; // switch to internal coordinates: 0...arr.length - 1
      if (ind > chars.size()) {
        ind = chars.size() - 1;
      }

      if (select) {
        Symbol s = chars.get(ind);
        if (s.selected) {
          return;
        }
        double dx = textBox.dx;

        // select left from cursor
        if (cursorLeft) {

          // skip selection from impossible positions
          if (textBox.dx == 0) {
            return;
          }

          textBox.cursor.moveTo(textBox.dx - textBox.symbolWidth);
          s.selected = true;
          selectArea(dx - textBox.symbolWidth, textBox.symbolWidth);

        } else { // select right from cursor

          // skip selection from impossible positions
          if (dx == textBox.getMaxTextWidth()) {
            return;
          }

          textBox.cursor.moveTo(dx + textBox.symbolWidth);
          s.selected = true;
          selectArea(dx, textBox.symbolWidth);
        }

      } else {
        // TODO
      }
    }

    void selectAll() {
      selectAll(true);
    }

    void selectAll(boolean select) {

      if (isAllSelected) {
        return;
      }

      clearSelection(); // something can be selected..

      if (select) {
        for (Symbol s : chars) {
          s.selected = true;
        }
        isAllSelected = true;
        selectArea(0, getTextWidth());
      } else {
        isAllSelected = false;
      }
    }

    void clearSelection() {
      Context2d ctx = textBox.context;
      int canvasHeight = textBox.canvas.getCanvasElement().getHeight();
      ctx.clearRect(0, 0, getTextWidth(), canvasHeight);
      textBox.setText(textBox.textBuilder.toString());
    }

    private void selectArea(double x, double w) {
      Context2d ctx = textBox.context;
      int canvasHeight = textBox.canvas.getCanvasElement().getHeight();
      double startY = 0.1 * canvasHeight;
      double endY = 0.99 * canvasHeight;
      ctx.save();
      ctx.setFillStyle("blue");
      ctx.setGlobalAlpha(0.3);
      ctx.fillRect(x, startY, w, endY);
      ctx.restore();
    }

    private int getTextWidth() {
      return textBox.symbolWidth * chars.size();
    }
  }

  private final SelectionArea selectionArea;

  SelectionHandler(CustomTextBox textBox) {

    this.textBox = textBox;
    selectionArea = new SelectionArea();

    textBox.addMouseMoveHandler(this);
    textBox.addMouseDownHandler(this);
    textBox.addMouseUpHandler(this);
    textBox.addDoubleClickHandler(this);
    textBox.addClickHandler(this);
  }

  void onSetText(String text) {
    selectionArea.chars = new ArrayList<Symbol>();
    char[] symbols = text.toCharArray();
    for (int i = 0; i < symbols.length; i++) {
      Symbol e = selectionArea.new Symbol();
      e.ind = i;
      e.selected = false;
      e.value = symbols[i];
      selectionArea.chars.add(e);
    }
  }

  void onAddChar(char c) {
    Symbol e = selectionArea.new Symbol();
    e.ind = selectionArea.chars.size();
    e.selected = false;
    e.value = c;
    selectionArea.chars.add(e);
  }

  @Override
  public void onMouseDown(MouseDownEvent event) {
    if (event.getNativeButton() == NativeEvent.BUTTON_LEFT) {
      isDown = true;
    }
  }

  @Override
  public void onMouseUp(MouseUpEvent event) {
    if (event.getNativeButton() == NativeEvent.BUTTON_LEFT) {
      isDown = false;
    }
  }

  @Override
  public void onMouseMove(MouseMoveEvent event) {

    // skip all actions while user don't press the mouse button
    if (!isDown || (textBox.textBuilder.length() == 0)) {
      return;
    }

    // load current dx
    CanvasElement canvasElement = textBox.canvas.getCanvasElement();
    int x = event.getRelativeX(canvasElement);

    // if relative (dx) displacement greater than 25%,
    // execute selection and move cursor
    double dx = x - textBox.dx;
    boolean skip = Math.abs((dx / textBox.symbolWidth)) < 0.25;

    if (skip) {
      return;
    }

    int ind = (int) ((1.1 * textBox.dx) / textBox.symbolWidth);
    boolean isLeft = dx < 0;

    selectionArea.selectItem(ind + (isLeft ? -1 : 1), true, isLeft);
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

        int startInd = dx / textBox.symbolWidth;
        int endInd = absoluteX / textBox.symbolWidth;

        if (startInd == 0) {
          startInd = 1;
        } else {
          startInd++;
        }

        selectionArea.selectItems(startInd, endInd);

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
    clearSelection();

    // load relative coordinate of click
    int relativeX = event.getRelativeX(textBox.canvas.getCanvasElement());
    int symbolUnderCLick = (int) (Math.ceil(relativeX / textBox.symbolWidth) + 1);
    double curDx = textBox.symbolWidth * symbolUnderCLick;

    // change cursor position
    textBox.cursor.moveTo(curDx);
  }

  native void removeSelection() /*-{
		if ($wnd.getSelection) {
			$wnd.getSelection().removeAllRanges();
		} else if ($wnd.selection) {
			$wnd.selection.empty();
		}
  }-*/;

  public void selectLeft() {
    selectionArea.selectItem(selectionArea.getCurrentInd() - 1, true, true);
  }

  public void selectRight() {
    selectionArea.selectItem(selectionArea.getCurrentInd() + 1, true, false);
  }

  public void clearSelection() {
    if (textBox.textBuilder.length() == 0) {
      return;
    }
    selectionArea.clearSelection();
  }
}