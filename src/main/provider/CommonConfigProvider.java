package main.provider;

import main.repo.DefaultCommonConfigRepository;

public class CommonConfigProvider {

	/** TODO ������ */
	private DefaultCommonConfigRepository repo;

	public int getDefault() {
		return repo.find();
	}
}
