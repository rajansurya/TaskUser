/**
 * 
 */
package com.taskmanager.service;

import java.util.Date;
import java.util.List;

import android.content.Context;

import com.taskmanager.dto.TaskInfoEntity;

/**
 * @author IBM_ADMIN
 *
 */
public class TaskServiceImpl implements ITaskService {
	private Context context;
	public TaskServiceImpl(Context context) {
		
		this.context=context;
	}
	@Override
	public Long createTask(TaskInfoEntity entity) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long updateTask(TaskInfoEntity entity) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TaskInfoEntity> openTask(String mobileNumber,String taskTypes,boolean selfFlag,boolean receivedFlag,boolean assignedFlag ) throws Exception {
		String taskType="R";
		StringBuilder builder=new StringBuilder("Select * from Task where status='OPEN' AND TaskType in ("+taskTypes+") AND ");
		if(selfFlag&&receivedFlag&assignedFlag){
			
		}
		
		
		
		return null;
	}

	@Override
	public List<TaskInfoEntity> selfTask(String mobileNumber,
			Date lastUpdateTime) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TaskInfoEntity> recievedTask(String mobileNumber,
			Date lastUpdateTime) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TaskInfoEntity> assignedTask(String mobileNumber,
			Date lastUpdateTime) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setReminder(Long taskId, boolean isReminder,
			Date reminderDate) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setFire(Long taskId, boolean isFire) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean changePriority(Long taskId, String priority)
			throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean changeStatus(Long taskId, String mobileNumber,
			String status, boolean isAssiner) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setFavouraite(Long taskId, boolean isFavourate)
			throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isValidTaskId(Long taskId) throws RuntimeException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean changeAssignee(String newAssignee, String oldAssignee,
			Long taskId) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<TaskInfoEntity> getChangeAssignies(String mobileNumber,
			Date lastUpdateTime) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TaskInfoEntity> getTaskInfoByGroupId(Long groupId)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	
	
}
