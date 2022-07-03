package main.sample.repo;

import main.sample.constant.EnumCommonConfig;

public class DefaultCommonConfigRepoSitoryImpl implements DefaultCommonConfigRepository {

	@Override
	public int getDefaultVal() {
		// TODO DB‚©‚çŽæ‚Á‚Ä‚­‚é
		return EnumCommonConfig.USE.getVal();
	}

}
