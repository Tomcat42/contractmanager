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
package de.janrieke.contractmanager.gui.menu;

import de.janrieke.contractmanager.Settings;
import de.janrieke.contractmanager.gui.action.DeleteAddress;
import de.janrieke.contractmanager.gui.action.ShowAddressDetailView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.parts.CheckedContextMenuItem;
import de.willuhn.jameica.gui.parts.ContextMenu;
import de.willuhn.jameica.gui.parts.ContextMenuItem;
import de.willuhn.util.ApplicationException;

/**
 * Prepared context menu for project tables.
 */
public class AddressListMenu extends ContextMenu {
	/**
	 * ct.
	 * 
	 * @param showNew
	 */
	public AddressListMenu(boolean showNew) {
		// CheckedContextMenuItems will be disabled, if the user clicks into an
		// empty space of the table
		addItem(new CheckedContextMenuItem(Settings.i18n().tr("Open..."),
				new ShowAddressDetailView(), "document-open.png"));

		if (showNew) {
			// separator
			addItem(ContextMenuItem.SEPARATOR);
			
			addItem(new ContextMenuItem(Settings.i18n().tr(
					"Create a New Address..."), new Action() {
				public void handleAction(Object context)
						throws ApplicationException {
					// we force the context to be null to create a new
					// project in any case
					new ShowAddressDetailView().handleAction(null);
				}
			}, "document-new.png"));

			addItem(ContextMenuItem.SEPARATOR);
			addItem(new CheckedContextMenuItem(Settings.i18n().tr(
					"Delete Address..."), new DeleteAddress(),
					"window-close.png"));
		}

	}
}