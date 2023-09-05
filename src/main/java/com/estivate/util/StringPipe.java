package com.estivate.util;
public class StringPipe {
	
	StringBuilder sb = new StringBuilder();
	String separator = "";
	
	public StringPipe separator(String separator) {
		if(separator != null) {
			this.separator = separator;
		}
		return this;
	}
	
	public StringPipe append(String string) {
		if(sb.length() > 0 && !separator.isEmpty()) {
			sb.append(separator);
		}
		sb.append(string);
		return this;
	}
	
	public StringPipe appendIf(boolean condition, String ifTrue) {
		if(condition) {
			this.append(ifTrue);
		}
		return this;
	}
	
	public StringPipe appendIf(boolean condition, Object ifTrue, Object ifFalse) {
		if(condition) {
			sb.append(ifTrue.toString());
		}
		else {
			sb.append(ifFalse.toString());
		}
		return this;
	}
	
	
	
	public String toString() {
		return sb.toString();
	}
	
}