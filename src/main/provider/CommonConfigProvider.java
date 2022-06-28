package main.provider;

import main.repo.DefaultCommonConfigRepository;

public class CommonConfigProvider {

	/** TODO ‰Šú‰» */
	private DefaultCommonConfigRepository repo;

	public int getDefault() {
		return repo.find();
	}
}
