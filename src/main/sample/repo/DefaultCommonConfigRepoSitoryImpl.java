package main.sample.repo;

import main.sample.constant.EnumCommonConfig;

public class DefaultCommonConfigRepoSitoryImpl implements DefaultCommonConfigRepository {

	@Override
	public int getDefaultVal() {
		// TODO DB�������Ă���
		return EnumCommonConfig.USE.getVal();
	}

}
