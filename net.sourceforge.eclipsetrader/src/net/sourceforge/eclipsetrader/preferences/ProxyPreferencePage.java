/*******************************************************************************
 * Copyright (c) 2004 Marco Maccaferri and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Marco Maccaferri - initial API and implementation
 *******************************************************************************/
package net.sourceforge.eclipsetrader.preferences;

import java.util.Vector;

import net.sourceforge.eclipsetrader.TraderPlugin;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Proxy settings preference page
 * <p></p>
 */
public class ProxyPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{
  private FieldEditor[] editor;
  private Text httpHost;
  private Text httpPort;
  private Text httpsHost;
  private Text httpsPort;
  private Text socksHost;
  private Text socksPort;
  private Text text7;
  private Text text8;
  private Button button1;
  private Button button2;
  
  /* (non-Javadoc)
   * @see org.eclipse.jface.preference.PreferencePage#init(IWorkbench)
   */
  public void init(IWorkbench workbench) 
  {
    // Initialize the preference store we wish to use
    setPreferenceStore(TraderPlugin.getDefault().getPreferenceStore());
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.preference.PreferencePage#createContents(Composite)
   */
  protected Control createContents(Composite parent)
  {
    Vector _v = new Vector();

    Composite entryTable = new Composite(parent, SWT.NULL);
    entryTable.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    GridLayout gridLayout = new GridLayout(4, false);
//    gridLayout.marginWidth = 0;
    gridLayout.marginHeight = 0;
    entryTable.setLayout(gridLayout);

    button1 = new Button(entryTable, SWT.RADIO);
    button1.setText("Direct connection to the Internet");
    button1.setSelection(getPreferenceStore().getInt("PROXY_ENABLED") == 0 ? true : false);
    GridData gridData = new GridData();
    gridData.horizontalSpan = 4;
    button1.setLayoutData(gridData);
    
    button2 = new Button(entryTable, SWT.RADIO);
    button2.setText("Manual proxy configuration");
    button2.setSelection(getPreferenceStore().getInt("PROXY_ENABLED") == 1 ? true : false);
    gridData = new GridData();
    gridData.horizontalSpan = 4;
    button2.setLayoutData(gridData);

    Label label = new Label(entryTable, SWT.NONE);
    label.setText("HTTP Proxy");
    label.setLayoutData(new GridData());
    httpHost = new Text(entryTable, SWT.BORDER);
    httpHost.setText(getPreferenceStore().getString("HTTP_PROXY_HOST"));
    gridData = new GridData();
    gridData.widthHint = 120;
    httpHost.setLayoutData(gridData);
    label = new Label(entryTable, SWT.NONE);
    label.setText("Port");
    label.setLayoutData(new GridData());
    httpPort = new Text(entryTable, SWT.BORDER);
    httpPort.setText(getPreferenceStore().getString("HTTP_PROXY_PORT"));
    gridData = new GridData();
    gridData.widthHint = 30;
    httpPort.setLayoutData(gridData);

    label = new Label(entryTable, SWT.NONE);
    label.setText("SSL Proxy");
    label.setLayoutData(new GridData());
    httpsHost = new Text(entryTable, SWT.BORDER);
    httpsHost.setText(getPreferenceStore().getString("HTTPS_PROXY_HOST"));
    gridData = new GridData();
    gridData.widthHint = 120;
    httpsHost.setLayoutData(gridData);
    label = new Label(entryTable, SWT.NONE);
    label.setText("Port");
    label.setLayoutData(new GridData());
    httpsPort = new Text(entryTable, SWT.BORDER);
    httpsPort.setText(getPreferenceStore().getString("HTTPS_PROXY_PORT"));
    gridData = new GridData();
    gridData.widthHint = 30;
    httpsPort.setLayoutData(gridData);

    label = new Label(entryTable, SWT.NONE);
    label.setText("SOCKS Host");
    label.setLayoutData(new GridData());
    socksHost = new Text(entryTable, SWT.BORDER);
    socksHost.setText(getPreferenceStore().getString("SOCKS_PROXY_HOST"));
    gridData = new GridData();
    gridData.widthHint = 120;
    socksHost.setLayoutData(gridData);
    label = new Label(entryTable, SWT.NONE);
    label.setText("Port");
    label.setLayoutData(new GridData());
    socksPort = new Text(entryTable, SWT.BORDER);
    socksPort.setText(getPreferenceStore().getString("SOCKS_PROXY_PORT"));
    gridData = new GridData();
    gridData.widthHint = 30;
    socksPort.setLayoutData(gridData);

    Group group = new Group(entryTable, SWT.NONE);
    group.setText("Authentication");
    group.setLayout(new GridLayout(2, false));
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 4;
    group.setLayoutData(gridData);

    label = new Label(group, SWT.NONE);
    label.setText("User");
    label.setLayoutData(new GridData());
    text7 = new Text(group, SWT.BORDER);
    text7.setText(getPreferenceStore().getString("PROXY_USER_NAME"));
    gridData = new GridData();
    gridData.widthHint = 120;
    text7.setLayoutData(gridData);
    label = new Label(group, SWT.NONE);
    label.setText("Password");
    label.setLayoutData(new GridData());
    text8 = new Text(group, SWT.BORDER);
    text8.setEchoChar('*');
    text8.setText(getPreferenceStore().getString("PROXY_PASSWORD"));
    gridData = new GridData();
    gridData.widthHint = 120;
    text8.setLayoutData(gridData);

    // Perform operations common to all field editors
    editor = new FieldEditor[_v.size()];
    for (int i = 0; i < _v.size(); i++)
    {
      editor[i] = (FieldEditor)_v.elementAt(i);
      editor[i].setPreferencePage(this);
      editor[i].setPreferenceStore(getPreferenceStore());
      editor[i].load();
    }
    
    return entryTable;
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
   */
  protected void performDefaults()
  {
    for (int i = 0; i < editor.length; i++)
      editor[i].loadDefault();
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.preference.PreferencePage#performOk()
   */
  public boolean performOk() 
  {
    for (int i = 0; i < editor.length; i++)
      editor[i].store();

    IPreferenceStore ps = getPreferenceStore();
    ps.setValue("HTTP_PROXY_HOST", httpHost.getText());
    ps.setValue("HTTP_PROXY_PORT", httpPort.getText());
    ps.setValue("HTTPS_PROXY_HOST", httpsHost.getText());
    ps.setValue("HTTPS_PROXY_PORT", httpsPort.getText());
    ps.setValue("SOCKS_PROXY_HOST", socksHost.getText());
    ps.setValue("SOCKS_PROXY_PORT", socksPort.getText());
    ps.setValue("PROXY_USER_NAME", text7.getText());
    ps.setValue("PROXY_PASSWORD", text8.getText());
    if (button1.getSelection() == true)
      ps.setValue("PROXY_ENABLED", 0);
    else if (button2.getSelection() == true)
      ps.setValue("PROXY_ENABLED", 1);

    return super.performOk();
  }
}