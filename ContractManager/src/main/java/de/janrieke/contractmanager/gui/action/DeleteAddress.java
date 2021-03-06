/*
 *   This file is part of ContractManager for Jameica.
 *   Copyright (C) 2010-2011  Jan Rieke
 *
 *   ContractManager is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   ContractManager is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.janrieke.contractmanager.gui.action;

import java.rmi.RemoteException;

import de.janrieke.contractmanager.Settings;
import de.janrieke.contractmanager.gui.view.AddressDetailView;
import de.janrieke.contractmanager.rmi.Address;
import de.janrieke.contractmanager.rmi.Contract;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.datasource.rmi.DBService;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.dialogs.YesNoDialog;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

/**
 * Action for "delete contract".
 */
public class DeleteAddress implements Action {

	/**
	 * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
	 */
	@Override
	public void handleAction(Object context) throws ApplicationException {

		// check if the context is a address
		if (context == null || !(context instanceof Address)) {
			throw new ApplicationException(Settings.i18n().tr(
					"Please choose a address."));
		}

		Address p = (Address) context;

		// first check that there is no contract associated with that address
		try {
			// 1) Get the Database Service.
			DBService service = Settings.getDBService();

			// 2) We create the contract list using createList(Class)
			DBIterator<Contract> contracts = service.createList(Contract.class);

			// 3) we add a filter to only query for tasks with our address id
			contracts.addFilter("address_id = " + p.getID());

			if (contracts.size()>0) {
				GUI.getView().setErrorText(Settings.i18n().tr("Address was not deleted because it is still in use."));
				GUI.getStatusBar().setErrorText(Settings.i18n().tr("Address was not deleted because it is still in use."));
				return;
			}
		} catch (RemoteException e) {
			throw new ApplicationException("unable to load contract list", e);
		}

		try {
			// before deleting the address, we show up a confirm dialog ;)

			YesNoDialog d = new YesNoDialog(YesNoDialog.POSITION_CENTER);
			d.setTitle(Settings.i18n().tr("Are you sure?"));
			d.setText(Settings.i18n().tr(
					"Do you really want to delete this address?"));

			Boolean choice = (Boolean) d.open();
			if (!choice.booleanValue()) {
				return;
			}

			p.delete();
			GUI.getStatusBar().setSuccessText(
					Settings.i18n().tr("Address deleted successfully"));
		} catch (Exception e) {
			Logger.error("error while deleting address", e);
			throw new ApplicationException(Settings.i18n().tr(
					"Error while deleting address"));
		}

		if (GUI.getCurrentView() instanceof AddressDetailView) {
			GUI.startPreviousView();
		}
	}

}