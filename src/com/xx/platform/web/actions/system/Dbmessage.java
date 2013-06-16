package com.xx.platform.web.actions.system;

import java.util.List;

import com.xx.platform.domain.model.database.Dbconfig;
import com.xx.platform.domain.model.database.Dbtable;

public class Dbmessage {
	private Dbconfig dbconfig;
	private List<Dbtable> dbtablelist;
	public Dbconfig getDbconfig() {
		return dbconfig;
	}
	public void setDbconfig(Dbconfig dbconfig) {
		this.dbconfig = dbconfig;
	}
	public List<Dbtable> getDbtablelist() {
		return dbtablelist;
	}
	public void setDbtablelist(List<Dbtable> dbtablelist) {
		this.dbtablelist = dbtablelist;
	}
}
