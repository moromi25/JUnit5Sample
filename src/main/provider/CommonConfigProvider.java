package main.provider;

import main.repo.DefaultCommonConfigRepoSitoryImpl;
import main.repo.DefaultCommonConfigRepository;

public class CommonConfigProvider {

	private DefaultCommonConfigRepository repo;

	public CommonConfigProvider() {
		repo = new DefaultCommonConfigRepoSitoryImpl();
	}
	
	public int getDefaultVal() {
		return repo.getDefaultVal();
	}
}
