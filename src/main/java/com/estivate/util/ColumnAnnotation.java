package com.estivate.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Unified view of the {@code @Column} attributes Estivate uses, whether the
 * annotation is {@code javax.persistence.Column} or {@code jakarta.persistence.Column}.
 */
@Getter
@Builder
@AllArgsConstructor
public class ColumnAnnotation {

	private Integer length;
	private Integer precision;
	private Integer scale;
	private Boolean nullable;

	private ColumnDefinition columnDefinition;
	

	@Getter
	@Builder
	@AllArgsConstructor
	public static class ColumnDefinition {

		private static final Pattern COLUMN_TYPE_PATTERN = Pattern.compile("([a-zA-Z]+)(?:\\s*\\(([0-9]+)(?:,([0-9]+))?\\))?(?:\\s+(UNSIGNED|SIGNED))?(?:\\s+ZEROFILL)?", Pattern.CASE_INSENSITIVE);

		private String type;
		private Integer dimension;
		private Integer scale;


		public ColumnDefinition(String columnType) {

			this.type = null;
			this.dimension = null;
			this.scale = null;
			
			if (columnType == null) {
				return;
			}
			if (columnType.toUpperCase().startsWith("ENUM")) {
				this.type = columnType;
				return;
			}
			Matcher matcher = COLUMN_TYPE_PATTERN.matcher(columnType.trim());
			if (matcher.find()) {
				this.type = matcher.group(1);
				this.dimension = matcher.group(2) == null ? null : Integer.parseInt(matcher.group(2));
				this.scale = matcher.group(3) == null ? null : Integer.parseInt(matcher.group(3));
				if (matcher.group(4) != null) {
					this.type = this.type + " " + matcher.group(4);
				}
			}
			return;
		}
	}

	public ColumnDefinition getColumnDefinition() {
		if(columnDefinition == null) {
			return new ColumnDefinition(null, null, null);
		}
		return columnDefinition;
	}



}
