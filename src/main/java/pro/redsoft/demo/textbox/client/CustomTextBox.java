package pro.redsoft.demo.textbox.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.Context2d.TextAlign;
import com.google.gwt.canvas.dom.client.Context2d.TextBaseline;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.FocusPanel;

/**
 * <p>
 * The control supports any UTF-8 encoded text input, updating its displayed
 * contents real-time (as the user types it).
 * </p>
 * <p>
 * Every letter at an even position is shown turned upside down.
 * </p>
 * <p>
 * The baselines of the inverted font must be adjusted not to break the text
 * vertical alignment of the letters in the resulting text view which contains
 * letters with mixed orientations (as shown on the picture).
 * </p>
 * <p>
 * The control must display a regular text cursor indicating the current
 * position. This cursor must be blinking roughly once a second (like in a
 * normal text input).
 * </p>
 * 
 * @see https://umstelkb.atlassian.net/wiki/display/SC/Crazy+Text+Input
 * @author Alex N. Oreshkevich
 */
public class CustomTextBox extends FocusPanel {

  // Note: It’s best to use vector fonts when scaling or rotating text,
  // because bitmapped fonts can appear jagged when scaled up or rotated.
  private final String fontName = "Courier";
  private final int MAX_CHARS = 310;

  public interface KeyProcessingStrategy {
    void addChar(char symbol, Canvas canvas, Context2d context);
  }

  StringBuilder textBuilder = new StringBuilder();

  private final StrategyProvider provider = new StrategyProvider(this);
  final CursorHandler cursor = new CursorHandler(this);

  Canvas canvas = Canvas.createIfSupported();
  Context2d context = canvas.getContext2d();

  SelectionHandler selectionHandler = new SelectionHandler(this);
  StringBuilder currentText = new StringBuilder();

  double dx = 0;
  int fontHeight = 0;
  int symbolWidth;

  private int maxWidth, maxHeight;

  FocusBlurHandler focusBlurHandler;

  public final SimpleContextMenu menu = new SimpleContextMenu();

  public CustomTextBox() {
    setWidget(canvas);

    // add handlers
    focusBlurHandler = new FocusBlurHandler(this);
    new InputHandler(this);

    setStyleName("gwt-CustomTextBox");
    setSize("300px", "30px");

    Event.addNativePreviewHandler(new Event.NativePreviewHandler() {

      @Override
      public void onPreviewNativeEvent(NativePreviewEvent event) {
        if (event.getTypeInt() == Event.ONCLICK) {
          if (menu.isShowing()) {
            menu.hide();
            return;
          }
          int x = event.getNativeEvent().getClientX();
          int y = event.getNativeEvent().getClientY();
          if ((x > getAbsoluteLeft())
              && (x < (getAbsoluteLeft() + getOffsetWidth()))
              && (y > getAbsoluteTop())
              && (y < (getAbsoluteTop() + getOffsetHeight()))) {
          } else {
            cursor.removeCursor(dx);
            selectionHandler.clearSelection();
            focusBlurHandler.cancelTimer();
          }
        }
      }
    });
  }

  Context2d buildItemContext() {
    Canvas item = Canvas.createIfSupported();
    Context2d itemContext = item.getContext2d();
    itemContext.save();
    itemContext.setFont(fontHeight + "pt " + fontName);
    return itemContext;
  }

  private void initContext(String font) {

    resetInd();

    context.setFont(font);
    context.setTextAlign(TextAlign.LEFT);
    context.setFillStyle("black");
    context.setTextBaseline(TextBaseline.TOP);
  }

  boolean isNumber(int c) {
    return (c >= 48) && (c <= 59);
  }

  void resetInd() {
    dx = 0;
    provider.cntr = 0;
  }

  void addChar(char symbol) {

    // The maximum input length must be set to 310 characters.
    if (textBuilder.length() == MAX_CHARS) {
      return;
    }

    provider.getStrategy().addChar(symbol, canvas, context);
    textBuilder.append(symbol);
    selectionHandler.onAddChar(symbol);
  }

  void removeChar() {

    // skip removing for empty textBox
    if (textBuilder.length() == 0) {
      return;
    }

    // total redraw for shorter buffer width
    CanvasElement elem = canvas.getCanvasElement();
    context.clearRect(0, 0, elem.getWidth(), elem.getHeight());
    String word = textBuilder.toString();
    setText(word.substring(0, word.length() - 1));
  }

  @Override
  public void setSize(String width, String height) {

    int w = Integer.valueOf(width.substring(0, width.indexOf("px")));
    int h = Integer.valueOf(height.substring(0, height.indexOf("px")));
    fontHeight = (int) (0.95 * h);

    h *= 1.2;

    canvas.setWidth(w + "px");
    canvas.setHeight(h + "px");

    maxWidth = w;
    maxHeight = h;

    canvas.setCoordinateSpaceWidth(maxWidth);
    canvas.setCoordinateSpaceHeight(maxHeight);

    symbolWidth = (int) (fontHeight * 0.95);
    initContext(fontHeight + "pt " + fontName);
  }

  public void setText(String text) {
    resetInd();
    for (char symbol : text.toCharArray()) {
      provider.getStrategy().addChar(symbol, canvas, context);
    }
    textBuilder = new StringBuilder();
    textBuilder.append(text);
    selectionHandler.onSetText(text);
  }

  void updateIndex() {
    dx += symbolWidth;
  }

  public double getMaxTextWidth() {
    return symbolWidth * textBuilder.length();
  }
}
