package pro.redsoft.demo.textbox.client;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;

class SimpleContextMenu extends PopupPanel {

  CustomTextBox textBox;

  SimpleContextMenu(CustomTextBox textBox) {
    this.textBox = textBox;
    setStyleName("popup");
    createPopupMenu();
    setAutoHideEnabled(true);
  }

  private void createPopupMenu() {
    MenuBar popupMenuBar = new MenuBar(true);
    buildMenuItem(popupMenuBar, "Copy (Ctrl + C)", getCopyCommand());
    buildMenuItem(popupMenuBar, "Cut (Ctrl + X)", getCutCommand());
    buildMenuItem(popupMenuBar, "Paste (Ctrl + V)", getPasteCommand());
    buildMenuItem(popupMenuBar, "Select All (Ctrl + A)", getSelectAllCommand());

    popupMenuBar.setVisible(true);
    add(popupMenuBar);
  }

  private void buildMenuItem(MenuBar popupMenuBar, String cmdText,
      ScheduledCommand cmd) {
    MenuItem item = new MenuItem(SafeHtmlUtils.fromTrustedString(cmdText), cmd);
    item.addStyleName("popup-item");
    popupMenuBar.addItem(item);
  }

  private ScheduledCommand getCopyCommand() {
    return new ScheduledCommand() {

      @Override
      public void execute() {
        textBox.operationHandler.copy();
        hide();
        textBox.setFocus(true);
      }
    };
  }

  private ScheduledCommand getCutCommand() {
    return new ScheduledCommand() {

      @Override
      public void execute() {
        textBox.operationHandler.cut();
        hide();
        textBox.setFocus(true);
      }
    };
  }

  private ScheduledCommand getPasteCommand() {
    return new ScheduledCommand() {

      @Override
      public void execute() {
        textBox.operationHandler.paste();
        hide();
        textBox.setFocus(true);
      }
    };
  }

  private ScheduledCommand getSelectAllCommand() {
    return new ScheduledCommand() {

      @Override
      public void execute() {
        textBox.operationHandler.selectAll();
        hide();
        textBox.setFocus(true);
      }
    };
  }
}
