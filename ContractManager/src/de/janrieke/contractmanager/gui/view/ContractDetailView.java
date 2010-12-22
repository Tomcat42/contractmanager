package de.janrieke.contractmanager.gui.view;

import de.janrieke.contractmanager.Settings;
import de.janrieke.contractmanager.gui.action.NavigateBack;
import de.janrieke.contractmanager.gui.action.DeleteContract;
import de.janrieke.contractmanager.gui.action.GenerateCancelation;
import de.janrieke.contractmanager.gui.control.ContractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.util.ButtonArea;
import de.willuhn.jameica.gui.util.ColumnLayout;
import de.willuhn.jameica.gui.util.Headline;
import de.willuhn.jameica.gui.util.SimpleContainer;
import de.willuhn.util.ApplicationException;

/**
 * this is the dialog for the contract details.
 */
public class ContractDetailView extends AbstractView {

	/**
	 * @see de.willuhn.jameica.gui.AbstractView#bind()
	 */
	public void bind() throws Exception {
		// draw the title
		GUI.getView().setTitle(Settings.i18n().tr("Contract details"));

		// instanciate controller
		final ContractControl control = new ContractControl(this);
		
	    ColumnLayout columns = new ColumnLayout(getParent(),2);
	    SimpleContainer left = new SimpleContainer(columns.getComposite());

	    left.addHeadline(Settings.i18n().tr("Contract Information"));
		// create a bordered group
		//LabelGroup group = new LabelGroup(getParent(), Settings.i18n().tr(
		//		"Contract details"));

		// all all input fields to the group.
	    left.addLabelPair(Settings.i18n().tr("Name"), control.getName());

	    left.addHeadline(Settings.i18n().tr("Financial Details"));
	    left.addLabelPair(Settings.i18n().tr("One-time costs"), control.getMoneyOnce());
	    left.addLabelPair(Settings.i18n().tr("Daily costs"), control.getMoneyPerDay());
	    left.addLabelPair(Settings.i18n().tr("Weekly costs"), control.getMoneyPerWeek());
	    left.addLabelPair(Settings.i18n().tr("Monthly costs"), control.getMoneyPerMonth());
	    left.addLabelPair(Settings.i18n().tr("Annual costs"), control.getMoneyPerYear());
	    left.addHeadline(Settings.i18n().tr("Runtime"));
	    left.addLabelPair(Settings.i18n().tr("Start date"),	control.getStartDate());
	    left.addLabelPair(Settings.i18n().tr("End date"), control.getEndDate());
	    left.addLabelPair(Settings.i18n().tr("Cancellation period"), control.getCancellationPeriod());
	    left.addLabelPair(Settings.i18n().tr("First minimum term"), control.getFirstMinRuntime());
	    left.addLabelPair(Settings.i18n().tr("Following minimum terms"), control.getNextMinRuntime());
	    left.addLabelPair(Settings.i18n().tr("Next term extension"), control.getNextExtension());
	    left.addLabelPair(Settings.i18n().tr("Deadline for next cancellation "), control.getNextCancellationDeadline());
	    ButtonArea buttons = new ButtonArea(getParent(), 1);
		buttons.addButton(Settings.i18n().tr("Generate Cancelation"), new GenerateCancelation(), control.getCurrentObject());

	    SimpleContainer right = new SimpleContainer(columns.getComposite(), true);
	    right.addHeadline(Settings.i18n().tr("Comment"));
	    right.addPart(control.getComment());
	    
		// add some buttons
		buttons = new ButtonArea(getParent(), 4);

		buttons.addButton(Settings.i18n().tr("<< Back"), new NavigateBack());
//		buttons.addButton(Settings.i18n().tr("New Task within this Project"),
//				new TaskDetail(), control.getCurrentObject());
		buttons.addButton(Settings.i18n().tr("Delete Project"),
				new DeleteContract(), control.getCurrentObject());
		buttons.addButton(Settings.i18n().tr("Store Project"), new Action() {
			public void handleAction(Object context)
					throws ApplicationException {
				control.handleStore();
			}
		}, null, true); // "true" defines this button as the default button

		// show transactions of this contract
		new Headline(getParent(), Settings.i18n().tr(
				"Transactions of this contract"));
		//		control.getTaskList().paint(getParent());

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