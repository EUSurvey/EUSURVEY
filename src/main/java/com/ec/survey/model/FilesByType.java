package com.ec.survey.model;

import com.ec.survey.model.survey.base.File;

import java.util.*;

public class FilesByType<K> {

	private final Map<K, List<File>> filesByType = new HashMap<>();

	public void addFile(final K key, final File file) {
		if (!filesByType.containsKey(key))
		{
			filesByType.put(key, new ArrayList<>());
		}
		filesByType.get(key).add(file);
	}

	public List<File> getFiles(final K key) {
		if (filesByType.containsKey(key)) {
			return filesByType.get(key);
		}
		return new ArrayList<>();
	}

	public Map<K, List<File>> getAllFiles() {
		return filesByType;
	}

	public boolean hasFiles() {
		for (final Map.Entry<K, List<File>> filesByQuestionUidEntry : filesByType.entrySet()) {
			if (!filesByQuestionUidEntry.getValue().isEmpty()) {
				return true;
			}
		}
		return false;
	}

	@FunctionalInterface
	public interface BiFunctionReturningVoid<In1, In2> {
		void apply(In1 in1, In2 in2) throws Exception;
	}

	public void applyFunctionOnEachFile(final BiFunctionReturningVoid<K, File> function) throws Exception {
		for (final Map.Entry<K, List<File>> filesByQuestionUidEntry : filesByType.entrySet()) {
			for (final File file : filesByQuestionUidEntry.getValue()) {
				K key = filesByQuestionUidEntry.getKey();
				function.apply(key, file);
			}
		}
	}

}
