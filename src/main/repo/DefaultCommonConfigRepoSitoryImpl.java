package main.repo;

import main.constant.EnumCommonConfig;

public class DefaultCommonConfigRepoSitoryImpl implements DefaultCommonConfigRepository {

	@Override
	public int getDefaultVal() {
		// TODO DB�������Ă���
		return EnumCommonConfig.USE.getVal();
	}

}
