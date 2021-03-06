/*
 *   This file is part of This file is part of ContractManager for Jameica..
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
package de.janrieke.contractmanager;

import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import de.janrieke.contractmanager.rmi.ContractDBService;
import de.janrieke.contractmanager.server.SettingsUtil;
import de.willuhn.datasource.rmi.DBService;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * This class holds some settings for our plugin.
 *
 * @author willuhn, jrieke
 */
public class Settings {

	private static volatile DBService db;
	private static volatile I18N i18n;

	/**
	 * Our DateFormatter.
	 */
	private final static DateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd");

	public static synchronized String dateformat(Date date) {
		return DATEFORMAT.format(date);
	}

	public static DateFormat getNewDateFormat() {
		return new SimpleDateFormat("yyyy-MM-dd");
	}

	/**
	 * Our decimal formatter.
	 */
	public final static DecimalFormat DECIMALFORMAT = (DecimalFormat) DecimalFormat
			.getInstance(Application.getConfig().getLocale());

	/**
	 * Our currency name.
	 */
	public final static String CURRENCY = "\u20AC";

	public static String formatAsCurrency(double value) {
		return formatAsCurrency(value, CURRENCY);
	}

	public static String formatAsCurrency(double value, String currencyLabel) {
		return DECIMALFORMAT.format(value) + " " + currencyLabel;
	}

	static {
		DECIMALFORMAT.setMinimumFractionDigits(2);
		DECIMALFORMAT.setMaximumFractionDigits(2);
	}

	/**
	 * Small helper function to get the database service.
	 *
	 * @return db service.
	 * @throws RemoteException
	 */
	public static DBService getDBService() throws RemoteException {
		if (db != null) {
			return db;
		}
		try {
			db = (ContractDBService) Application.getServiceFactory()
					.lookup(ContractManagerPlugin.class, "contract_db");
			return db;
		} catch (ConnectException ce) {
			// Die Exception fliegt nur bei RMI-Kommunikation mit fehlendem
			// RMI-Server
			I18N i18n = Application.getPluginLoader().getPlugin(ContractManagerPlugin.class)
					.getResources().getI18N();
			String host = Application.getServiceFactory().getLookupHost(ContractManagerPlugin.class,
					"contract_db");
			int port = Application.getServiceFactory().getLookupPort(ContractManagerPlugin.class,
					"contract_db");
			String msg = i18n.tr("DB-Server \"{0}\" nicht erreichbar", (host + ":" + port));
			try {
				Application.getCallback().notifyUser(msg);
				throw new RemoteException(msg);
			} catch (Exception e) {
				Logger.error("error while notifying user", e);
				throw new RemoteException(msg);
			}
		} catch (ApplicationException ae) {
			// Da interessiert uns der Stacktrace nicht
			throw new RemoteException(ae.getMessage());
		} catch (RemoteException re) {
			throw re;
		} catch (Exception e) {
			throw new RemoteException("unable to open/create database", e);
		}
	}

	/**
	 * Small helper function to get the translator.
	 *
	 * @return translator.
	 */
	public static I18N i18n() {
		if (i18n != null) {
			return i18n;
		}
		i18n = Application.getPluginLoader().getPlugin(ContractManagerPlugin.class).getResources()
				.getI18N();
		return i18n;
	}

	public static int getExtensionWarningTime() throws RemoteException {
		return Integer.parseInt(SettingsUtil.get("extension_warning_time", "7"));
	}

	public static void setExtensionWarningTime(int time)
			throws RemoteException, ApplicationException {
		SettingsUtil.set("extension_warning_time", Integer.toString(time));
	}

	public static int getExtensionNoticeTime() throws RemoteException {
		return Integer.parseInt(SettingsUtil.get("extension_notice_time", "30"));
	}

	public static void setExtensionNoticeTime(int time)
			throws RemoteException, ApplicationException {
		SettingsUtil.set("extension_notice_time", Integer.toString(time));
	}

	public static boolean getNamedICalExport() throws RemoteException {
		return Boolean.parseBoolean(SettingsUtil.get("ical_name_export", "true"));
	}

	public static void setNamedICalExport(boolean name)
			throws RemoteException, ApplicationException {
		SettingsUtil.set("ical_name_export", Boolean.toString(name));
	}

	public static String getTemplateFolder() throws RemoteException {
		String defaultFolder = ContractManagerPlugin.getInstance().getManifest().getPluginDir()
				+ "/templates/";
		return SettingsUtil.get("template_folder", defaultFolder);
	}

	public static void setTemplateFolder(String folder)
			throws RemoteException, ApplicationException {
		SettingsUtil.set("template_folder", folder);
	}

	public static boolean getShowHibiscusCategorySelector() throws RemoteException {
		return Boolean.parseBoolean(SettingsUtil.get("show_hibiscus_category_selector", "true"));
	}

	public static void setShowHibiscusCategorySelector(boolean show)
			throws RemoteException, ApplicationException {
		SettingsUtil.set("show_hibiscus_category_selector", Boolean.toString(show));
	}

	public static boolean getShowHibiscusTransactionList() throws RemoteException {
		return Boolean.parseBoolean(SettingsUtil.get("show_hibiscus_transaction_list", "true"));
	}

	public static void setShowHibiscusTransactionList(boolean show)
			throws RemoteException, ApplicationException {
		SettingsUtil.set("show_hibiscus_transaction_list", Boolean.toString(show));
	}

	public static int getHibiscusTransactionListHeight() throws RemoteException {
		return Integer.parseInt(SettingsUtil.get("hibiscus_transaction_list_height", "105"));
	}

	public static void setHibiscusTransactionListHeight(int height)
			throws RemoteException, ApplicationException {
		SettingsUtil.set("hibiscus_transaction_list_height", Integer.toString(height));
	}

	public static boolean getShowSEPACreditorInput() throws RemoteException {
		return Boolean.parseBoolean(SettingsUtil.get("show_sepa_creditor_input", "false"));
	}

	public static void setShowSEPACreditorInput(boolean show)
			throws RemoteException, ApplicationException {
		SettingsUtil.set("show_sepa_creditor_input", Boolean.toString(show));
	}

	public static boolean getShowSEPACustomerInput() throws RemoteException {
		return Boolean.parseBoolean(SettingsUtil.get("show_sepa_debitor_input", "false"));
	}

	public static void setShowFixedTermsInput(boolean show)
			throws RemoteException, ApplicationException {
		SettingsUtil.set("show_fixed_terms_input", Boolean.toString(show));
	}

	public static boolean getShowFixedTermsInput() throws RemoteException {
		return Boolean.parseBoolean(SettingsUtil.get("show_fixed_terms_input", "false"));
	}

	public static void setShowSEPADebitorInput(boolean show)
			throws RemoteException, ApplicationException {
		SettingsUtil.set("show_sepa_debitor_input", Boolean.toString(show));
	}

	public static Color getNotActiveForegroundColor() {
		return Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
	}

	public static Color getErrorColor() {
		// derive from Jameica settings
		return de.willuhn.jameica.gui.util.Color.ERROR.getSWTColor();
	}

	public static boolean getHibiscusAutoImportNewTransactions() throws RemoteException {
		return Boolean.parseBoolean(SettingsUtil.get("hibiscus_auto_import", "false"));
	}

	public static void setHibiscusAutoImportNewTransactions(boolean value)
			throws RemoteException, ApplicationException {
		SettingsUtil.set("hibiscus_auto_import", Boolean.toString(value));
	}
}