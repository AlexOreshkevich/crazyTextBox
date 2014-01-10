package pro.redsoft.demo.textbox.client;

import com.google.gwt.canvas.dom.client.Context2d.TextAlign;
import com.google.gwt.canvas.dom.client.Context2d.TextBaseline;

/**
 * Bean for holding font settings.
 * 
 * @author Alex N. Oreshkevich
 */
public class FontSettings {

  public enum Font {
    Courier, Georgia, Tahoma("Tahoma, Geneva, sans-serif"), Console(
        "Lucida Console"), Monaco, Arial, Helvetica;

    /** Enum id and actual font string representation may be different. */
    private String name;

    private Font() {
      this(null);
    }

    private Font(String name) {
      this.name = name;
    }

    public String getFontName() {
      return name == null ? toString() : name;
    }
  }

  public FontSettings() {
    font = Font.Arial;
    fontSize = "20pt";
    textAlign = TextAlign.LEFT;
    fillStyle = "black";
    textBaseline = TextBaseline.TOP;
  }

  public Font getFont() {
    return font;
  }

  public void setFont(Font font) {
    this.font = font;
  }

  public String getFontSize() {
    return fontSize;
  }

  public void setFontSize(String fontSize) {
    this.fontSize = fontSize;
  }

  public TextAlign getTextAlign() {
    return textAlign;
  }

  public void setTextAlign(TextAlign textAlign) {
    this.textAlign = textAlign;
  }

  public String getFillStyle() {
    return fillStyle;
  }

  public void setFillStyle(String fillStyle) {
    this.fillStyle = fillStyle;
  }

  public TextBaseline getTextBaseline() {
    return textBaseline;
  }

  public void setTextBaseline(TextBaseline textBaseline) {
    this.textBaseline = textBaseline;
  }

  private Font font;
  private String fontSize;
  private TextAlign textAlign;
  private String fillStyle;
  private TextBaseline textBaseline;
}
