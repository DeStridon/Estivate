package com.estivate.context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A session / process currently active on the database server,
 * typically including the SQL being executed when available.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RunningQuery {

	Long id;
	String user;
	String host;
	String database;
	String command;
	Long timeSeconds;
	String state;
	String sql;

}
