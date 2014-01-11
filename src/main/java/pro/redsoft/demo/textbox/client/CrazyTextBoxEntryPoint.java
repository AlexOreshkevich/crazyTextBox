package pro.redsoft.demo.textbox.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class CrazyTextBoxEntryPoint implements EntryPoint {

  private final CustomTextBox textBox = new CustomTextBox();

  /**
   * This is the entry point method.
   */
  @Override
  public void onModuleLoad() {

    textBox.setSize("600px", "30px");
    textBox.setText("abcdefghwxyz 123456");

    // init settingsPanel
    SettingsPanel settingsPanel = new SettingsPanel();
    settingsPanel.addChangeHandler(textBox);

    VerticalPanel root = new VerticalPanel();
    root.add(textBox);
    root.add(settingsPanel);

    RootPanel.get("crazyTextBoxContainer").add(root);
  }
}
