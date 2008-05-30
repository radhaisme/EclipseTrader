/*
 * Copyright (c) 2004-2008 Marco Maccaferri and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Maccaferri - initial API and implementation
 */

package org.eclipsetrader.opentick.internal.core;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IExecutableExtensionFactory;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipsetrader.core.feed.IFeedConnector;
import org.eclipsetrader.core.feed.IFeedIdentifier;
import org.eclipsetrader.core.feed.IFeedSubscription;
import org.eclipsetrader.opentick.internal.OTActivator;
import org.eclipsetrader.opentick.internal.core.repository.IdentifierType;
import org.eclipsetrader.opentick.internal.core.repository.IdentifiersList;
import org.otfeed.IConnection;
import org.otfeed.OTConnectionFactory;
import org.otfeed.event.IConnectionStateListener;
import org.otfeed.event.OTError;
import org.otfeed.event.OTHost;

public class FeedConnector implements IFeedConnector, IExecutableExtension, IExecutableExtensionFactory, Runnable, PropertyChangeListener {
	private static FeedConnector instance;

	private String id;
	private String name;

	protected Map<String, FeedSubscription> symbolSubscriptions;

	private Thread thread;
	private boolean stopping = false;
	private String userName;
	private String password;

	private IConnection connection;
	private IConnectionStateListener connectionStateListener = new IConnectionStateListener() {
		public void onConnected() {
		}

		public void onConnecting(OTHost host) {
		}

		public void onError(OTError error) {
			connection = null;
		}

		public void onLogin() {
			synchronized (symbolSubscriptions) {
				for (FeedSubscription subscription : symbolSubscriptions.values())
					subscription.submitRequests(connection);
			}
		}

		public void onRedirect(OTHost host) {
		}
	};

	private IPropertyChangeListener propertyChangeListener = new IPropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent event) {
			if (OTActivator.PREFS_USERNAME.equals(event.getProperty()))
				userName = (String) event.getNewValue();
			else if (OTActivator.PREFS_PASSWORD.equals(event.getProperty()))
				userName = (String) event.getNewValue();

			if (OTActivator.PREFS_SERVER.equals(event.getProperty()) || OTActivator.PREFS_PORT.equals(event.getProperty()) || OTActivator.PREFS_PASSWORD.equals(event.getProperty()) || OTActivator.PREFS_USERNAME.equals(event.getProperty())) {
				disconnect();
				connect();
			}
		}
	};

	public FeedConnector() {
		symbolSubscriptions = new HashMap<String, FeedSubscription>();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement, java.lang.String, java.lang.Object)
	 */
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
		id = config.getAttribute("id");
		name = config.getAttribute("name");
	}

	/* (non-Javadoc)
     * @see org.eclipse.core.runtime.IExecutableExtensionFactory#create()
     */
    public Object create() throws CoreException {
    	if (instance == null)
    		instance = this;
	    return instance;
    }

	public static FeedConnector getInstance() {
    	return instance;
    }

	/* (non-Javadoc)
	 * @see org.eclipsetrader.core.feed.IFeedConnector#getId()
	 */
	public String getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see org.eclipsetrader.core.feed.IFeedConnector#getName()
	 */
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see org.eclipsetrader.core.feed.IFeedConnector#subscribe(org.eclipsetrader.core.feed.IFeedIdentifier)
	 */
	public IFeedSubscription subscribe(IFeedIdentifier identifier) {
		synchronized (symbolSubscriptions) {
			IdentifierType identifierType = IdentifiersList.getInstance().getIdentifierFor(identifier);
			FeedSubscription subscription = symbolSubscriptions.get(identifier.getSymbol());
			if (subscription == null) {
				try {
					subscription = new FeedSubscription(this, identifierType);
					symbolSubscriptions.put(identifier.getSymbol(), subscription);
					if (connection != null)
						subscription.submitRequests(connection);

		    	    PropertyChangeSupport propertyChangeSupport = (PropertyChangeSupport) identifier.getAdapter(PropertyChangeSupport.class);
		    	    if (propertyChangeSupport != null)
		    	    	propertyChangeSupport.addPropertyChangeListener(this);
                } catch (Exception e) {
    				Status status = new Status(Status.ERROR, OTActivator.PLUGIN_ID, 0, "Error submitting requests", e);
    				OTActivator.log(status);
                }
			}
	    	if (subscription != null)
	    		subscription.incrementInstanceCount();
			return subscription;
		}
	}

	protected void disposeSubscription(FeedSubscription subscription) {
		synchronized (symbolSubscriptions) {
			if (subscription.decrementInstanceCount() <= 0) {
				IdentifierType identifierType = subscription.getIdentifierType();

		    	if (subscription.getIdentifier() != null) {
		    	    PropertyChangeSupport propertyChangeSupport = (PropertyChangeSupport) subscription.getIdentifier().getAdapter(PropertyChangeSupport.class);
		    	    if (propertyChangeSupport != null)
		    	    	propertyChangeSupport.removePropertyChangeListener(this);
		    	}

				symbolSubscriptions.remove(identifierType.getSymbol());
				try {
					subscription.cancelRequests();
				} catch (Exception e) {
					Status status = new Status(Status.ERROR, OTActivator.PLUGIN_ID, 0, "Error canceling requests", e);
					OTActivator.log(status);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipsetrader.core.feed.IFeedConnector#connect()
	 */
	public void connect() {
		final IPreferenceStore preferences = getPreferenceStore();
		userName = preferences.getString(OTActivator.PREFS_USERNAME);
		password = preferences.getString(OTActivator.PREFS_PASSWORD);

		if (userName.length() == 0 || password.length() == 0) {
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					Shell shell = PlatformUI.isWorkbenchRunning() ? PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() : null;
					LoginDialog dlg = new LoginDialog(shell, userName, password);
					if (dlg.open() == LoginDialog.OK) {
						userName = dlg.getUserName();
						password = dlg.getPassword();
						preferences.setValue(OTActivator.PREFS_USERNAME, userName);
						preferences.setValue(OTActivator.PREFS_PASSWORD, dlg.isSavePassword() ? password : "");
					}
				}
			});
			if (userName.length() == 0 || password.length() == 0)
				return;
		}

		if (thread == null) {
			stopping = false;
			thread = new Thread(this, getName() + " - Notification");
			thread.start();
		}

		if (connection == null) {
			OTConnectionFactory factory = new OTConnectionFactory();
			factory.getHostList().add(new OTHost(preferences.getString(OTActivator.PREFS_SERVER), preferences.getInt(OTActivator.PREFS_PORT)));
			factory.setUsername(userName);
			factory.setPassword(password);
			connection = factory.connect(connectionStateListener);
			connection.waitForCompletion(30 * 1000);
		}

		if (OTActivator.getDefault() != null)
			OTActivator.getDefault().getPreferenceStore().addPropertyChangeListener(propertyChangeListener);
	}

	protected IPreferenceStore getPreferenceStore() {
		return OTActivator.getDefault().getPreferenceStore();
	}

	/* (non-Javadoc)
	 * @see org.eclipsetrader.core.feed.IFeedConnector#disconnect()
	 */
	public void disconnect() {
		if (OTActivator.getDefault() != null)
			OTActivator.getDefault().getPreferenceStore().removePropertyChangeListener(propertyChangeListener);

		stopping = true;
		if (connection != null) {
			try {
				connection.shutdown();
			} catch (Exception e) {
				Status status = new Status(Status.ERROR, OTActivator.PLUGIN_ID, 0, "Error stopping shutting down connection", e);
				OTActivator.log(status);
			}
			connection = null;
		}
		if (thread != null) {
			try {
				synchronized (thread) {
					thread.notify();
				}
				thread.join(30 * 1000);
			} catch (InterruptedException e) {
				Status status = new Status(Status.ERROR, OTActivator.PLUGIN_ID, 0, "Error stopping thread", e);
				OTActivator.log(status);
			}
			thread = null;
		}
	}

	public boolean isStopping() {
		return stopping;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		synchronized (thread) {
			while (!isStopping()) {
				FeedSubscription[] subscriptions;
				synchronized (symbolSubscriptions) {
					Collection<FeedSubscription> c = symbolSubscriptions.values();
					subscriptions = c.toArray(new FeedSubscription[c.size()]);
				}
				for (FeedSubscription s : subscriptions)
					s.fireNotification();

				try {
					thread.wait();
				} catch (InterruptedException e) {
					// Ignore exception, not important at this time
				}
			}
		}
	}

	protected void wakeupNotifyThread() {
		if (thread != null) {
			synchronized (thread) {
				thread.notifyAll();
			}
		}
	}

	/* (non-Javadoc)
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(java.beans.PropertyChangeEvent evt) {
    	if (evt.getSource() instanceof IFeedIdentifier) {
    		IFeedIdentifier identifier = (IFeedIdentifier) evt.getSource();
			synchronized(symbolSubscriptions) {
				for (FeedSubscription subscription : symbolSubscriptions.values()) {
					if (subscription.getIdentifier() == identifier) {
						symbolSubscriptions.remove(subscription.getIdentifierType().getSymbol());
						try {
			                subscription.cancelRequests();
		                } catch (Exception e) {
		    				Status status = new Status(Status.ERROR, OTActivator.PLUGIN_ID, 0, "Error canceling requests", e);
		    				OTActivator.log(status);
		                }

						IdentifierType identifierType = IdentifiersList.getInstance().getIdentifierFor(identifier);
				    	subscription.setIdentifierType(identifierType);

				    	try {
							if (connection != null)
								subscription.submitRequests(connection);
		                } catch (Exception e) {
		    				Status status = new Status(Status.ERROR, OTActivator.PLUGIN_ID, 0, "Error submitting requests", e);
		    				OTActivator.log(status);
		                }
				    	symbolSubscriptions.put(identifierType.getSymbol(), subscription);
					}
				}
			}
    	}
    }
}