package vsb.gai0010;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class Table<K, T> {
	private final Map<K, Map<K, T>> values;
	private final T defaultValue;
	private final Set<K> columnNames;
	private final Set<K> rowNames;
	
	public Table(T defaultValue) {
		this.values = new HashMap<>();
		this.defaultValue = defaultValue;
		this.columnNames = new HashSet<>();
		this.rowNames = new HashSet<>();
	}
	
	public void addRow(K name) {
		if (rowNames.contains(name)) {
			return;
		}
		
		HashMap<K, T> value = new HashMap<>();		
		for (K column : columnNames) {
			value.put(column, this.defaultValue);
		}
		this.values.put(name, value);
		this.rowNames.add(name);
	}
	
	public void addColumn(K name) {
		if (this.columnNames.contains(name)) {
			return;
		}
		
		for (var entry : this.values.entrySet()) {
			entry.getValue().put(name, this.defaultValue);
		}
		this.columnNames.add(name);
	}
	
	public void setCell(K row, K col, T value) {
		if (columnNames.contains(col) == false && rowNames.contains(row) == false) {
			return;
		}
		
		this.values.get(row).put(col, value);
	}
	
	public T getCell(K row, K col) {
		if (columnNames.contains(col) == false && rowNames.contains(row) == false) {
			return null;
		}
		
		return this.values.get(row).get(col);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (var entry : this.values.entrySet()) {
			builder.append(entry.getKey().toString());
			builder.append(": ");
			for (var valueEntry : entry.getValue().entrySet() ) {
				builder.append(valueEntry.getKey().toString());
				builder.append("(");
				builder.append(valueEntry.getValue().toString());
				builder.append(") ");
			}
			builder.append('\n');
		}
		
		return builder.toString();
	}

	public Set<K> getColumnNames() {
		return columnNames;
	}

	public Set<K> getRowNames() {
		return rowNames;
	}
}
