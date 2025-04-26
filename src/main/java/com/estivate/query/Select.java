package com.estivate.query;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Select extends Attribute implements Comparable {
	
	public String alias;

	@Override
	public int compareTo(Object o) {
		if(!(o instanceof Select)) {
			return -1;
		}
		Select select = (Select) o;
		
		if(this.function != null && select.function == null) {
			return -1;
		}
		else if(this.function == null && select.function != null) {
			return 1;
		}
		return StringUtils.compare(this.toString(), select.toString());
		
	}


}
