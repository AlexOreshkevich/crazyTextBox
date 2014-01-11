package pro.redsoft.demo.textbox.client;

import pro.redsoft.demo.textbox.client.FontSettings.Font;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.Context2d.TextAlign;
import com.google.gwt.canvas.dom.client.Context2d.TextBaseline;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
 * 
 * @author Alex N. Oreshkevich
 */
public class CustomTextBox extends FocusPanel implements SettingsChangeHandler {

  private class EvenKeyProcessingStrategy implements KeyProcessingStrategy {

    /**
     * Every letter at an even position is shown turned upside down.
     * 
     * @inheritDoc
     */
    @Override
    public void addChar(char symbol, Canvas canvas, Context2d context) {

      Canvas item = Canvas.createIfSupported();
      item.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
      Context2d itemContext = item.getContext2d();
      itemContext.save();
      double y = -fontHeight / 5;
      if (!isNumber(symbol)) {
        itemContext.setTextBaseline(TextBaseline.BOTTOM);
        y = -fontHeight / 11;
      }
      itemContext.setFont(fontHeight + "pt " + fontName);
      itemContext.translate(0, fontHeight);
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
      Canvas item = Canvas.createIfSupported();
      Context2d itemContext = item.getContext2d();
      itemContext.save();
      itemContext.setFont(fontHeight + "pt " + fontName);
      itemContext.translate(0, fontHeight);
      itemContext.fillText(symbol + "", 0, 0);
      itemContext.restore();
      context.drawImage(itemContext.getCanvas(), dx, 0);
      updateIndex(itemContext, symbol);
    }
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

  private class StrategyProvider {

    KeyProcessingStrategy evenStrategy = new EvenKeyProcessingStrategy();
    KeyProcessingStrategy oddStrategy = new OddKeyProcessingStrategy();

    int cntr = 0;

    KeyProcessingStrategy getStrategy() {
      return (++cntr % 2) == 0 ? evenStrategy : oddStrategy;
    }

    void init() {
      cntr = 0;
    }
  }

  private StringBuilder textBuilder = new StringBuilder();
  private final StrategyProvider provider = new StrategyProvider();
  private final CursorAnimation animation = new CursorAnimation(this);
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
  double fontHeight = 0;

  String fontName = Font.Monospace.getFontName();

  CustomTextBox() {
    setWidget(canvas);
    registerHandlers();

    getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
    getElement().getStyle().setBorderColor("black");
    getElement().getStyle().setBorderWidth(1., Unit.PX);

    getElement().getStyle().setCursor(Cursor.TEXT);

    // Note: It’s best to use vector fonts when scaling or rotating text,
    // because bitmapped fonts can appear jagged when scaled up or rotated.
    setSize("300px", "30px");
    sinkEvents(Event.FOCUSEVENTS);
  }

  private void initContext(String font) {

    dx = 0;
    provider.init();

    context.setFont(font);
    context.setTextAlign(TextAlign.LEFT);
    context.setFillStyle("black");
    context.setTextBaseline(TextBaseline.TOP);
  }

  private boolean isNumber(char c) {
    return ((c) >= 49) && ((c) <= 59);
  }

  @Override
  public void onChangeSettings(FontSettings settings) {
    CanvasElement elem = canvas.getCanvasElement();
    context.save();
    context.clearRect(0, 0, elem.getWidth(), elem.getHeight());
    fontName = settings.getFont().getFontName();
    initContext(fontHeight + "pt " + fontName);
    setText(textBuilder.toString());
    context.restore();
  }

  void registerHandlers() {

    addDomHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        setFocus(true);
      }
    }, ClickEvent.getType());

    addKeyPressHandler(new KeyPressHandler() {

      @Override
      public void onKeyPress(KeyPressEvent event) {

        char symbol = event.getCharCode();

        // choose and execute strategy
        provider.getStrategy().addChar(symbol, canvas, context);
        textBuilder.append(symbol);
      }
    });
  }

  private int maxWidth, maxHeight;

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

    initContext(fontHeight + "pt " + fontName);
  }

  public void setText(String text) {
    for (char symbol : text.toCharArray()) {
      provider.getStrategy().addChar(symbol, canvas, context);
    }
    textBuilder = new StringBuilder();
    textBuilder.append(text);
  }

  private void updateIndex(Context2d context, char symbol) {
    dx += fontHeight * 0.9;
    animation.updatePosition();
  }
}