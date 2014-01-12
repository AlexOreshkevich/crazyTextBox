package pro.redsoft.demo.textbox.client;

import pro.redsoft.demo.textbox.client.CustomTextBox.KeyProcessingStrategy;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;

class OddKeyProcessingStrategy implements CustomTextBox.KeyProcessingStrategy {

  /**
   * 
   */
  private final CustomTextBox textBox;

  /**
   * @param customTextBox
   */
  OddKeyProcessingStrategy(CustomTextBox customTextBox) {
    textBox = customTextBox;
  }

  @Override
  public void addChar(char symbol, Canvas canvas, Context2d context) {

    Context2d itemContext = textBox.buildItemContext();
    textBox.animation.removeCursor(textBox.dx);
    context.clearRect(textBox.dx, 0, textBox.symbolWidth, canvas.getCoordinateSpaceHeight());

    itemContext.translate(0, textBox.fontHeight);
    itemContext.fillText(symbol + "", 0, 0);
    itemContext.restore();
    context.drawImage(itemContext.getCanvas(), textBox.dx, 0);
    textBox.updateIndex();
  }
}