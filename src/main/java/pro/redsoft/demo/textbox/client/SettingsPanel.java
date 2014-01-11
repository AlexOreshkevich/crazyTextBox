package pro.redsoft.demo.textbox.client;

import pro.redsoft.demo.textbox.client.FontSettings.Font;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Panel for fast debugging and manipulations of font settings.
 * 
 * @author Alex N. Oreshkevich
 */
public class SettingsPanel extends Composite {

  private FontSettings fontSettings;
  private SettingsChangeHandler handler;

  FlexTable root = new FlexTable();
  int rowCntr = 0;

  public SettingsPanel() {

    fontSettings = new FontSettings();

    DecoratorPanel decPanel = new DecoratorPanel();
    initWidget(decPanel);
    decPanel.add(root);
    root.setWidth("800px");

    // label and widget for font selections
    Label fontLabel = new Label("Select font");
    final ListBox fontSelector = new ListBox(false);
    for (Font font : FontSettings.Font.values()) {
      fontSelector.addItem(font.toString());
    }
    addWidget(fontLabel, fontSelector);

    // changeHandler for font selector
    fontSelector.addChangeHandler(new ChangeHandler() {

      @Override
      public void onChange(ChangeEvent event) {
        int ind = fontSelector.getSelectedIndex();
        fontSettings.setFont(Font.valueOf(fontSelector.getItemText(ind)));
        onValueChange();
      }
    });

    // set first font as default
    fontSettings.setFont(Font.valueOf(fontSelector.getItemText(0)));

    // selector for font size
    Label fontSizeLabel = new Label("Select font size");
    TextBox fontSizeSelector = new TextBox();
    addWidget(fontSizeLabel, fontSizeSelector);
    fontSizeSelector.addValueChangeHandler(new ValueChangeHandler<String>() {

      @Override
      public void onValueChange(ValueChangeEvent<String> event) {
        fontSettings.setFontSize(event.getValue());
        SettingsPanel.this.onValueChange();
      }
    });
    fontSizeSelector.setText("40");
  }

  private void addWidget(Widget label, Widget selector) {
    root.setWidget(rowCntr, 0, label);
    root.setWidget(rowCntr, 1, selector);
    rowCntr++;
  }

  public FontSettings getFontSettings() {
    return fontSettings;
  }

  void onValueChange() {
    if (handler != null) {
      handler.onChangeSettings(fontSettings);
    }
  }

  public void addChangeHandler(SettingsChangeHandler handler) {
    this.handler = handler;
  }
}
