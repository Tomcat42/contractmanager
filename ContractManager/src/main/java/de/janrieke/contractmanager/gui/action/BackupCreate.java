/*
 *   This file is part of ContractManager for Jameica.
 *   Copyright (C) 2010-2015  Jan Rieke
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
/**********************************************************************
 * Copy from Hibiscus
 * Copyright (c) by willuhn software & services
 **********************************************************************/

package de.janrieke.contractmanager.gui.action;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;

import de.janrieke.contractmanager.ContractManagerPlugin;
import de.janrieke.contractmanager.Settings;
import de.janrieke.contractmanager.io.CsvWriter;
import de.janrieke.contractmanager.server.AddressImpl;
import de.janrieke.contractmanager.server.ContractImpl;
import de.janrieke.contractmanager.server.CostsImpl;
import de.janrieke.contractmanager.server.SettingImpl;
import de.janrieke.contractmanager.server.StorageImpl;
import de.janrieke.contractmanager.server.TransactionImpl;
import de.willuhn.datasource.BeanUtil;
import de.willuhn.datasource.GenericObject;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.datasource.rmi.DBObject;
import de.willuhn.datasource.serialize.Writer;
import de.willuhn.datasource.serialize.XmlWriter;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.BackgroundTask;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;
import de.willuhn.util.ProgressMonitor;

/**
 * Action zum Erstellen eines Komplett-Backups im XML-Format.
 */
public class BackupCreate implements Action {
	/**
	 * Dateformat, welches fuer den Dateinamen genutzt wird.
	 */
	public static DateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd");

	private static I18N i18n = Application.getPluginLoader().getPlugin(ContractManagerPlugin.class)
			.getResources().getI18N();

	/**
	 * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
	 */
	@Override
	public void handleAction(Object context) throws ApplicationException {
		FileDialog fd = new FileDialog(GUI.getShell(), SWT.SAVE);
		fd.setOverwrite(true);
		fd.setFileName("contractmanager-backup-" + DATEFORMAT.format(new Date()) + ".xml");
		fd.setFilterExtensions(new String[] { "*.xml", "*.zip" });
		fd.setFilterNames(new String[] { i18n.tr("XML file (*.xml)"),
				i18n.tr("Comma-separated values (compressed) (*.zip)") });
		fd.setText(i18n.tr("Select file for export"));
		String f = fd.open();
		if (f == null || f.length() == 0) {
			return;
		}
		final int filterIndex = fd.getFilterIndex();

		final File file = new File(f);
		Application.getController().start(new BackgroundTask() {
			private boolean cancel = false;

			/**
			 * @see de.willuhn.jameica.system.BackgroundTask#run(de.willuhn.util.ProgressMonitor)
			 */
			@Override
			public void run(ProgressMonitor monitor) throws ApplicationException {
				Writer writer = null;
				try {
					Logger.info("creating backup to " + file.getAbsolutePath());

					switch (filterIndex) {
					case 1:
						writer = new CsvWriter(new BufferedOutputStream(new FileOutputStream(file)));
						break;
					case 0:
					default:
						writer = new XmlWriter(new BufferedOutputStream(new FileOutputStream(file)));
					}

					monitor.setStatusText(i18n.tr("Saving Addresses..."));
					backup(AddressImpl.class, writer, monitor);
					monitor.addPercentComplete(10);

					monitor.setStatusText(i18n.tr("Saving Contracts..."));
					backup(ContractImpl.class, writer, monitor);
					monitor.addPercentComplete(10);

					monitor.setStatusText(i18n.tr("Saving Costs Data..."));
					backup(CostsImpl.class, writer, monitor);
					monitor.addPercentComplete(15);

					monitor.setStatusText(i18n.tr("Saving Transactions..."));
					backup(TransactionImpl.class, writer, monitor);
					monitor.addPercentComplete(20);

					monitor.setStatusText(i18n.tr("Saving Settings..."));
					backup(SettingImpl.class, writer, monitor);
					monitor.addPercentComplete(5);

					monitor.setStatusText(i18n.tr("Saving Data Storage..."));
					backup(StorageImpl.class, writer, monitor);
					monitor.addPercentComplete(40);

					monitor.setStatus(ProgressMonitor.STATUS_DONE);
					monitor.setStatusText(i18n.tr("Export complete."));
					monitor.setPercentComplete(100);
				} catch (Exception e) {
					throw new ApplicationException(e.getMessage());
				} finally {
					if (writer != null) {
						try {
							writer.close();
							Logger.info("backup created");
						} catch (Exception e) {/* useless */
						}
					}
				}
			}

			/**
			 * @see de.willuhn.jameica.system.BackgroundTask#isInterrupted()
			 */
			@Override
			public boolean isInterrupted() {
				return this.cancel;
			}

			/**
			 * @see de.willuhn.jameica.system.BackgroundTask#interrupt()
			 */
			@Override
			public void interrupt() {
				this.cancel = true;
			}

		});
	}

	/**
	 * Hilfsfunktion.
	 *
	 * @param type
	 *            der Typ der zu speichernden Objekte.
	 * @param writer
	 *            der Writer.
	 * @param monitor
	 *            der Monitor.
	 * @throws Exception
	 */
	private static void backup(Class<? extends DBObject> type, Writer writer,
			ProgressMonitor monitor) throws Exception {
		DBIterator<DBObject> list = Settings.getDBService().createList(type);

		while (list.hasNext()) {
			GenericObject o = null;
			try {
				o = list.next();
				writer.write(o);
			} catch (Exception e) {
				Logger.error("error while writing object " + BeanUtil.toString(o) + " - skipping",
						e);
				monitor.log("  "
						+ i18n.tr("Error during export of {0} ({1}), skipping",
								new String[] { BeanUtil.toString(o), e.getMessage() }));
			}
		}
	}
}
