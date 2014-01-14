package pro.redsoft.demo.textbox.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class CrazyTextBoxEntryPoint implements EntryPoint {

  private final CustomTextBox textBox = new CustomTextBox();

  CrazyTextBoxEntryPoint() {
    GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {

      @Override
      public void onUncaughtException(Throwable e) {
        logPanel.getElement().setInnerText(e.getCause().getMessage());
        e.getCause().printStackTrace();
      }
    });
  }

  HTMLPanel logPanel = new HTMLPanel("pre", "");

  /**
   * This is the entry point method.
   */
  @Override
  public void onModuleLoad() {

    textBox.setSize("1000px", "30px");
    textBox.setText("abcdefghwxyz 1234+++");

    // ===============================================================
    VerticalPanel root = new VerticalPanel();
    root.add(textBox);
    root.setSpacing(20);
    root.add(logPanel);

    RootPanel.get("crazyTextBoxContainer").add(root);

    textBox.setFocus(true);
  }
}
