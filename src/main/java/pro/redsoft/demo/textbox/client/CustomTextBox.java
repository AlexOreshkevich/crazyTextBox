package pro.redsoft.demo.textbox.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.Context2d.TextAlign;
import com.google.gwt.canvas.dom.client.Context2d.TextBaseline;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;

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
 * @author Alex N. Oreshkevich
 * @see https://umstelkb.atlassian.net/wiki/display/SC/Crazy+Text+Input
 */
public class CustomTextBox extends FocusPanel {

    public interface KeyProcessingStrategy {
        void addChar(char symbol, Canvas canvas, Context2d context);
    }

    // canvas and context2D as required by task
    Canvas canvas = Canvas.createIfSupported();
    Context2d context = canvas.getContext2d();
    TextBox hiddenBox = new TextBox();
    HorizontalPanel panel = new HorizontalPanel();

    // representation of text based on char buffer: stringBuilder
    StringBuilder textBuilder = new StringBuilder();

    // manipulation of different aspects of user interaction
    // based on browser event handling
    FocusBlurHandler focusBlurHandler;
    CursorHandler cursorHandler;
    OperationHandler operationHandler;
    InputHandler inputHandler;
    TouchHandler touchHandler;

    /**
     * Current displacement of cursor at the X axis.
     */
    double dx = 0;

    // font settings
    int fontHeight = 0;
    String fontName = "Courier";
    int symbolWidth;
    private final int MAX_CHARS = 310;

    // current calculated width/height
    private int maxWidth, maxHeight;

    /**
     * Custom context menu.
     */
    SimpleContextMenu menu;

    /**
     * Provides input strategies (odd, even).
     */
    private final StrategyProvider provider = new StrategyProvider(this);

    public CustomTextBox() {
//    setWidget(canvas);
        hiddenBox.getElement().getStyle().setZIndex(-100);
        hiddenBox.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
        hiddenBox.getElement().getStyle().setLeft(100, Style.Unit.PX);
        panel.getElement().getStyle().setBackgroundColor("white");
        panel.add(canvas);
        panel.add(hiddenBox);
        setWidget(panel);


        // add event handlers
        cursorHandler = new CursorHandler(this);
        focusBlurHandler = new FocusBlurHandler(this);
        inputHandler = new InputHandler(this);
        operationHandler = new OperationHandler(this);
        menu = new SimpleContextMenu(this);
        touchHandler = new TouchHandler(this);

        // load styles from .css
        setStyleName("gwt-CustomTextBox");

        // set default size
        setSize("300px", "30px");
    }

    /**
     * Called every time user types a char.
     *
     * @param symbol
     */
    void addChar(char symbol) {

        // The maximum input length must be set to 310 characters.
        if (textBuilder.length() == MAX_CHARS) {
            return;
        }

        // execute appropriate input strategy
        provider.getStrategy().addChar(symbol, canvas, context);

        // modify text representation parameters
        textBuilder.append(symbol);
        operationHandler.onAddChar(symbol);
    }

    // TODO should be moved to the base class for all input strategies
    Context2d buildItemContext() {
        Canvas item = Canvas.createIfSupported();
        Context2d itemContext = item.getContext2d();
        itemContext.save();
        itemContext.setFont(fontHeight + "pt " + fontName);
        return itemContext;
    }

    // TODO the same
    boolean isNumber(int c) {
        return (c >= 48) && (c <= 59);
    }

    void clearCanvas() {
        context.clearRect(0, 0, maxWidth, maxHeight);
    }

    double getMaxTextWidth() {
        return symbolWidth * textBuilder.length();
    }

    private void initContext(String font) {
        resetInd();
        context.setFont(font);
        context.setTextAlign(TextAlign.LEFT);
        context.setFillStyle("black");
        context.setTextBaseline(TextBaseline.TOP);
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
        clearCanvas();
        for (char symbol : text.toCharArray()) {
            provider.getStrategy().addChar(symbol, canvas, context);
        }
        textBuilder = new StringBuilder();
        textBuilder.append(text);
//    operationHandler.onSetText(text);
    }

    public String getText() {
        return textBuilder.toString();
    }

    void updateIndex() {
        dx += symbolWidth;
    }
}
