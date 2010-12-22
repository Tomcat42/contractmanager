package de.janrieke.contractmanager.gui.view;

import de.janrieke.contractmanager.Settings;
import de.janrieke.contractmanager.gui.action.NavigateBack;
import de.janrieke.contractmanager.gui.control.SettingsControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.util.ButtonArea;
import de.willuhn.jameica.gui.util.ColumnLayout;
import de.willuhn.jameica.gui.util.SimpleContainer;
import de.willuhn.util.ApplicationException;

/**
 * this is the dialog for the contract details.
 */
public class SettingsView extends AbstractView {

	/**
	 * @see de.willuhn.jameica.gui.AbstractView#bind()
	 */
	public void bind() throws Exception {
		// draw the title
		GUI.getView().setTitle(Settings.i18n().tr("Settings"));

		// instanciate controller
		final SettingsControl control = new SettingsControl(this);
		
	    ColumnLayout columns = new ColumnLayout(getParent(),2);
	    SimpleContainer left = new SimpleContainer(columns.getComposite());

	    left.addHeadline(Settings.i18n().tr("User Information"));
		// create a bordered group
		//LabelGroup group = new LabelGroup(getParent(), Settings.i18n().tr(
		//		"Contract details"));

		// all all input fields to the group.
	    left.addLabelPair(Settings.i18n().tr("Name"), control.getName());
	    left.addLabelPair(Settings.i18n().tr("Street"), control.getStreetNumber());
	    left.addLabelPair(Settings.i18n().tr("Extra"), control.getExtra());
	    left.addLabelPair(Settings.i18n().tr("City"), control.getZipcodeCity());
	    left.addLabelPair(Settings.i18n().tr("State"), control.getState());
	    left.addLabelPair(Settings.i18n().tr("Country"), control.getCountry());

		// add some buttons
	    ButtonArea buttons = new ButtonArea(getParent(), 4);

		buttons.addButton(Settings.i18n().tr("<< Back"), new NavigateBack());
		buttons.addButton(Settings.i18n().tr("Save Settings"), new Action() {
			public void handleAction(Object context)
					throws ApplicationException {
				control.handleStore();
			}
		}, null, true); // "true" defines this button as the default button
	}

	/**
	 * @see de.willuhn.jameica.gui.AbstractView#unbind()
	 */
	public void unbind() throws ApplicationException {
		// this method will be invoked when leaving the dialog.
		// You are able to interrupt the unbind by throwing an
		// ApplicationException.
	}

}