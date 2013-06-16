package com.xx.platform.domain.model.system;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.xx.platform.domain.service.DomainLogic;

@Entity
@Table(name = "crontabtaskauto")
@org.hibernate.annotations.Proxy(lazy = false)
public class CrontabTaskAuto extends DomainLogic implements java.lang.Cloneable,java.io.Serializable{
	private String id;
	private String kind;
	private String weeks;
	private String days;
	private String taskbeginhour;
	private String taskendhour;
	private String taskbeginminutes;
	private String taskendminutes;
	private String state;
	private String timespan;
	private String tasktype;
	private String times;
	private String name;
    private Date lasttime;
	private String dbtid;
	@Transient
	public static String running;
	
	public String getDays() {
		return days;
	}
	public void setDays(String days) {
		this.days = days;
	}
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public String getWeeks() {
		return weeks;
	}
	public void setWeeks(String weeks) {
		this.weeks = weeks;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
    @Id
    @Column(length = 32)
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@Transient
	public boolean contain(String str,String strs)
	{
		boolean hasit=false;
		String [] s=strs.split(" ");
		for(int i=0;i<s.length;i++)
		{
			if(str.equals(s[i]))
				hasit=true;
		}
			
		return hasit;
	}
	public String getTimespan() {
		return timespan;
	}
	public void setTimespan(String timespan) {
		this.timespan = timespan;
	}
	public String getTaskbeginhour() {
		return taskbeginhour;
	}
	public void setTaskbeginhour(String taskbeginhour) {
		this.taskbeginhour = taskbeginhour;
	}
	public String getTaskbeginminutes() {
		return taskbeginminutes;
	}
	public void setTaskbeginminutes(String taskbeginminutes) {
		this.taskbeginminutes = taskbeginminutes;
	}
	public String getTaskendhour() {
		return taskendhour;
	}
	public void setTaskendhour(String taskendhour) {
		this.taskendhour = taskendhour;
	}
	public String getTaskendminutes() {
		return taskendminutes;
	}
	public void setTaskendminutes(String taskendminutes) {
		this.taskendminutes = taskendminutes;
	}
	public String getTasktype() {
		return tasktype;
	}
	public void setTasktype(String tasktype) {
		this.tasktype = tasktype;
	}
	public String getTimes() {
		return times;
	}
	public void setTimes(String times) {
		this.times = times;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Transient
	public static String getRunning() {
		return running;
	}
	@Transient
	public static void setRunning(String running) {
		CrontabTaskAuto.running = running;
	}
	public Date getLasttime() {
		return lasttime;
	}
	public void setLasttime(Date lasttime) {
		this.lasttime = lasttime;
	}
	public String getDbtid() {
		return dbtid;
	}
	public void setDbtid(String dbtid) {
		this.dbtid = dbtid;
	}
	

}
