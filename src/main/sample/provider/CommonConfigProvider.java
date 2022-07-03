package main.sample.provider;

import main.sample.repo.DefaultCommonConfigRepoSitoryImpl;
import main.sample.repo.DefaultCommonConfigRepository;

public class CommonConfigProvider {

	private DefaultCommonConfigRepository repo;

	public CommonConfigProvider() {
		repo = new DefaultCommonConfigRepoSitoryImpl();
	}
	
	public int getDefaultVal() {
		return repo.getDefaultVal();
	}
}
