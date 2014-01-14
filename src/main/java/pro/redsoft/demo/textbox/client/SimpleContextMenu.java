package pro.redsoft.demo.textbox.client;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;

public class SimpleContextMenu extends PopupPanel {

  public SimpleContextMenu() {
    createPopupMenu();
  }

  private void createPopupMenu() {
    MenuBar popupMenuBar = new MenuBar(true);
    MenuItem copyItem = new MenuItem(SafeHtmlUtils.fromTrustedString("Copy"),
        getCopyCommand());
    MenuItem cutItem = new MenuItem(SafeHtmlUtils.fromTrustedString("Cut"),
        getCutCommand());
    MenuItem pasteItem = new MenuItem(SafeHtmlUtils.fromTrustedString("Paste"),
        getPasteCommand());

    setStyleName("popup");
    copyItem.addStyleName("popup-item");
    cutItem.addStyleName("popup-item");
    pasteItem.addStyleName("popup-item");

    getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
    getElement().getStyle().setBorderColor("black");
    getElement().getStyle().setBorderWidth(1., Unit.PX);

    popupMenuBar.addItem(copyItem);
    popupMenuBar.addItem(cutItem);
    popupMenuBar.addItem(pasteItem);

    popupMenuBar.setVisible(true);
    add(popupMenuBar);
  }

  private ScheduledCommand getCopyCommand() {
    return new ScheduledCommand() {

      @Override
      public void execute() {
        Window.alert("copy");
        hide();
      }
    };
  }

  private ScheduledCommand getCutCommand() {
    return new ScheduledCommand() {

      @Override
      public void execute() {
        Window.alert("cut");
        hide();
      }
    };
  }

  private ScheduledCommand getPasteCommand() {
    return new ScheduledCommand() {

      @Override
      public void execute() {
        Window.alert("paste");
        hide();
      }
    };
  }

}
