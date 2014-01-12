package pro.redsoft.demo.textbox.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.Context2d.TextBaseline;

class EvenKeyProcessingStrategy implements CustomTextBox.KeyProcessingStrategy {

  private final CustomTextBox textBox;

  /**
   * @param customTextBox
   */
  EvenKeyProcessingStrategy(CustomTextBox customTextBox) {
    textBox = customTextBox;
  }

  @Override
  public void addChar(char symbol, Canvas canvas, Context2d context) {

    // Every letter at an even position is shown turned upside down.
    Context2d itemContext = textBox.buildItemContext();
    textBox.cursor.removeCursor(textBox.dx);
    context.clearRect(textBox.dx, 0, textBox.symbolWidth,
        canvas.getCoordinateSpaceHeight());

    int y = 1;
    if (!textBox.isNumber(symbol)) {
      itemContext.setTextBaseline(TextBaseline.BOTTOM);
    } else {
      y = -2;
    }

    itemContext.translate(0, textBox.fontHeight + 3);
    itemContext.scale(1, -1);
    itemContext.fillText(symbol + "", 0, textBox.fontHeight + y);
    itemContext.restore();
    context.drawImage(itemContext.getCanvas(), textBox.dx, 0);
    textBox.updateIndex();
  }
}