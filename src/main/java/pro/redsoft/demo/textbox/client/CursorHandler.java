package pro.redsoft.demo.textbox.client;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.Context2d.LineCap;

/**
 * Handles cursor management logic.
 * 
 * @author Alex N. Oreshkevich
 */
class CursorHandler extends Animation {

  /** Displacement from start X coordinate of symbol. */
  private final int curDx;

  /** Width of cursor line. */
  private final int curLineWidth;

  private boolean isVisible;
  private final CustomTextBox textBox;

  CursorHandler(CustomTextBox customTextBox) {
    this.textBox = customTextBox;
    curDx = 0;
    curLineWidth = 1;
  }

  /**
   * Move cursor left from current position for width equal one symbol.
   */
  void moveLeft() {
    moveTo(textBox.dx - textBox.symbolWidth);
  }

  /**
   * Move cursor right from current position for width equal one symbol.
   */
  void moveRight() {
    moveTo(textBox.dx + textBox.symbolWidth);
  }

  /**
   * Move cursor to the X coordinate as specified.
   * 
   * @param x
   *          displacement by X axis
   */
  void moveTo(double x) {

    // check that moving to left/right (any x) is available
    // if no, fix parameter x for valid value
    double maxDx = textBox.getMaxTextWidth();
    if (x < 0) {
      x = 0;
    } else if (x > maxDx) {
      x = maxDx;
    }

    // clear current cursor
    removeCursor(textBox.dx);

    // update current cursor dx
    textBox.dx = x;
  }

  @Override
  protected void onStart() {
    isVisible = true;
    drawCursor("black");
    super.onStart();
  }

  @Override
  protected void onUpdate(double progress) {
    if (isVisible && (progress > 0.3)) {
      drawCursor("#FFFFFF");
      isVisible = false;
    }
  }

  void removeCursor(double x) {
    Context2d ctx = textBox.context;
    double dx = (curDx + curLineWidth);
    ctx.clearRect(x - dx, 0, 2 * dx, ctx.getCanvas().getHeight());
  }

  private void drawCursor(String strokeStyle) {
    Context2d ctx = textBox.context;
    ctx.setStrokeStyle(strokeStyle);
    ctx.beginPath();
    ctx.setLineWidth(curLineWidth);
    ctx.setLineCap(LineCap.BUTT);
    double dx = textBox.dx;
    ctx.moveTo(dx, textBox.fontHeight / 10);
    ctx.lineTo(dx, 1.2 * textBox.fontHeight);
    ctx.stroke();
  }
}