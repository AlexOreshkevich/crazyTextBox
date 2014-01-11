package pro.redsoft.demo.textbox.client;

import com.google.gwt.canvas.dom.client.Context2d;

/**
 * 
 * @author Alex N. Oreshkevich
 */
public class WebCursor {

  private Context2d ctx;
  private int x;
  private final int height;

  WebCursor(Context2d ctx, int height) {
    this.ctx = ctx;
    this.height = height;
  }

  void move(int dx) {

    if (dx == 0) {

    }
  }
}
