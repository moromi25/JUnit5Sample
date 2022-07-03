package main.sample.vo;

import main.sample.constant.EnumCommonConfig;

public class UseServiceConfigVo {

	private EnumCommonConfig commonConfig;
	private int systemDefaultVal;
	private boolean existsUserConfig;
	private int userConfigVal;

	public UseServiceConfigVo(Builder builder) {
		this.commonConfig = builder.commonConfig;
		this.systemDefaultVal = builder.systemDefaultVal;
		this.existsUserConfig = builder.existsUserConfig;
		this.userConfigVal = builder.userConfigVal;
	}

	public EnumCommonConfig getCommonConfig() {
		return commonConfig;
	}

	public int getSystemDefaultVal() {
		return systemDefaultVal;
	}

	public boolean isExistsUserConfig() {
		return existsUserConfig;
	}

	public int getUserConfigVal() {
		return userConfigVal;
	}

	public static class Builder {

		private EnumCommonConfig commonConfig;
		private int systemDefaultVal;
		private boolean existsUserConfig;
		private int userConfigVal;

		public UseServiceConfigVo build() {
			return new UseServiceConfigVo(this);
		}

		public Builder commonConfig(EnumCommonConfig commonConfig) {
			this.commonConfig = commonConfig;
			return this;
		}

		public Builder systemDefaultVal(int systemDefaultVal) {
			this.systemDefaultVal = systemDefaultVal;
			return this;
		}

		public Builder existsUserConfig(boolean existsUserConfig) {
			this.existsUserConfig = existsUserConfig;
			return this;
		}

		public Builder userConfigVal(int userConfigVal) {
			this.userConfigVal = userConfigVal;
			return this;
		}

	}

}
