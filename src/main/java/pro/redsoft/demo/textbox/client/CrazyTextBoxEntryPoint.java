package pro.redsoft.demo.textbox.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.user.client.Window;
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
        Window.alert("UncaughtException: " + e.getMessage());
        e.printStackTrace();
        if (GWT.isProdMode()) {
          Window.Location.reload();
        }
      }
    });
  }

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

    RootPanel.get("crazyTextBoxContainer").add(root);
    textBox.setFocus(true);
  }
}
