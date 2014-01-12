package pro.redsoft.demo.textbox.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.Context2d.TextAlign;
import com.google.gwt.canvas.dom.client.Context2d.TextBaseline;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
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
  private final SelectionHandler selectionHandler = new SelectionHandler(this);

  private class EvenKeyProcessingStrategy implements KeyProcessingStrategy {

    /**
     * Every letter at an even position is shown turned upside down.
     * 
     * @inheritDoc
     */
    @Override
    public void addChar(char symbol, Canvas canvas, Context2d context) {

      Context2d itemContext = buildItemContext();
      animation.removeCursor(dx);
      context.clearRect(dx, 0, symbolWidth, canvas.getCoordinateSpaceHeight());

      int y = 1;
      if (!isNumber(symbol)) {
        itemContext.setTextBaseline(TextBaseline.BOTTOM);
      } else {
        y = -1;
        /* for courier */y = -2;/* for courier */
      }

      itemContext.translate(0, fontHeight/* for courier */+ 3/*
                                                              * for courier
                                                              */);
      itemContext.scale(1, -1);
      itemContext.fillText(symbol + "", 0, fontHeight + y);
      itemContext.restore();
      context.drawImage(itemContext.getCanvas(), dx, 0);
      updateIndex(itemContext, symbol);
    }
  }

  private interface KeyProcessingStrategy {
    void addChar(char symbol, Canvas canvas, Context2d context);
  }

  private class OddKeyProcessingStrategy implements KeyProcessingStrategy {

    @Override
    public void addChar(char symbol, Canvas canvas, Context2d context) {

      Context2d itemContext = buildItemContext();
      animation.removeCursor(dx);
      context.clearRect(dx, 0, symbolWidth, canvas.getCoordinateSpaceHeight());

      itemContext.translate(0, fontHeight);
      itemContext.fillText(symbol + "", 0, 0);
      itemContext.restore();
      context.drawImage(itemContext.getCanvas(), dx, 0);
      updateIndex(itemContext, symbol);
    }
  }

  private class StrategyProvider {

    KeyProcessingStrategy evenStrategy = new EvenKeyProcessingStrategy();
    KeyProcessingStrategy oddStrategy = new OddKeyProcessingStrategy();

    int cntr = 0;

    KeyProcessingStrategy getStrategy() {
      return (++cntr % 2) == 0 ? evenStrategy : oddStrategy;
    }
  }

  StringBuilder textBuilder = new StringBuilder();

  private final StrategyProvider provider = new StrategyProvider();
  final CursorAnimation animation = new CursorAnimation(this);

  private final Timer cursorTimer = new Timer() {

    @Override
    public void run() {
      animation.run(1000);
    }
  };

  Canvas canvas = Canvas.createIfSupported();
  Context2d context = canvas.getContext2d();

  StringBuilder currentText = new StringBuilder();

  double dx = 0;
  int fontHeight = 0;
  int symbolWidth;

  private int maxWidth, maxHeight;

  public CustomTextBox() {
    setWidget(canvas);
    registerHandlers();

    getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
    getElement().getStyle().setBorderColor("black");
    getElement().getStyle().setBorderWidth(1., Unit.PX);

    getElement().getStyle().setCursor(Cursor.TEXT);

    setSize("300px", "30px");
    sinkEvents(Event.FOCUSEVENTS);
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

  private boolean isNumber(char c) {
    return ((c) >= 48) && ((c) <= 59);
  }

  @Override
  public void onBrowserEvent(Event event) {
    super.onBrowserEvent(event);
    switch (event.getTypeInt()) {

    case Event.ONFOCUS:
      cursorTimer.scheduleRepeating(1000);
      break;

    case Event.ONBLUR:
      cursorTimer.cancel();
      break;
    }
  }

  void registerHandlers() {

    addClickHandler(selectionHandler);

    addKeyPressHandler(new KeyPressHandler() {

      @Override
      public void onKeyPress(KeyPressEvent event) {

        if (event.isAnyModifierKeyDown()) {
          return;
        }

        // remove last char
        if ((event.getNativeEvent().getCharCode()) == KeyCodes.KEY_BACKSPACE) {
          CanvasElement elem = canvas.getCanvasElement();
          context.clearRect(0, 0, elem.getWidth(), elem.getHeight());
          String word = textBuilder.toString();
          setText(word.substring(0, word.length() - 1));
          event.preventDefault();
          return;
        }

        char symbol = event.getCharCode();

        // choose and execute strategy
        provider.getStrategy().addChar(symbol, canvas, context);
        textBuilder.append(symbol);
      }
    });

    addMouseMoveHandler(selectionHandler);
    addMouseDownHandler(selectionHandler);
    addMouseUpHandler(selectionHandler);
    addDoubleClickHandler(selectionHandler);
  }

  void resetInd() {
    dx = 0;
    provider.cntr = 0;
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
  }

  private void updateIndex(Context2d itemContext, char symbol) {
    dx += symbolWidth;
  }
}
