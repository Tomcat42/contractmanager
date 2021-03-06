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

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import de.janrieke.contractmanager.rmi.ContractDBService;
import de.janrieke.contractmanager.server.DBSupportH2Impl;
import de.janrieke.contractmanager.server.ContractDBUpdateProvider;
import de.willuhn.logging.Logger;
import de.willuhn.sql.ScriptExecutor;
import de.willuhn.sql.version.Update;
import de.willuhn.sql.version.UpdateProvider;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Remind me later function
 */
public class update0020 implements Update {
	private Map<String, String> statements = new HashMap<String, String>();

	/**
	 * Default constructor
	 */
	public update0020() {
		// Update for H2
		statements.put(DBSupportH2Impl.class.getName(),
				"ALTER TABLE contract ADD COLUMN do_not_remind_before date;\n" +
				"ALTER TABLE transactions DROP CONSTRAINT fk_contract;\n" +
				"ALTER TABLE transactions ADD CONSTRAINT fk_transactions_contract FOREIGN KEY (contract_id) REFERENCES contract (id) ON DELETE CASCADE ON UPDATE CASCADE;\n" +
				"DELETE FROM costs WHERE contract_id NOT IN (SELECT id from contract);\n" + //in case some unassigned costs got lost in the DB 
				"ALTER TABLE costs ADD CONSTRAINT fk_costs_contract FOREIGN KEY (contract_id) REFERENCES contract (id) ON DELETE CASCADE ON UPDATE CASCADE;\n");
	}

	/**
	 * @see de.willuhn.sql.version.Update#execute(de.willuhn.sql.version.UpdateProvider)
	 */
	public void execute(UpdateProvider provider) throws ApplicationException {
		ContractDBUpdateProvider myProvider = (ContractDBUpdateProvider) provider;
		I18N i18n = myProvider.getResources().getI18N();

		// Get the SQL dialect
		String driver = ContractDBService.SETTINGS.getString("database.driver",
				DBSupportH2Impl.class.getName());
		String sql = (String) statements.get(driver);
		if (sql == null)
			throw new ApplicationException(i18n.tr(
					"Database {0} not supported", driver));

		try {
			ScriptExecutor
					.execute(new StringReader(sql), myProvider.getConnection(),
							myProvider.getProgressMonitor());
			myProvider.getProgressMonitor().log(i18n.tr("Database updated."));
		} catch (ApplicationException ae) {
			throw ae;
		} catch (Exception e) {
			Logger.error("unable to execute update", e);
			throw new ApplicationException(
					i18n.tr("Error during database update"), e);
		}
	}

	/**
	 * @see de.willuhn.sql.version.Update#getName()
	 */
	public String getName() {
		return "Database update v19 to v20 (remind me later function, DB cleanup)";
	}

}