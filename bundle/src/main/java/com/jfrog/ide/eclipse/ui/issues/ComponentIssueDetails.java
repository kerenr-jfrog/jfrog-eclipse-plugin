package com.jfrog.ide.eclipse.ui.issues;


import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.widgets.Composite;

import com.jfrog.ide.common.nodes.FileIssueNode;
import com.jfrog.ide.common.nodes.ScaIssueNode;
import com.jfrog.ide.common.parse.Applicability;
import com.jfrog.ide.eclipse.ui.ComponentDetails;

/**
 * @author yahavi
 */
public class ComponentIssueDetails extends ComponentDetails {

	private static ComponentIssueDetails instance;
	private Browser browser;

	public static ComponentIssueDetails createComponentIssueDetails(Composite parent) {
		instance = new ComponentIssueDetails(parent);
		return instance;
	}

	public static ComponentIssueDetails getInstance() {
		return instance;
	}

	private ComponentIssueDetails(Composite parent) {
		super(parent, "Issue Details");
	}

	@Override
	public void createDetailsView(FileIssueNode node) {
		createCommonInfo(node);
		if (node instanceof ScaIssueNode ) {
			ScaIssueNode scaNode = (ScaIssueNode) node;
			Applicability applicability = scaNode.getApplicability();
			addSection("Component Name:", scaNode.getComponentName());
			addSection("Component Version:", scaNode.getComponentVersion());
			addSection("Fixed Versions:", scaNode.getFixedVersions());
			addSection("Applicability:", applicability != null ? applicability.getValue() : "");  
		} else {
			addSection("Location:", "row: " + node.getRowStart() + " col: " + node.getColStart());
			addSection("Reason:", node.getReason());
		}
		refreshPanel();
	}

	public static void disposeComponentDetails() {
		if (instance != null) {
			instance.dispose();
		}
	}
	
	@Override
    protected void createBrowser(Composite parent) {
        try {
            // Initialize the browser
            browser = new Browser(parent, SWT.WEBKIT);
//            String browserType = browser.getBrowserType();

            // Set the size and layout for the browser
            browser.setBounds(10, 10, 1000, 600); // Adjust dimensions as needed

            // Load a URL or HTML content
//            String url = "file:///" + System.getProperty("user.dir") + "/src/main/resources/jfrog-ide-webview/index.html";
            String url = "file:///C:/Users/Keren%20Reshef/Projects/jfrog-eclipse-plugin/bundle/src/main/resources/jfrog-ide-webview/index.html";
            browser.setUrl(url); 
            
            browser.addProgressListener(new ProgressListener() {
                @Override
                public void completed(ProgressEvent event) {
//                	runJavaScript();
                }

				@Override
				public void changed(ProgressEvent event) {
					// TODO Auto-generated method stub
					
				}
           
            });
            
            parent.layout(true, true);
            
        } catch (Exception e) {
            System.err.println("Failed to initialize browser: " + e.getMessage());
        }
    }
	
    private void runJavaScript() {
        String script = "window.postMessage({type: 'SHOW_PAGE',data: {pageType: 'SECRETS',location: 'EXP-1527-00001'}},'*')";
        browser.execute(script);
    }
}
