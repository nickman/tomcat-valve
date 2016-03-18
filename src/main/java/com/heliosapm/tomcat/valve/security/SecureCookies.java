/**
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
 */
package com.heliosapm.tomcat.valve.security;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.servlet.ServletException;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Title: SecureCookies</p>
 * <p>Description: </p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.tomcat.valve.security.SecureCookies</code></p>
 */

public class SecureCookies extends ValveBase implements Lifecycle {
	/** Instance logger */
	protected final Logger log = LoggerFactory.getLogger(getClass());
	/** Lifecycle listeners */
	protected final Set<LifecycleListener> listeners = new CopyOnWriteArraySet<LifecycleListener>();
	
	/** The MBeanServer this valve is registered in */
	protected MBeanServer server = null;
	/** This valve's JMX ObjectName */
	protected ObjectName objectName = null;
	
	/** The valce enabled flag */
	protected final AtomicBoolean enabled = new AtomicBoolean(true);	
	
	
	/**
	 * {@inheritDoc}
	 * @see org.apache.catalina.Lifecycle#start()
	 */
	@Override
	public void start() throws LifecycleException {
		log.info(">>>>> Starting SecureCookies Valve.....");
		
		log.info("<<<<< Started SecureCookies Valve");
	}

	/**
	 * {@inheritDoc}
	 * @see org.apache.catalina.Lifecycle#stop()
	 */
	@Override
	public void stop() throws LifecycleException {
		log.info(">>>>> Stopping SecureCookies Valve.....");
		
		log.info("<<<<< Stopped SecureCookies Valve");		
	}

	
	
	/**
	 * Creates a new SecureCookies
	 */
	public SecureCookies() {
		log.info("\n\t========================================\n\tCreated SecureCookies Valve\n\t========================================");
	}

	/**
	 * {@inheritDoc}
	 * @see org.apache.catalina.valves.ValveBase#invoke(org.apache.catalina.connector.Request, org.apache.catalina.connector.Response)
	 */
	@Override
	public void invoke(final Request request, final Response response) throws IOException, ServletException {
		final String req = request.getPathInfo() + "/" + request.getContextPath()  + "/" + request.getQueryString();
		try {
			if(enabled.get()) {
				 log.info("Executing [{}]", req);
				 final Response wrappedResponse = WrappedResponse.wrap(response);
				 request.setResponse(wrappedResponse);
				 getNext().invoke(request, wrappedResponse);
			} else {
				log.info("SecureCookies disabled. Skipping.");
				getNext().invoke(request, response);
			}
		} catch (IOException ioe) {
			log.error("Valve IOException on [{}]", req, ioe);
			throw ioe;
		} catch (ServletException se) {
			log.error("Valve ServletException on [{}]", req, se);
			throw se;
		} catch (Exception ex) {
			log.error("Valve Unexpected Exception on [{}]", req, ex);
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @see org.apache.catalina.valves.ValveBase#preRegister(javax.management.MBeanServer, javax.management.ObjectName)
	 */
	@Override
	public ObjectName preRegister(final MBeanServer server, final ObjectName name) throws Exception {
		this.objectName = name;
		this.server = server;
		return super.preRegister(server, name);
	}
	
	/**
	 * Sets the enabled state of the valve
	 * @param enabled true to enable, false to disable
	 */
	public void setEnabled(final boolean enabled) {
		this.enabled.set(enabled);
	}
	
	/**
	 * Indicates if the valve is enabled
	 * @return true if the valve is enabled, false otherwise
	 */
	public boolean isEnabled() {
		return enabled.get();
	}

	/**
	 * {@inheritDoc}
	 * @see org.apache.catalina.Lifecycle#addLifecycleListener(org.apache.catalina.LifecycleListener)
	 */
	@Override
	public void addLifecycleListener(final LifecycleListener listener) {
		if(listener!=null) listeners.add(listener);
	}

	/**
	 * {@inheritDoc}
	 * @see org.apache.catalina.Lifecycle#findLifecycleListeners()
	 */
	@Override
	public LifecycleListener[] findLifecycleListeners() {		
		return listeners.toArray(new LifecycleListener[listeners.size()]);
	}

	/**
	 * {@inheritDoc}
	 * @see org.apache.catalina.Lifecycle#removeLifecycleListener(org.apache.catalina.LifecycleListener)
	 */
	@Override
	public void removeLifecycleListener(final LifecycleListener listener) {
		if(listener!=null) listeners.remove(listener);		
	}
	
	private static final Connector[] EMPTY_CONN_ARR = {};
	
	public Connector[] findConnectors() {
		return EMPTY_CONN_ARR;
	}


}
