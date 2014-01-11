package pro.redsoft.demo.textbox.client;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.Context2d.LineCap;

// 1# draw black line
// 2# pause for 0.3 of progress
// 3# draw white line
class CursorAnimation extends Animation {

  private final CustomTextBox customTextBox;

  CursorAnimation(CustomTextBox customTextBox) {
    this.customTextBox = customTextBox;
  }

  private boolean isVisible;

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

    Context2d ctx = customTextBox.context;
    ctx.setStrokeStyle(strokeStyle);
    ctx.beginPath();
    ctx.setLineCap(LineCap.BUTT);

    double curDx = customTextBox.dx;
    if (curDx == 0) {
      curDx = customTextBox.fontHeight / 10;
    }

    ctx.moveTo(curDx, customTextBox.fontHeight / 10);
    ctx.lineTo(curDx, 1.2 * customTextBox.fontHeight);

    ctx.stroke();
  }

  void updatePosition() {
    double dx = customTextBox.dx;
    Context2d ctx = customTextBox.context;
    ctx.save();
    // ctx.clearRect(0.95 * dx, 0, 2, customTextBox.fontHeight);
    ctx.restore();
  }
}