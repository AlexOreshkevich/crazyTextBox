package pro.redsoft.demo.textbox.client;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;

public class MegaPopupMenu extends PopupPanel {
	
	public MegaPopupMenu(){
		createPopupMenu();
		setAutoHideEnabled(true);
	}
	
	private SafeHtml getSafeHtml(final String str) {
		return new SafeHtml() {

			@Override
			public String asString() {
				return str;
			}
		};
	}

	private void createPopupMenu() {
		MenuBar popupMenuBar = new MenuBar(true);
		MenuItem copyItem = new MenuItem(getSafeHtml("Copy"), getCopyCommand());
		MenuItem cutItem = new MenuItem(getSafeHtml("Cut"), getCutCommand());
		MenuItem pasteItem = new MenuItem(getSafeHtml("Paste"), getPasteCommand());

		setStyleName("popup");
		copyItem.addStyleName("popup-item");
		cutItem.addStyleName("popup-item");
		pasteItem.addStyleName("popup-item");

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
