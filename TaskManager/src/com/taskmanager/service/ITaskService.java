/**
 * 
 */
package com.taskmanager.service;

import java.util.Date;
import java.util.List;

import com.taskmanager.dto.TaskInfoEntity;

/**
 * @author IBM_ADMIN
 * 
 */
public interface ITaskService {
	
	public Long createTask(TaskInfoEntity entity) throws Exception;
	
	/**
	 * 
	 * @param entity
	 * @return
	 * @throws Exception
	 */
	public Long updateTask(TaskInfoEntity entity) throws Exception;
	/**
	 * 
	 * @param mobileNumber
	 * @return
	 * @throws Exception
	 */
	public List<TaskInfoEntity> openTask(String mobileNumber,String taskTypes,boolean selfFlag,boolean receivedFlag,boolean assignedFlag )throws Exception;
	
	
	/**
	 * 
	 * @param mobileNumber
	 * @return
	 * @throws Exception
	 */
	public List<TaskInfoEntity> selfTask(String mobileNumber,Date lastUpdateTime) throws Exception;
	/**
	 * 
	 * @param mobileNumber
	 * @return
	 * @throws Exception
	 */
	public List<TaskInfoEntity> recievedTask(String mobileNumber,Date lastUpdateTime) throws Exception;
	
	/**
	 * 
	 * @param mobileNumber
	 * @return
	 * @throws Exception
	 */
	public List<TaskInfoEntity> assignedTask(String mobileNumber,Date lastUpdateTime) throws Exception;
	
	
	
	/**
	 * 
	 * @param taskId
	 * @param isReminder
	 * @return
	 * @throws Exception
	 */
	public boolean setReminder(Long taskId,boolean isReminder,Date reminderDate) throws Exception;
	
	/**
	 * 
	 * @param taskId
	 * @param isFire
	 * @return
	 * @throws Exception
	 */
	
	public boolean setFire(Long taskId, boolean isFire) throws Exception;
	
	/**
	 * 
	 * @param taskId
	 * @param priority
	 * @return
	 * @throws Exception
	 */
	
	public boolean changePriority(Long taskId,String priority) throws Exception;
	
	/**
	 * 
	 * @param taskId
	 * @param status
	 * @return
	 * @throws Exception
	 */
	
	public boolean changeStatus(Long taskId,String mobileNumber, String status,boolean isAssiner) throws Exception;
	
	/**
	 * 
	 * @param taskId
	 * @param isFavourate
	 * @return
	 * @throws Exception
	 */
	
	public boolean setFavouraite(Long taskId, boolean isFavourate) throws Exception;
	
	/**
	 * 
	 * @param taskId
	 * @return
	 */
	
	public boolean isValidTaskId(Long taskId) throws RuntimeException;
	
	/**
	 * 
	 * @param newAssignee
	 * @param oldAssignee
	 * @param taskId
	 * @return
	 * @throws Exception
	 */
	
	public boolean changeAssignee(String newAssignee,String oldAssignee,Long taskId) throws Exception;
	
	/**
	 * 
	 * @param mobileNumber
	 * @param lastUpdateTime
	 * @return
	 * @throws Exception
	 */
	public List<TaskInfoEntity> getChangeAssignies(String mobileNumber,Date lastUpdateTime) throws Exception ;
	
	/**
	 * 
	 * @param groupId
	 * @return
	 * @throws Exception
	 */
	public List<TaskInfoEntity> getTaskInfoByGroupId(Long groupId) throws Exception;
}
