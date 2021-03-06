/*
 * oxTrust is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.oxtrust.action;

import static org.jboss.seam.ScopeType.CONVERSATION;

import java.io.Serializable;

import org.gluu.oxtrust.ldap.service.ApplianceService;
import org.gluu.oxtrust.model.GluuAppliance;
import org.gluu.oxtrust.util.OxTrustConstants;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * Action class for health check display
 * 
 * @author Oleksiy Tataryn Date: 11.14.2013
 */
@Scope(CONVERSATION)
@Name("applianceStatusAction")
public class ApplianceStatusAction implements Serializable {

	private static final long serialVersionUID = -7470520478553992898L;

	@In
	private ApplianceService applianceService;

	private String health;
	
	public String checkHealth(){
		GluuAppliance appliance = applianceService.getAppliance();
		String lastUpdateString = appliance.getLastUpdate();
		int lastUpdate = 0;
		if(lastUpdateString !=null){
			lastUpdate = Integer.parseInt(lastUpdateString);
		}
		
		int currentTime = (int) (System.currentTimeMillis() / 1000);
		int timeSinceLastUpdate = currentTime - lastUpdate;
		if(timeSinceLastUpdate >= 0 && timeSinceLastUpdate < 100){
			this.setHealth("OK");
		}else{
			this.setHealth("FAIL");
		}
		return OxTrustConstants.RESULT_SUCCESS;
	}

	public String getHealth() {
		return health;
	}

	public void setHealth(String health) {
		this.health = health;
	}
	
}
