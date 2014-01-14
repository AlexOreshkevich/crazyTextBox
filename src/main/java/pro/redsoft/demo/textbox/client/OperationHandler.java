package pro.redsoft.demo.textbox.client;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import pro.redsoft.demo.textbox.client.OperationHandler.SelectionArea.Symbol;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;

class OperationHandler implements MouseMoveHandler, MouseDownHandler,
    MouseUpHandler, DoubleClickHandler {

  private final CustomTextBox textBox;
  boolean isDown;
  private List<Character> buffer;

  class SelectionArea {

    class Symbol {
      int ind;
      boolean selected;
      char value;
    }

    boolean isAllSelected;
    List<Symbol> chars = new ArrayList<OperationHandler.SelectionArea.Symbol>();

    void selectItems(int startInd, int endInd) {

      // something can be selected..
      clearSelection();

      for (int i = startInd; i <= endInd; i++) {
        chars.get(i - 1).selected = true;
      }

      textBox.cursorHandler.moveTo(endInd * textBox.symbolWidth);
      selectArea((startInd - 1) * textBox.symbolWidth,
          ((endInd - startInd) + 1) * textBox.symbolWidth);
    }

    int getCurrentInd() {
      return (int) ((textBox.dx) / textBox.symbolWidth) - 1;
    }

    void selectItem(int ind, boolean select, boolean cursorLeft) {

      // System.err.println("<system> Select item: ind = " + ind + " select = "
      // + select + " cursorLeft = " + cursorLeft);

      // we cann't select anything if textBox is empty
      if ((chars.size() == 0) || (ind > (chars.size() - 1))) {
        return;
      }

      if (!cursorLeft) {
        ind++;
      }

      if (!cursorLeft && (ind == (chars.size()))) {
        return;
      }

      if (!(ind >= 0) || !(ind < chars.size())) {
        return;
      }

      if (select) {
        Symbol s = chars.get(ind);
        double dx = textBox.dx;

        // if element is already selected, simple move cursor to next element
        if (s.selected) {
          textBox.cursorHandler.moveTo(dx
              + ((cursorLeft ? -1 : 1) * textBox.symbolWidth));
          return;
        }

        // select left from cursor
        if (cursorLeft) {

          // skip selection from impossible positions
          if (textBox.dx == 0) {
            return;
          }

          textBox.cursorHandler.moveTo(textBox.dx - textBox.symbolWidth);
          s.selected = true;
          selectArea(dx - textBox.symbolWidth, textBox.symbolWidth);

        } else { // select right from cursor

          // skip selection from impossible positions
          if (dx == textBox.getMaxTextWidth()) {
            return;
          }

          textBox.cursorHandler.moveTo(dx + textBox.symbolWidth);
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
      isAllSelected = false;
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
      ctx.fillRect(x - 1, startY, w + 1, endY);
      ctx.restore();
    }

    private int getTextWidth() {
      return textBox.symbolWidth * chars.size();
    }
  }

  private final SelectionArea selectionArea;

  OperationHandler(CustomTextBox textBox) {

    this.textBox = textBox;
    selectionArea = new SelectionArea();

    textBox.addMouseMoveHandler(this);
    textBox.addMouseDownHandler(this);
    textBox.addMouseUpHandler(this);
    textBox.addDoubleClickHandler(this);

    buffer = new LinkedList<Character>();
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
      moveCursorOnClick(event);
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

    if ((x > selectionArea.getTextWidth()) || (x <= 0)) {
      return;
    }

    // if relative (dx) displacement greater than 25%,
    // execute selection and move cursor
    double dx = x - textBox.dx;
    boolean skip = Math.abs((dx / textBox.symbolWidth)) < 0.25;

    if (skip) {
      return;
    }

    boolean isLeft = dx < 0;
    if (isLeft) {
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

  void moveCursorOnClick(MouseEvent<?> event) {

    // request focus
    event.preventDefault();
    textBox.setFocus(true);

    // remove existing selection
    clearSelection();

    // load relative coordinate of click
    int relativeX = event.getRelativeX(textBox.canvas.getCanvasElement());
    int symbolUnderCLick = (int) (Math.ceil(relativeX / textBox.symbolWidth) + 1);
    double curDx = textBox.symbolWidth * symbolUnderCLick;

    // change cursor position
    textBox.cursorHandler.moveTo(curDx);
  }

  native void removeSelection() /*-{
		if ($wnd.getSelection) {
			$wnd.getSelection().removeAllRanges();
		} else if ($wnd.selection) {
			$wnd.selection.empty();
		}
  }-*/;

  public void selectLeft() {
    selectionArea.selectItem(selectionArea.getCurrentInd(), true, true);
  }

  public void selectRight() {
    selectionArea.selectItem(selectionArea.getCurrentInd(), true, false);
  }

  public void clearSelection() {
    if (textBox.textBuilder.length() == 0) {
      return;
    }
    selectionArea.clearSelection();
  }

  public void copy() {

    // flush buffer
    buffer.clear();

    // load all selected symbols into the buffer
    for (Symbol symbol : selectionArea.chars) {
      if (symbol.selected) {
        buffer.add(symbol.value);
      }
    }
  }

  public void cut() {

    // flush buffer
    buffer.clear();

    // build String that contains all unselected chars
    StringBuilder sb = new StringBuilder();
    for (Symbol symbol : selectionArea.chars) {
      if (symbol.selected) {
        buffer.add(symbol.value);
      } else {
        sb.append(symbol.value);
      }
    }

    // remove selected chars from textBox
    // TODO simplest implementation: full redraw
    textBox.setText(sb.toString());
  }

  public void paste() {

    // do nothing if buffer is empty
    if (buffer.isEmpty()) {
      return;
    }

    // paste symbols from buffer one by one
    for (Character symbol : buffer) {
      textBox.addChar(symbol);
    }
  }

  public void selectAll() {
    selectionArea.selectAll();
  }
}