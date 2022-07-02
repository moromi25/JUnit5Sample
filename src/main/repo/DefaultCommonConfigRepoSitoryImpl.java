package main.repo;

import main.constant.EnumCommonConfig;

public class DefaultCommonConfigRepoSitoryImpl implements DefaultCommonConfigRepository {

	@Override
	public int getDefaultVal() {
		// TODO DB‚©‚çŽæ‚Á‚Ä‚­‚é
		return EnumCommonConfig.USE.getVal();
	}

}
