package org.hifly.service;

import java.util.HashMap;
import java.util.Map;

public class ProcessInstanceInfo {

	private long id;
	private String processId;
	private String processName;
	private int processStateCode;
	private String processState;
	private Map<String,Object> processInstanceVariables;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}
	
	public String getProcessState() {
		return processState;
	}

	public int getProcessStateCode() {
		return processStateCode;
	}

	public void setProcessStateCode(int processStateCode) {
		this.processStateCode = processStateCode;
		switch (processStateCode) {
		case 0:
			this.processState = "PENDING";
			break;
		case 1:
			this.processState = "ACTIVE";
			break;
		case 2:
			this.processState = "COMPLETE";
			break;
		case 3:
			this.processState = "ABORTED";
			break;
		case 4:
			this.processState = "SUSPENDED";
			break;
		default:
			this.processState = "UNKNOW";
			break;
		}
	}
	
	public Map<String,Object> getProcessInstanceVariables() {
		if(processInstanceVariables == null) {
			this.processInstanceVariables = new HashMap<>();
		}
		return processInstanceVariables;
	}

	public String toString() {
		return id + " - " + processId + "-" + processState + " - " + processName + " - variables:" +  getProcessInstanceVariables();
	}
	
	
}