package pro.redsoft.demo.textbox.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.Context2d.TextAlign;
import com.google.gwt.canvas.dom.client.Context2d.TextBaseline;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
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

  private interface KeyProcessingStrategy {

    void addChar(char symbol, Canvas canvas, Context2d context);
  }

  private class EvenKeyProcessingStrategy implements KeyProcessingStrategy {

    /**
     * Every letter at an even position is shown turned upside down.
     * 
     * @inheritDoc
     */
    @Override
    public void addChar(char symbol, Canvas canvas, Context2d context) {

      Canvas item = Canvas.createIfSupported();
      item.setWidth("50px");
      item.setHeight("50px");
      item.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
      Context2d itemContext = item.getContext2d();
      itemContext.save();
      int y = 0;
      if (!isNumber(symbol)) {
        itemContext.setTextBaseline(TextBaseline.BOTTOM);
        y = font / 11;
      }
      itemContext.setFont(font + "pt " + fontName);
      itemContext.translate(0, 60);
      itemContext.scale(1, -1);
      itemContext.fillText(symbol + "", 0, font + y);
      updateIndex(itemContext, symbol);
      itemContext.restore();
      context.drawImage(itemContext.getCanvas(), dx, 0);

    }
  }

  private class OddKeyProcessingStrategy implements KeyProcessingStrategy {

    /**
     * @inheritDoc
     */
    @Override
    public void addChar(char symbol, Canvas canvas, Context2d context) {
      Canvas item = Canvas.createIfSupported();
      item.setWidth("50px");
      item.setHeight("50px");
      Context2d itemContext = item.getContext2d();
      itemContext.save();
      itemContext.setFont(font + "pt " + fontName);
      itemContext.translate(0, 60);
      itemContext.fillText(symbol + "", 0, 0);
      updateIndex(itemContext, symbol);
      itemContext.restore();
      context.drawImage(itemContext.getCanvas(), dx, 0);

    }
  }

  private boolean isNumber(char c) {
    return ((c) >= 49) && ((c) <= 59);
  }

  private StringBuilder textBuilder = new StringBuilder();

  private class StrategyProvider {

    KeyProcessingStrategy evenStrategy = new EvenKeyProcessingStrategy();
    KeyProcessingStrategy oddStrategy = new OddKeyProcessingStrategy();

    int cntr = 0;

    void init() {
      cntr = 0;
    }

    KeyProcessingStrategy getStrategy() {
      return (++cntr % 2) == 0 ? evenStrategy : oddStrategy;
    }
  }

  private final StrategyProvider provider = new StrategyProvider();

  Canvas canvas = Canvas.createIfSupported();
  Context2d context = canvas.getContext2d();

  StringBuilder currentText = new StringBuilder();

  // int symbolHeight = 25;

  private double dx = 0;

  CustomTextBox() {
    setWidget(canvas);
    registerHandlers();

    getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
    getElement().getStyle().setBorderColor("black");
    getElement().getStyle().setBorderWidth(1., Unit.PX);

    // Note: It’s best to use vector fonts when scaling or rotating text,
    // because bitmapped fonts can appear jagged when scaled up or rotated.
    initContext("20pt Monaco");
  }

  private void initContext(String font) {

    provider.init();
    dx = 0;

    context.setFont(font);
    context.setTextAlign(TextAlign.LEFT);
    context.setFillStyle("black");
    context.setTextBaseline(TextBaseline.TOP);
  }

  int font = 40;
  String fontName = "Monospace";

  @Override
  public void onChangeSettings(FontSettings settings) {

    CanvasElement elem = canvas.getCanvasElement();

    context.clearRect(0, 0, elem.getWidth(), elem.getHeight());

    fontName = settings.getFont().getFontName();
    font = Integer.valueOf(settings.getFontSize());

    initContext(font + "pt " + fontName);

    setText(textBuilder.toString());
  }

  @Override
  public void setSize(String width, String height) {
    super.setSize(width, height);
    canvas.setWidth(width + "px");
    canvas.setHeight(height + "px");
  }

  private void updateIndex(Context2d context, char symbol) {
    dx += context.measureText(symbol + "").getWidth();
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

  public void setText(String text) {
    for (char symbol : text.toCharArray()) {
      provider.getStrategy().addChar(symbol, canvas, context);
    }
    textBuilder = new StringBuilder();
    textBuilder.append(text);
  }
}