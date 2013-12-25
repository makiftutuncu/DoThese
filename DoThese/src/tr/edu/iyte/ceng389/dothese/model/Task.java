package tr.edu.iyte.ceng389.dothese.model;

import java.io.Serializable;
import java.util.Date;

/** A model class to represent a task
 * 
 * @author ezgihacihalil - 180201058
 * @author mehmetakiftutuncu - 170201018 */
public class Task implements Comparable<Task>, Serializable
{
	private static final long serialVersionUID = -618102254113173951L;
	
	/** An enum class to represent task statuses */
	public static enum Status
	{
		NOT_COMPLETED(0), COMPLETED(1);
		
		/** Numerical value of the status */
		public final int numericValue;
		
		Status(int numericValue)
		{
			this.numericValue = numericValue;
		}
	}

	/** An enum class to represent task priorities */
	public static enum Priority
	{
		HIGH(1), NORMAL(2), LOW(3);
		
		/** Numerical value of the priority level */
		public final int numericValue;
		
		Priority(int numericValue)
		{
			this.numericValue = numericValue;
		}
	}
	
	/** Row id of the task */
	private long id = -1;
	/** Description of the task */
	private String description;
	/** Deadline of the task */
	private Date deadline;
	/** Status of the task */
	private Status status;
	/** Priority of the task */
	private Priority priority;
	/** Notify state of the task */
	private boolean notifyState;
	
	/** Constructs a new task
	 * 
	 * @param description {@link Task#description}
	 * @param deadline {@link Task#deadline}
	 * @param status {@link Task#status}
	 * @param priority {@link Task#priority}
	 * @param notifyState {@link Task#notifyState} */
	public Task(String description, Date deadline, Status status, Priority priority, boolean notifyState) 
	{
		this(-1, description, deadline, status, priority, notifyState);
	}
	
	/** Constructs a new task
	 * 
	 * @param id {@link Task#id}
	 * @param description {@link Task#description}
	 * @param deadline {@link Task#deadline}
	 * @param status {@link Task#status}
	 * @param priority {@link Task#priority}
	 * @param notifyState {@link Task#notifyState} */
	public Task(long id, String description, Date deadline, Status status, Priority priority, boolean notifyState)
	{
		setId(id);
		setDescription(description);
		setDeadline(deadline);
		setStatus(status);
		setPriority(priority);
		setNotifyState(notifyState);
	}
	
	/** Creates a new instance of Task by copying values of the given Task object
	 * 
	 * @param item Task whose values to copy */
	public static Task copy(Task item)
	{
		Task copy = new Task(item.getDescription(), item.getDeadline(), item.getStatus(), item.getPriority(), item.getNotifyState());
		copy.setId(item.getId());
		return copy;
	}
	
	/** @return {@link Task#id} */
	public long getId() 
	{
		return id;
	}
	
	/** Sets {@link Task#id} */
	public void setId(long id) 
	{
		this.id = id;
	}
	
	/** @return {@link Task#description} */
	public String getDescription() 
	{
		return description;
	}
	
	/** Sets {@link Task#description} */
	public void setDescription(String description) 
	{
		this.description = description;
	}
	
	/** @return {@link Task#deadline} */
	public Date getDeadline() 
	{
		return deadline;
	}
	
	/** Sets {@link Task#deadline} */
	public void setDeadline(Date deadline) 
	{
		this.deadline = deadline;
	}
	
	/** @return {@link Task#status} */
	public Status getStatus() 
	{
		return status;
	}
	
	/** Sets {@link Task#status} */
	public void setStatus(Status status) 
	{
		this.status = status;
	}
	
	/** @return {@link Task#priority} */
	public Priority getPriority() 
	{
		return priority;
	}
	
	/** Sets {@link Task#priority} */
	public void setPriority(Priority priority) 
	{
		this.priority = priority;
	}
	
	/** @return {@link Task#notifyState} */
	public boolean getNotifyState() 
	{
		return notifyState;
	}
	
	/** Sets {@link Task#notifyState} */
	public void setNotifyState(boolean notifyState) 
	{
		this.notifyState = notifyState;
	}

	@Override
	public int compareTo(Task that)
	{
		// Compare done statuses
		if(this.status.numericValue < that.getStatus().numericValue)
		{
			return -1;
		}
		else if(this.status.numericValue < that.getStatus().numericValue)
		{
			return 1;
		}
		else
		{
			// Compare priorities
			if(this.getPriority().numericValue < that.getPriority().numericValue)
			{
				return -1;
			}
			else if(this.getPriority().numericValue > that.getPriority().numericValue)
			{
				return 1;
			}
			else
			{
				// Compare deadlines
				if(this.getDeadline().before(that.getDeadline()))
				{
					return -1;
				}
				else if(this.getDeadline().after(that.getDeadline()))
				{
					return 1;
				}
				else
				{
					return 0;
				}
			}
		}
	}
	
	@Override
	public boolean equals(Object o)
	{
		Task that = (Task) o;
		
		return this.description.equals(that.getDescription()) &&
				this.deadline.equals(that.getDeadline()) &&
				this.status.equals(that.getStatus()) &&
				this.priority.equals(that.getPriority()) &&
				this.notifyState == that.getNotifyState();
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder("{");
		
		if(id != -1) builder.append("\"id\":").append(id).append(",");
		
		builder.append("\"description\":\"").append(description).append("\",")
				.append("\"deadline\":").append(deadline.getTime()).append(",")
				.append("\"status\":\"").append(status.toString()).append("\",")
				.append("\"priority\":\"").append(priority.toString()).append("\",")
				.append("\"notifyState\":").append(notifyState).append("}");
		
		return builder.toString();
	}
}