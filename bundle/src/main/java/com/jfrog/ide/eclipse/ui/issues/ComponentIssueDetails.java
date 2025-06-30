package com.jfrog.ide.eclipse.ui.issues;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Composite;
import org.osgi.framework.Bundle;

import com.jfrog.ide.common.nodes.FileIssueNode;
import com.jfrog.ide.eclipse.log.Logger;
import com.jfrog.ide.eclipse.ui.ComponentDetails;
import com.jfrog.ide.eclipse.ui.webview.WebviewManager;

/**
 * ComponentIssueDetails provides a detailed view of security issues using a webview.
 * It uses WebviewManager to handle all webview operations and communication.
 */
public class ComponentIssueDetails extends ComponentDetails {

	private static ComponentIssueDetails instance;
	private static final Logger log = Logger.getInstance();
	
	private WebviewManager webviewManager;

	public static ComponentIssueDetails createComponentIssueDetails(Composite parent) {
		instance = new ComponentIssueDetails(parent);
		return instance;
	}

	public static ComponentIssueDetails getInstance() {
		return instance;
	}

	private ComponentIssueDetails(Composite parent) {
		super(parent, "Issue Details");
		initializeWebviewManager();
	}

	@Override
	public void createDetailsView(FileIssueNode node) {
		if (webviewManager == null || !webviewManager.isReady()) {
			log.warn("WebviewManager not ready. Cannot display issue.");
			return;
		}
		
		try {
			webviewManager.displayIssue(node);
			refreshPanel();
		} catch (Exception e) {
			log.error("Error creating details view: " + e.getMessage(), e);
		}
	}

	@Override
	protected void createBrowserJCEF(Composite parent) {
		try {
			if (webviewManager == null) {
				log.error("WebviewManager not initialized");
				return;
			}
			
			String webviewUrl = getWebviewUrl();
			if (webviewUrl == null) {
				log.error("Could not find webview resources");
				return;
			}
			
			webviewManager.createBrowser(parent, webviewUrl);
			log.debug("Webview browser created successfully");
			
		} catch (Exception e) {
			log.error("Error in createBrowserJCEF: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Initializes the WebviewManager with proper configuration.
	 */
	private void initializeWebviewManager() {
		try {
			webviewManager = new WebviewManager();
			webviewManager.initialize();
				
			log.debug("WebviewManager initialized successfully");
			
		} catch (Exception e) {
			log.error("Failed to initialize WebviewManager: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Gets the webview index.html URL as string.
	 */
	private String getWebviewUrl() {
        try {
            Bundle bundle = Platform.getBundle("com.jfrog.ide.eclipse"); 
            URL webviewFileUrl = FileLocator.toFileURL(bundle.getEntry(WebviewManager.WEBVIEW_INDEX_HTML_PATH));
            if (webviewFileUrl == null) {
            	throw new RuntimeException("Unable to locate Webview file: 'index.html' from plugin bundle");
            }
            return new File(webviewFileUrl.getPath()).getAbsolutePath();
        } catch (IOException e) {
        	throw new RuntimeException("Failed to generate path for webview index.html", e);
        }
	}

	public static void disposeComponentDetails() {
		if (instance != null) {
			instance.dispose();
		}
	}

	@Override
	public void dispose() {
		if (webviewManager != null) {
			webviewManager.dispose();
			webviewManager = null;
		}
		super.dispose();
	}
}
