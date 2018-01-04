package com.httphelper.main;

public class Parameter {

private static final String delimiter = "=";
	
	private final String key;
	private final String value;

	Parameter(String queryParameter) {
		String[] strs = queryParameter.split(delimiter);
		this.key = strs[0];
		this.value = strs[1];
	}
	
	public Parameter(String key, String value) {
		super();
		this.key = key;
		this.value = value;
	}

	@Override
	public String toString() {
		return key + delimiter + value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Parameter other = (Parameter) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}
}
