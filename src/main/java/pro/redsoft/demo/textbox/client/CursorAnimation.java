package pro.redsoft.demo.textbox.client;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.Context2d.LineCap;

// 1# draw black line
// 2# pause for 0.3 of progress
// 3# draw white line
class CursorAnimation extends Animation {

  private boolean isVisible;
  private final CustomTextBox textBox;
  private final int curDx;
  private final double curLineWidth;

  CursorAnimation(CustomTextBox customTextBox) {
    this.textBox = customTextBox;
    curDx = 1;
    curLineWidth = 1;
  }

  @Override
  protected void onUpdate(double progress) {
    if (isVisible && (progress > 0.3)) {
      drawCursor("#FFFFFF");
      isVisible = false;
    }
  }

  @Override
  protected void onStart() {
    isVisible = true;
    drawCursor("black");
    super.onStart();
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

  void removeCursor(double x) {
    Context2d ctx = textBox.context;
    double dx = (curDx + curLineWidth);
    ctx.clearRect(x - dx, 0, 2 * dx, ctx.getCanvas().getHeight());
  }

  void moveTo(double x) {

    // clear current cursor
    removeCursor(textBox.dx);

    // update current cursor dx
    textBox.dx = x;
  }
}