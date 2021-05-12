package com.ec.survey.model;

import com.ec.survey.model.survey.base.File;

import java.util.*;

public class FilesByTypes<K, T> {

	private final Map<K, FilesByType<T>> filesByTypes = new HashMap<>();

	public void addFile(final K key, final T secondKey, final File file) {
		if (!filesByTypes.containsKey(key))
		{
			filesByTypes.put(key, new FilesByType<>());
		}
		filesByTypes.get(key).addFile(secondKey, file);
	}

	public List<File> getFiles(final K key, final T secondKey) {
		if (filesByTypes.containsKey(key)) {
			return filesByTypes.get(key).getFiles(secondKey);
		}
		return new ArrayList<>();
	}

	public boolean hasFiles() {
		for (final Map.Entry<K, FilesByType<T>> filesByTypesEntry : filesByTypes.entrySet()) {
			if (filesByTypesEntry.getValue().hasFiles()) {
				return true;
			}
		}
		return false;
	}

	@FunctionalInterface
	public interface TriFunctionReturningVoid<In1, In2, In3> {
		void apply(In1 in1, In2 in2, In3 in3) throws Exception;
	}

	public void applyFunctionOnEachFile(final TriFunctionReturningVoid<K, T, File> function) throws Exception {
		for (final Map.Entry<K, FilesByType<T>> filesByTypesEntry : filesByTypes.entrySet()) {
			final Map<T, List<File>> filesByType = filesByTypesEntry.getValue().getAllFiles();
			for (final Map.Entry<T, List<File>> filesByTypeEntry : filesByType.entrySet()) {
				for (File file : filesByTypeEntry.getValue()) {
					final K key = filesByTypesEntry.getKey();
					final T secondKey = filesByTypeEntry.getKey();
					function.apply(key, secondKey, file);
				}
			}
		}
	}

}
