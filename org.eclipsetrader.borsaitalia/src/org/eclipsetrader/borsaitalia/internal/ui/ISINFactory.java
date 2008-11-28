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

package org.eclipsetrader.borsaitalia.internal.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IExecutableExtensionFactory;
import org.eclipsetrader.core.feed.IFeedIdentifier;
import org.eclipsetrader.core.feed.IFeedProperties;
import org.eclipsetrader.core.views.IDataProvider;
import org.eclipsetrader.core.views.IDataProviderFactory;

public class ISINFactory implements IDataProviderFactory, IExecutableExtension, IExecutableExtensionFactory {
	public static final String SYMBOL_PROPERTY = "org.eclipsetrader.borsaitalia.isin";
	private String id;
	private String name;

	public class DataProvider implements IDataProvider {

		public DataProvider() {
        }

		/* (non-Javadoc)
         * @see org.eclipsetrader.core.views.IDataProvider#init(org.eclipse.core.runtime.IAdaptable)
         */
        public void init(IAdaptable adaptable) {
        }

		/* (non-Javadoc)
         * @see org.eclipsetrader.core.views.IDataProvider#getFactory()
         */
        public IDataProviderFactory getFactory() {
	        return ISINFactory.this;
        }

		/* (non-Javadoc)
         * @see org.eclipsetrader.core.views.IDataProvider#getValue(org.eclipse.core.runtime.IAdaptable)
         */
        public IAdaptable getValue(IAdaptable adaptable) {
        	IFeedIdentifier identifier = (IFeedIdentifier) adaptable.getAdapter(IFeedIdentifier.class);
        	if (identifier != null) {
        		IFeedProperties properties = (IFeedProperties) identifier.getAdapter(IFeedProperties.class);
        		if (properties != null) {
        			if (properties.getProperty(SYMBOL_PROPERTY) != null) {
        				final String value = properties.getProperty(SYMBOL_PROPERTY);
                		return new IAdaptable() {
                            @SuppressWarnings("unchecked")
                            public Object getAdapter(Class adapter) {
                            	if (adapter.isAssignableFrom(String.class))
                            		return value;
        	                    return null;
                            }

                            @Override
                            public boolean equals(Object obj) {
                            	if (!(obj instanceof IAdaptable))
                            		return false;
                            	String s = (String) ((IAdaptable) obj).getAdapter(String.class);
                            	return value.equals(s);
                            }
                		};
        			}
        		}
        	}
	        return null;
        }

		/* (non-Javadoc)
         * @see org.eclipsetrader.core.views.IDataProvider#dispose()
         */
        public void dispose() {
        }
	}

	public ISINFactory() {
	}

	/* (non-Javadoc)
     * @see org.eclipsetrader.core.views.IDataProviderFactory#getId()
     */
    public String getId() {
	    return id;
    }

	/* (non-Javadoc)
	 * @see org.eclipsetrader.core.views.IDataProviderFactory#getName()
	 */
	public String getName() {
		return name;
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
	    return this;
    }

	/* (non-Javadoc)
	 * @see org.eclipsetrader.core.views.IDataProviderFactory#createProvider()
	 */
	public IDataProvider createProvider() {
		return new DataProvider();
	}

	/* (non-Javadoc)
     * @see org.eclipsetrader.core.views.IDataProviderFactory#getType()
     */
    @SuppressWarnings("unchecked")
    public Class[] getType() {
	    return new Class[] {
	    		String.class,
	    	};
    }
}
